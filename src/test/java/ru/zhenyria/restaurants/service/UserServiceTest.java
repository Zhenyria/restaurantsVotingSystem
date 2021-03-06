package ru.zhenyria.restaurants.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionSystemException;
import ru.zhenyria.restaurants.model.Role;
import ru.zhenyria.restaurants.model.User;
import ru.zhenyria.restaurants.to.UserTo;
import ru.zhenyria.restaurants.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.zhenyria.restaurants.UserTestData.*;

class UserServiceTest extends AbstractServiceTest {

    @Autowired
    private UserService service;

    @Test
    void create() {
        User created = service.create(getNew());
        int newId = created.id();
        User newUser = getNew();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    void createWithDuplicateEmail() {
        User newUser = getNew();
        newUser.setEmail(USER_EMAIL);
        assertThrows(DataAccessException.class, () -> service.create(newUser));
    }

    @Test
    void createWithException() {
        validateRootCause(() -> service.create(new User("  ", "password", "email@email.com", Role.USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User("Mr. Black", "  ", "email@email.com", Role.USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User("Mrs. Black", "password", "  ", Role.USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User("Mrs. Black", "password", "mrsblack", Role.USER)), ConstraintViolationException.class);
    }

    @Test
    void get() {
        User user = service.get(FIRST_USER_ID);
        USER_MATCHER.assertMatch(user, user1);
    }

    @Test
    void getNotExist() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND_ID));
    }

    @Test
    void getByEmail() {
        User user = service.getByEmail(USER_EMAIL);
        USER_MATCHER.assertMatch(user, user1);
    }

    @Test
    void getByEmailNotFound() {
        assertThrows(NotFoundException.class, () -> service.getByEmail("notfound@mail.ru"));
    }

    @Test
    void update() {
        service.update(getUpdated());
        USER_MATCHER.assertMatch(service.get(FIRST_USER_ID), getUpdated());
    }

    @Test
    void updateInvalid() {
        User updated = getUpdated();
        updated.setName(null);
        assertThrows(TransactionSystemException.class, () -> service.update(updated));
    }

    @Test
    void updateTo() {
        User updated = getUpdated();
        UserTo updatedTo = new UserTo(updated);
        service.update(updatedTo);
        USER_UPDATED_TO_MATCHER.assertMatch(service.get(FIRST_USER_ID), updated);
    }

    @Test
    void updateToInvalid() {
        UserTo updated = new UserTo(getUpdated());
        updated.setName(null);
        assertThrows(TransactionSystemException.class, () -> service.update(updated));
    }

    @Test
    void delete() {
        service.delete(FIRST_USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(FIRST_USER_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND_ID));
    }

    @Test
    void getAll() {
        USER_MATCHER.assertMatch(service.getAll(), users);
    }
}