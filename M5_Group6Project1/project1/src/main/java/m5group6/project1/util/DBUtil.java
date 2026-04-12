package m5group6.project1.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import m5group6.project1.config.DBConfig;
import m5group6.project1.exceptions.DataAccessException;

public class DBUtil {
    //private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);

    public static Connection getConnection() {
        try {
			Connection conn = DriverManager.getConnection(
			        DBConfig.getUrl(),
			        DBConfig.getUser(),
			        DBConfig.getPass()
			);
			//logger.debug("Successfully obtained a DB connection.");
			return conn;
		} catch (SQLException e) {
			throw new DataAccessException("Unable to obtain DB connection", e);
		}
    }

}