package m5group6.project1.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import m5group6.project1.dao.UserDAO;
import m5group6.project1.exceptions.DataAccessException;
import m5group6.project1.model.User;
import m5group6.project1.util.DBInputValidator;
import m5group6.project1.util.DBUtil;


public class UserDAOImpl implements UserDAO {

    private static final String SQL_INSERT =
        "INSERT INTO users (name) VALUES (?) RETURNING user_id";

    @Override
    public int insert(User user) {

		if (user == null) {
		    throw new IllegalArgumentException("User cannot be null");
		}
		
		// Enforce DB-aligned rules: non-blank + ≤ 200 chars
		DBInputValidator.ensureUserNameForInsert(user.getName());

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, user.getName().trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // or "user_id"
                }
                throw new DataAccessException("Insert succeeded but no user_id returned", null);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert user", e);
        }
    }
}
