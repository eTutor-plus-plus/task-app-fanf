package at.jku.dke.task_app.fanf.evaluation.analysis.keys;



import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.task_app.fanf.evaluation.model.Key;
import at.jku.dke.task_app.fanf.evaluation.model.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.logging.Level;

public class KeysAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(KeysAnalyzer.class);
	/**
	 * Tests whether the supplied relation has the correct minimal keys and only minimal keys.
	 * @param relation The relation to be tested
	 * @param config The configuration for this analyzer
	 * @return A <code>KeysAnalysis</code> about the supplied relation
	 */
	public static KeysAnalysis  analyze(Relation relation, KeysAnalyzerConfig config){
		// Log progress

    LOG.info("Start analyzing keys determination.");



		Set<Key> correctKeys = config.getCorrectMinimalKeys();
		KeysAnalysis analysis = new KeysAnalysis();
        analysis.setSubmission(relation.getMinimalKeys());

		analysis.setMissingKeys(correctKeys);
		analysis.removeAllMissingKeys(relation.getMinimalKeys());

		analysis.setAdditionalKeys(relation.getMinimalKeys());
		analysis.removeAllAdditionalKeys(correctKeys);
        analysis.setSubmissionSuitsSolution(analysis.getMissingKeys().isEmpty() && analysis.getAdditionalKeys().isEmpty());

		LOG.info( "Found " + analysis.getMissingKeys().size() + " missing keys.");
		LOG.info( "Found " + analysis.getAdditionalKeys().size() + " additional keys.");

		// Log Progress
		LOG.info( "Exit analyzing keys determination.");

		return analysis;
	}
}
