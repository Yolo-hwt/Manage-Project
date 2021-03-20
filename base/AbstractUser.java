package base;

import java.io.Serializable;

public class AbstractUser implements Serializable {
    static final double EXCEPTION_PROBABILITY = 1;
    private String name;
    private String password;
    private String role;
    private String login;//on表示已登录无法重复登陆//off表示未登录

    public AbstractUser(String name, String password, String role,String login) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.login=login;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password=password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "AbstractUser{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
