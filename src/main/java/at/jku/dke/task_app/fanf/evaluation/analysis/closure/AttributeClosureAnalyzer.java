package at.jku.dke.task_app.fanf.evaluation.analysis.closure;



import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.task_app.fanf.evaluation.algorithms.Closure;
import at.jku.dke.task_app.fanf.evaluation.analysis.NFAnalysis;
import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class AttributeClosureAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(AttributeClosureAnalyzer.class);

    /**
     * Analyzes the submitted attributes with respect to the correct attributes and the functional dependencies.
     * @param dependencies The functional dependencies
     * @param baseAttributes The correct attributes
     * @param submittedAttributes The submitted attributes
     * @return The analysis result
     */
	public static NFAnalysis analyze(Collection<FunctionalDependency> dependencies, Collection<String> baseAttributes, Collection<String> submittedAttributes){
        List<CriterionDto> criteria = new ArrayList<>();
        AttributeClosureAnalysis analysis = new AttributeClosureAnalysis();
		Collection<String> correctAttributes = Closure.execute(baseAttributes, dependencies);

		analysis.setSubmissionSuitsSolution(true);

		//DETERMINING MISSING ATTRIBUTES
		analysis.setMissingAttributes(correctAttributes);
		analysis.removeAllMissingAttributes(submittedAttributes);
		if (!analysis.getMissingAttributes().isEmpty()){
			analysis.setSubmissionSuitsSolution(false);
			LOG.info( "FOUND MISSING ATTRIBUTES");
		}

		//DETERMINING ADDITIONAL ATTRIBUTES
		analysis.setAdditionalAttributes(submittedAttributes);
		analysis.removeAllAdditionalAttributes(correctAttributes);
		if (!analysis.getAdditionalAttributes().isEmpty()){
			analysis.setSubmissionSuitsSolution(false);
			LOG.info("FOUND ADDITIONAL ATTRIBUTES");
		}

		return analysis;
	}
}
