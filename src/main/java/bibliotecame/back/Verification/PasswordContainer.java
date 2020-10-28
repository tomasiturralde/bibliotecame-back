package bibliotecame.back.Verification;

public class PasswordContainer {

    private String password;

    public PasswordContainer(String password) {
        this.password = password;
    }

    public PasswordContainer(){ }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
