package at.jku.dke.task_app.fanf.evaluation.analysis.normalization;



import at.jku.dke.task_app.fanf.evaluation.algorithms.Closure;
import at.jku.dke.task_app.fanf.evaluation.analysis.CombinationGenerator;
import at.jku.dke.task_app.fanf.evaluation.exercises.TupleSet;
import at.jku.dke.task_app.fanf.evaluation.model.*;

import java.util.*;

public class KeysDeterminator {

	public static TupleSet determineMinimalKeys(TupleSet tuples){
		TupleSet keys = new TupleSet();
		List<int[]> keyCandidates = calculateKeyCandidates(tuples.get(0).length);

        for (int[] key : keyCandidates) {
            boolean isKey = true;
            for (int j = 0; j < tuples.size(); j++) {
                if (!holdsKey(key, tuples.get(j), tuples)) {
                    isKey = false;
                }
            }

            if (isKey) {
                keys.add(key);
            }
        }

		return keys;
	}

	public static List<int[]> calculateKeyCandidates(int valueNumber){
		int[] keyAttributePositions = new int[valueNumber];

        List<int[]> keyCandidates = new LinkedList<>();

		//CALCULATE KEY ATTRIBUTE POSITIONS
		for (int i=0; i<valueNumber; i++){
			keyAttributePositions[i] = i;
		}

		//CALCULATE KEY CANDIDATES
		for (int i = 1; i <= keyAttributePositions.length; i++) {
			CombinationGenerator generator = new CombinationGenerator(keyAttributePositions.length, i);

			while (generator.hasMore()) {
				int[] indices = generator.getNext();

				int [] keyCandidate = new int[indices.length];
				for (int j = 0; j < indices.length; j++) {
					keyCandidate[j] = keyAttributePositions[indices[j]];
				}
				keyCandidates.add(keyCandidate);
			}
		}

		return keyCandidates;
	}

	public static boolean holdsKey(int[] key, int[] tuple, TupleSet tuples){
		TupleSet temp = new TupleSet();
		temp.addAll(tuples);
		temp.remove(tuple);

		TupleSet existingKeyValueCombinations = extractKeyValueCombinations(key, temp);

		int[] keyValueCombination = extractKeyValueCombination(key, tuple);

		return !existingKeyValueCombinations.contains(keyValueCombination);
	}

	private static int[] extractKeyValueCombination(int[] key, int[] tuple){
		int[] keyValueCombination = new int[key.length];

		for (int pos=0; pos<key.length; pos++){
			keyValueCombination[pos] = tuple[key[pos]];
		}

		return keyValueCombination;
	}

	private static TupleSet extractKeyValueCombinations(int[] key, TupleSet tuples){
		TupleSet keyValueCombinations = new TupleSet();

		for (int tupleNumber=0; tupleNumber<tuples.size(); tupleNumber++){
			int[] currTuple = tuples.get(tupleNumber);
			keyValueCombinations.add(extractKeyValueCombination(key, currTuple));
		}

		return keyValueCombinations;
	}

	public static KeysContainer determineAllKeys(Relation relation){
		KeysContainer container = new KeysContainer();
		determineMinimalKeys(relation, container);
		container.setPartialKeys(determinePartialKeys(container.getMinimalKeys()));

		return container;
	}

	private static void determineMinimalKeys(Relation relation, KeysContainer container){
		TreeSet<Key> keys = new TreeSet<>(new KeyComparator());
		TreeSet<Key> superKeys = determineSuperKeys(relation);

        for (Key superKey : superKeys) {
            if (isMinimalKey(superKey, relation)) {
                keys.add(superKey);
            }
        }

		container.setMinimalKeys(keys);
		container.setSuperKeys(superKeys);
	}

	public static Set<Key> determineMinimalKeys(Relation relation){
		KeysContainer container = new KeysContainer();
		determineMinimalKeys(relation, container);

		return container.getMinimalKeys();
	}

