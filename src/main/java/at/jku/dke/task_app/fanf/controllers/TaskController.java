package at.jku.dke.task_app.fanf.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskController;
import at.jku.dke.task_app.fanf.data.entities.FanfTask;
import at.jku.dke.task_app.fanf.dto.FanfTaskDto;
import at.jku.dke.task_app.fanf.dto.ModifyFanfTaskDto;
import at.jku.dke.task_app.fanf.evaluation.model.*;
import at.jku.dke.task_app.fanf.parser.NFParser;
import at.jku.dke.task_app.fanf.services.FanfTaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing {@link FanfTask}s.
 */
@RestController
public class TaskController extends BaseTaskController<FanfTask, FanfTaskDto, ModifyFanfTaskDto> {


    private final ObjectMapper objectMapper;
    /**
     * Creates a new instance of class {@link TaskController}.
     *
     * @param taskService The task service.
     */
    public TaskController(FanfTaskService taskService, ObjectMapper objectMapper) {
        super(taskService);
        this.objectMapper = objectMapper;
    }

    @Override
    protected FanfTaskDto mapToDto(FanfTask task) {
        //reverse the json to a fanfTaskdto object
        FanfTaskDto fanfTaskDto = new FanfTaskDto();
        fanfTaskDto.setSubtype(task.getRdbdType());

        switch (fanfTaskDto.getSubtype())
        {
            case 0:
                KeysDeterminationSpecification keysDeterminationSpecification = new KeysDeterminationSpecification();
                try {
                    keysDeterminationSpecification = objectMapper.readValue(task.getSpecification(), KeysDeterminationSpecification.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                fanfTaskDto.setBaseRelationName(keysDeterminationSpecification.getBaseRelation().getName());
                fanfTaskDto.setBaseRelationAttributes(keysDeterminationSpecification.getBaseRelation().getAttributes().toString());
                fanfTaskDto.setBaseRelationDependencies(keysDeterminationSpecification.getBaseRelation().getFunctionalDependencies().toString());
                fanfTaskDto.setKeysDeterminationPenaltyPerIncorrectKey(keysDeterminationSpecification.getPenaltyPerIncorrectKey());
                fanfTaskDto.setKeysDeterminationPenaltyPerMissingKey(keysDeterminationSpecification.getPenaltyPerMissingKey());
                break;
            case 1:
                NormalizationSpecification normalizationSpecification = new NormalizationSpecification();
                try {
                    normalizationSpecification = objectMapper.readValue(task.getSpecification(), NormalizationSpecification.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                fanfTaskDto.setBaseRelationName(normalizationSpecification.getBaseRelation().getName());
                fanfTaskDto.setBaseRelationAttributes(normalizationSpecification.getBaseRelation().getAttributes().toString());
                fanfTaskDto.setBaseRelationDependencies(normalizationSpecification.getBaseRelation().getFunctionalDependencies().toString());
                fanfTaskDto.setNormalizationTargetLevel(normalizationSpecification.getTargetLevel().toString());
                fanfTaskDto.setNormalizationMaxLostDependencies(normalizationSpecification.getMaxLostDependencies());
                fanfTaskDto.setNormalizationPenaltyPerLostAttribute(normalizationSpecification.getPenaltyPerLostAttribute());
                fanfTaskDto.setNormalizationPenaltyForLossyDecomposition(normalizationSpecification.getPenaltyForLossyDecomposition());
                fanfTaskDto.setNormalizationPenaltyPerNonCanonicalDependency(normalizationSpecification.getPenaltyPerNonCanonicalDependency());
                fanfTaskDto.setNormalizationPenaltyPerTrivialDependency(normalizationSpecification.getPenaltyPerTrivialDependency());
                fanfTaskDto.setNormalizationPenaltyPerExtraneousAttributeInDependencies(normalizationSpecification.getPenaltyPerExtraneousAttributeInDependencies());
                fanfTaskDto.setNormalizationPenaltyPerRedundantDependency(normalizationSpecification.getPenaltyPerRedundantDependency());
                fanfTaskDto.setNormalizationPenaltyPerExcessiveLostDependency(normalizationSpecification.getPenaltyPerExcessiveLostDependency());
                fanfTaskDto.setNormalizationPenaltyPerMissingNewDependency(normalizationSpecification.getPenaltyPerMissingNewDependency());
                fanfTaskDto.setNormalizationPenaltyPerIncorrectNewDependency(normalizationSpecification.getPenaltyPerIncorrectNewDependency());
                fanfTaskDto.setNormalizationPenaltyPerMissingKey(normalizationSpecification.getPenaltyPerMissingKey());
                fanfTaskDto.setNormalizationPenaltyPerIncorrectKey(normalizationSpecification.getPenaltyPerIncorrectKey());
                fanfTaskDto.setNormalizationPenaltyPerIncorrectNFRelation(normalizationSpecification.getPenaltyPerIncorrectNFRelation());
                break;
            case 2:
                MinimalCoverSpecification minimalCoverSpecification = new MinimalCoverSpecification();
                try {
                    minimalCoverSpecification = objectMapper.readValue(task.getSpecification(), MinimalCoverSpecification.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                fanfTaskDto.setBaseRelationName(minimalCoverSpecification.getBaseRelation().getName());
                fanfTaskDto.setBaseRelationAttributes(minimalCoverSpecification.getBaseRelation().getAttributes().toString());
                fanfTaskDto.setBaseRelationDependencies(minimalCoverSpecification.getBaseRelation().getFunctionalDependencies().toString());
                fanfTaskDto.setMinimalCoverPenaltyPerNonCanonicalDependency(minimalCoverSpecification.getPenaltyPerNonCanonicalDependency());
                fanfTaskDto.setMinimalCoverPenaltyPerTrivialDependency(minimalCoverSpecification.getPenaltyPerTrivialDependency());
                fanfTaskDto.setMinimalCoverPenaltyPerExtraneousAttribute(minimalCoverSpecification.getPenaltyPerExtraneousAttribute());
                fanfTaskDto.setMinimalCoverPenaltyPerRedundantDependency(minimalCoverSpecification.getPenaltyPerRedundantDependency());
                fanfTaskDto.setMinimalCoverPenaltyPerMissingDependencyVsSolution(minimalCoverSpecification.getPenaltyPerMissingDependencyVsSolution());
                fanfTaskDto.setMinimalCoverPenaltyPerIncorrectDependencyVsSolution(minimalCoverSpecification.getPenaltyPerIncorrectDependencyVsSolution());
                break;
            case 3:
                AttributeClosureSpecification attributeClosureSpecification = new AttributeClosureSpecification();
                try {
                    attributeClosureSpecification = objectMapper.readValue(task.getSpecification(), AttributeClosureSpecification.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                fanfTaskDto.setBaseRelationName(attributeClosureSpecification.getBaseRelation().getName());
                fanfTaskDto.setBaseRelationAttributes(attributeClosureSpecification.getBaseRelation().getAttributes().toString());
                fanfTaskDto.setBaseRelationDependencies(attributeClosureSpecification.getBaseRelation().getFunctionalDependencies().toString());
                fanfTaskDto.setAttributeClosureBaseAttributes(attributeClosureSpecification.getBaseAttributes().toString());
                fanfTaskDto.setAttributeClosurePenaltyPerMissingAttribute(attributeClosureSpecification.getPenaltyPerMissingAttribute());
                fanfTaskDto.setAttributeClosurePenaltyPerIncorrectAttribute(attributeClosureSpecification.getPenaltyPerIncorrectAttribute());
                break;
            case 4:
                NormalformDeterminationSpecification normalformDeterminationSpecification = new NormalformDeterminationSpecification();
                try {
                    normalformDeterminationSpecification = objectMapper.readValue(task.getSpecification(), NormalformDeterminationSpecification.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                fanfTaskDto.setBaseRelationName(normalformDeterminationSpecification.getBaseRelation().getName());
                fanfTaskDto.setBaseRelationAttributes(normalformDeterminationSpecification.getBaseRelation().getAttributes().toString());
                fanfTaskDto.setBaseRelationDependencies(normalformDeterminationSpecification.getBaseRelation().getFunctionalDependencies().toString());
                fanfTaskDto.setNormalFormDeterminationPenaltyForIncorrectOverallNormalform(normalformDeterminationSpecification.getPenaltyForIncorrectNFOverall());
                fanfTaskDto.setNormalFormDeterminationPenaltyPerIncorrectDependencyNormalform(normalformDeterminationSpecification.getPenaltyPerIncorrectNFDependency());
                break;

        }

        return fanfTaskDto;
    }

}
