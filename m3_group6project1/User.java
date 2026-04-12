package m3_group6project1;

public class User{

	private String name;
	private static int userID = 0;
	
	public User() {
		User.userID++;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name.trim();
	}
	public int getUserID() {
		return userID;
	}
	
}