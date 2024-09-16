package at.jku.dke.task_app.fanf.evaluation.analysis.normalformdetermination;


import at.jku.dke.task_app.fanf.evaluation.analysis.NFAnalysis;
import at.jku.dke.task_app.fanf.evaluation.analysis.normalform.NormalformAnalysis;
import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;
import at.jku.dke.task_app.fanf.evaluation.model.NormalformLevel;

import java.util.LinkedList;
import java.util.List;

public class NormalformDeterminationAnalysis extends NormalformAnalysis  {

	private NormalformLevel submittedLevel;
	private boolean overallLevelIsCorrect;
	private final List<Object[]> wrongLeveledDependencies;

	public NormalformDeterminationAnalysis() {
		super();

		this.submittedLevel = NormalformLevel.FIRST;
		this.wrongLeveledDependencies = new LinkedList<>();
	}

	public void addWrongLeveledDependency(FunctionalDependency dependency, NormalformLevel correctLevel, NormalformLevel foundLevel){
		Object[] entry = new Object[3];
		entry[0] = dependency;
		entry[1] = correctLevel;
		entry[2] = foundLevel;

		this.wrongLeveledDependencies.add(entry);
	}

	public List<Object[]> getWrongLeveledDependencies(){
		return new LinkedList<>(this.wrongLeveledDependencies);
	}

	public NormalformLevel getSubmittedLevel() {
		return this.submittedLevel;
	}

	public void setSubmittedLevel(NormalformLevel level) {
		this.submittedLevel = level;
	}

	public boolean getOverallLevelIsCorrect() {
		return overallLevelIsCorrect;
	}

	public void setOverallLevelIsCorrect(boolean overallLevelIsCorrect) {
		this.overallLevelIsCorrect = overallLevelIsCorrect;
	}
}
