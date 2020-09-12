package bibliotecame.back.Copy;

import javax.persistence.*;

@Entity
@Table
@SuppressWarnings("unused")
public class CopyModel {

    @Id
    @Column(nullable = false)
    private String id;

    private Boolean isBooked;

    public CopyModel() {
    }

    public CopyModel(String id) {
        this.id = id;
        isBooked = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getBooked() {
        return isBooked;
    }

    public void setBooked(Boolean booked) {
        isBooked = booked;
    }
}
