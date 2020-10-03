package bibliotecame.back.Loan;

public enum LoanStatus {
    PENDING_EXTENSION(0),
    APPROVED_EXTENSION(1),
    REJECTED_EXTENSION(2),
    DELAYED(3),
    WITHDRAWN(4),
    READY_FOR_WITHDRAWAL(5);

    public final int id;

    LoanStatus(int id) {
        this.id = id;
    }

    public static LoanStatus getFromInt(int id) {
        for (LoanStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid Status id: " + id);
    }
}
