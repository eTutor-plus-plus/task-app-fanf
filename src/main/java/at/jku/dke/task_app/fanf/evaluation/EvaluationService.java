package at.jku.dke.task_app.fanf.evaluation;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.fanf.data.entities.FanfTask;
import at.jku.dke.task_app.fanf.data.repositories.FanfTaskRepository;
import at.jku.dke.task_app.fanf.dto.FanfSubmissionDto;
import at.jku.dke.task_app.fanf.evaluation.analysis.closure.AttributeClosureAnalysis;
import at.jku.dke.task_app.fanf.evaluation.analysis.closure.AttributeClosureAnalyzer;
import at.jku.dke.task_app.fanf.evaluation.analysis.keys.KeysAnalysis;
import at.jku.dke.task_app.fanf.evaluation.analysis.keys.KeysAnalyzer;
import at.jku.dke.task_app.fanf.evaluation.analysis.keys.KeysAnalyzerConfig;
import at.jku.dke.task_app.fanf.evaluation.analysis.minimalcover.MinimalCoverAnalysis;
import at.jku.dke.task_app.fanf.evaluation.analysis.minimalcover.MinimalCoverAnalyzer;
import at.jku.dke.task_app.fanf.evaluation.analysis.normalform.NormalformAnalyzerConfig;
import at.jku.dke.task_app.fanf.evaluation.analysis.normalformdetermination.NormalformDeterminationAnalysis;
import at.jku.dke.task_app.fanf.evaluation.analysis.normalformdetermination.NormalformDeterminationAnalyzer;
import at.jku.dke.task_app.fanf.evaluation.analysis.normalization.KeysDeterminator;
import at.jku.dke.task_app.fanf.evaluation.model.*;
import at.jku.dke.task_app.fanf.parser.NFLexer;
import at.jku.dke.task_app.fanf.parser.NFParser;
import at.jku.dke.task_app.fanf.parser.NFParserErrorCollector;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Service that evaluates submissions.
 */
@Service
public class EvaluationService {
    private static final Logger LOG = LoggerFactory.getLogger(EvaluationService.class);

    private final FanfTaskRepository taskRepository;
    private final MessageSource messageSource;


    /**
     * Creates a new instance of class {@link EvaluationService}.
     *
     * @param taskRepository The task repository.
     * @param messageSource  The message source.
     */
    public EvaluationService(FanfTaskRepository taskRepository, MessageSource messageSource, HttpMessageConverters messageConverters, MessageSourceAutoConfiguration messageSourceAutoConfiguration) {
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
    }

    /**
     * Evaluates a input.
     *
     * @param submission The input to evaluate.
     * @return The evaluation result.
     */
    @Transactional
    public GradingDto evaluate(SubmitSubmissionDto<FanfSubmissionDto> submission) {
        // find task
        FanfTask task = this.taskRepository.findById(submission.taskId()).orElseThrow(() -> new EntityNotFoundException("Task " + submission.taskId() + " does not exist."));
        LOG.info("Evaluating submission for task {}", task.getId());
        try {
            switch (task.getRdbdType()) {
                case 0:
                    return evaluateKeyDetermination(task, submission);
                case 1:
                    return evaluateNormalization(task, submission);
                case 2:
                    return evaluateMinimalCover(task, submission);
                case 3:
                    return evaluateAttributeClosure(task, submission);
                case 4:
                    return evaluateNormalFormDetermination(task, submission);
                default:
                    throw new IllegalArgumentException("Invalid task type.");
            }
        } catch (Exception e) {
            LOG.error("Error evaluating submission for task {}", task.getId(), e);
            return new GradingDto(BigDecimal.ZERO, BigDecimal.ZERO, e.getMessage(), null);
        }


    }

