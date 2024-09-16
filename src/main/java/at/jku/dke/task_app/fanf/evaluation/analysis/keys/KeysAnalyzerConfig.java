package at.jku.dke.task_app.fanf.evaluation.analysis.keys;



import at.jku.dke.task_app.fanf.evaluation.model.Key;
import at.jku.dke.task_app.fanf.evaluation.model.KeyComparator;

import java.util.Set;
import java.util.TreeSet;

public class KeysAnalyzerConfig {

	private Set<Key> correctMinimalKeys;

	public KeysAnalyzerConfig() {
		this.correctMinimalKeys = new TreeSet<>(new KeyComparator());
	}

	public void setCorrectMinimalKeys(Set<Key> correctMinimalKeys){
		this.correctMinimalKeys = correctMinimalKeys;
	}

	public Set<Key> getCorrectMinimalKeys() {
		TreeSet<Key> ret = new TreeSet<>(new KeyComparator());
		ret.addAll(correctMinimalKeys);
		return ret;
	}
}
