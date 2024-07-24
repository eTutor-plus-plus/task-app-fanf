package at.jku.dke.task_app.fanf.description_generation;

import at.jku.dke.etutor.task_app.dto.TaskModificationResponseDto;
import at.jku.dke.task_app.fanf.evaluation.model.*;

import java.util.Collection;
import java.util.Locale;
import java.util.StringJoiner;

/**
 * The type Description generation.
 */
public class DescriptionGeneration {
    /**
     * The constant LINE_SEP.
     */
    public static final String LINE_SEP = System.getProperty("line.separator");
    /**
     * The constant OFFSET.
     */
    public static final int OFFSET = 15;

    /**
     * Print assignment for keys determination task modification response dto.
     *
     * @param spec   The spec.
     * @param indent The indent.
     * @return The task modification response dto.
     */
    public static TaskModificationResponseDto printAssignmentForKeysDetermination(IdentifiedRelation spec, int indent) {
        String offset = getOffset(indent);

        StringBuilder outDE = new StringBuilder();
        outDE.append(offset).append("<p>").append(LINE_SEP);

        outDE.append(offset).append("	Berechnen Sie alle Schlüssel der Relation <strong>").append(spec.getName()).append("</strong> auf ").append(LINE_SEP);
        outDE.append(offset).append("	Basis der Funktionalen Abhängigkeiten gekennzeichnet durch das Präfix <strong>-></strong>. ").append(LINE_SEP);

        outDE.append(offset).append("</p>").append(LINE_SEP);
        outDE.append(offset).append("<table rules=\"none\" frame=\"void\">").append(LINE_SEP);
        outDE.append(generateAttributeSetTableRowHTML(offset, spec.getName(), spec.getAttributes()));
        outDE.append(generateFunctionalDependencySetTableRowHTML(offset, spec.getFunctionalDependencies()));
        outDE.append(offset).append("</table>").append(LINE_SEP);

        StringBuilder outEN = new StringBuilder();
        outEN.append(offset).append("<p>").append(LINE_SEP);


        outEN.append(offset).append("	Let <strong>").append(spec.getName()).append("</strong> be a relation scheme with a set of functional ").append(LINE_SEP);
        outEN.append(offset).append("	dependencies prefixed with <strong>-></strong>. Determine all keys of <strong>").append(spec.getName()).append("</strong>. ").append(LINE_SEP);

        outEN.append(offset).append("</p>").append(LINE_SEP);
        outEN.append(offset).append("<table rules=\"none\" frame=\"void\">").append(LINE_SEP);
        outEN.append(generateAttributeSetTableRowHTML(offset, spec.getName(), spec.getAttributes()));
        outEN.append(generateFunctionalDependencySetTableRowHTML(offset, spec.getFunctionalDependencies()));
        outEN.append(offset).append("</table>").append(LINE_SEP);
        return new TaskModificationResponseDto(outDE.toString(), outEN.toString());
    }

    /**
     * Print assignment for minimal cover task modification response dto.
     *
     * @param spec   The spec.
     * @param indent The indent.
     * @return The task modification response dto.
     */
    public static TaskModificationResponseDto printAssignmentForMinimalCover(IdentifiedRelation spec, int indent) {
        String offset = getOffset(indent);

        StringBuilder outDE = new StringBuilder();
        outDE.append(offset).append("<p>").append(LINE_SEP);

        outDE.append(offset).append("	Geben Sie für die Menge an Funktionalen Abhängigkeiten gekennzeichnet durch das Präfix <strong>-></strong> eine minimale  ").append(LINE_SEP);
        outDE.append(offset).append("	Überdeckung an. Streichen Sie alle redundanten Funktionalen Abhängigkeiten ").append(LINE_SEP);
        outDE.append(offset).append("	und alle redundanten Attribute in den linken Seiten der Funktionalen Abhängigkeiten. ").append(LINE_SEP);


        outDE.append(offset).append("</p>").append(LINE_SEP);
        outDE.append(offset).append("<table rules=\"none\" frame=\"void\">").append(LINE_SEP);
        outDE.append(generateFunctionalDependencySetTableRowHTML(offset, spec.getFunctionalDependencies()));
        outDE.append(offset).append("</table>").append(LINE_SEP);
        outDE.append(offset).append("<p>").append(LINE_SEP);

        StringBuilder outEN = new StringBuilder();

        outEN.append(offset).append("<p>").append(LINE_SEP);


        outEN.append(offset).append("	Determine a minimal cover for the set of functional dependencies prefixed with <strong>-></strong>. Eliminate all redundant functional ").append(LINE_SEP);
        outEN.append(offset).append("	dependencies and redundant attributes at left hand sides of functional dependencies. ").append(LINE_SEP);

        outEN.append(offset).append("</p>").append(LINE_SEP);
        outEN.append(offset).append("<table rules=\"none\" frame=\"void\">").append(LINE_SEP);
        outEN.append(generateFunctionalDependencySetTableRowHTML(offset, spec.getFunctionalDependencies()));
        outEN.append(offset).append("</table>").append(LINE_SEP);
        outEN.append(offset).append("<p>").append(LINE_SEP);
        return new TaskModificationResponseDto(outDE.toString(), outEN.toString());
    }

