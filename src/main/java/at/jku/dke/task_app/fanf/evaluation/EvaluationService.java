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
import at.jku.dke.task_app.fanf.evaluation.analysis.normalization.NormalizationAnalysis;
import at.jku.dke.task_app.fanf.evaluation.analysis.normalization.NormalizationAnalyzer;
import at.jku.dke.task_app.fanf.evaluation.analysis.normalization.NormalizationAnalyzerConfig;
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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


        List<CriterionDto> criteria = new ArrayList<>();
        //SyntaxCheck on RUN

        if (submission.mode().equals(SubmissionMode.RUN)) {
            if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
                criteria.add(new CriterionDto("Syntax", null, true, "Syntax correct"));
            } else {
                criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
            }
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, null, criteria);
        }

        //grade


        BigDecimal actualPoints = task.getMaxPoints();
        if (!analysis.getOverallLevelIsCorrect()) {
            actualPoints = actualPoints.subtract(BigDecimal.valueOf(specification.getPenaltyForIncorrectNFOverall()));
        }
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getWrongLeveledDependencies().size() * specification.getPenaltyPerIncorrectNFDependency()));

        //report

        String generalFeedback = "";

        if (analysis.submissionSuitsSolution()) {
            generalFeedback = messageSource.getMessage("correct", null, Locale.of(submission.language()));
        } else {
            generalFeedback = messageSource.getMessage("incorrect", null, Locale.of(submission.language()));
        }
        if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
            criteria.add(new CriterionDto("Syntax", null, true, "Syntax correct"));
        } else {
            criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, generalFeedback, criteria);
        }

        if (!analysis.getOverallNormalformLevel().equals(analysis.getSubmittedLevel())) {
            if ((submission.feedbackLevel() == 1) || (submission.feedbackLevel() == 2)) {
                criteria.add(new CriterionDto(messageSource.getMessage("incorrectnormalform", null, Locale.of(submission.language())), null, false, messageSource.getMessage("normalformnotcorrect", new Object[]{analysis.getSubmittedLevel()}, Locale.of(submission.language()))));
            }

            if (submission.feedbackLevel() == 3) {
                criteria.add(new CriterionDto(messageSource.getMessage("incorrectnormalform", null, Locale.of(submission.language())), null, false, messageSource.getMessage("normalformdoesnotmatch", new Object[]{normalformLevelToString(analysis.getSubmittedLevel(), Locale.of(submission.language())), normalformLevelToString(analysis.getOverallNormalformLevel(), Locale.of(submission.language()))}, Locale.of(submission.language()))));
            }
        }
        if (!analysis.getWrongLeveledDependencies().isEmpty()) {
            if (analysis.getWrongLeveledDependencies() != null) {
                StringBuilder feedback = new StringBuilder();
                switch (submission.feedbackLevel()) {
                    case 0:
                        break;
                    case 1:
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectnormalformviolations", null, Locale.of(submission.language())), null, false, messageSource.getMessage("dependencynotviolatesnormalform", null, Locale.of(submission.language()))));
                        break;
                    case 2:
                        feedback = new StringBuilder();
                        feedback.append(analysis.getWrongLeveledDependencies().size());
                        if (analysis.getWrongLeveledDependencies().size() == 1) {
                            feedback.append(" ").append(messageSource.getMessage("dependencyis", null, Locale.of(submission.language())));
                        } else {
                            feedback.append(" ").append(messageSource.getMessage("dependenciesare", null, Locale.of(submission.language())));
                        }
                        feedback.append(" ").append(messageSource.getMessage("notviolatenormalform", null, Locale.of(submission.language())));
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectnormalformviolations", null, Locale.of(submission.language())), null, false, feedback.toString()));
                        break;
                    case 3:
                        feedback = new StringBuilder();
                        feedback.append(messageSource.getMessage("violatenotspecifiednormalform", null, Locale.of(submission.language())));
                        feedback.append("<table rules='cols' border='1' style='margin-top:5px;'>");
                        feedback.append("	<tr>");
                        feedback.append("		<td style='border-bottom:solid;border-bottom-width:thin;padding-left:10px;padding-right:10px'><i>").append(messageSource.getMessage("functionaldependency", null, Locale.of(submission.language()))).append("</i></td>");
                        feedback.append("		<td style='border-bottom:solid;border-bottom-width:thin;padding-left:10px;padding-right:10px'><i>").append(messageSource.getMessage("violatednormalform", null, Locale.of(submission.language()))).append("</i></td>");
                        feedback.append("		<td style='border-bottom:solid;border-bottom-width:thin;padding-left:10px;padding-right:10px'><i>").append(messageSource.getMessage("yoursolution", null, Locale.of(submission.language()))).append("</i></td>");
                        feedback.append("	</tr>");

                        for (Object[] entry : analysis.getWrongLeveledDependencies()) {
                            feedback.append("	<tr>");
                            feedback.append("		<td align='center'>").append(entry[0].toString().replaceAll("->", "&rarr;")).append("</td>");
                            feedback.append("		<td align='center'>").append(normalformLevelToString((NormalformLevel) entry[1], Locale.of(submission.language()))).append("</td>");
                            feedback.append("		<td align='center'>").append(normalformLevelToString((NormalformLevel) entry[2], Locale.of(submission.language()))).append("</td>");
                            feedback.append("	</tr>");
                        }
                        feedback.append("</table>");
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectnormalformviolations", null, Locale.of(submission.language())), null, false, feedback.toString()));
                        break;
                }
            }

        }
        return new GradingDto(task.getMaxPoints(), actualPoints, generalFeedback, criteria);
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

                analysis = (AttributeClosureAnalysis) AttributeClosureAnalyzer.analyze(specification.getBaseRelation().getFunctionalDependencies(), specification.getBaseAttributes(), submissionRelation.getAttributes());
            }
        }

        //Set Submission
        analysis.setSubmission(submission);


        //SyntaxCheck on RUN
        List<CriterionDto> criteria = new ArrayList<>();

        if (submission.mode().equals(SubmissionMode.RUN)) {
            if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
                criteria.add(new CriterionDto("Syntax", null, true, "Syntax correct"));
            } else {
                criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));

            }
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, null, criteria);
        }


        String generalFeedback = "";

        if (analysis.submissionSuitsSolution()) {
            generalFeedback = messageSource.getMessage("correct", null, Locale.of(submission.language()));
        } else {
            generalFeedback = messageSource.getMessage("incorrect", null, Locale.of(submission.language()));
        }
        if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
            criteria.add(new CriterionDto("Syntax", null, true, "Syntax correct"));
        } else {
            criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, null, criteria);

        }
        //grade

        BigDecimal actualPoints = task.getMaxPoints();
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getMissingAttributes().size() * specification.getPenaltyPerMissingAttribute()));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getAdditionalAttributes().size() * specification.getPenaltyPerIncorrectAttribute()));

        //report


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
                            feedback.append(" ").append(messageSource.getMessage("attributeisa", null, Locale.of(submission.language()))).append(" ");
                        } else {
                            feedback.append(" ").append(messageSource.getMessage("attributesarea", null, Locale.of(submission.language()))).append(" ");
                        }
                        feedback.append(messageSource.getMessage("toomuch", null, Locale.of(submission.language())));
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectclosure", null, Locale.of(submission.language())), null, false, feedback.toString()));
                    }
                }

                break;
            case 3:
                if (analysis.getMissingAttributes() != null) {
                    if (analysis.getMissingAttributes().size() > 0) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectclosure", null, Locale.of(submission.language())), null, false, analysis.getMissingAttributes().toString().replace("[", "").replace("]", "") + " " + messageSource.getMessage("missing", null, Locale.of(submission.language()))));
                    }
                }
                if (analysis.getAdditionalAttributes() != null) {
                    if (analysis.getAdditionalAttributes().size() > 0) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectclosure", null, Locale.of(submission.language())), null, false, analysis.getAdditionalAttributes().toString().replace("[", "").replace("]", "") + " " + messageSource.getMessage("toomuch", null, Locale.of(submission.language()))));
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


        List<CriterionDto> criteria = new ArrayList<>();
        //check mode

        if (submission.mode().equals(SubmissionMode.RUN)) {

            if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
                criteria.add(new CriterionDto("Syntax", null, true, "Syntax correct"));
            } else {
                criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));

            }
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, null, criteria);
        }


        String generalFeedback = "";

        if (analysis.submissionSuitsSolution()) {
            generalFeedback = messageSource.getMessage("correct", null, Locale.of(submission.language()));
        } else {
            generalFeedback = messageSource.getMessage("incorrect", null, Locale.of(submission.language()));
        }
        if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
            criteria.add(new CriterionDto("Syntax", null, true, "Syntax correct"));
        } else {
            criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, null, criteria);
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
                    StringBuilder feedback = new StringBuilder();
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
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectnumberdependencies", null, Locale.of(submission.language())), null, false, feedback.toString()));
                    }
                }


                break;
            case 2:
                if (analysis.getCanonicalRepresentationAnalysis() != null) {
                    if (!analysis.getCanonicalRepresentationAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectcanonicalrepresentation", null, Locale.of(submission.language())), null, false, analysis.getCanonicalRepresentationAnalysis().getNotCanonicalDependencies().size()+" " + messageSource.getMessage("notcanonicalrepresentation", null, Locale.of(submission.language()))));
                    }
                }

                if (analysis.getTrivialDependenciesAnalysis() != null) {
                    if (!analysis.getTrivialDependenciesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("trivialdependencies", null, Locale.of(submission.language())), null, false, analysis.getTrivialDependenciesAnalysis().getTrivialDependencies().size() + " " + (analysis.getTrivialDependenciesAnalysis().getTrivialDependencies().size() == 1 ? messageSource.getMessage("dependencyis", null, Locale.of(submission.language())) : messageSource.getMessage("dependenciesare", null, Locale.of(submission.language()))) +" "+ messageSource.getMessage("trivial", null, Locale.of(submission.language()))));
                    }
                }

                if (analysis.getExtraneousAttributesAnalysis() != null) {
                    if (!analysis.getExtraneousAttributesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("extraneousattribute", null, Locale.of(submission.language())), null, false, analysis.getExtraneousAttributesAnalysis().getExtraneousAttributes().size() + " " + (analysis.getExtraneousAttributesAnalysis().getExtraneousAttributes().size() == 1 ? messageSource.getMessage("extraneousattributefound", null, Locale.of(submission.language())) : messageSource.getMessage("extraneousattributesfound", null, Locale.of(submission.language())))));
                    }
                }

                if (analysis.getRedundantDependenciesAnalysis() != null) {
                    if (!analysis.getRedundantDependenciesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("redundanddependency", null, Locale.of(submission.language())), null, false, analysis.getRedundantDependenciesAnalysis().getRedundantDependencies().size() + " " + (analysis.getRedundantDependenciesAnalysis().getRedundantDependencies().size() == 1 ? messageSource.getMessage("dependencyis", null, Locale.of(submission.language())) : messageSource.getMessage("dependenciesare", null, Locale.of(submission.language()))) + messageSource.getMessage("redundand", null, Locale.of(submission.language()))));

                    }
                }
                if (analysis.getDependenciesCoverAnalysis() != null) {
                    StringBuilder feedback = new StringBuilder();
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
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectnumberdependencies", null, Locale.of(submission.language())), null, false, feedback.toString()));

                    }
                }
                break;
            case 3:
                if (analysis.getCanonicalRepresentationAnalysis() != null) {
                    if (!analysis.getCanonicalRepresentationAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectcanonicalrepresentation", null, Locale.of(submission.language())), null, false, analysis.getCanonicalRepresentationAnalysis().getNotCanonicalDependencies().toString().replace("[","").replace("]","") +" "+ messageSource.getMessage("notcanonicalrepresentation", null, Locale.of(submission.language()))));
                    }
                }
                if (analysis.getTrivialDependenciesAnalysis() != null) {
                    if (!analysis.getTrivialDependenciesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("trivialdependencies", null, Locale.of(submission.language())), null, false, analysis.getTrivialDependenciesAnalysis().getTrivialDependencies().toString().replace("[","").replace("]","")+" " + messageSource.getMessage("istrivial", null, Locale.of(submission.language()))));
                    }
                }
                if (analysis.getExtraneousAttributesAnalysis() != null) {
                    if (!analysis.getExtraneousAttributesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("extraneousattribute", null, Locale.of(submission.language())), null, false, analysis.getExtraneousAttributesAnalysis().getExtraneousAttributes().toString().replace("[","").replace("]","")));
                    }
                }

                if (analysis.getRedundantDependenciesAnalysis() != null) {
                    if (!analysis.getRedundantDependenciesAnalysis().submissionSuitsSolution()) {
                        criteria.add(new CriterionDto(messageSource.getMessage("redundanddependency", null, Locale.of(submission.language())), null, false, analysis.getRedundantDependenciesAnalysis().getRedundantDependencies().toString().replace("[","").replace("]","")));
                    }
                }

                if (analysis.getDependenciesCoverAnalysis() != null) {
                    StringBuilder feedback = new StringBuilder();
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
                        criteria.add(new CriterionDto(messageSource.getMessage("incorrectnumberdependencies", null, Locale.of(submission.language())), null, false, feedback.toString().replace("[","").replace("]","")));

                    }
                }
                break;
        }


        return new GradingDto(task.getMaxPoints(), actualPoints, generalFeedback, criteria);
    }

    private GradingDto evaluateNormalization(FanfTask task, SubmitSubmissionDto<FanfSubmissionDto> submission) throws Exception {
        String submissionString = submission.submission().input();

        CharStream submissionLexerInput = CharStreams.fromString(submissionString);
        Lexer submissionLexer = new NFLexer(submissionLexerInput);
        TokenStream submissionParserInput = new CommonTokenStream(submissionLexer);
        NFParser submissionParser = new NFParser(submissionParserInput);

        NFParserErrorCollector errorCollector = new NFParserErrorCollector();
        // Source for adding to lexer: https://groups.google.com/g/antlr-discussion/c/FfiwtHCrgc0/m/_5wwPD3tK04J (Gerald Wimmer, 2024-01-21).
        submissionLexer.addErrorListener(errorCollector);
        submissionParser.addErrorListener(errorCollector);

        NormalizationSpecification specification;
        try {
            specification = new ObjectMapper().readValue(task.getSpecification(), NormalizationSpecification.class);
        } catch (Exception e) {
            throw new Exception("Could not deserialize NormalizationSpecification because: " + e.getMessage());
        }

        NormalizationAnalyzerConfig normalizationAnalyzerConfig = new NormalizationAnalyzerConfig();

        normalizationAnalyzerConfig.setBaseRelation(specification.getBaseRelation());
        normalizationAnalyzerConfig.setDesiredNormalformLevel(specification.getTargetLevel());
        normalizationAnalyzerConfig.setMaxLostDependencies(specification.getMaxLostDependencies());

        // Get normalized relations from input String. (Gerald Wimmer, 2023-12-02)
        Set<IdentifiedRelation> submissionSet = submissionParser.relationSetSubmission().relations;
        NormalizationAnalysis analysis = new NormalizationAnalysis();
        if (!errorCollector.getSyntaxErrors().isEmpty()) {
            analysis.setSyntaxError(errorCollector.getSyntaxErrors().toArray(new String[0]));
        } else {
            boolean hasIncorrectSyntax = false;

            // Check if there are relations with identical IDs (Gerald Wimmer, 2024-01-12)
            Set<String> relationIDs = new HashSet<>();
            Set<String> registeredDuplicates = new HashSet<>();
            for (IdentifiedRelation r : submissionSet) {
                if (relationIDs.contains(r.getID()) && !registeredDuplicates.contains(r.getID())) {
                    analysis.appendSyntaxError("Syntax error: Duplicate relation ID \"" + r.getID() + "\"");
                    registeredDuplicates.add(r.getID());
                    hasIncorrectSyntax = true;
                }
                relationIDs.add(r.getID());
            }

            // Check if the relations contain any attributes not found in the base relation (Gerald Wimmer, 2024-01-12)
            for (IdentifiedRelation r : submissionSet) {
                Set<String> incorrectAttributes = new HashSet<>(r.getAttributes());
                incorrectAttributes.addAll(r.getFunctionalDependencies().stream().flatMap(f -> f.getLhsAttributes().stream()).collect(Collectors.toSet()));
                incorrectAttributes.addAll(r.getFunctionalDependencies().stream().flatMap(f -> f.getRhsAttributes().stream()).collect(Collectors.toSet()));
                incorrectAttributes.addAll(r.getMinimalKeys().stream().flatMap(k -> k.getAttributes().stream()).collect(Collectors.toSet()));

                incorrectAttributes.removeAll(specification.getBaseRelation().getAttributes());

                if (!incorrectAttributes.isEmpty()) {
                    analysis.appendSyntaxError(getAttributesNotInBaseRelationErrorMessage(incorrectAttributes, "Relation \"" + r.getID() + "\""));

                    hasIncorrectSyntax = true;
                }
            }

            if (!hasIncorrectSyntax) {
                normalizationAnalyzerConfig.setNormalizedRelations(submissionSet);

                analysis = NormalizationAnalyzer.analyze(normalizationAnalyzerConfig);
            }
        }
        analysis.setSubmission((Serializable) submissionSet);
        List<CriterionDto> criteria = new ArrayList<>();

        if (submission.mode().equals(SubmissionMode.RUN)) {

            if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
                criteria.add(new CriterionDto("Syntax", null, true, "Syntax correct"));
            } else {
                criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
            }
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, null, criteria);
        }

        //SYNTAX
        if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
            criteria.add(new CriterionDto("Syntax", null, true, "Syntax correct"));
        } else {
            criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, null, criteria);
        }

        //grade

        BigDecimal actualPoints = task.getMaxPoints();

        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getDecompositionAnalysis().getMissingAttributes().size()).multiply(BigDecimal.valueOf(specification.getPenaltyPerLostAttribute())));
        if (!analysis.getLossLessAnalysis().submissionSuitsSolution()) {
            actualPoints = actualPoints.subtract(BigDecimal.valueOf(specification.getPenaltyForLossyDecomposition()));
        }
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getCanonicalRepresentationAnalyses().values().stream().mapToInt(canonicalRepresentationAnalysis -> canonicalRepresentationAnalysis.getNotCanonicalDependencies().size()).sum()).multiply(BigDecimal.valueOf(specification.getPenaltyPerNonCanonicalDependency())));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getTrivialDependenciesAnalyses().values().stream().mapToInt(trivialDependenciesAnalysis -> trivialDependenciesAnalysis.getTrivialDependencies().size()).sum()).multiply(BigDecimal.valueOf(specification.getPenaltyPerTrivialDependency())));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getExtraneousAttributesAnalyses().values().stream().mapToInt(extraneousAttributeAnalysis -> extraneousAttributeAnalysis.getExtraneousAttributes().values().stream().mapToInt(List::size).sum()).sum()).multiply(BigDecimal.valueOf(specification.getPenaltyPerExtraneousAttributeInDependencies())));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getRedundantDependenciesAnalyses().values().stream().mapToInt(redundantDependenciesAnalysis -> redundantDependenciesAnalysis.getRedundantDependencies().size()).sum()).multiply(BigDecimal.valueOf(specification.getPenaltyPerRedundantDependency())));
        if (analysis.getDepPresAnalysis().lostFunctionalDependenciesCount() > analysis.getMaxLostDependencies()) {
            actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getDepPresAnalysis().lostFunctionalDependenciesCount() - analysis.getMaxLostDependencies()).multiply(BigDecimal.valueOf(specification.getPenaltyPerExcessiveLostDependency())));
        }
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getRbrAnalyses().values().stream().mapToInt(rbrAnalysis -> rbrAnalysis.getMissingFunctionalDependencies().size()).sum()).multiply(BigDecimal.valueOf(specification.getPenaltyPerMissingNewDependency())));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getRbrAnalyses().values().stream().mapToInt(rbrAnalysis -> rbrAnalysis.getAdditionalFunctionalDependencies().size()).sum()).multiply(BigDecimal.valueOf(specification.getPenaltyPerIncorrectNewDependency())));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getKeysAnalyses().values().stream().mapToInt(keysAnalysis -> keysAnalysis.getMissingKeys().size()).sum()).multiply(BigDecimal.valueOf(specification.getPenaltyPerMissingKey())));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getKeysAnalyses().values().stream().mapToInt(keysAnalysis -> keysAnalysis.getAdditionalKeys().size()).sum()).multiply(BigDecimal.valueOf(specification.getPenaltyPerIncorrectKey())));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getNormalformAnalyses().values().stream().filter(normalformAnalysis -> !normalformAnalysis.submissionSuitsSolution()).count()).multiply(BigDecimal.valueOf(specification.getPenaltyPerIncorrectNFRelation())));

        //get language
        Locale locale = Locale.of(submission.language());

        //report


        String generalFeedback = "";
        if (analysis.submissionSuitsSolution()) {
            generalFeedback = messageSource.getMessage("correctsolution", null, locale);
        } else {
            generalFeedback = messageSource.getMessage("notcorrectsolution", null, locale);
        }





        //REPORT DECOMPOSITION_ANALYSIS
        if ((analysis.getDecompositionAnalysis() != null) && (!analysis.getDecompositionAnalysis().submissionSuitsSolution())) {
            switch (submission.feedbackLevel()) {
                case 0:
                    break;
                case 1:
                    criteria.add(new CriterionDto(messageSource.getMessage("invaliddecomposition", null, locale), null, false, messageSource.getMessage("notcomprisedbaserelation", null, locale)));
                    break;
                case 2:
                    StringBuilder feedback = new StringBuilder();
                    feedback.append(analysis.getDecompositionAnalysis().getMissingAttributes().size());
                    if (analysis.getDecompositionAnalysis().getMissingAttributes().size() == 1) {
                        feedback.append(" ").append(messageSource.getMessage("attributeis", null, locale)).append(" ");
                    } else {
                        feedback.append(" ").append(messageSource.getMessage("attributesare", null, locale)).append(" ");
                    }
                    feedback.append(messageSource.getMessage("notcompriseddecomposed", null, locale));
                    criteria.add(new CriterionDto(messageSource.getMessage("invaliddecomposition", null, locale), null, false, feedback.toString()));
                    break;
                case 3:
                    criteria.add(new CriterionDto(messageSource.getMessage("invaliddecomposition", null, locale), null, false, generateLevel3Div(analysis.getDecompositionAnalysis().getMissingAttributes(), "attributeisa", "attributesarea", "missing", locale, messageSource)));
                    break;
            }
        }

        //REPORT LOSS_LESS_ANALYSIS
        if ((analysis.getLossLessAnalysis() != null) && (!analysis.getLossLessAnalysis().submissionSuitsSolution())) {
            criteria.add(new CriterionDto(messageSource.getMessage("notlossless", null, locale), null, false, messageSource.getMessage("joinnotbaserelation", null, locale)));
        }

        //REPORT DEPENDENCIES_PRESERVATION_ANALYSIS
        if ((analysis.getDepPresAnalysis() != null) && (!analysis.getDepPresAnalysis().submissionSuitsSolution())) {
            int numberOfLostDependencies = analysis.getDepPresAnalysis().getLostFunctionalDependencies().size();
            StringBuilder feedback = new StringBuilder();
            if (submission.feedbackLevel() == 1) {
                feedback.append(messageSource.getMessage("minonerelationlost", null, locale));
            }

            if (submission.feedbackLevel() == 2) {
                feedback.append(numberOfLostDependencies);
                if (numberOfLostDependencies == 1) {
                    feedback.append(" ").append(messageSource.getMessage("dependencybaserelation", null, locale)).append(" ");
                } else {
                    feedback.append(" ").append(messageSource.getMessage("dependenciesbaserelation", null, locale)).append(" ");
                }
                feedback.append(" ").append(messageSource.getMessage("lost", null, locale)).append(".");
            }

            if ((submission.feedbackLevel() == 3)) {
                feedback.append(generateLevel3Div(analysis.getDepPresAnalysis().getLostFunctionalDependencies(), "dependencybaserelationa", "dependenciesbaserelationa", "lost", locale, messageSource));
            }

            if (analysis.getMaxLostDependencies() < analysis.getDepPresAnalysis().lostFunctionalDependenciesCount()) {
                criteria.add(new CriterionDto(messageSource.getMessage("toomanylost", null, locale), null, false, feedback.toString()));
            } else {
                criteria.add(new CriterionDto(messageSource.getMessage("notdependenciespreserving", null, locale), null, false, feedback.toString()));
            }
        }


        return new GradingDto(task.getMaxPoints(), actualPoints, generalFeedback, criteria);
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
        List<CriterionDto> criteria = new ArrayList<>();

        if (submission.mode().equals(SubmissionMode.RUN)) {

            if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
                criteria.add(new CriterionDto("Syntax", null, true, "Syntax correct"));
            } else {
                criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));

            }
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, null, criteria);
        }


        String generalFeedback = "";

        if (analysis.submissionSuitsSolution()) {
            generalFeedback = messageSource.getMessage("correct", null, Locale.of(submission.language()));
        } else {
            generalFeedback = messageSource.getMessage("incorrect", null, Locale.of(submission.language()));
        }
        if (analysis.getSyntaxError() == null || analysis.getSyntaxError().isEmpty()) {
            criteria.add(new CriterionDto("Syntax", null, true, "Syntax correct"));
        } else {
            criteria.add(new CriterionDto("Syntax", null, false, analysis.getSyntaxError()));
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, null, criteria);
        }

        //grade
        BigDecimal actualPoints = task.getMaxPoints();
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getMissingKeys().size() * specification.getPenaltyPerMissingKey()));
        actualPoints = actualPoints.subtract(BigDecimal.valueOf(analysis.getAdditionalKeys().size() * specification.getPenaltyPerIncorrectKey()));



        switch (submission.feedbackLevel()) {
            case 0:
                break;
            case 1:
                if (!analysis.getMissingKeys().isEmpty() && analysis.getMissingKeys() != null) {
                    criteria.add(new CriterionDto(messageSource.getMessage("missingKeys", null, Locale.of(submission.language())), null, false, messageSource.getMessage("keymissing", null, Locale.of(submission.language()))));
                }

                if (!analysis.getAdditionalKeys().isEmpty() && analysis.getAdditionalKeys() != null) {
                    criteria.add(new CriterionDto(messageSource.getMessage("additionalKeys", null, Locale.of(submission.language())), null, false, messageSource.getMessage("keytoomuch", null, Locale.of(submission.language()))));
                }
                break;
            case 2:
                if (!analysis.getMissingKeys().isEmpty() && analysis.getMissingKeys() != null) {
                    criteria.add(new CriterionDto(messageSource.getMessage("missingKeys", null, Locale.of(submission.language())), null, false, analysis.getMissingKeys().size() + " " + messageSource.getMessage("missingKeys", null, Locale.of(submission.language()))));
                }
                if (!analysis.getAdditionalKeys().isEmpty() && analysis.getAdditionalKeys() != null) {
                    criteria.add(new CriterionDto(messageSource.getMessage("additionalKeys", null, Locale.of(submission.language())), null, false, analysis.getAdditionalKeys().size() + " " + messageSource.getMessage("additionalKeys", null, Locale.of(submission.language()))));
                }
                break;
            case 3:
                if (!analysis.getMissingKeys().isEmpty() && analysis.getMissingKeys() != null) {
                    criteria.add(new CriterionDto(messageSource.getMessage("missingKeys", null, Locale.of(submission.language())), null, false, analysis.getMissingKeys().toString().replace("[", "").replace("]", "")));
                }
                if (!analysis.getAdditionalKeys().isEmpty() && analysis.getAdditionalKeys() != null) {
                    criteria.add(new CriterionDto(messageSource.getMessage("additionalKeys", null, Locale.of(submission.language())), null, false, analysis.getAdditionalKeys().toString().replace("[", "").replace("]", "")));
                }
                break;

        }

        return new GradingDto(task.getMaxPoints(), actualPoints, generalFeedback, criteria);
    }

    private static String getAttributesNotInBaseRelationErrorMessage(Collection<String> incorrectAttributes, String culprit) {
        StringJoiner attributesJoiner = new StringJoiner(", ");
        incorrectAttributes.forEach(attributesJoiner::add);

        return "Syntax error: " + culprit + " contains attributes \"" + attributesJoiner + "\" not found in the base relation";
    }

    public String normalformLevelToString(NormalformLevel level, Locale locale) {
        if (level == null) {
            return messageSource.getMessage("none", null, locale);
        } else if (level.equals(NormalformLevel.FIRST)) {
            return messageSource.getMessage("first", null, locale);
        } else if (level.equals(NormalformLevel.SECOND)) {
            return messageSource.getMessage("second", null, locale);
        } else if (level.equals(NormalformLevel.THIRD)) {
            return messageSource.getMessage("third", null, locale);
        } else if (level.equals(NormalformLevel.BOYCE_CODD)) {
            return messageSource.getMessage("boycecodd", null, locale);
        } else {
            return "";
        }
    }

    protected static String generateLevel3Div(Collection<?> collection, String singularNameKey, String pluralNameKey, String issueNameKey, Locale locale, MessageSource messageSource) {
        StringBuilder description = new StringBuilder();

        description.append("<div>").append(collection.size());

        description.append(" ");
        if (collection.size() == 1) {
            description.append(messageSource.getMessage(singularNameKey, null, locale));
        } else {
            description.append(messageSource.getMessage(pluralNameKey, null, locale));
        }
        description.append(" ");

        description.append(messageSource.getMessage(issueNameKey, null, locale)).append(".");
        description.append("<p/>");

        description.append(generateTable(collection));

        description.append("</div>");

        return description.toString();
    }

    private static String generateTable(Collection<?> collection) {
        StringBuilder ret = new StringBuilder(TABLE_HEADER);

        for (Object k : collection) {
            ret.append("<tr><td>").append(k.toString()).append("</td></tr>");
        }

        ret.append("</table>");

        return ret.toString();
    }

    protected static final String HTML_HEADER = "<head><link rel='stylesheet' href='/etutor/css/etutor.css'></link></head>";

    protected static final String TABLE_HEADER = "<table border='2' rules='all'>";

}
