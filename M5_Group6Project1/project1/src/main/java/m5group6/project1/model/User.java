package m5group6.project1.model;

public class User {
    private String name;
    private int userID; // DB-generated id

    public User() {}

    public User(int userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = (name == null ? null : name.trim());
    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
}