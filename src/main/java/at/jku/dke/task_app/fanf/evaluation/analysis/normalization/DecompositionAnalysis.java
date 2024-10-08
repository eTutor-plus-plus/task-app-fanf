package at.jku.dke.task_app.fanf.evaluation.analysis.normalization;

import at.jku.dke.task_app.fanf.evaluation.analysis.NFAnalysis;
import java.text.Collator;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class DecompositionAnalysis extends NFAnalysis {

	private final Set<String> missingAttributes;

	public DecompositionAnalysis() {
		super();
		this.missingAttributes = new TreeSet<>(Collator.getInstance());
	}

	public void addMissingAttribute(String attribute){
		this.missingAttributes.add(attribute);
	}

	public void setMissingAttributes(Collection<String> attributes){
		this.missingAttributes.clear();
		this.missingAttributes.addAll(attributes);
	}

	public Set<String> getMissingAttributes() {
		TreeSet<String> ret = new TreeSet<>(Collator.getInstance());
		ret.addAll(missingAttributes);
		return ret;
	}

	public void removeAllMissingAttributes(Collection<String> attributes){
		this.missingAttributes.removeAll(attributes);
	}
}
