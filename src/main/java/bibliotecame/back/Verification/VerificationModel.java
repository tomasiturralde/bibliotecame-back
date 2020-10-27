package bibliotecame.back.Verification;

import bibliotecame.back.User.UserModel;

import javax.persistence.*;

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
        this.token = generateToken();
        this.userModel = userModel;
    }

    private String generateToken() {
        StringBuilder sb = new StringBuilder(40);
        String options = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        for (int i = 0; i < 40; i++) {
            int index = (int)(options.length() * Math.random());
            sb.append(options.charAt(index));
        }
        return sb.toString();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) { this.token = token; }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
