package ru.zhenyria.restaurants.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionSystemException;
import ru.zhenyria.restaurants.model.Dish;
import ru.zhenyria.restaurants.util.exception.NotAvailableOperationException;
import ru.zhenyria.restaurants.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.zhenyria.restaurants.DishTestData.*;
import static ru.zhenyria.restaurants.UserTestData.NOT_FOUND_ID;

class DishServiceTest extends AbstractServiceTest {

    @Autowired
    private DishService service;

    @Test
    void create() {
        Dish created = service.create(getNew());
        int id = created.id();
        Dish newDish = getNew();
        newDish.setId(id);
        DISH_MATCHER.assertMatch(created, newDish);
        DISH_MATCHER.assertMatch(service.get(id), newDish);
    }

    @Test
    void createWithDuplicateNamePrice() {
        Dish dish = getNew();
        dish.setName(FIRST_DISH_NAME);
        dish.setPrice(FIRST_DISH_PRICE);
        assertThrows(DataAccessException.class, () -> service.create(dish));
    }

    @Test
    void createWithException() {
        validateRootCause(() -> service.create(new Dish(null, "  ", 100)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new Dish(null, "Meet", 0)), ConstraintViolationException.class);
    }

    @Test
    void get() {
        Dish dish = service.get(FIRST_DISH_ID);
        DISH_MATCHER.assertMatch(dish, dish1);
    }

    @Test
    void getNotExist() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND_ID));
    }

    @Test
    void getAll() {
        DISH_MATCHER.assertMatch(service.getAll(), dishes);
    }

    @Test
    void update() {
        service.update(getUpdated());
        DISH_MATCHER.assertMatch(service.get(FIRST_DISH_ID), getUpdated());
    }

    @Test
    void updateInvalid() {
        Dish updated = getUpdated();
        updated.setName("  ");
        assertThrows(TransactionSystemException.class, () -> service.update(updated));
    }

    @Test
    void delete() {
        service.delete(NOT_USING_DISH_ID);
        assertThrows(NotFoundException.class, () -> service.get(NOT_USING_DISH_ID));
    }

    @Test
    void deleteUsing() {
        assertThrows(NotAvailableOperationException.class, () -> service.delete(FIRST_DISH_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND_ID));
    }
}
