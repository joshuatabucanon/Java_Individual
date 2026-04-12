package m5group6.project1.dao;

import m5group6.project1.model.User;

public interface UserDAO {
    int insert(User user);  // returns generated DB id
}