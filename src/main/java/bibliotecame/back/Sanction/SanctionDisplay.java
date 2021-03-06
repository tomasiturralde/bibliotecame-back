package bibliotecame.back.Sanction;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class SanctionDisplay {

    @NotNull
    private Integer id;

    @NotNull
    private String email;

    @NotNull
    private LocalDate creationDate;

    @NotNull
    private LocalDate endDate;
    private String reason;

    public SanctionDisplay() {
    }

    public SanctionDisplay(int id, String email, LocalDate creationDate, LocalDate endDate, String reason) {
        this.id = id;
        this.email = email;
        this.creationDate = creationDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
