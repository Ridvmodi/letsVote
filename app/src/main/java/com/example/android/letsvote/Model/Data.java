package com.example.android.letsvote.Model;

public class Data {

    private String userId;
    private String userName;
    private String userEmail;
    private String userRole;
    private String userAAdharNo;
    private String key;

    public Data(String userId, String userName, String userPass, String userRole, String userAAdharNo, String key) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userPass;
        this.userRole = userRole;
        this.userAAdharNo = userAAdharNo;
        this.key = key;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail= userEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserAAdharNo() {
        return userAAdharNo;
    }

    public void setUserAAdharNo(String userAAdharNo) {
        this.userAAdharNo = userAAdharNo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
