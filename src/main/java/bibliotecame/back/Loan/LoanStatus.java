package bibliotecame.back.Loan;

public enum LoanStatus {
    PENDING_EXTENSION(0, "Prórroga Pend."),
    APPROVED_EXTENSION(1, "Prórroga Acp."),
    REJECTED_EXTENSION(2, "Prórroga Rech."),
    DELAYED(3, "Atrasado"),
    WITHDRAWN(4, "Retirado"),
    READY_FOR_WITHDRAWAL(5, "No Retirado"),
    RETURNED(6, "Devuelto");

    private final int id;
    private final String label;

    LoanStatus(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public static LoanStatus getFromInt(int id) {
        for (LoanStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid Status id: " + id);
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
