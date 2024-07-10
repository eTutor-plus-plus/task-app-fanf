package at.jku.dke.task_app.fanf.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link at.jku.dke.task_app.fanf.data.entities.FanfTask}
 */
public class FanfTaskDto implements Serializable {
    private String baseRelationAttributes;
    private String baseRelationDependencies;
    private String baseRelationName;
    private int subtype;
    private double keysDeterminationPenaltyPerMissingKey;
    private double keysDeterminationPenaltyPerIncorrectKey;
    private String attributeClosureBaseAttributes;
    private double attributeClosurePenaltyPerMissingAttribute;
    private double attributeClosurePenaltyPerIncorrectAttribute;
    private double minimalCoverPenaltyPerNonCanonicalDependency;
    private double minimalCoverPenaltyPerTrivialDependency;
    private double minimalCoverPenaltyPerExtraneousAttribute;
    private double minimalCoverPenaltyPerRedundantDependency;
    private double minimalCoverPenaltyPerMissingDependencyVsSolution;
    private double minimalCoverPenaltyPerIncorrectDependencyVsSolution;
    private double normalFormDeterminationPenaltyForIncorrectOverallNormalform;
    private double normalFormDeterminationPenaltyPerIncorrectDependencyNormalform;
    private String normalizationTargetLevel;
    private double normalizationMaxLostDependencies;
    private double normalizationPenaltyPerLostAttribute;
    private double normalizationPenaltyForLossyDecomposition;
    private double normalizationPenaltyPerNonCanonicalDependency;
    private double normalizationPenaltyPerTrivialDependency;
    private double normalizationPenaltyPerExtraneousAttributeInDependencies;
    private double normalizationPenaltyPerRedundantDependency;
    private double normalizationPenaltyPerExcessiveLostDependency;
    private double normalizationPenaltyPerMissingNewDependency;
    private double normalizationPenaltyPerIncorrectNewDependency;
    private double normalizationPenaltyPerMissingKey;
    private double normalizationPenaltyPerIncorrectKey;
    private double normalizationPenaltyPerIncorrectNFRelation;

    public FanfTaskDto() {
    }