	public static TreeSet<Key> determinePartialKeys(Collection<Key> minimalKeys) {
		TreeSet<Key> partialKeys = new TreeSet<>(new KeyComparator());

        for (Key currKey : minimalKeys) {
            for (int i = 1; i <= currKey.getAttributes().size(); i++) {
                CombinationGenerator generator = new CombinationGenerator(currKey.getAttributes().size(), i);

                while (generator.hasMore()) {
                    int[] indices = generator.getNext();
                    Key partialKey = new Key();
                    if (indices.length != currKey.getAttributes().size()) {
                        for (int index : indices) {
                            partialKey.addAttribute((String) currKey.getAttributes().toArray()[index]);
                        }
                    }
                    if (!partialKey.getAttributes().isEmpty()) {
                        partialKeys.add(partialKey);
                    }
                }
            }
        }

		return partialKeys;
	}

	/**
	 * Checks whether there are no attributes that could be removed from the supplied <code>Key</code>.
	 * @param key The <code>Key</code> to be checked.
	 * @param relation The <code>Relation</code> containing this <code>Key</code>
	 * @return Whether there are no attributes that could be removed from the supplied <code>Key</code>
	 */
	private static boolean isMinimalKey(Key key, Relation relation) {
		for (int i = 1; i <= key.getAttributes().size(); i++) {
			CombinationGenerator generator = new CombinationGenerator(key.getAttributes().size(), i);

			while (generator.hasMore()) {
				Key candidate = new Key();

				int[] indices = generator.getNext();
				if (indices.length != key.getAttributes().size()) {
                    for (int index : indices) {
                        candidate.addAttribute((String) key.getAttributes().toArray()[index]);
                    }
				}

				List<String> attributes = new LinkedList<>(candidate.getAttributes());

				Collection<String> closure = Closure.execute(attributes, relation.getFunctionalDependencies());
				if (closure.containsAll(relation.getAttributes())) {
					return false;
				}
			}
		}

		return true;
	}

	public static TreeSet<Key> determineSuperKeys(Relation relation) {
		//CALCULATING ATTRIBUTES THAT ARE PART OF EVERY KEY - RESTRICTING SET OF CANDIDATE ATTRIBUTES
        Set<String> constantAttributes = new HashSet<>(relation.getAttributes());
		for (FunctionalDependency currDependency : relation.getFunctionalDependencies()){
			constantAttributes.removeAll(currDependency.getLhsAttributes());
			constantAttributes.removeAll(currDependency.getRhsAttributes());
		}

        List<String> candidateAttributes = new LinkedList<>(relation.getAttributes());
		candidateAttributes.removeAll(constantAttributes);

		TreeSet<Key> superKeys = new TreeSet<>(new KeyComparator());
		if (!candidateAttributes.isEmpty()){
			//CALCULATING SUPER KEYS
			for (int i = 1; i <= candidateAttributes.size(); i++) {
				CombinationGenerator generator = new CombinationGenerator(candidateAttributes.size(), i);

				while (generator.hasMore()) {
					Key candidate = new Key(constantAttributes);

					int[] indices = generator.getNext();
                    for (int index : indices) {
                        candidate.addAttribute(candidateAttributes.get(index));
                    }

					List<String> attributes = new LinkedList<>(candidate.getAttributes());

					Collection<String> closure = Closure.execute(attributes, relation.getFunctionalDependencies());
					if (closure.containsAll(relation.getAttributes())) {
						Key superKey = new Key(attributes);
						superKeys.add(superKey);
					}
				}
			}
		} else {
			Key superKey = new Key(relation.getAttributes());
			superKeys.add(superKey);
		}

		return superKeys;
	}

	private static void test_HOLDS_KEY(){
		int[] key = new int[]{0,1};
		TupleSet tuples = new TupleSet();

		tuples.add(new int[]{1,2,3});
		tuples.add(new int[]{1,3,4});
		tuples.add(new int[]{3,4,1});
		tuples.add(new int[]{4,1,2});

		System.out.println("Holds key: " + holdsKey(key, new int[]{4,1,3}, tuples));
	}

	private static void test_DETERMINE_MINIMAL_KEYS(){
		TupleSet tuples = new TupleSet();

		tuples.add(new int[]{1,2,3});
		tuples.add(new int[]{1,3,4});
		tuples.add(new int[]{3,4,1});
		tuples.add(new int[]{4,1,2});

		determineMinimalKeys(tuples);
	}

	public static void main(String[] args){
		test_DETERMINE_MINIMAL_KEYS();
	}
}
