package at.jku.dke.task_app.fanf.evaluation;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.fanf.data.entities.FanfTask;
import at.jku.dke.task_app.fanf.data.repositories.FanfTaskRepository;
import at.jku.dke.task_app.fanf.dto.FanfSubmissionDto;
import at.jku.dke.task_app.fanf.evaluation.analysis.keys.KeysAnalysis;
import at.jku.dke.task_app.fanf.evaluation.analysis.keys.KeysAnalyzer;
import at.jku.dke.task_app.fanf.evaluation.analysis.keys.KeysAnalyzerConfig;
import at.jku.dke.task_app.fanf.evaluation.analysis.normalization.KeysDeterminator;
import at.jku.dke.task_app.fanf.evaluation.model.Key;
import at.jku.dke.task_app.fanf.evaluation.model.KeysContainer;
import at.jku.dke.task_app.fanf.evaluation.model.KeysDeterminationSpecification;
import at.jku.dke.task_app.fanf.evaluation.model.Relation;
import at.jku.dke.task_app.fanf.parser.NFLexer;
import at.jku.dke.task_app.fanf.parser.NFParser;
import at.jku.dke.task_app.fanf.parser.NFParserErrorCollector;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public EvaluationService(FanfTaskRepository taskRepository, MessageSource messageSource) {
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

    private GradingDto evaluateNormalFormDetermination(FanfTask task, SubmitSubmissionDto<FanfSubmissionDto> submission) {
        return new GradingDto(BigDecimal.ZERO, BigDecimal.ZERO, "Not implemented", null);
    }

    private GradingDto evaluateAttributeClosure(FanfTask task, SubmitSubmissionDto<FanfSubmissionDto> submission) {
        return new GradingDto(BigDecimal.ZERO, BigDecimal.ZERO, "Not implemented", null);
    }

    private GradingDto evaluateMinimalCover(FanfTask task, SubmitSubmissionDto<FanfSubmissionDto> submission) {
        return new GradingDto(BigDecimal.ZERO, BigDecimal.ZERO, "Not implemented", null);
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

        //Check

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
                criteria.add(new CriterionDto("Missing Keys", null, false, analysis.getMissingKeys().size() + " Missing Keys"));
                criteria.add(new CriterionDto("Additional Keys", null, false, analysis.getAdditionalKeys().size() + " Additional Keys"));
                break;
            case 3:
                criteria.add(new CriterionDto("Missing Keys", null, false, analysis.getMissingKeys().toString()));
                criteria.add(new CriterionDto("Additional Keys", null, false, analysis.getAdditionalKeys().toString()));
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