    /**
     * Print assignment for normalization task modification response dto.
     *
     * @param spec   The spec.
     * @param indent The indent.
     * @return The task modification response dto.
     */
    public static TaskModificationResponseDto printAssignmentForNormalization(NormalizationSpecification spec, int indent) {
        String offset = getOffset(indent);

        StringBuilder outEN = new StringBuilder();
        StringBuilder outDE = new StringBuilder();
        outEN.append(offset).append("<p>").append(LINE_SEP);
        outDE.append(offset).append("<p>").append(LINE_SEP);


        outDE.append(offset).append("	Finden Sie eine <strong>verlustfreie Zerlegung</strong> der Relation ").append(LINE_SEP);
        outDE.append(offset).append("	<strong>").append(spec.getBaseRelation().getName()).append("</strong> mit den Funktionalen Abhängigkeiten gekennzeichnet durch das Präfix <strong>-></strong> in ").append(LINE_SEP);
        outDE.append(offset).append("	<strong>");
        if (spec.getTargetLevel().equals(NormalformLevel.FIRST)) {
            outDE.append(offset).append("erster ");
        } else if (spec.getTargetLevel().equals(NormalformLevel.SECOND)) {
            outDE.append(offset).append("zweiter ");
        } else if (spec.getTargetLevel().equals(NormalformLevel.THIRD)) {
            outDE.append(offset).append("dritter ");
        } else if (spec.getTargetLevel().equals(NormalformLevel.BOYCE_CODD)) {
            outDE.append(offset).append("Boyce-Codd ");
        }
        outDE.append(offset).append("	Normalform</strong>. Geben Sie für jede Teilrelation in dieser Reihenfolge an:<br>").append(LINE_SEP);
        outDE.append(offset).append("&nbsp".repeat(4)).append(" -Einen einzigartigen Relations-Namen (Suffix <strong>:</strong>),<br>").append(LINE_SEP);
        outDE.append(offset).append("&nbsp".repeat(4)).append(" -Die Attribute in runden Klammern (kein Präfix vor der öffnenden Klammer),<br>").append(LINE_SEP);
        outDE.append(offset).append("&nbsp".repeat(4)).append(" -Die ableitbaren Funktionalen Abhängigkeiten in runden Klammern (Präfix vor der öffnenden Klammer <strong>-></strong>; Klammern leer lassen, wenn es keine Abhängigkeiten gibt) und<br>").append(LINE_SEP);
        outDE.append(offset).append("&nbsp".repeat(4)).append(" -Die Schlüssel in runden Klammern (Präfix vor der öffnenden Klammer <strong>#</strong>).<br><br>").append(LINE_SEP);
        if (spec.getMaxLostDependencies() == 0) {
            outDE.append(offset).append("	Sie dürfen bei der Zerlegung <strong>keine</strong> Funktionale Abhängigkeit verlieren!").append(LINE_SEP);
        } else if (spec.getMaxLostDependencies() >= spec.getBaseRelation().getFunctionalDependencies().size()) {
            outDE.append(offset).append("	Die Zerlegung muss <strong>nicht abhängigkeitstreu</strong> sein. ").append(LINE_SEP);
        } else {
            outDE.append(offset).append("	Sie dürfen bei der Zerlegung maximal <strong>").append(spec.getMaxLostDependencies()).append("</strong> Funktionale ").append(LINE_SEP);
            if (spec.getMaxLostDependencies() == 1) {
                outDE.append(offset).append("	Abhängigkeit ").append(LINE_SEP);
            } else {
                outDE.append(offset).append("	Abhängigkeiten ").append(LINE_SEP);
            }
            outDE.append(offset).append("	verlieren!").append(LINE_SEP);
        }

        outEN.append(offset).append("	Find a <strong>lossless decomposition</strong> of relation ").append(LINE_SEP);
        outEN.append(offset).append("	<strong>").append(spec.getBaseRelation().getName()).append("</strong> with functional dependencies prefixed with <strong>-></strong>. ").append(LINE_SEP);
        outEN.append(offset).append("	The decomposition must be in <strong>");
        if (spec.getTargetLevel().equals(NormalformLevel.FIRST)) {
            outEN.append(offset).append("first ");
        } else if (spec.getTargetLevel().equals(NormalformLevel.SECOND)) {
            outEN.append(offset).append("second ");
        } else if (spec.getTargetLevel().equals(NormalformLevel.THIRD)) {
            outEN.append(offset).append("third ");
        } else if (spec.getTargetLevel().equals(NormalformLevel.BOYCE_CODD)) {
            outEN.append(offset).append("Boyce-Codd ");
        }
        outEN.append(offset).append("	normal form</strong>. For each relation fragment, specify (in this order):<br>").append(LINE_SEP);
        outEN.append(offset).append("&nbsp".repeat(4)).append(" -A unique relation name (suffixed with <strong>:</strong>),<br>").append(LINE_SEP);
        outEN.append(offset).append("&nbsp".repeat(4)).append(" -Its attributes in parentheses (no prefix before the opening parenthesis),<br>").append(LINE_SEP);
        outEN.append(offset).append("&nbsp".repeat(4)).append(" -Its functional dependencies in parentheses (opening parenthesis prefixed with <strong>-></strong>; leave parentheses empty if there are no dependencies) and<br>").append(LINE_SEP);
        outEN.append(offset).append("&nbsp".repeat(4)).append(" -Its keys in parentheses (opening parenthesis prefixed with <strong>#</strong>).<br><br>").append(LINE_SEP);
        if (spec.getMaxLostDependencies() == 0) {
            outEN.append(offset).append("	You may not lose <strong>any </strong> functional dependency!").append(LINE_SEP);
        } else if (spec.getMaxLostDependencies() >= spec.getBaseRelation().getFunctionalDependencies().size()) {
            outEN.append(offset).append("	The decomposition does not have to be <strong>dependency preserving</strong>. ").append(LINE_SEP);
        } else {
            outEN.append(offset).append("	At most <strong>").append(spec.getMaxLostDependencies()).append("</strong> ").append(LINE_SEP);
            if (spec.getMaxLostDependencies() == 1) {
                outEN.append(offset).append("	functional dependency ").append(LINE_SEP);
            } else {
                outEN.append(offset).append("	functional dependencies ").append(LINE_SEP);
            }
            outEN.append(offset).append("	may be lost during decomposition!").append(LINE_SEP);
        }

        outEN.append(offset).append("</p>").append(LINE_SEP);
        outEN.append(offset).append("<table rules=\"none\" frame=\"void\">").append(LINE_SEP);
        outEN.append(generateAttributeSetTableRowHTML(offset, spec.getBaseRelation().getName(), spec.getBaseRelation().getAttributes()));
        outEN.append(generateFunctionalDependencySetTableRowHTML(offset, spec.getBaseRelation().getFunctionalDependencies()));
        outEN.append(offset).append("</table>").append(LINE_SEP);


        outDE.append(offset).append("</p>").append(LINE_SEP);
        outDE.append(offset).append("<table rules=\"none\" frame=\"void\">").append(LINE_SEP);
        outDE.append(generateAttributeSetTableRowHTML(offset, spec.getBaseRelation().getName(), spec.getBaseRelation().getAttributes()));
        outDE.append(generateFunctionalDependencySetTableRowHTML(offset, spec.getBaseRelation().getFunctionalDependencies()));
        outDE.append(offset).append("</table>").append(LINE_SEP);
        return new TaskModificationResponseDto(outDE.toString(), outEN.toString());
    }

