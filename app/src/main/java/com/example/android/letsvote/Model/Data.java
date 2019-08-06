package com.example.android.letsvote.Model;

public class Data {

    private String userId;
    private String userName;
    private String userPass;
    private String userRole;

    public Data(String userId, String userName, String userPass, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userPass = userPass;
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
