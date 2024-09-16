package at.jku.dke.task_app.fanf.evaluation.analysis.minimalcover;



import at.jku.dke.task_app.fanf.evaluation.analysis.NFAnalysis;
import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;

import java.util.HashSet;
import java.util.Set;

public class RedundantDependenciesAnalysis extends NFAnalysis {

	private final Set<FunctionalDependency> redundantDependencies;

	public RedundantDependenciesAnalysis() {
		super();
		this.redundantDependencies = new HashSet<>();
	}

	public void addRedundantDependency(FunctionalDependency dependency){
		this.redundantDependencies.add(dependency);
	}

	public Set<FunctionalDependency> getRedundantDependencies() {
		return new HashSet<>(this.redundantDependencies);
	}

}
