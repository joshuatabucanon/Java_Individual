package m7group6.project1.repo;

import m7group6.project1.model.UserEntity;

public interface UserRepository {
    int insert(UserEntity user);  // returns generated DB id
}