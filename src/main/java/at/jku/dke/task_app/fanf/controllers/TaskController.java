package at.jku.dke.task_app.fanf.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskController;
import at.jku.dke.task_app.fanf.data.entities.FanfTask;
import at.jku.dke.task_app.fanf.dto.FanfTaskDto;
import at.jku.dke.task_app.fanf.dto.ModifyFanfTaskDto;
import at.jku.dke.task_app.fanf.services.FanfTaskService;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing {@link FanfTask}s.
 */
@RestController
public class TaskController extends BaseTaskController<FanfTask, FanfTaskDto, ModifyFanfTaskDto> {

    /**
     * Creates a new instance of class {@link TaskController}.
     *
     * @param taskService The task service.
     */
    public TaskController(FanfTaskService taskService) {
        super(taskService);
    }

    @Override
    protected FanfTaskDto mapToDto(FanfTask task) {
        return new FanfTaskDto(task.getSpecification(),task.getRdbdType());
    }

}
