package bibliotecame.back.Publisher;

import javax.persistence.*;

@Entity
@Table
public class PublisherModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column
    private String name;

    public PublisherModel() {
    }

    public PublisherModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}