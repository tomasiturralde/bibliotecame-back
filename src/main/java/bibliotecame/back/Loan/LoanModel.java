package bibliotecame.back.Loan;

import bibliotecame.back.Copy.CopyModel;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
@SuppressWarnings("unused")
public class LoanModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @ManyToOne
    private CopyModel copy;

    @Column
    private LocalDate reservationDate;

    @Column
    private LocalDate withdrawalDate;

    @Column
    private LocalDate returnDate;

    @Column
    private LocalDate expirationDate;

    public LoanModel() {
    }

    public LoanModel(CopyModel copy, LocalDate reservationDate, LocalDate expirationDate) {
        this.copy = copy;
        this.reservationDate = reservationDate;
        this.expirationDate = expirationDate;
    }

    public int getId() {
        return id;
    }

    public CopyModel getCopy() {
        return copy;
    }

    public void setCopy(CopyModel copy) {
        this.copy = copy;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDate getWithdrawalDate() {
        return withdrawalDate;
    }

    public void setWithdrawalDate(LocalDate withdrawalDate) {
        this.withdrawalDate = withdrawalDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
