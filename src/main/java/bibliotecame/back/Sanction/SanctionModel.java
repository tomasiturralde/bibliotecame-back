package bibliotecame.back.Sanction;

import bibliotecame.back.User.UserModel;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
@SuppressWarnings("unused")
public class SanctionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column
    private String reason;

    @Column
    private LocalDate creationDate;

    @Column
    private LocalDate endDate;

    @OneToOne
    private UserModel user;

    public SanctionModel(){

    }

    public SanctionModel(String reason, LocalDate creationDate, LocalDate endDate, UserModel user) {
        this.reason = reason;
        this.creationDate = creationDate;
        this.endDate = endDate;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
