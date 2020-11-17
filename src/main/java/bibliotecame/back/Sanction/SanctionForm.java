package bibliotecame.back.Sanction;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class SanctionForm {

    @NotNull
    private String email;

    @NotNull
    private String reason;

    @NotNull
    private LocalDate endDate;

    public SanctionForm() {
    }

    public SanctionForm(String email, String reason, LocalDate endDate) {
        this.email = email;
        this.reason = reason;
        this.endDate = endDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
