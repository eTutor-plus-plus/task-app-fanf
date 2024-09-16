package at.jku.dke.task_app.fanf.evaluation.analysis.normalformdetermination;

import at.jku.dke.task_app.fanf.evaluation.analysis.keys.KeysAnalyzer;
import at.jku.dke.task_app.fanf.evaluation.analysis.normalform.NormalformAnalyzer;
import at.jku.dke.task_app.fanf.evaluation.analysis.normalform.NormalformAnalyzerConfig;
import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;
import at.jku.dke.task_app.fanf.evaluation.model.Key;
import at.jku.dke.task_app.fanf.evaluation.model.NormalformDeterminationSubmission;
import at.jku.dke.task_app.fanf.evaluation.model.NormalformLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

public class NormalformDeterminationAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(NormalformDeterminationAnalyzer.class);

	public static NormalformDeterminationAnalysis analyze(NormalformDeterminationSubmission submission, NormalformAnalyzerConfig config){
		NormalformDeterminationAnalysis analysis = new NormalformDeterminationAnalysis();
		analysis.setSubmissionSuitsSolution(true);
		analysis.setOverallLevelIsCorrect(true);

		StringBuilder temp = new StringBuilder();
        for (Key key : config.getCorrectMinimalKeys()) {
            temp.append(key).append("; ");
        }
		LOG.info( "Correct Minimal Keys: " + temp);

		//CHECK DEPENDENCIES
		for (FunctionalDependency currDependency : config.getRelation().getFunctionalDependencies()){
			//NFHelper.getLogger().log(Level.INFO, "Check Dependency: " + currDependency);

			if (NormalformAnalyzer.satisfiesFirstNormalform(analysis, currDependency, config)){
                if (NormalformAnalyzer.satisfiesSecondNormalform(analysis, currDependency, config)){
					if (NormalformAnalyzer.satisfiesThirdNormalform(analysis, currDependency, config)){
						NormalformAnalyzer.satisfiesBoyceCoddNormalform(analysis, currDependency, config);
					}
				}
			}
		}

		//COMPUTE OVERALL NORMALFORM LEVEL
		if (config.getRelation().getFunctionalDependencies().isEmpty()){
			if (config.getCorrectMinimalKeys().containsAll(config.getRelation().getMinimalKeys())) {
				analysis.setOverallNormalformLevel(NormalformLevel.BOYCE_CODD);
			} else {
				analysis.setOverallNormalformLevel(NormalformLevel.FIRST);
				analysis.setSubmissionSuitsSolution(false);
			}
		} else {
			analysis.setOverallNormalformLevel(NormalformLevel.BOYCE_CODD);
			if (!analysis.getBoyceCoddNormalformViolations().isEmpty()){
				analysis.setOverallNormalformLevel(NormalformLevel.THIRD);
			}
			if (!analysis.getThirdNormalformViolations().isEmpty()){
				analysis.setOverallNormalformLevel(NormalformLevel.SECOND);
			}
			if (!analysis.getSecondNormalformViolations().isEmpty()){
				analysis.setOverallNormalformLevel(NormalformLevel.FIRST);
			}
		}

		//DETERMINE WRONG LEVELED DEPENDENCIES
		for (FunctionalDependency currDependency : config.getRelation().getFunctionalDependencies()){
			LOG.info( "Check NF-Level of dependency: " + currDependency);

			NormalformLevel foundViolatedLevel = submission.getViolatedNormalformLevel(currDependency);
			NormalformLevel correctViolatedLevel = analysis.getViolatedNormalformLevel(currDependency);
			LOG.info("Found: " + foundViolatedLevel + " Correct: " + correctViolatedLevel);

			if (foundViolatedLevel != correctViolatedLevel) { // Note: Simplified because NormalFormLevel is now an enum. (Gerald Wimmer, 2023-12-02)
				LOG.info( "ADD WRONG LEVELED DEPENDENCY");
				analysis.addWrongLeveledDependency(currDependency, correctViolatedLevel, foundViolatedLevel);
				analysis.setSubmissionSuitsSolution(false);
			}
		}

		//DETERMINE CORRECTNESS OF OVERALL NF LEVEL
		analysis.setSubmittedLevel(submission.getOverallLevel());
		if (analysis.getOverallNormalformLevel().compareTo(analysis.getSubmittedLevel()) != 0) {
			analysis.setOverallLevelIsCorrect(false);
			analysis.setSubmissionSuitsSolution(false);
		}

		return analysis;
	}
}
