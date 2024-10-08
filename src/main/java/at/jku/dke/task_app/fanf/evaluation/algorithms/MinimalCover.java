package at.jku.dke.task_app.fanf.evaluation.algorithms;

import at.jku.dke.task_app.fanf.evaluation.model.FunctionalDependency;
import java.util.*;

public class MinimalCover {

	private MinimalCover() {
		// This class is not meant to be instantiated. (Gerald Wimmer, 2023-12-02)
	}

	public static Set<FunctionalDependency> execute(Collection<FunctionalDependency> dependencies){
		//DERIVE CANONICAL FORM OF FUNCTIONAL DEPENDENCIES
		Set<FunctionalDependency> minimalCover = unfold(dependencies);

		//DELETING EXTRANEOUS ATTRIBUTES
		Map<FunctionalDependency, List<String>> extraneousAttributes = calculateExtraneousAttributes(minimalCover);

		for (Map.Entry<FunctionalDependency, List<String>> curEntry : extraneousAttributes.entrySet()){
			for (FunctionalDependency currFD : minimalCover){
				if (currFD.equals(curEntry.getKey())){
					currFD.removeLhsAttributes(curEntry.getValue());
				}
			}
		}

		//DELETING DUPLICATES
		minimalCover = new HashSet<>(minimalCover);

		//DELETING REDUNDANT FUNCTIONAL DEPENDENCIES
		minimalCover.removeAll(calculateRedundantFunctionalDependencies(minimalCover));

		return minimalCover;
	}

	public static Set<FunctionalDependency> calculateRedundantFunctionalDependencies(Collection<FunctionalDependency> dependencies){
		List<FunctionalDependency> tempDependencies = new LinkedList<>();
		HashSet<FunctionalDependency> redundantDependencies = new HashSet<>();

        for (FunctionalDependency currDependency : dependencies) {
            tempDependencies.clear();
            tempDependencies.addAll(dependencies);
            tempDependencies.remove(currDependency);
            tempDependencies.removeAll(redundantDependencies);

            if (Cover.execute(tempDependencies, dependencies)) {
                redundantDependencies.add(currDependency);
            }
        }

		return redundantDependencies;
	}


	public static Map<FunctionalDependency, List<String>> calculateExtraneousAttributes (Collection<FunctionalDependency> dependencies) {
		List<FunctionalDependency> tempFDs = new LinkedList<>();
		FunctionalDependency tempFD = new FunctionalDependency();
		Map<FunctionalDependency, List<String>> extraneousAttributes = new HashMap<>();

        for (FunctionalDependency currFD : dependencies) {
			for (String currAttribute : currFD.getLhsAttributes()) {
                tempFD.setLhsAttributes(currFD.getLhsAttributes());
                tempFD.setRhsAttributes(currFD.getRhsAttributes());
                tempFD.removeLhsAttribute(currAttribute);

                if (extraneousAttributes.containsKey(currFD)) {
                    tempFD.removeLhsAttributes(extraneousAttributes.get(currFD));
                }

                tempFDs.clear();
                tempFDs.addAll(dependencies);
                tempFDs.remove(currFD);
                tempFDs.add(tempFD);

                if (Cover.execute(dependencies, tempFDs)) {
                    if (!extraneousAttributes.containsKey(currFD)) {
                        extraneousAttributes.put(currFD, new LinkedList<>());
                    }
                    extraneousAttributes.get(currFD).add(currAttribute);
                }
            }
        }

		return extraneousAttributes;
	}

	public static Set<FunctionalDependency> fold(Collection<FunctionalDependency> dependencies) {
		Set<FunctionalDependency> foldedDependencies = new HashSet<>();
		Set<FunctionalDependency> processedDependencies = new HashSet<>();

		FunctionalDependency[] dependencyArray = dependencies.toArray(new FunctionalDependency[0]);
		for (int i = 0; i < dependencyArray.length; i++) {
			FunctionalDependency currDependency = dependencyArray[i];

			if (!processedDependencies.contains(currDependency)) {

				for (int j = i; j < dependencyArray.length; j++) {
					FunctionalDependency compDependency = dependencyArray[j];

					if (compareLHS(compDependency, currDependency)) {
						currDependency.addAllRhsAttributes(compDependency.getRhsAttributes());
						processedDependencies.add(compDependency);
					}
				}
				foldedDependencies.add(currDependency);
				processedDependencies.add(currDependency);
			}
		}

		return foldedDependencies;
	}

	/**
	 * Checks whether the supplied FunctionalDependencies have identical left-hand-sides. (Gerald Wimmer, 2023-12-02)
	 * @param fd1 The second FunctionalDependency
	 * @param fd2 The first FunctionalDependency
	 * @return true if the left-hand-sides of both FunctionalDependencies are equal (i.e., contain the same attributes)
	 */
	private static boolean compareLHS(FunctionalDependency fd1, FunctionalDependency fd2) {
		return fd2.getLhsAttributes().containsAll(fd1.getLhsAttributes()) && fd1.getLhsAttributes().containsAll(fd2.getLhsAttributes());
	}

	/**
	 * This method splits each of the supplied <code>FunctionalDependency</code> objects up into multiple
	 * <code>FunctionalDependency</code> objects, each with the same left-hand side and a single attribute on the
	 * right-hand side.
	 * @param dependencies The <code>FunctionalDependency</code> objects to be unfolded
	 * @return A <code>Set</code> of unfolded <code>FunctionalDependency</code> objects
	 */
	public static Set<FunctionalDependency> unfold(Collection<FunctionalDependency> dependencies) {
		HashSet<FunctionalDependency> unfoldedDependencies = new HashSet<>();

        for (FunctionalDependency dependency : dependencies) {
            unfoldedDependencies.addAll(dependency.unfold());
        }

		return unfoldedDependencies;
	}
}
