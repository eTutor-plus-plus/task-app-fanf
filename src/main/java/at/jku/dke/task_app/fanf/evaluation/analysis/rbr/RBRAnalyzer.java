package at.jku.dke.task_app.fanf.evaluation.analysis.rbr;



import at.jku.dke.task_app.fanf.evaluation.algorithms.Member;
import at.jku.dke.task_app.fanf.evaluation.algorithms.ReductionByResolution;
import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;
import at.jku.dke.task_app.fanf.evaluation.model.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.logging.Level;

public class RBRAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(RBRAnalyzer.class.getName());
	/**
	 * Tests whether a subrelation contains the correct, minimal functional dependencies with respect to the base
	 * relation.
	 * @param baseRelation The base relation
	 * @param subRelation The subrelation
	 * @return An <code>RBRAnalysis</code> for the supplied subrelation with respect to the base relation
	 */
	public static RBRAnalysis analyze(Relation baseRelation, Relation subRelation){
		LOG.info( "ANALYZE RBR for base-relation: " + baseRelation);
		LOG.info( "ANALYZE RBR for sub-relation: " + subRelation);

		RBRAnalysis analysis = new RBRAnalysis();
		analysis.setSubmissionSuitsSolution(true);

		Collection<FunctionalDependency> correctDependencies = ReductionByResolution.execute(baseRelation, subRelation.getAttributes());

		StringBuilder temp = new StringBuilder();
		for (FunctionalDependency currDependency : correctDependencies){
			temp.append(currDependency).append("; ");
		}
		LOG.info("CORRECT DEPENDENCIES: " + temp);

		/*
		 * Check if there is an equivalent for each correct dependency in the submission (i.e., whether any
		 * dependencies are missing from the submission (Gerald Wimmer, 2024-01-01).
		 */
		for (FunctionalDependency currCorrectDependency : correctDependencies){
			if (!Member.execute(currCorrectDependency, subRelation.getFunctionalDependencies())) {
				analysis.addMissingFunctionalDependency(currCorrectDependency);
				analysis.setSubmissionSuitsSolution(false);

				LOG.info( "Found missing functional dependency: " + currCorrectDependency);
			}
		}

		/*
		 * Check if there is an equivalent for each submitted dependency in the correct solution (i.e., whether there
		 * are any superfluous dependencies in the submission) (Gerald Wimmer, 2024-01-01).
		 */
		for (FunctionalDependency currSubmittedDependency : subRelation.getFunctionalDependencies()){
			if (!Member.execute(currSubmittedDependency, correctDependencies)) {
				analysis.addAdditionalFunctionalDependency(currSubmittedDependency);
				analysis.setSubmissionSuitsSolution(false);

				LOG.info( "Found additional functional dependency: " + currSubmittedDependency);
			}
		}

		return analysis;
	}
}
