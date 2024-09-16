package at.jku.dke.task_app.fanf.services;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.etutor.task_app.services.BaseSubmissionService;
import at.jku.dke.task_app.fanf.data.entities.FanfSubmission;

import at.jku.dke.task_app.fanf.data.entities.FanfTask;
import at.jku.dke.task_app.fanf.data.repositories.FanfSubmissionRepository;
import at.jku.dke.task_app.fanf.data.repositories.FanfTaskRepository;
import at.jku.dke.task_app.fanf.dto.FanfSubmissionDto;
import at.jku.dke.task_app.fanf.evaluation.EvaluationService;
import org.springframework.stereotype.Service;

/**
 * This class provides methods for managing {@link FanfSubmission}s.
 */
@Service
public class FanfSubmissionService extends BaseSubmissionService<FanfTask, FanfSubmission, FanfSubmissionDto> {

    private final EvaluationService evaluationService;

    /**
     * Creates a new instance of class {@link FanfSubmissionService}.
     *
     * @param submissionRepository The input repository.
     * @param taskRepository       The task repository.
     * @param evaluationService    The evaluation service.
     */
    public FanfSubmissionService(FanfSubmissionRepository submissionRepository, FanfTaskRepository taskRepository, EvaluationService evaluationService) {
        super(submissionRepository, taskRepository);
        this.evaluationService = evaluationService;
    }

    @Override
    protected FanfSubmission createSubmissionEntity(SubmitSubmissionDto<FanfSubmissionDto> submitSubmissionDto) {
        return new FanfSubmission(submitSubmissionDto.submission().input());
    }

    @Override
    protected GradingDto evaluate(SubmitSubmissionDto<FanfSubmissionDto> submitSubmissionDto) {
        return this.evaluationService.evaluate(submitSubmissionDto);
    }

    @Override
    protected FanfSubmissionDto mapSubmissionToSubmissionData(FanfSubmission submission) {
        return new FanfSubmissionDto(submission.getSubmission());
    }

}
