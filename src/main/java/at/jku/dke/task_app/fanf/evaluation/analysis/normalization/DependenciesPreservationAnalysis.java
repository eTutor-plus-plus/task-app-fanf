package at.jku.dke.task_app.fanf.evaluation.analysis.normalization;

import at.jku.dke.task_app.fanf.evaluation.analysis.NFAnalysis;
import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;

import java.util.HashSet;
import java.util.Set;

public class DependenciesPreservationAnalysis extends NFAnalysis {

	private final HashSet<FunctionalDependency> lostDependencies;

	public DependenciesPreservationAnalysis() {
		super();
		this.lostDependencies = new HashSet<>();
	}

	public void addLostFunctionalDependency(FunctionalDependency lostDependency){
		this.lostDependencies.add(lostDependency);
	}

	public int lostFunctionalDependenciesCount(){
		return this.lostDependencies.size();
	}

	public Set<FunctionalDependency> getLostFunctionalDependencies(){
		return new HashSet<>(this.lostDependencies);
	}
}
