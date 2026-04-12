package m5group6.project1.config;

public class DBConfig {
	//See project's read.me for procedure on setting up postgreSQL user and database to be used by group.
    private static final String URL  = "jdbc:postgresql://localhost:5432/library_db";
    private static final String USER = "group6";     
    //Password will be bypassed if pg_hba.conf uses trust authentication that allows any logged in local user to connect to the database.
    private static final String PASS = "p05+gr3$";   
    
	public static String getUrl() {
		return URL;
	}
	public static String getUser() {
		return USER;
	}
	public static String getPass() {
		return PASS;
	}
    
}
