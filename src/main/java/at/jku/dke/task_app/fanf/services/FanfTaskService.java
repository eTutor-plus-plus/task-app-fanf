package at.jku.dke.task_app.fanf.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskService;
import at.jku.dke.task_app.fanf.data.entities.FanfTask;
import at.jku.dke.task_app.fanf.data.repositories.FanfTaskRepository;
import at.jku.dke.task_app.fanf.dto.ModifyFanfTaskDto;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

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
        if (!modifyTaskDto.taskType().equals("binary-search"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        return new FanfTask(modifyTaskDto.additionalData().getSpecification(), modifyTaskDto.additionalData().getRdbdType());
    }


    @Override
    protected void updateTask(FanfTask task, ModifyTaskDto<ModifyFanfTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("fanf"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        task.setSpecification(modifyTaskDto.additionalData().getSpecification());
        task.setRdbdType(modifyTaskDto.additionalData().getRdbdType());
    }

    @Override
    protected TaskModificationResponseDto mapToReturnData(FanfTask task, boolean create) {
        return new TaskModificationResponseDto(
            this.messageSource.getMessage("defaultTaskDescription", null, Locale.GERMAN),
            this.messageSource.getMessage("defaultTaskDescription", null, Locale.ENGLISH)
        );
    }
}
