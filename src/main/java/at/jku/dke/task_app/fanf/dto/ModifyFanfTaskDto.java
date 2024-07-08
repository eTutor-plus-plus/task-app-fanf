package at.jku.dke.task_app.fanf.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link at.jku.dke.task_app.fanf.data.entities.FanfTask}
 */
public class ModifyFanfTaskDto implements Serializable {
    @NotNull
    private final String specification;
    @NotNull
    private final Integer rdbdType;

    public ModifyFanfTaskDto(String specification, Integer rdbdType) {
        this.specification = specification;
        this.rdbdType = rdbdType;
    }

    public String getSpecification() {
        return specification;
    }

    public Integer getRdbdType() {
        return rdbdType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModifyFanfTaskDto entity = (ModifyFanfTaskDto) o;
        return Objects.equals(this.specification, entity.specification) &&
            Objects.equals(this.rdbdType, entity.rdbdType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specification, rdbdType);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
            "specification = " + specification + ", " +
            "rdbdType = " + rdbdType + ")";
    }
}
