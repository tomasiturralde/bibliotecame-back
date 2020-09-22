package bibliotecame.back.Tag;

import javax.persistence.*;

@Entity
@Table
@SuppressWarnings("unused")
public class TagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column
    private String name;

    public TagModel() {
    }

    public TagModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