    private GradingDto evaluateNormalFormDetermination(FanfTask task, SubmitSubmissionDto<FanfSubmissionDto> submission) throws Exception {
        String submissionString = submission.submission().input();

        CharStream submissionLexerInput = CharStreams.fromString(submissionString);
        Lexer submissionLexer = new NFLexer(submissionLexerInput);
        TokenStream submissionParserInput = new CommonTokenStream(submissionLexer);
        NFParser submissionParser = new NFParser(submissionParserInput);

        NFParserErrorCollector errorCollector = new NFParserErrorCollector();
        // Source for adding to lexer: https://groups.google.com/g/antlr-discussion/c/FfiwtHCrgc0/m/_5wwPD3tK04J (Gerald Wimmer, 2024-01-21).
        submissionLexer.addErrorListener(errorCollector);
        submissionParser.addErrorListener(errorCollector);

        NormalformDeterminationSpecification specification;
        try {
            specification = new ObjectMapper().readValue(task.getSpecification(), NormalformDeterminationSpecification.class);
        } catch (Exception e) {
            throw new Exception("Could not deserialize NormalformDeterminationSpecification because: " + e.getMessage());
        }

        // Get submission from input String. (Gerald Wimmer, 2023-12-02)
        NormalformDeterminationSubmission normalformDeterminationSubmission = submissionParser.normalFormSubmission().submission;
        NormalformDeterminationAnalysis analysis = new NormalformDeterminationAnalysis();
        if (!errorCollector.getSyntaxErrors().isEmpty()) {
            analysis.setSyntaxError(errorCollector.getSyntaxErrors().toArray(new String[0]));
        } else {
            boolean hasIncorrectAttributes = false;

            Set<FunctionalDependency> incorrectFDs = normalformDeterminationSubmission.getNormalformViolations().keySet();
            incorrectFDs.removeAll(specification.getBaseRelation().getFunctionalDependencies());

            if (!incorrectFDs.isEmpty()) {
                StringJoiner depsJoiner = new StringJoiner(", ");
                incorrectFDs.forEach(f -> depsJoiner.add(f.toString()));

                analysis.appendSyntaxError("Syntax error: Submission contains functional dependencies \"" + depsJoiner + "\" not found in the base relation");

                hasIncorrectAttributes = true;
            }


            if (!hasIncorrectAttributes) {
                NormalformAnalyzerConfig normalformAnalyzerConfig = new NormalformAnalyzerConfig();

                normalformAnalyzerConfig.setCorrectMinimalKeys(KeysDeterminator.determineMinimalKeys(specification.getBaseRelation()));
                normalformAnalyzerConfig.setRelation(specification.getBaseRelation());

                analysis = NormalformDeterminationAnalyzer.analyze(normalformDeterminationSubmission, normalformAnalyzerConfig);
            }
        }

        //Set Submission
        analysis.setSubmission(normalformDeterminationSubmission);

        //SyntaxCheck on RUN

        if (submission.mode().equals(SubmissionMode.RUN)) {

            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty() ? "Syntax correct" : analysis.getSyntaxError(), null);
        }

        //grade


