package gssato.wins_pool.models;

import java.math.BigDecimal;

public class User {

    private int userId;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String favoriteTeam;

    private boolean deleted;

    private boolean isAdmin;

    private BigDecimal moneyOwed;

    public User() {
    }

    public User(int userId, String firstName, String lastName, String email, String password, String favoriteTeam, boolean deleted, boolean isAdmin, BigDecimal moneyOwed) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.favoriteTeam = favoriteTeam;
        this.deleted = deleted;
        this.isAdmin = isAdmin;
        this.moneyOwed = moneyOwed;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getFavoriteTeam() {
        return favoriteTeam;
    }

    public void setFavoriteTeam(String favoriteTeam) {
        this.favoriteTeam = favoriteTeam;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public BigDecimal getMoneyOwed() {
        return moneyOwed;
    }

    public void setMoneyOwed(BigDecimal moneyOwed) {
        this.moneyOwed = moneyOwed;
    }
}