    /**
     * Print assignment for attribute closure task modification response dto.
     *
     * @param spec   The spec.
     * @param indent The indent.
     * @return The task modification response dto.
     */
    public static TaskModificationResponseDto printAssignmentForAttributeClosure(AttributeClosureSpecification spec, int indent) {
        String offset = getOffset(indent);

        StringBuilder outEN = new StringBuilder();
        StringBuilder outDE = new StringBuilder();

        outEN.append(offset).append("<p>").append(LINE_SEP);
        outDE.append(offset).append("<p>").append(LINE_SEP);


        outDE.append(offset).append("	Berechnen Sie die Hülle der Attribut-Kombination <strong>A</strong> ").append(LINE_SEP);
        outDE.append(offset).append("	bezüglich der Menge an Funktionalen Abhängigkeiten gekennzeichnet durch das Präfix <strong>-></strong> ").append(LINE_SEP);
        outDE.append(offset).append("	der Relation <strong>").append(spec.getBaseRelation().getName()).append("</strong>.").append(LINE_SEP);

        outEN.append(offset).append("	Determine the attribute closure of the set of attributes <strong>A</strong> ").append(LINE_SEP);
        outEN.append(offset).append("	with respect to relation scheme <strong>").append(spec.getBaseRelation().getName()).append("</strong> and the set of ").append(LINE_SEP);
        outEN.append(offset).append("	functional dependencies prefixed with <strong>-></strong>.").append(LINE_SEP);

        outDE.append(offset).append("</p>").append(LINE_SEP);
        outDE.append(offset).append("<table rules=\"none\" frame=\"void\">").append(LINE_SEP);
        outDE.append(generateAttributeSetTableRowHTML(offset, spec.getBaseRelation().getName(), spec.getBaseRelation().getAttributes()));
        outDE.append(generateFunctionalDependencySetTableRowHTML(offset, spec.getBaseRelation().getFunctionalDependencies()));
        outDE.append(generateAttributeSetTableRowHTML(offset, "A", spec.getBaseAttributes()));
        outDE.append(offset).append("</table>").append(LINE_SEP);

        outEN.append(offset).append("</p>").append(LINE_SEP);
        outEN.append(offset).append("<table rules=\"none\" frame=\"void\">").append(LINE_SEP);
        outEN.append(generateAttributeSetTableRowHTML(offset, spec.getBaseRelation().getName(), spec.getBaseRelation().getAttributes()));
        outEN.append(generateFunctionalDependencySetTableRowHTML(offset, spec.getBaseRelation().getFunctionalDependencies()));
        outEN.append(generateAttributeSetTableRowHTML(offset, "A", spec.getBaseAttributes()));
        outEN.append(offset).append("</table>").append(LINE_SEP);
        return new TaskModificationResponseDto(outDE.toString(), outEN.toString());
    }


