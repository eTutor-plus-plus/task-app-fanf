package at.jku.dke.task_app.fanf.evaluation.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NormalformDeterminationSpecification extends NFSpecification {

    /**
     * Points deducted if the determined overall normal form is incorrect.
     * <br><br>
     * (a in the grading schema V3)
     */
    private int penaltyForIncorrectNFOverall;

    /**
     * Points deducted for every functional dependency whose determined normal form is incorrect.
     * <br><br>
     * (b in the grading schema V3)
     */
    private int penaltyPerIncorrectNFDependency;

    public NormalformDeterminationSpecification() {
        super();
    }

    public int getPenaltyForIncorrectNFOverall() {
        return penaltyForIncorrectNFOverall;
    }

    public void setPenaltyForIncorrectNFOverall(int penaltyForIncorrectNFOverall) {
        this.penaltyForIncorrectNFOverall = penaltyForIncorrectNFOverall;
    }

    public int getPenaltyPerIncorrectNFDependency() {
        return penaltyPerIncorrectNFDependency;
    }

    public void setPenaltyPerIncorrectNFDependency(int penaltyPerIncorrectNFDependency) {
        this.penaltyPerIncorrectNFDependency = penaltyPerIncorrectNFDependency;
    }

    @Override
    public boolean semanticallyEquals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof NormalformDeterminationSpecification)) {
            return false;
        }

        NormalformDeterminationSpecification spec = (NormalformDeterminationSpecification) obj;

        if(spec.getPenaltyForIncorrectNFOverall() != this.penaltyForIncorrectNFOverall) {
            return false;
        }

        if(spec.getPenaltyPerIncorrectNFDependency() != this.penaltyPerIncorrectNFDependency) {
            return false;
        }

        return super.semanticallyEquals(spec);
    }
}
