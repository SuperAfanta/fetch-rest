package ru.spring.boot_security.service;

import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spring.boot_security.dao.UserDao;
import ru.spring.boot_security.model.User;
import ru.spring.boot_security.util.UserNotFoundException;

import java.util.List;

@Service
public class UserServiceImp implements UserService, UserDetailsService {
    private final UserDao userDao;
    public UserServiceImp(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    @Override
    public void add(User user) {
        userDao.add(user);
    }

    @Transactional
    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Transactional
    @Override
    public void delete(long id) {
        try {
            userDao.delete(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Введены неверные данные", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public User getUser(long id) {
        User user = userDao.getById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getUsersList() {
        return userDao.getUsersList();
    }

    @Transactional()
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.getByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        Hibernate.initialize(user.getRoleSet());
        return user;
    }


    @Transactional
    @Override
    public void UserNameCheck(User user) {
        if (userDao.getByName(user.getUsername()) != null) {
            throw new IllegalArgumentException("Пользователь с таким именем существует");
        }
    }

}