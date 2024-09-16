package at.jku.dke.task_app.fanf.evaluation.analysis.minimalcover;



import at.jku.dke.task_app.fanf.evaluation.analysis.NFAnalysis;
import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;

import java.util.HashSet;
import java.util.Set;

public class CanonicalRepresentationAnalysis extends NFAnalysis {

	private final Set<FunctionalDependency> notCanonicalDependencies;

	public CanonicalRepresentationAnalysis() {
		super();
		this.notCanonicalDependencies = new HashSet<>();
	}

	public void addNotCanonicalDependency(FunctionalDependency dependency){
		this.notCanonicalDependencies.add(dependency);
	}

	public Set<FunctionalDependency> getNotCanonicalDependencies() {
		return new HashSet<>(this.notCanonicalDependencies);
	}

}
