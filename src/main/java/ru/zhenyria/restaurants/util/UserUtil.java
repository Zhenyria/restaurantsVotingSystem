package ru.zhenyria.restaurants.util;

import ru.zhenyria.restaurants.model.Role;
import ru.zhenyria.restaurants.model.User;
import ru.zhenyria.restaurants.to.UserTo;

public class UserUtil {
    private UserUtil() {
    }

    public static User updateUserFromTo(User user, UserTo updated) {
        user.setName(updated.getName());
        user.setEmail(updated.getEmail());
        user.setPassword(updated.getPassword());
        return user;
    }

    public static User getUserFromTo(UserTo user) {
        return new User(user.getName(), user.getEmail().toLowerCase(), user.getPassword(), Role.USER);
    }

    public static UserTo getToFromUser(User user) {
        return new UserTo(user.getId(), user.getName(), user.getEmail(), user.getPassword());
    }
}