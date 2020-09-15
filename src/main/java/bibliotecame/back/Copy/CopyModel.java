package bibliotecame.back.Copy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@SuppressWarnings("unused")
public class CopyModel {

    @Id
    @Column(nullable = false)
    private String id;

    private Boolean isBooked;

    private Boolean isActive;

    public CopyModel() {
    }

    public CopyModel(String id) {
        this.id = id;
        isBooked = false;
        isActive = true;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
