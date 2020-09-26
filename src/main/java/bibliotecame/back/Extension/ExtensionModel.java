package bibliotecame.back.Extension;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
@SuppressWarnings("unused")
public class ExtensionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column
    private ExtensionStatus status;

    @Column
    private LocalDate creationDate;

    public ExtensionModel(LocalDate creationDate) {
        this.status = ExtensionStatus.PENDING_APPROVAL;
        this.creationDate = creationDate;
    }

    public ExtensionModel() {}

    public int getId() {
        return id;
    }

    public ExtensionStatus getStatus() {
        return status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setStatus(ExtensionStatus status) { this.status = status; }
}
