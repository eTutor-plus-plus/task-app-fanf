package at.jku.dke.task_app.fanf.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskService;
import at.jku.dke.task_app.fanf.data.entities.FanfTask;
import at.jku.dke.task_app.fanf.data.repositories.FanfTaskRepository;
import at.jku.dke.task_app.fanf.description_generation.DescriptionGeneration;
import at.jku.dke.task_app.fanf.dto.ModifyFanfTaskDto;
import at.jku.dke.task_app.fanf.evaluation.model.*;
import at.jku.dke.task_app.fanf.parser.NFLexer;
import at.jku.dke.task_app.fanf.parser.NFParser;
import at.jku.dke.task_app.fanf.parser.NFParserErrorCollector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.antlr.v4.runtime.*;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Set;

/**
 * This class provides methods for managing {@link FanfTask}s.
 */
@Service
public class FanfTaskService extends BaseTaskService<FanfTask, ModifyFanfTaskDto> {

    private final MessageSource messageSource;

    /**
     * Creates a new instance of class {@link FanfTaskService}.
     *
     * @param repository    The task repository.
     * @param messageSource The message source.
     */
    public FanfTaskService(FanfTaskRepository repository, MessageSource messageSource) {
        super(repository);
        this.messageSource = messageSource;
    }

    @Override
    protected FanfTask createTask(long id, ModifyTaskDto<ModifyFanfTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("fanf"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        String specification = convertToJSONString(modifyTaskDto.additionalData());


        return new FanfTask(specification, modifyTaskDto.additionalData().getSubtype());
    }


    @Override
    protected void updateTask(FanfTask task, ModifyTaskDto<ModifyFanfTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("fanf"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        String specification = convertToJSONString(modifyTaskDto.additionalData());

        task.setSpecification(specification);
        task.setRdbdType(modifyTaskDto.additionalData().getSubtype());

    }

    @Override
    protected TaskModificationResponseDto mapToReturnData(FanfTask task, boolean create) {
        ObjectMapper objectMapper = new ObjectMapper();
        switch (task.getRdbdType())
        {
            case 0 -> {
                try {
                    return DescriptionGeneration.printAssignmentForKeysDetermination(objectMapper.readValue(task.getSpecification(),KeysDeterminationSpecification.class).getBaseRelation(), 0);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case 1 -> {
                try {
                    return DescriptionGeneration.printAssignmentForNormalization(objectMapper.readValue(task.getSpecification(),NormalizationSpecification.class), 0);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case 2 -> {
                try {
                    return DescriptionGeneration.printAssignmentForMinimalCover(objectMapper.readValue(task.getSpecification(), MinimalCoverSpecification.class).getBaseRelation(), 0);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case 3 -> {
                try {
                    return DescriptionGeneration.printAssignmentForAttributeClosure(objectMapper.readValue(task.getSpecification(),AttributeClosureSpecification.class), 0);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case 4 -> {
                try {
                    return DescriptionGeneration.printAssignmentForNormalFormDetermination(objectMapper.readValue(task.getSpecification(),NormalformDeterminationSpecification.class).getBaseRelation(), 0);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }




    private String convertToJSONString(ModifyFanfTaskDto dto) {
        IdentifiedRelation baseRelation = new IdentifiedRelation();

        baseRelation.setName(dto.getBaseRelationName());
        baseRelation.setID(dto.getBaseRelationName());

        NFParserErrorCollector errorCollector = new NFParserErrorCollector();

        NFParser baseAttributesParser = getParser(dto.getBaseRelationAttributes(), errorCollector);

        Set<String> baseAttributes = baseAttributesParser.attributeSetSubmission().attributes;

        if (!errorCollector.getSyntaxErrors().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Syntax error(s) in base attributes: " + errorCollector.getStringOfAllErrors());
        }

        baseRelation.setAttributes(baseAttributes);

        if (!dto.getBaseRelationDependencies().isBlank()) {
            NFParser baseDependenciesParser = getParser(dto.getBaseRelationDependencies(), errorCollector);

            Set<FunctionalDependency> baseDependencies = baseDependenciesParser.functionalDependencySetSubmission().functionalDependencies;

            if (!errorCollector.getSyntaxErrors().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Syntax error(s) in base dependencies: " + errorCollector.getStringOfAllErrors());
            }

            baseRelation.setFunctionalDependencies(baseDependencies);
        }

        // source: https://stackoverflow.com/a/15786175 (Gerald Wimmer, 2024-01-05)
        ObjectWriter objectWriter = new ObjectMapper().writer();

        try {
            switch (dto.getSubtype()) {
                case 0 -> {
                    KeysDeterminationSpecification specification = new KeysDeterminationSpecification();
                    specification.setBaseRelation(baseRelation);

                    specification.setPenaltyPerMissingKey((int) dto.getKeysDeterminationPenaltyPerMissingKey());
                    specification.setPenaltyPerIncorrectKey((int) dto.getKeysDeterminationPenaltyPerIncorrectKey());

                    return objectWriter.writeValueAsString(specification);
                }
                case 1 -> {
                    NFParser targetLevelParser = getParser(dto.getNormalizationTargetLevel(), errorCollector);

                    NormalformLevel targetLevel = targetLevelParser.normalFormSpecification().level;

                    if (!errorCollector.getSyntaxErrors().isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Syntax error(s) in target level: " + errorCollector.getStringOfAllErrors());
                    }

                    NormalizationSpecification specification = new NormalizationSpecification();
                    specification.setBaseRelation(baseRelation);

                    specification.setTargetLevel(targetLevel);
                    specification.setMaxLostDependencies((int) dto.getNormalizationMaxLostDependencies());

                    specification.setPenaltyPerLostAttribute((int) dto.getNormalizationPenaltyPerLostAttribute());
                    specification.setPenaltyForLossyDecomposition((int) dto.getNormalizationPenaltyForLossyDecomposition());
                    specification.setPenaltyPerNonCanonicalDependency((int) dto.getNormalizationPenaltyPerNonCanonicalDependency());
                    specification.setPenaltyPerTrivialDependency((int) dto.getNormalizationPenaltyPerTrivialDependency());
                    specification.setPenaltyPerExtraneousAttributeInDependencies((int) dto.getNormalizationPenaltyPerExtraneousAttributeInDependencies());
                    specification.setPenaltyPerRedundantDependency((int) dto.getNormalizationPenaltyPerRedundantDependency());
                    specification.setPenaltyPerExcessiveLostDependency((int) dto.getNormalizationPenaltyPerExcessiveLostDependency());
                    specification.setPenaltyPerMissingNewDependency((int) dto.getNormalizationPenaltyPerMissingNewDependency());
                    specification.setPenaltyPerIncorrectNewDependency((int) dto.getNormalizationPenaltyPerIncorrectNewDependency());
                    specification.setPenaltyPerMissingKey((int) dto.getNormalizationPenaltyPerMissingKey());
                    specification.setPenaltyPerIncorrectKey((int) dto.getNormalizationPenaltyPerIncorrectKey());
                    specification.setPenaltyPerIncorrectNFRelation((int) dto.getNormalizationPenaltyPerIncorrectNFRelation());

                    return objectWriter.writeValueAsString(specification);
                }
                case 2 -> {
                    MinimalCoverSpecification specification = new MinimalCoverSpecification();
                    specification.setBaseRelation(baseRelation);

                    specification.setPenaltyPerNonCanonicalDependency((int) dto.getMinimalCoverPenaltyPerNonCanonicalDependency());
                    specification.setPenaltyPerTrivialDependency((int) dto.getMinimalCoverPenaltyPerTrivialDependency());
                    specification.setPenaltyPerExtraneousAttribute((int) dto.getMinimalCoverPenaltyPerExtraneousAttribute());
                    specification.setPenaltyPerRedundantDependency((int) dto.getMinimalCoverPenaltyPerRedundantDependency());
                    specification.setPenaltyPerMissingDependencyVsSolution((int) dto.getMinimalCoverPenaltyPerMissingDependencyVsSolution());
                    specification.setPenaltyPerIncorrectDependencyVsSolution((int) dto.getMinimalCoverPenaltyPerIncorrectDependencyVsSolution());

                    return objectWriter.writeValueAsString(specification);
                }
                case 3 -> {
                    NFParser acBaseAttributesParser = getParser(dto.getAttributeClosureBaseAttributes(), errorCollector);

                    Set<String> acBaseAttributes = acBaseAttributesParser.attributeSetSubmission().attributes;

                    if (!errorCollector.getSyntaxErrors().isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Syntax error(s) in attribute closure base attributes: " + errorCollector.getStringOfAllErrors());
                    }

                    AttributeClosureSpecification specification = new AttributeClosureSpecification();
                    specification.setBaseRelation(baseRelation);

                    specification.setBaseAttributes(acBaseAttributes);

                    specification.setPenaltyPerMissingAttribute((int) dto.getAttributeClosurePenaltyPerMissingAttribute());
                    specification.setPenaltyPerIncorrectAttribute((int) dto.getAttributeClosurePenaltyPerIncorrectAttribute());

                    return objectWriter.writeValueAsString(specification);
                }
                case 4 -> {
                    NormalformDeterminationSpecification specification = new NormalformDeterminationSpecification();
                    specification.setBaseRelation(baseRelation);

                    specification.setPenaltyForIncorrectNFOverall((int) dto.getNormalFormDeterminationPenaltyForIncorrectOverallNormalform());
                    specification.setPenaltyPerIncorrectNFDependency((int) dto.getNormalFormDeterminationPenaltyPerIncorrectDependencyNormalform());

                    return objectWriter.writeValueAsString(specification);
                }
                default ->
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not generate JSON for exercise specification due to unrecognized task type \"" + dto.getSubtype() + "\".");
            }
        } catch (JsonProcessingException jp) {
            jp.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not generate JSON for exercise specification because: " + jp.getMessage());
        }
    }

    /**
     * Creates a new <code>NFParser</code> instance from the supplied input <code>String</code> and with the
     * supplied <code>NFParserErrorCollector</code>
     *
     * @param input          The <code>String</code> to serve as the parser input
     * @param errorCollector The <code>NFParserErrorCollector</code> to collect any syntax errors
     * @return A new <code>NFParser</code> object
     */
    private NFParser getParser(String input, NFParserErrorCollector errorCollector) {
        // Source: https://datacadamia.com/antlr/getting_started (Gerald Wimmer, 2023-11-27)
        CharStream lexerInput = CharStreams.fromString(input);
        Lexer lexer = new NFLexer(lexerInput);
        TokenStream parserInput = new CommonTokenStream(lexer);
        NFParser parser = new NFParser(parserInput);

        // Source for adding to lexer: https://groups.google.com/g/antlr-discussion/c/FfiwtHCrgc0/m/_5wwPD3tK04J (Gerald Wimmer, 2024-01-21).
        lexer.addErrorListener(errorCollector);
        parser.addErrorListener(errorCollector);

        return parser;
    }
}
