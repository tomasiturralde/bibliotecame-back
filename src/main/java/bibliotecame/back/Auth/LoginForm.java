package bibliotecame.back.Auth;

import javax.validation.constraints.NotNull;

public class LoginForm {
    @NotNull
    private String email;

    @NotNull
    private String password;

    public LoginForm() {
    }

    public LoginForm(@NotNull String email, @NotNull String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
