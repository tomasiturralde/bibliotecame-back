package bibliotecame.back.Review;

import bibliotecame.back.User.UserModel;

import javax.persistence.*;

@Entity
@Table
@SuppressWarnings("unused")
public class ReviewModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column
    private String description;

    @Column
    private int value;

    @ManyToOne
    private UserModel userModel;

    @Column
    private boolean active;

    public ReviewModel(String description, int value, UserModel userModel) {
        this.description = description;
        this.value = value;
        this.userModel = userModel;
        this.active = true;
    }

    public ReviewModel(int value, UserModel userModel) {
        this.description = "";
        this.value = value;
        this.userModel = userModel;
        this.active = true;
    }

    public ReviewModel() {
        this.active=true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if(value<0 || value>5) throw new RuntimeException("Invalid review value!");
        this.value = value;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}