    public FanfTaskDto(String baseRelationAttributes, String baseRelationDependencies, String baseRelationName, int subtype, double keysDeterminationPenaltyPerMissingKey, double keysDeterminationPenaltyPerIncorrectKey, String attributeClosureBaseAttributes, double attributeClosurePenaltyPerMissingAttribute, double attributeClosurePenaltyPerIncorrectAttribute, double minimalCoverPenaltyPerNonCanonicalDependency, double minimalCoverPenaltyPerTrivialDependency, double minimalCoverPenaltyPerExtraneousAttribute, double minimalCoverPenaltyPerRedundantDependency, double minimalCoverPenaltyPerMissingDependencyVsSolution, double minimalCoverPenaltyPerIncorrectDependencyVsSolution, double normalFormDeterminationPenaltyForIncorrectOverallNormalform, double normalFormDeterminationPenaltyPerIncorrectDependencyNormalform, String normalizationTargetLevel, double normalizationMaxLostDependencies, double normalizationPenaltyPerLostAttribute, double normalizationPenaltyForLossyDecomposition, double normalizationPenaltyPerNonCanonicalDependency, double normalizationPenaltyPerTrivialDependency, double normalizationPenaltyPerExtraneousAttributeInDependencies, double normalizationPenaltyPerRedundantDependency, double normalizationPenaltyPerExcessiveLostDependency, double normalizationPenaltyPerMissingNewDependency, double normalizationPenaltyPerIncorrectNewDependency, double normalizationPenaltyPerMissingKey, double normalizationPenaltyPerIncorrectKey, double normalizationPenaltyPerIncorrectNFRelation) {
        this.baseRelationAttributes = baseRelationAttributes;
        this.baseRelationDependencies = baseRelationDependencies;
        this.baseRelationName = baseRelationName;
        this.subtype = subtype;
        this.keysDeterminationPenaltyPerMissingKey = keysDeterminationPenaltyPerMissingKey;
        this.keysDeterminationPenaltyPerIncorrectKey = keysDeterminationPenaltyPerIncorrectKey;
        this.attributeClosureBaseAttributes = attributeClosureBaseAttributes;
        this.attributeClosurePenaltyPerMissingAttribute = attributeClosurePenaltyPerMissingAttribute;
        this.attributeClosurePenaltyPerIncorrectAttribute = attributeClosurePenaltyPerIncorrectAttribute;
        this.minimalCoverPenaltyPerNonCanonicalDependency = minimalCoverPenaltyPerNonCanonicalDependency;
        this.minimalCoverPenaltyPerTrivialDependency = minimalCoverPenaltyPerTrivialDependency;
        this.minimalCoverPenaltyPerExtraneousAttribute = minimalCoverPenaltyPerExtraneousAttribute;
        this.minimalCoverPenaltyPerRedundantDependency = minimalCoverPenaltyPerRedundantDependency;
        this.minimalCoverPenaltyPerMissingDependencyVsSolution = minimalCoverPenaltyPerMissingDependencyVsSolution;
        this.minimalCoverPenaltyPerIncorrectDependencyVsSolution = minimalCoverPenaltyPerIncorrectDependencyVsSolution;
        this.normalFormDeterminationPenaltyForIncorrectOverallNormalform = normalFormDeterminationPenaltyForIncorrectOverallNormalform;
        this.normalFormDeterminationPenaltyPerIncorrectDependencyNormalform = normalFormDeterminationPenaltyPerIncorrectDependencyNormalform;
        this.normalizationTargetLevel = normalizationTargetLevel;
        this.normalizationMaxLostDependencies = normalizationMaxLostDependencies;
        this.normalizationPenaltyPerLostAttribute = normalizationPenaltyPerLostAttribute;
        this.normalizationPenaltyForLossyDecomposition = normalizationPenaltyForLossyDecomposition;
        this.normalizationPenaltyPerNonCanonicalDependency = normalizationPenaltyPerNonCanonicalDependency;
        this.normalizationPenaltyPerTrivialDependency = normalizationPenaltyPerTrivialDependency;
        this.normalizationPenaltyPerExtraneousAttributeInDependencies = normalizationPenaltyPerExtraneousAttributeInDependencies;
        this.normalizationPenaltyPerRedundantDependency = normalizationPenaltyPerRedundantDependency;
        this.normalizationPenaltyPerExcessiveLostDependency = normalizationPenaltyPerExcessiveLostDependency;
        this.normalizationPenaltyPerMissingNewDependency = normalizationPenaltyPerMissingNewDependency;
        this.normalizationPenaltyPerIncorrectNewDependency = normalizationPenaltyPerIncorrectNewDependency;
        this.normalizationPenaltyPerMissingKey = normalizationPenaltyPerMissingKey;
        this.normalizationPenaltyPerIncorrectKey = normalizationPenaltyPerIncorrectKey;
        this.normalizationPenaltyPerIncorrectNFRelation = normalizationPenaltyPerIncorrectNFRelation;
    }

    public String getBaseRelationAttributes() {
        return baseRelationAttributes;
    }

    public void setBaseRelationAttributes(String baseRelationAttributes) {
        this.baseRelationAttributes = baseRelationAttributes;
    }

    public String getBaseRelationDependencies() {
        return baseRelationDependencies;
    }

    public void setBaseRelationDependencies(String baseRelationDependencies) {
        this.baseRelationDependencies = baseRelationDependencies;
    }

    public String getBaseRelationName() {
        return baseRelationName;
    }

