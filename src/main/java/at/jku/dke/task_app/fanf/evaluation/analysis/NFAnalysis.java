package at.jku.dke.task_app.fanf.evaluation.analysis;


import java.io.Serializable;
import java.util.StringJoiner;

public class NFAnalysis {
    private int exerciseId;

    private String syntaxError;

    /**
     * The student´s submission
     */
    private Serializable submission;
    /**
     * Boolean determining whether the submission suits the solution
     */
    private boolean submissionSuitsSolution;
    public void setSubmission(Serializable submission){
        this.submission = submission;
    }

    /**
     * Returns the submission
     * @return the submission
     */
    public Serializable getSubmission(){
        return this.submission;
    }

    /**
     * Returns submissionSuitsSolution
     * @return the boolean value
     */
    public boolean submissionSuitsSolution(){
        return this.submissionSuitsSolution;
    }

    /**
     * Sets submissionSuitsSolution
     * @param submissionSuitsSolution the boolean value
     */
    public void setSubmissionSuitsSolution(boolean submissionSuitsSolution){
        this.submissionSuitsSolution = submissionSuitsSolution;
    }

    public NFAnalysis() {
        super();
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getSyntaxError() {
        return syntaxError;
    }

    public void setSyntaxError(String syntaxError) {
        this.syntaxError = syntaxError;
    }

    public void setSyntaxError(String... syntaxErrors) {
        StringJoiner sj = new StringJoiner(";");

        for (String s : syntaxErrors) {
            sj.add(s);
        }

        this.syntaxError = sj.toString();
    }

    public void appendSyntaxError(String syntaxError) {
        if (this.syntaxError == null) {
            setSyntaxError(syntaxError);
        } else {
            this.syntaxError += ";" + syntaxError;
        }
    }

}
