package bibliotecame.back.Request;

public enum RequestStatus {
    PENDING(0, "A revisar"),
    APPROVED(1, "Aprobada"),
    REJECTED(2, "Rechazada");

    private final int id;
    private final String label;

    RequestStatus(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public static RequestStatus getFromInt(int id) {
        for (RequestStatus status : values()) {
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
