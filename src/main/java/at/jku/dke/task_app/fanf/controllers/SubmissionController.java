package at.jku.dke.task_app.fanf.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseSubmissionController;
import at.jku.dke.task_app.fanf.data.entities.FanfSubmission;
import at.jku.dke.task_app.fanf.dto.BinarySearchSubmissionDto;
import at.jku.dke.task_app.fanf.services.FanfSubmissionService;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing {@link FanfSubmission}s.
 */
@RestController
public class SubmissionController extends BaseSubmissionController<BinarySearchSubmissionDto> {
    /**
     * Creates a new instance of class {@link SubmissionController}.
     *
     * @param submissionService The input service.
     */
    public SubmissionController(FanfSubmissionService submissionService) {
        super(submissionService);
    }
}
