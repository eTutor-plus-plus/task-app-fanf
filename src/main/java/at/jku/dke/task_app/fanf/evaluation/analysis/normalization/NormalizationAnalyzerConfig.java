package at.jku.dke.task_app.fanf.evaluation.analysis.normalization;

import at.jku.dke.task_app.fanf.evaluation.model.IdentifiedRelation;
import at.jku.dke.task_app.fanf.evaluation.model.NormalformLevel;
import at.jku.dke.task_app.fanf.evaluation.model.IdentifiedRelationComparator;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class NormalizationAnalyzerConfig implements Serializable {
	private int maxLostDependencies;
	private final Set<IdentifiedRelation> normalizedRelations;
	private IdentifiedRelation baseRelation;
	private NormalformLevel desiredNormalformLevel;

	public NormalizationAnalyzerConfig() {
		this.baseRelation = null;
		this.maxLostDependencies = 0;
		this.desiredNormalformLevel = NormalformLevel.FIRST;
		this.normalizedRelations = new TreeSet<>(new IdentifiedRelationComparator());
	}

	public void setMaxLostDependencies(int maxLostDependencies){
		this.maxLostDependencies = maxLostDependencies;
	}

	public int getMaxLostDependencies(){
		return this.maxLostDependencies;
	}

	public void setNormalizedRelations(Collection<IdentifiedRelation> normalizedRelations){
		this.normalizedRelations.clear();
		this.normalizedRelations.addAll(normalizedRelations);
	}

	public void addNormalizedRelation(IdentifiedRelation normalizedRelation){
		this.normalizedRelations.add(normalizedRelation);
	}

	public Set<IdentifiedRelation> getNormalizedRelations(){
		Set<IdentifiedRelation> ret = new TreeSet<>(new IdentifiedRelationComparator());
		ret.addAll(this.normalizedRelations);
		return ret;
	}

	public void setBaseRelation(IdentifiedRelation baseRelation){
		this.baseRelation = baseRelation;
	}

	public IdentifiedRelation getBaseRelation(){
		return this.baseRelation;
	}

	public void setDesiredNormalformLevel(NormalformLevel level){
		this.desiredNormalformLevel = level;
	}

	public NormalformLevel getDesiredNormalformLevel(){
		return this.desiredNormalformLevel;
	}

}
