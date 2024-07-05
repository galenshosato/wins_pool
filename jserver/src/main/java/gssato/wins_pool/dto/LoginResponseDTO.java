package gssato.wins_pool.dto;

import gssato.wins_pool.models.User;

public class LoginResponseDTO {

    private String token;
    private User user;

    public LoginResponseDTO(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
