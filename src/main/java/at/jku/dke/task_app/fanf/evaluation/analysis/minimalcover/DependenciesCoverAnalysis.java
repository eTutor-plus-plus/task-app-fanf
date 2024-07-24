package at.jku.dke.task_app.fanf.evaluation.analysis.minimalcover;

import java.util.LinkedList;
import java.util.List;

import at.jku.dke.task_app.fanf.evaluation.analysis.NFAnalysis;
import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;

public class DependenciesCoverAnalysis extends NFAnalysis {

	final List<FunctionalDependency> missingDependencies;
	final List<FunctionalDependency> additionalDependencies;

	public DependenciesCoverAnalysis() {
		super();
		this.missingDependencies = new LinkedList<>();
		this.additionalDependencies = new LinkedList<>();
	}

	public List<FunctionalDependency> getMissingDependencies(){
		return new LinkedList<>(this.missingDependencies);
	}

	public List<FunctionalDependency> getAdditionalDependencies(){
		return new LinkedList<>(this.additionalDependencies);
	}

	public void addMissingDependency(FunctionalDependency dependency){
		this.missingDependencies.add(dependency);
	}

	public void addAdditionalDependency(FunctionalDependency dependency){
		this.additionalDependencies.add(dependency);
	}

}
