package at.jku.dke.task_app.fanf.evaluation.analysis.normalform;


import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;

public abstract class NormalformViolation {

	private FunctionalDependency dependency;

	public void setFunctionalDependency(FunctionalDependency dependency){
		this.dependency = dependency;
	}

	public FunctionalDependency getFunctionalDependency(){
		return this.dependency;
	}

}
