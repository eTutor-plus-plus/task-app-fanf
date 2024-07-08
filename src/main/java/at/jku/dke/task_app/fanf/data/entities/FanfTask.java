package at.jku.dke.task_app.fanf.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTask;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;



@Entity
@Table(name = "task")
public class FanfTask extends BaseTask {

    @NotNull
    @Column(name = "specification", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String specification;
    @NotNull
    @Column(name = "rdbd_type", nullable = false)
    private Integer rdbdType;

    public FanfTask(){
        super();
    }

    public FanfTask(String specification, Integer rdbdType)
    {
        super();
        this.rdbdType = rdbdType;
        this.specification = specification;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public Integer getRdbdType() {
        return rdbdType;
    }

    public void setRdbdType(Integer rdbdType) {
        this.rdbdType = rdbdType;
    }

/*
 TODO [Reverse Engineering] create field to map the 'status' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "status", columnDefinition = "task_status not null")
    private Object status;
*/
}