    /**
     * Print assignment for normal form determination string.
     *
     * @param spec   The spec.
     * @param indent The indent.
     * @return The string.
     */
    public static TaskModificationResponseDto printAssignmentForNormalFormDetermination(Relation spec, int indent) {
        String offset = getOffset(indent);

        StringBuilder out = new StringBuilder();
        StringBuilder outEN = new StringBuilder();
        StringBuilder outDE = new StringBuilder();

        outEN.append(offset).append("<p>").append(LINE_SEP);
        outDE.append(offset).append("<p>").append(LINE_SEP);


            outDE.append(offset).append("	Geben Sie an, in welcher Normalform sich die Relation <strong>").append(spec.getName()).append("</strong> ").append(LINE_SEP);
            outDE.append(offset).append("	mit den Funktionalen Abhängigkeiten gekennzeichnet durch das Präfix <strong>-></strong> befindet. ").append(LINE_SEP);
            outDE.append(offset).append("	Geben Sie weiters für jede Funktionale Abhängigkeit ").append(LINE_SEP);
            outDE.append(offset).append("	an, welche Normalform durch diese verletzt wird. ").append(LINE_SEP);

            outEN.append(offset).append("	Determine the highest normal form that is fulfilled in relation scheme ").append(LINE_SEP);
            outEN.append(offset).append("	<strong>").append(spec.getName()).append("</strong> with the set of functional dependencies prefixed with <strong>-></strong>. ").append(LINE_SEP);
            outEN.append(offset).append("	Further, determine for each functional dependency ").append(LINE_SEP);
            outEN.append(offset).append("	the normal form that is violated by it. ").append(LINE_SEP);

        out.append(offset).append("</p>").append(LINE_SEP);
        out.append(offset).append("<table rules=\"none\" frame=\"void\">").append(LINE_SEP);
        out.append(generateAttributeSetTableRowHTML(offset, spec.getName(), spec.getAttributes())).append(LINE_SEP);
        out.append(generateFunctionalDependencySetTableRowHTML(offset, spec.getFunctionalDependencies()));
        out.append(offset).append("</table>").append(LINE_SEP);
        outEN.append(out);
        outDE.append(out);

        return new TaskModificationResponseDto(outDE.toString(), outEN.toString());
    }


    //support methods


    private static String getOffset(int indent) {
        return "\t".repeat(Math.max(0, indent));
    }

    private static String generateAttributeSetTableRowHTML(String offset, String setName, Collection<String> attributes) {
        StringBuilder out = new StringBuilder();

        out.append(offset).append("	<tr>").append(LINE_SEP);
        out.append(offset).append("		<td><strong>").append(setName).append("</strong>: </td>").append(LINE_SEP);
        out.append(offset).append("		<td>(").append(generateSetHTML(attributes, ",&nbsp;")).append(")</td>").append(LINE_SEP);
        out.append(offset).append("	</tr>").append(LINE_SEP);

        return out.toString();
    }

    private static String generateFunctionalDependencySetTableRowHTML(String offset, Collection<FunctionalDependency> dependencies) {
        StringBuilder out = new StringBuilder();

        out.append(offset).append("	<tr>").append(LINE_SEP);
        out.append(offset).append("		<td><strong>-></strong> </td>").append(LINE_SEP);
        out.append(offset).append("		<td>(").append(generateSetHTML(dependencies, "; ")).append(")</td>").append(LINE_SEP);
        out.append(offset).append("	</tr>").append(LINE_SEP);

        return out.toString();
    }

    private static String generateSetHTML(Collection<?> elements, String delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);

        for (Object element : elements) {
            joiner.add(element.toString());
        }

        return joiner.toString();
    }

}
