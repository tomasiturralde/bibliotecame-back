package bibliotecame.back.Request;

import javax.persistence.*;

@Entity
@Table
public class RequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

}
