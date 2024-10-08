package at.jku.dke.task_app.fanf.evaluation.algorithms;

import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;

import java.util.Collection;

public class Member {

	private Member() {
		// This class is not meant to be instantiated. (Gerald Wimmer, 2023-12-02)
	}

	/**
	 * Tests whether the attribute closure for the left-hand side of the passed functional dependency contains all
	 * attributes on its right-hand side, based on another set of functional dependencies.
	 * @param dependency The functional dependency to be tested
	 * @param dependencies The functional dependencies that serve as a base for determining the attribute closure
	 * @return Whether the attribute closure for the left-hand side of the passed functional dependency contains all
	 * attributes on its right-hand side
	 */
	public static boolean execute(FunctionalDependency dependency, Collection<FunctionalDependency> dependencies){
		return Closure.execute(dependency.getLhsAttributes(), dependencies).containsAll(dependency.getRhsAttributes());
	}
}