        BigDecimal actualPoints = task.getMaxPoints();
        if (!analysis.getOverallLevelIsCorrect()) {
            actualPoints = actualPoints.subtract(BigDecimal.valueOf(specification.getPenaltyForIncorrectNFOverall()));
        }
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getWrongLeveledDependencies().size() * specification.getPenaltyPerIncorrectNFDependency()));

        //report
        List<CriterionDto> criteria = new ArrayList<>();
        String generalFeedback = "";

        if (analysis.submissionSuitsSolution()) {
            generalFeedback = messageSource.getMessage("correct", null, Locale.of(submission.language()));
        } else {
            generalFeedback = messageSource.getMessage("incorrect", null, Locale.of(submission.language()));
        }
        if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
            criteria.add(new CriterionDto("Syntax", null, false, "Syntax correct"));
        } else {
            criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
        }

        switch (submission.feedbackLevel()) {
            case 0:
                break;
            case 1:
                if (analysis.getWrongLeveledDependencies() != null) {

                    if (analysis.getWrongLeveledDependencies().size() > 0) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectnormalformviolations", null, Locale.of(submission.language())), null, false, messageSource.getMessage("dependencynotviolatesnormalform", null, Locale.of(submission.language()))));
                    }
                }
                break;
            case 2:
                break;
            case 3:
                break;


        }
        return new GradingDto(BigDecimal.ZERO, BigDecimal.ZERO, "Not implemented", null);

    }

    private GradingDto evaluateAttributeClosure(FanfTask task, SubmitSubmissionDto<FanfSubmissionDto> submission) throws Exception {
        String submissionString = submission.submission().input();

        CharStream submissionLexerInput = CharStreams.fromString(submissionString);
        Lexer submissionLexer = new NFLexer(submissionLexerInput);
        TokenStream submissionParserInput = new CommonTokenStream(submissionLexer);
        NFParser submissionParser = new NFParser(submissionParserInput);

        NFParserErrorCollector errorCollector = new NFParserErrorCollector();
        // Source for adding to lexer: https://groups.google.com/g/antlr-discussion/c/FfiwtHCrgc0/m/_5wwPD3tK04J (Gerald Wimmer, 2024-01-21).
        submissionLexer.addErrorListener(errorCollector);
        submissionParser.addErrorListener(errorCollector);
        AttributeClosureSpecification specification;
        try {
            specification = new ObjectMapper().readValue(task.getSpecification(), AttributeClosureSpecification.class);
        } catch (Exception e) {
            throw new Exception("Could not deserialize AttributeClosureSpecification because: " + e.getMessage());
        }

        // Assemble relation from input String. (Gerald Wimmer, 2023-11-27)
        Relation submissionRelation = new Relation();
        Set<String> attributes = submissionParser.attributeSetSubmission().attributes;
        AttributeClosureAnalysis analysis = new AttributeClosureAnalysis();
        if (!errorCollector.getSyntaxErrors().isEmpty()) {
            analysis.setSyntaxError(errorCollector.getSyntaxErrors().toArray(new String[0]));
        } else {
            boolean hasIncorrectAttributes = false;

            Set<String> incorrectAttributes = new HashSet<>(attributes);
            incorrectAttributes.removeAll(specification.getBaseRelation().getAttributes());

            if (!incorrectAttributes.isEmpty()) {
                analysis.appendSyntaxError(getAttributesNotInBaseRelationErrorMessage(incorrectAttributes, "Attribute closure"));

                hasIncorrectAttributes = true;
            }

            if (!hasIncorrectAttributes) {
                submissionRelation.setAttributes(attributes);

                analysis = (AttributeClosureAnalysis) AttributeClosureAnalyzer.analyze(
                    specification.getBaseRelation().getFunctionalDependencies(),
                    specification.getBaseAttributes(),
                    submissionRelation.getAttributes());
            }
        }

        //Set Submission
        analysis.setSubmission(submission);


        //SyntaxCheck on RUN
        if (submission.mode().equals(SubmissionMode.RUN)) {

            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty() ? "Syntax correct" : analysis.getSyntaxError(), null);
        }

        //grade

        BigDecimal actualPoints = task.getMaxPoints();
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getMissingAttributes().size() * specification.getPenaltyPerMissingAttribute()));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getAdditionalAttributes().size() * specification.getPenaltyPerIncorrectAttribute()));

        //report
        List<CriterionDto> criteria = new ArrayList<>();
        String generalFeedback = "";

        if (analysis.submissionSuitsSolution()) {
            generalFeedback = messageSource.getMessage("correct", null, Locale.of(submission.language()));
        } else {
            generalFeedback = messageSource.getMessage("incorrect", null, Locale.of(submission.language()));
        }
        if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
            criteria.add(new CriterionDto("Syntax", null, false, "Syntax correct"));
        } else {
            criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
        }

        switch (submission.feedbackLevel()) {
            case 0:
                break;
            case 1:
                if (analysis.getMissingAttributes() != null) {

                    if (analysis.getMissingAttributes().size() > 0) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectclosure", null, Locale.of(submission.language())), null, false, messageSource.getMessage("attributemissing", null, Locale.of(submission.language()))));
                    }
                }
                if (analysis.getAdditionalAttributes() != null) {

                    if (analysis.getAdditionalAttributes().size() > 0) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectclosure", null, Locale.of(submission.language())), null, false, messageSource.getMessage("attributetoomuch", null, Locale.of(submission.language()))));
                    }

                }
                break;
            case 2:
                if (analysis.getMissingAttributes() != null) {
                    StringBuilder feedback = new StringBuilder();
                    if (analysis.getMissingAttributes().size() > 0) {
                        feedback.append(analysis.getMissingAttributes().size());
                        if (analysis.getMissingAttributes().size() == 1) {
                            feedback.append(" ").append(messageSource.getMessage("attributeisa", null, Locale.of(submission.language()))).append(" ");
                        } else {
                            feedback.append(" ").append(messageSource.getMessage("attributesarea", null, Locale.of(submission.language()))).append(" ");
                        }
                        feedback.append(messageSource.getMessage("missing", null, Locale.of(submission.language())));
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectclosure", null, Locale.of(submission.language())), null, false, feedback.toString()));
                    }
                }
                if (analysis.getAdditionalAttributes() != null) {
                    StringBuilder feedback = new StringBuilder();
                    if (analysis.getAdditionalAttributes().size() > 0) {
                        feedback.append(analysis.getAdditionalAttributes().size());
                        if (analysis.getAdditionalAttributes().size() == 1) {
                            feedback.append(" ").append(messageSource.getMessage("attributeisa", null, Locale.of(submission.language())));
                        } else {
                            feedback.append(" ").append(messageSource.getMessage("attributesarea", null, Locale.of(submission.language())));
                        }
                        feedback.append(messageSource.getMessage("toomuch", null, Locale.of(submission.language())));
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectclosure", null, Locale.of(submission.language())), null, false, feedback.toString()));
                    }
                }

                break;
            case 3:
                if (analysis.getMissingAttributes() != null) {
                    if (analysis.getMissingAttributes().size() > 0) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectclosure", null, Locale.of(submission.language())), null, false,
                            analysis.getMissingAttributes().toString() + messageSource.getMessage("missing", null, Locale.of(submission.language()))));
                    }
                }
                if (analysis.getAdditionalAttributes() != null) {
                    if (analysis.getAdditionalAttributes().size() > 0) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectclosure", null, Locale.of(submission.language())), null, false,
                            analysis.getAdditionalAttributes().toString() + messageSource.getMessage("toomuch", null, Locale.of(submission.language()))));
                    }
                }
                break;
        }


        return new GradingDto(task.getMaxPoints(), actualPoints, generalFeedback, criteria);
    }

    private GradingDto evaluateMinimalCover(FanfTask task, SubmitSubmissionDto<FanfSubmissionDto> submission) throws Exception {
        String submissionString = submission.submission().input();

        CharStream submissionLexerInput = CharStreams.fromString(submissionString);
        Lexer submissionLexer = new NFLexer(submissionLexerInput);
        TokenStream submissionParserInput = new CommonTokenStream(submissionLexer);
        NFParser submissionParser = new NFParser(submissionParserInput);

        NFParserErrorCollector errorCollector = new NFParserErrorCollector();
        // Source for adding to lexer: https://groups.google.com/g/antlr-discussion/c/FfiwtHCrgc0/m/_5wwPD3tK04J (Gerald Wimmer, 2024-01-21).
        submissionLexer.addErrorListener(errorCollector);
        submissionParser.addErrorListener(errorCollector);
        MinimalCoverSpecification specification;
        try {
            specification = new ObjectMapper().readValue(task.getSpecification(), MinimalCoverSpecification.class);
        } catch (Exception e) {
            throw new Exception("Could not deserialize MinimalCoverSpecification because: " + e.getMessage());
        }

        // Assemble relation from input String. (Gerald Wimmer, 2023-11-27)
        Relation submissionRelation = new Relation();
        Set<FunctionalDependency> functionalDependencies = submissionParser.functionalDependencySetSubmission().functionalDependencies;
        MinimalCoverAnalysis analysis = new MinimalCoverAnalysis();
        if (!errorCollector.getSyntaxErrors().isEmpty()) {
            analysis.setSyntaxError(errorCollector.getSyntaxErrors().toArray(new String[0]));
        } else {
            boolean hasIncorrectAttributes = false;

            for (FunctionalDependency f : functionalDependencies) {
                Set<String> incorrectAttributes = new HashSet<>(f.getLhsAttributes());
                incorrectAttributes.addAll(f.getRhsAttributes());

                incorrectAttributes.removeAll(specification.getBaseRelation().getAttributes());

                if (!incorrectAttributes.isEmpty()) {
                    analysis.appendSyntaxError(getAttributesNotInBaseRelationErrorMessage(incorrectAttributes, "Functional Dependency \"" + f + "\""));

                    hasIncorrectAttributes = true;
                }
            }

            if (!hasIncorrectAttributes) {
                submissionRelation.setFunctionalDependencies(functionalDependencies);

                analysis = MinimalCoverAnalyzer.analyze(submissionRelation, specification.getBaseRelation());
            }
        }

        //Set Submission
        analysis.setSubmission(submission);


        //check mode

        if (submission.mode().equals(SubmissionMode.RUN)) {

            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty() ? "Syntax correct" : analysis.getSyntaxError(), null);
        }

        //grade
        BigDecimal actualPoints = task.getMaxPoints();
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getCanonicalRepresentationAnalysis().getNotCanonicalDependencies().size() * specification.getPenaltyPerNonCanonicalDependency()));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getTrivialDependenciesAnalysis().getTrivialDependencies().size() * specification.getPenaltyPerTrivialDependency()));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getExtraneousAttributesAnalysis().getExtraneousAttributes().values().stream().mapToInt(List::size).sum() * specification.getPenaltyPerExtraneousAttribute()));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getRedundantDependenciesAnalysis().getRedundantDependencies().size() * specification.getPenaltyPerRedundantDependency()));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getDependenciesCoverAnalysis().getMissingDependencies().size() * specification.getPenaltyPerMissingDependencyVsSolution()));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size() * specification.getPenaltyPerIncorrectDependencyVsSolution()));


        //report

        List<CriterionDto> criteria = new ArrayList<>();
        String generalFeedback = "";

        if (analysis.submissionSuitsSolution()) {
            generalFeedback = messageSource.getMessage("correct", null, Locale.of(submission.language()));
        } else {
            generalFeedback = messageSource.getMessage("incorrect", null, Locale.of(submission.language()));
        }
        if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
            criteria.add(new CriterionDto("Syntax", null, false, "Syntax correct"));
        } else {
            criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
        }

        switch (submission.feedbackLevel()) {
            case 0:
                break;
            case 1:
                if (analysis.getCanonicalRepresentationAnalysis() != null) {
                    if (!analysis.getCanonicalRepresentationAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectcanonicalrepresentation", null, Locale.of(submission.language())), null, false, messageSource.getMessage("notcanonical", null, Locale.of(submission.language()))));
                    }
                }
                if (analysis.getTrivialDependenciesAnalysis() != null) {
                    if (!analysis.getTrivialDependenciesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("trivialdependencies", null, Locale.of(submission.language())), null, false, messageSource.getMessage("minonetrivial", null, Locale.of(submission.language()))));
                    }
                }
                if (analysis.getExtraneousAttributesAnalysis() != null) {
                    if (!analysis.getExtraneousAttributesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("extraneousattribute", null, Locale.of(submission.language())), null, false, messageSource.getMessage("minoneextraneous", null, Locale.of(submission.language()))));
                    }
                }

                if (analysis.getRedundantDependenciesAnalysis() != null) {
                    if (!analysis.getRedundantDependenciesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("redundanddependency", null, Locale.of(submission.language())), null, false, messageSource.getMessage("minoneredundant", null, Locale.of(submission.language()))));
                    }
                }

                if (analysis.getDependenciesCoverAnalysis() != null) {
                    StringBuffer feedback = new StringBuffer();
                    if (!analysis.getDependenciesCoverAnalysis().submissionSuitsSolution()) {
                        if (analysis.getDependenciesCoverAnalysis().getMissingDependencies().size() > 0) {
                            feedback.append(messageSource.getMessage("minonedependencymissing", null, Locale.of(submission.language())));
                        }
                        if ((analysis.getDependenciesCoverAnalysis().getMissingDependencies().size() > 0) && (analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size() > 0)) {
                            feedback.append("<br>");
                        }
                        if (analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size() > 0) {
                            feedback.append(messageSource.getMessage("dependencynotderived", null, Locale.of(submission.language())));
                        }
                    }
                    criteria.add(new CriterionDto(messageSource.getMessage("incorrectnumberdependencies", null, Locale.of(submission.language())), null, false, feedback.toString()));
                }


                criteria.add(new CriterionDto("Missing Dependencies", null, false, analysis.getDependenciesCoverAnalysis().getMissingDependencies().size() + " Missing Dependencies"));
                criteria.add(new CriterionDto("Additional Dependencies", null, false, analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size() + " Additional Dependencies"));
                break;
            case 2:
                if (analysis.getCanonicalRepresentationAnalysis() != null) {
                    if (!analysis.getCanonicalRepresentationAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectcanonicalrepresentation", null, Locale.of(submission.language())), null, false,
                            analysis.getCanonicalRepresentationAnalysis().getNotCanonicalDependencies().size() + messageSource.getMessage("notcanonicalrepresentation", null, Locale.of(submission.language()))));
                    }
                }

                if (analysis.getTrivialDependenciesAnalysis() != null) {
                    if (!analysis.getTrivialDependenciesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("trivialdependencies", null, Locale.of(submission.language())), null, false,
                            analysis.getTrivialDependenciesAnalysis().getTrivialDependencies().size() + " " +
                                (analysis.getTrivialDependenciesAnalysis().getTrivialDependencies().size() == 1 ? messageSource.getMessage("dependenciesis", null, Locale.of(submission.language())) : messageSource.getMessage("dependenciesare", null, Locale.of(submission.language())))
                                + messageSource.getMessage("trivial", null, Locale.of(submission.language()))));
                    }
                }

                if (analysis.getExtraneousAttributesAnalysis() != null) {
                    if (!analysis.getExtraneousAttributesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("extraneousattribute", null, Locale.of(submission.language())), null, false,
                            analysis.getExtraneousAttributesAnalysis().getExtraneousAttributes().size() + " " +
                                (analysis.getExtraneousAttributesAnalysis().getExtraneousAttributes().size() == 1 ? messageSource.getMessage("dependencyis", null, Locale.of(submission.language())) : messageSource.getMessage("dependenciesare", null, Locale.of(submission.language())))
                            + messageSource.getMessage("redundand", null, Locale.of(submission.language()))));
                    }
                }

                if (analysis.getRedundantDependenciesAnalysis() != null) {
                    if (!analysis.getRedundantDependenciesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("redundanddependency", null, Locale.of(submission.language())), null, false,
                            analysis.getRedundantDependenciesAnalysis().getRedundantDependencies().size() + " " + messageSource.getMessage("minoneredundant", null, Locale.of(submission.language()))));
                    }
                }
                if (analysis.getDependenciesCoverAnalysis() != null) {
                    StringBuffer feedback = new StringBuffer();
                    if (!analysis.getDependenciesCoverAnalysis().submissionSuitsSolution()) {
                        if (analysis.getDependenciesCoverAnalysis().getMissingDependencies().size() > 0) {
                            feedback.append(analysis.getDependenciesCoverAnalysis().getMissingDependencies().size());
                            if (analysis.getDependenciesCoverAnalysis().getMissingDependencies().size() == 1) {
                                feedback.append(" ").append(messageSource.getMessage("dependencyis", null, Locale.of(submission.language()))).append(" ");
                            } else {
                                feedback.append(" ").append(messageSource.getMessage("dependenciesare", null, Locale.of(submission.language()))).append(" ");
                            }
                            feedback.append(messageSource.getMessage("missing", null, Locale.of(submission.language()))).append(".");
                        }
                        if ((analysis.getDependenciesCoverAnalysis().getMissingDependencies().size() > 0) && (analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size() > 0)) {
                            feedback.append("<br>");
                        }
                        if (analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size() > 0) {
                            feedback.append(analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size());
                            if (analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size() == 1) {
                                feedback.append(" ").append(messageSource.getMessage("dependencyis", null, Locale.of(submission.language()))).append(" ");
                            } else {
                                feedback.append(" ").append(messageSource.getMessage("dependenciesare", null, Locale.of(submission.language()))).append(" ");
                            }
                            feedback.append(messageSource.getMessage("cannotbederived", null, Locale.of(submission.language())));
                        }
                    }
                    criteria.add(new CriterionDto(messageSource.getMessage("incorrectnumberdependencies", null, Locale.of(submission.language())), null, false, feedback.toString()));
                }
                break;
            case 3:
                if (analysis.getCanonicalRepresentationAnalysis() != null) {
                    if (!analysis.getCanonicalRepresentationAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectcanonicalrepresentation", null, Locale.of(submission.language())), null, false,
                            analysis.getCanonicalRepresentationAnalysis().getNotCanonicalDependencies().toString() + messageSource.getMessage("notcanonicalrepresentation", null, Locale.of(submission.language()))));
                    }
                }
                if (analysis.getTrivialDependenciesAnalysis() != null) {
                    if (!analysis.getTrivialDependenciesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("trivialdependencies", null, Locale.of(submission.language())), null, false,
                            messageSource.getMessage("trivial", null, Locale.of(submission.language())) + analysis.getTrivialDependenciesAnalysis().getTrivialDependencies().toString()));
                    }
                }
                if (analysis.getExtraneousAttributesAnalysis() != null) {
                    if (!analysis.getExtraneousAttributesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("extraneousattribute", null, Locale.of(submission.language())), null, false, analysis.getExtraneousAttributesAnalysis().getExtraneousAttributes().toString()));
                    }
                }

                if (analysis.getRedundantDependenciesAnalysis() != null) {
                    if (!analysis.getRedundantDependenciesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("redundanddependency", null, Locale.of(submission.language())), null, false, analysis.getRedundantDependenciesAnalysis().getRedundantDependencies().toString()));
                    }
                }

                if (analysis.getDependenciesCoverAnalysis() != null) {
                    StringBuffer feedback = new StringBuffer();
                    if (!analysis.getDependenciesCoverAnalysis().submissionSuitsSolution()) {
                        if (analysis.getDependenciesCoverAnalysis().getMissingDependencies().size() > 0) {
                            feedback.append(analysis.getDependenciesCoverAnalysis().getMissingDependencies());
                            if (analysis.getDependenciesCoverAnalysis().getMissingDependencies().size() == 1) {
                                feedback.append(" ").append(messageSource.getMessage("dependencyis", null, Locale.of(submission.language()))).append(" ");
                            } else {
                                feedback.append(" ").append(messageSource.getMessage("dependenciesare", null, Locale.of(submission.language()))).append(" ");
                            }
                            feedback.append(messageSource.getMessage("missing", null, Locale.of(submission.language()))).append(".");
                        }
                        if ((analysis.getDependenciesCoverAnalysis().getMissingDependencies().size() > 0) && (analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size() > 0)) {
                            feedback.append("<br>");
                        }
                        if (analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size() > 0) {
                            feedback.append(analysis.getDependenciesCoverAnalysis().getAdditionalDependencies());
                            if (analysis.getDependenciesCoverAnalysis().getAdditionalDependencies().size() == 1) {
                                feedback.append(" ").append(messageSource.getMessage("dependencyis", null, Locale.of(submission.language()))).append(" ");
                            } else {
                                feedback.append(" ").append(messageSource.getMessage("dependenciesare", null, Locale.of(submission.language()))).append(" ");
                            }
                            feedback.append(messageSource.getMessage("cannotbederived", null, Locale.of(submission.language())));
                        }
                    }
                    criteria.add(new CriterionDto(messageSource.getMessage("incorrectnumberdependencies", null, Locale.of(submission.language())), null, false, feedback.toString()));
                }
                break;
        }


        return new GradingDto(task.getMaxPoints(), actualPoints, generalFeedback, criteria);
    }

    private GradingDto evaluateNormalization(FanfTask task, SubmitSubmissionDto<FanfSubmissionDto> submission) {
        return new GradingDto(BigDecimal.ZERO, BigDecimal.ZERO, "Not implemented", null);
    }

    private GradingDto evaluateKeyDetermination(FanfTask task, SubmitSubmissionDto<FanfSubmissionDto> submission) throws Exception {
        String submissionString = submission.submission().input();

        CharStream submissionLexerInput = CharStreams.fromString(submissionString);
        Lexer submissionLexer = new NFLexer(submissionLexerInput);
        TokenStream submissionParserInput = new CommonTokenStream(submissionLexer);
        NFParser submissionParser = new NFParser(submissionParserInput);

        NFParserErrorCollector errorCollector = new NFParserErrorCollector();
        // Source for adding to lexer: https://groups.google.com/g/antlr-discussion/c/FfiwtHCrgc0/m/_5wwPD3tK04J (Gerald Wimmer, 2024-01-21).
        submissionLexer.addErrorListener(errorCollector);
        submissionParser.addErrorListener(errorCollector);

        KeysDeterminationSpecification specification;
        try {    // Source: https://mkyong.com/java/how-to-convert-java-object-to-from-json-jackson/
            specification = new ObjectMapper().readValue(task.getSpecification(), KeysDeterminationSpecification.class);
        } catch (Exception e) {
            throw new Exception("Could not deserialize KeysDeterminationSpecification because: " + e.getMessage());
        }

        KeysAnalyzerConfig keysAnalyzerConfig = new KeysAnalyzerConfig();
        KeysContainer correctKeys = KeysDeterminator.determineAllKeys(specification.getBaseRelation());
        keysAnalyzerConfig.setCorrectMinimalKeys(correctKeys.getMinimalKeys());

        // Assemble relation from input string (Gerald Wimmer, 2023-11-27)
        Relation submissionRelation = new Relation();
        Set<Key> minimalKeys = submissionParser.keySetSubmission().keys;
        KeysAnalysis analysis = new KeysAnalysis();
        if (!errorCollector.getSyntaxErrors().isEmpty()) {
            analysis.setSyntaxError(errorCollector.getSyntaxErrors().toArray(new String[0]));
        } else {
            boolean hasIncorrectAttributes = false;

            for (Key k : minimalKeys) {
                Set<String> incorrectAttributes = new HashSet<>(k.getAttributes());
                incorrectAttributes.removeAll(specification.getBaseRelation().getAttributes());

                if (!incorrectAttributes.isEmpty()) {
                    analysis.appendSyntaxError(getAttributesNotInBaseRelationErrorMessage(incorrectAttributes, "Key \"" + k + "\""));

                    hasIncorrectAttributes = true;
                }
            }

            if (!hasIncorrectAttributes) {
                submissionRelation.setMinimalKeys(minimalKeys);

                analysis = KeysAnalyzer.analyze(submissionRelation, keysAnalyzerConfig);
            }
        }
        //Set Submission
        analysis.setSubmission(submissionRelation);

        //Check mode

        if (submission.mode().equals(SubmissionMode.RUN)) {

            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty() ? "Syntax correct" : analysis.getSyntaxError(), null);

        }


        //grade
        BigDecimal actualPoints = task.getMaxPoints();
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getMissingKeys().size() * specification.getPenaltyPerMissingKey()));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getAdditionalKeys().size() * specification.getPenaltyPerIncorrectKey()));

        List<CriterionDto> criteria = new ArrayList<>();
        String generalFeedback = "";

        if (analysis.submissionSuitsSolution()) {
            generalFeedback = messageSource.getMessage("correct", null, Locale.of(submission.language()));
        } else {
            generalFeedback = messageSource.getMessage("incorrect", null, Locale.of(submission.language()));
        }
        if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
            criteria.add(new CriterionDto("Syntax", null, false, "Syntax correct"));
        } else {
            criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
        }

        switch (submission.feedbackLevel()) {
            case 0:
                break;
            case 1:
            case 2:
                criteria.add(new CriterionDto(messageSource.getMessage("missingKeys", null, Locale.of(submission.language())), null, false,
                    analysis.getMissingKeys().size() + " " + messageSource.getMessage("missingKeys", null, Locale.of(submission.language()))));
                criteria.add(new CriterionDto(messageSource.getMessage("additionalKeys", null, Locale.of(submission.language())), null, false,
                    analysis.getAdditionalKeys().size() + " " + messageSource.getMessage("additionalKeys", null, Locale.of(submission.language()))));
                break;
            case 3:
                criteria.add(new CriterionDto(messageSource.getMessage("missingKeys", null, Locale.of(submission.language())), null, false, analysis.getMissingKeys().toString()));
                criteria.add(new CriterionDto(messageSource.getMessage("additionalKeys", null, Locale.of(submission.language())), null, false, analysis.getAdditionalKeys().toString()));
                break;

        }

        return new GradingDto(task.getMaxPoints(), actualPoints, generalFeedback, criteria);
    }

    private static String getAttributesNotInBaseRelationErrorMessage(Collection<String> incorrectAttributes, String culprit) {
        StringJoiner attributesJoiner = new StringJoiner(", ");
        incorrectAttributes.forEach(attributesJoiner::add);

        return "Syntax error: " + culprit + " contains attributes \"" + attributesJoiner + "\" not found in the base relation";
    }
}
