package at.jku.dke.task_app.fanf.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskController;
import at.jku.dke.task_app.fanf.data.entities.FanfTask;
import at.jku.dke.task_app.fanf.dto.FanfTaskDto;
import at.jku.dke.task_app.fanf.dto.ModifyFanfTaskDto;
import at.jku.dke.task_app.fanf.evaluation.model.*;
import at.jku.dke.task_app.fanf.services.FanfTaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

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

        NFSpecificationImp nfSpecification;

        try {
            nfSpecification = objectMapper.readValue(task.getSpecification(), NFSpecificationImp.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        fanfTaskDto.setBaseRelationAttributes(nfSpecification.getBaseRelation().getAttributes().toString().replace("[", "").replace("]", ""));
        fanfTaskDto.setBaseRelationDependencies(nfSpecification.getBaseRelation().getFunctionalDependencies().toString().replace("[", "").replace("]", ""));
        fanfTaskDto.setBaseRelationDependencies(nfSpecification.getBaseRelation().getFunctionalDependencies().stream().map(Object::toString).collect(Collectors.joining("; ")).replace("[", "").replace("]", ""));
        fanfTaskDto.setBaseRelationName(nfSpecification.getBaseRelation().getName());

        switch (fanfTaskDto.getSubtype()) {
            case 0:
                KeysDeterminationSpecification keysDeterminationSpecification = new KeysDeterminationSpecification();
                try {
                    keysDeterminationSpecification = objectMapper.readValue(task.getSpecification(), KeysDeterminationSpecification.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

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


                if (normalizationSpecification.getTargetLevel().equals(NormalformLevel.BOYCE_CODD)) {
                    fanfTaskDto.setNormalizationTargetLevel("BCNF");
                }
                if (normalizationSpecification.getTargetLevel().equals(NormalformLevel.FIRST)) {
                    fanfTaskDto.setNormalizationTargetLevel("1NF");
                }
                if (normalizationSpecification.getTargetLevel().equals(NormalformLevel.SECOND)) {
                    fanfTaskDto.setNormalizationTargetLevel("2NF");
                }
                if (normalizationSpecification.getTargetLevel().equals(NormalformLevel.THIRD)) {
                    fanfTaskDto.setNormalizationTargetLevel("3NF");
                }

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


                fanfTaskDto.setAttributeClosureBaseAttributes(attributeClosureSpecification.getBaseAttributes().toString().replace("[", "").replace("]", ""));
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


                fanfTaskDto.setNormalFormDeterminationPenaltyForIncorrectOverallNormalform(normalformDeterminationSpecification.getPenaltyForIncorrectNFOverall());
                fanfTaskDto.setNormalFormDeterminationPenaltyPerIncorrectDependencyNormalform(normalformDeterminationSpecification.getPenaltyPerIncorrectNFDependency());
                break;

        }

        return fanfTaskDto;
    }

}
