package bibliotecame.back.Verification;

import bibliotecame.back.User.UserModel;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table
public class VerificationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column
    private String token;

    @OneToOne
    private UserModel userModel;

    public VerificationModel() {
    }

    public VerificationModel(UserModel userModel) {
        this.token = UUID.randomUUID().toString();
        this.userModel = userModel;
    }

    public String getToken() {
        return token;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
