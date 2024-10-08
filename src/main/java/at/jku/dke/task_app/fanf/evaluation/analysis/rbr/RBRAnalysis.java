package at.jku.dke.task_app.fanf.evaluation.analysis.rbr;

import at.jku.dke.task_app.fanf.evaluation.analysis.NFAnalysis;
import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;

import java.util.LinkedList;
import java.util.List;

public class RBRAnalysis extends NFAnalysis {

	private final List<FunctionalDependency> missingFunctionalDependencies;
	private final List<FunctionalDependency> additionalFunctionalDependencies;

	public RBRAnalysis() {
		super();

		this.missingFunctionalDependencies = new LinkedList<>();
		this.additionalFunctionalDependencies = new LinkedList<>();
	}

	public List<FunctionalDependency> getMissingFunctionalDependencies() {
		return new LinkedList<>(this.missingFunctionalDependencies);
	}

	public void addMissingFunctionalDependency(FunctionalDependency lostDependency) {
		this.missingFunctionalDependencies.add(lostDependency);
	}

	public List<FunctionalDependency> getAdditionalFunctionalDependencies() {
		return new LinkedList<>(this.additionalFunctionalDependencies);
	}

	public void addAdditionalFunctionalDependency(FunctionalDependency additionalDependency) {
		this.additionalFunctionalDependencies.add(additionalDependency);
	}
}