    public void setBaseRelationName(String baseRelationName) {
        this.baseRelationName = baseRelationName;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public double getKeysDeterminationPenaltyPerMissingKey() {
        return keysDeterminationPenaltyPerMissingKey;
    }

    public void setKeysDeterminationPenaltyPerMissingKey(double keysDeterminationPenaltyPerMissingKey) {
        this.keysDeterminationPenaltyPerMissingKey = keysDeterminationPenaltyPerMissingKey;
    }

    public double getKeysDeterminationPenaltyPerIncorrectKey() {
        return keysDeterminationPenaltyPerIncorrectKey;
    }

    public void setKeysDeterminationPenaltyPerIncorrectKey(double keysDeterminationPenaltyPerIncorrectKey) {
        this.keysDeterminationPenaltyPerIncorrectKey = keysDeterminationPenaltyPerIncorrectKey;
    }

    public String getAttributeClosureBaseAttributes() {
        return attributeClosureBaseAttributes;
    }

    public void setAttributeClosureBaseAttributes(String attributeClosureBaseAttributes) {
        this.attributeClosureBaseAttributes = attributeClosureBaseAttributes;
    }

    public double getAttributeClosurePenaltyPerMissingAttribute() {
        return attributeClosurePenaltyPerMissingAttribute;
    }

    public void setAttributeClosurePenaltyPerMissingAttribute(double attributeClosurePenaltyPerMissingAttribute) {
        this.attributeClosurePenaltyPerMissingAttribute = attributeClosurePenaltyPerMissingAttribute;
    }

    public double getAttributeClosurePenaltyPerIncorrectAttribute() {
        return attributeClosurePenaltyPerIncorrectAttribute;
    }

    public void setAttributeClosurePenaltyPerIncorrectAttribute(double attributeClosurePenaltyPerIncorrectAttribute) {
        this.attributeClosurePenaltyPerIncorrectAttribute = attributeClosurePenaltyPerIncorrectAttribute;
    }

    public double getMinimalCoverPenaltyPerNonCanonicalDependency() {
        return minimalCoverPenaltyPerNonCanonicalDependency;
    }

    public void setMinimalCoverPenaltyPerNonCanonicalDependency(double minimalCoverPenaltyPerNonCanonicalDependency) {
        this.minimalCoverPenaltyPerNonCanonicalDependency = minimalCoverPenaltyPerNonCanonicalDependency;
    }

    public double getMinimalCoverPenaltyPerTrivialDependency() {
        return minimalCoverPenaltyPerTrivialDependency;
    }

    public void setMinimalCoverPenaltyPerTrivialDependency(double minimalCoverPenaltyPerTrivialDependency) {
        this.minimalCoverPenaltyPerTrivialDependency = minimalCoverPenaltyPerTrivialDependency;
    }

    public double getMinimalCoverPenaltyPerExtraneousAttribute() {
        return minimalCoverPenaltyPerExtraneousAttribute;
    }

    public void setMinimalCoverPenaltyPerExtraneousAttribute(double minimalCoverPenaltyPerExtraneousAttribute) {
        this.minimalCoverPenaltyPerExtraneousAttribute = minimalCoverPenaltyPerExtraneousAttribute;
    }

    public double getMinimalCoverPenaltyPerRedundantDependency() {
        return minimalCoverPenaltyPerRedundantDependency;
    }

    public void setMinimalCoverPenaltyPerRedundantDependency(double minimalCoverPenaltyPerRedundantDependency) {
        this.minimalCoverPenaltyPerRedundantDependency = minimalCoverPenaltyPerRedundantDependency;
    }

    public double getMinimalCoverPenaltyPerMissingDependencyVsSolution() {
        return minimalCoverPenaltyPerMissingDependencyVsSolution;
    }

    public void setMinimalCoverPenaltyPerMissingDependencyVsSolution(double minimalCoverPenaltyPerMissingDependencyVsSolution) {
        this.minimalCoverPenaltyPerMissingDependencyVsSolution = minimalCoverPenaltyPerMissingDependencyVsSolution;
    }

    public double getMinimalCoverPenaltyPerIncorrectDependencyVsSolution() {
        return minimalCoverPenaltyPerIncorrectDependencyVsSolution;
    }

    public void setMinimalCoverPenaltyPerIncorrectDependencyVsSolution(double minimalCoverPenaltyPerIncorrectDependencyVsSolution) {
        this.minimalCoverPenaltyPerIncorrectDependencyVsSolution = minimalCoverPenaltyPerIncorrectDependencyVsSolution;
    }

    public double getNormalFormDeterminationPenaltyForIncorrectOverallNormalform() {
        return normalFormDeterminationPenaltyForIncorrectOverallNormalform;
    }

    public void setNormalFormDeterminationPenaltyForIncorrectOverallNormalform(double normalFormDeterminationPenaltyForIncorrectOverallNormalform) {
        this.normalFormDeterminationPenaltyForIncorrectOverallNormalform = normalFormDeterminationPenaltyForIncorrectOverallNormalform;
    }

    public double getNormalFormDeterminationPenaltyPerIncorrectDependencyNormalform() {
        return normalFormDeterminationPenaltyPerIncorrectDependencyNormalform;
    }

    public void setNormalFormDeterminationPenaltyPerIncorrectDependencyNormalform(double normalFormDeterminationPenaltyPerIncorrectDependencyNormalform) {
        this.normalFormDeterminationPenaltyPerIncorrectDependencyNormalform = normalFormDeterminationPenaltyPerIncorrectDependencyNormalform;
    }

    public String getNormalizationTargetLevel() {
        return normalizationTargetLevel;
    }

    public void setNormalizationTargetLevel(String normalizationTargetLevel) {
        this.normalizationTargetLevel = normalizationTargetLevel;
    }

    public double getNormalizationMaxLostDependencies() {
        return normalizationMaxLostDependencies;
    }

    public void setNormalizationMaxLostDependencies(double normalizationMaxLostDependencies) {
        this.normalizationMaxLostDependencies = normalizationMaxLostDependencies;
    }

    public double getNormalizationPenaltyPerLostAttribute() {
        return normalizationPenaltyPerLostAttribute;
    }

    public void setNormalizationPenaltyPerLostAttribute(double normalizationPenaltyPerLostAttribute) {
        this.normalizationPenaltyPerLostAttribute = normalizationPenaltyPerLostAttribute;
    }

    public double getNormalizationPenaltyForLossyDecomposition() {
        return normalizationPenaltyForLossyDecomposition;
    }

    public void setNormalizationPenaltyForLossyDecomposition(double normalizationPenaltyForLossyDecomposition) {
        this.normalizationPenaltyForLossyDecomposition = normalizationPenaltyForLossyDecomposition;
    }

    public double getNormalizationPenaltyPerNonCanonicalDependency() {
        return normalizationPenaltyPerNonCanonicalDependency;
    }

    public void setNormalizationPenaltyPerNonCanonicalDependency(double normalizationPenaltyPerNonCanonicalDependency) {
        this.normalizationPenaltyPerNonCanonicalDependency = normalizationPenaltyPerNonCanonicalDependency;
    }

    public double getNormalizationPenaltyPerTrivialDependency() {
        return normalizationPenaltyPerTrivialDependency;
    }

    public void setNormalizationPenaltyPerTrivialDependency(double normalizationPenaltyPerTrivialDependency) {
        this.normalizationPenaltyPerTrivialDependency = normalizationPenaltyPerTrivialDependency;
    }

    public double getNormalizationPenaltyPerExtraneousAttributeInDependencies() {
        return normalizationPenaltyPerExtraneousAttributeInDependencies;
    }

    public void setNormalizationPenaltyPerExtraneousAttributeInDependencies(double normalizationPenaltyPerExtraneousAttributeInDependencies) {
        this.normalizationPenaltyPerExtraneousAttributeInDependencies = normalizationPenaltyPerExtraneousAttributeInDependencies;
    }

    public double getNormalizationPenaltyPerRedundantDependency() {
        return normalizationPenaltyPerRedundantDependency;
    }

    public void setNormalizationPenaltyPerRedundantDependency(double normalizationPenaltyPerRedundantDependency) {
        this.normalizationPenaltyPerRedundantDependency = normalizationPenaltyPerRedundantDependency;
    }

    public double getNormalizationPenaltyPerExcessiveLostDependency() {
        return normalizationPenaltyPerExcessiveLostDependency;
    }

    public void setNormalizationPenaltyPerExcessiveLostDependency(double normalizationPenaltyPerExcessiveLostDependency) {
        this.normalizationPenaltyPerExcessiveLostDependency = normalizationPenaltyPerExcessiveLostDependency;
    }

    public double getNormalizationPenaltyPerMissingNewDependency() {
        return normalizationPenaltyPerMissingNewDependency;
    }

    public void setNormalizationPenaltyPerMissingNewDependency(double normalizationPenaltyPerMissingNewDependency) {
        this.normalizationPenaltyPerMissingNewDependency = normalizationPenaltyPerMissingNewDependency;
    }

    public double getNormalizationPenaltyPerIncorrectNewDependency() {
        return normalizationPenaltyPerIncorrectNewDependency;
    }

    public void setNormalizationPenaltyPerIncorrectNewDependency(double normalizationPenaltyPerIncorrectNewDependency) {
        this.normalizationPenaltyPerIncorrectNewDependency = normalizationPenaltyPerIncorrectNewDependency;
    }

    public double getNormalizationPenaltyPerMissingKey() {
        return normalizationPenaltyPerMissingKey;
    }

    public void setNormalizationPenaltyPerMissingKey(double normalizationPenaltyPerMissingKey) {
        this.normalizationPenaltyPerMissingKey = normalizationPenaltyPerMissingKey;
    }

    public double getNormalizationPenaltyPerIncorrectKey() {
        return normalizationPenaltyPerIncorrectKey;
    }

    public void setNormalizationPenaltyPerIncorrectKey(double normalizationPenaltyPerIncorrectKey) {
        this.normalizationPenaltyPerIncorrectKey = normalizationPenaltyPerIncorrectKey;
    }

    public double getNormalizationPenaltyPerIncorrectNFRelation() {
        return normalizationPenaltyPerIncorrectNFRelation;
    }

    public void setNormalizationPenaltyPerIncorrectNFRelation(double normalizationPenaltyPerIncorrectNFRelation) {
        this.normalizationPenaltyPerIncorrectNFRelation = normalizationPenaltyPerIncorrectNFRelation;
    }

}
