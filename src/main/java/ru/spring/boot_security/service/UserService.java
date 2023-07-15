package ru.spring.boot_security.service;


import ru.spring.boot_security.model.User;

import java.util.List;


public interface UserService {


    void add(User user);

    void update(User user);

    void delete(long id);

    User getUser(long id);

    List<User> getUsersList();

    void UserNameCheck(User user);

}