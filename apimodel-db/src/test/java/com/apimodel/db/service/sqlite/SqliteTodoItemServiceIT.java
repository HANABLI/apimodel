package com.apimodel.db.service.sqlite;


import com.apimodel.db.DataSourceExtension;
import com.apimodel.db.exceptions.ConflictException;
import com.apimodel.db.exceptions.ConstraintException;
import com.apimodel.db.service.TodoItemService;
import com.apimodel.db.service.TodoListService;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import com.apimodel.model.TodoItem;
import com.apimodel.model.TodoList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.List;


@ExtendWith(DataSourceExtension.class)
public class SqliteTodoItemServiceIT {
    private final TodoItemService todoItemService;
    private final TodoListService todoListService;

    private final RapidApiPrincipal principal1 = new RapidApiPrincipal("proxy-secret", "user1", Subscription.BASIC);
    private final RapidApiPrincipal principal2 = new RapidApiPrincipal("proxy-secret", "user2", Subscription.BASIC);

    public SqliteTodoItemServiceIT(DataSource dataSource) {
        todoItemService = new SqliteTodoItemService(dataSource);
        todoListService= new SqliteTodoListService(dataSource);
    }

    @Test
    public void testGetWithNoItem() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(todoListService.create(principal1, list));
        Optional<TodoList> fetchedList = todoListService.get(principal1, list.getId());
        TodoItem item = new TodoItem().setId("id").setTask("task").setDone(false);
        Optional<TodoItem> fetchedItem = todoItemService.get(principal1, fetchedList.get().getId(), item.getId());
        Assertions.assertTrue(fetchedItem.isEmpty());

    }

    @Test
    public void testGetWithOneItem() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(todoListService.create(principal1, list));
        Optional<TodoList> fetchedList = todoListService.get(principal1, list.getId());
        TodoItem item = new TodoItem().setId("id").setTask("task").setDone(false);
        Assertions.assertTrue(todoItemService.create(principal1, fetchedList.get().getId(), item));
        Optional<TodoItem> fetchedItem = todoItemService.get(principal1, fetchedList.get().getId(), item.getId());
        Assertions.assertTrue(fetchedItem.isPresent());
    }

    @Test
    public void testGetAllNoItems() {
        List<TodoItem> fetchedItems = todoItemService.getAll(principal1);
        Assertions.assertTrue(fetchedItems.isEmpty());
    }

    @Test
    public void testGetAllWithWrongPrincipal() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(todoListService.create(principal1, list));
        TodoItem item1 = new TodoItem().setId("id1").setTask("task1").setDone(false);
        TodoItem item2 = new TodoItem().setId("id2").setTask("task2").setDone(false);
        Assertions.assertTrue(todoItemService.create(principal1, list.getId(), item1));
        Assertions.assertTrue(todoItemService.create(principal1, list.getId(), item2));
        List<TodoItem> todoItems = todoItemService.getAll(principal2);
        Assertions.assertTrue(todoItems.isEmpty());
    }

    @Test
    public void testGetAllWithMultiItems() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(todoListService.create(principal1, list));
        TodoItem item1 = new TodoItem().setId("id1").setTask("task1").setDone(false);
        TodoItem item2 = new TodoItem().setId("id2").setTask("task2").setDone(false);
        Assertions.assertTrue(todoItemService.create(principal1, list.getId(), item1));
        Assertions.assertTrue(todoItemService.create(principal1, list.getId(), item2));
        List<TodoItem> todoItems = todoItemService.getAll(principal1);
        Assertions.assertEquals(todoItems.size(), 2);
        Assertions.assertTrue(todoItemService.delete(principal1, list.getId(), item2.getId()));
        Assertions.assertEquals(todoItemService.getAll(principal1).size(), 1);
    }

    @Test
    public void testCreateConflictDifferentTask() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(todoListService.create(principal1, list));
        TodoItem item1 = new TodoItem().setId("id").setTask("task1").setDone(false);
        TodoItem item2 = new TodoItem().setId("id").setTask("task2").setDone(false);
        Assertions.assertTrue(todoItemService.create(principal1, list.getId(), item1));
        ConflictException exception = Assertions.assertThrows(ConflictException.class,
                () -> todoItemService.create(principal1, list.getId(), item2));
        Assertions.assertEquals("A UNIQUE constraint failed: TodoItem already existed", exception.getMessage());
    }

    @Test
    public void testCreateDifferentUser() {
        TodoList list1 = new TodoList().setId("id1").setName("name1");
        Assertions.assertTrue(todoListService.create(principal1, list1));
        Assertions.assertTrue(todoListService.create(principal2, list1));
        TodoItem item = new TodoItem().setId("id").setTask("task").setDone(false);
        Assertions.assertTrue(todoItemService.create(principal1, list1.getId(), item));
        Assertions.assertTrue(todoItemService.create(principal2, list1.getId(), item));
    }

    @Test
    public void testCreateDifferentLists() {
        TodoList list1 = new TodoList().setId("id1").setName("name1");
        TodoList list2 = new TodoList().setId("id2").setName("name2");
        Assertions.assertTrue(todoListService.create(principal1, list1));
        Assertions.assertTrue(todoListService.create(principal1, list2));
        TodoItem item = new TodoItem().setId("id").setTask("task").setDone(false);
        Assertions.assertTrue(todoItemService.create(principal1, list1.getId(), item));
        Assertions.assertTrue(todoItemService.create(principal1, list2.getId(), item));
    }

    @Test
    public void testUpdateMissingTodoItem() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(todoListService.create(principal1, list));
        TodoItem item = new TodoItem().setId("id").setTask("task").setDone(false);
        Assertions.assertFalse(todoItemService.update(principal1, list.getId(), item));
    }

    @Test
    public void testUpdateTodoItem() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(todoListService.create(principal1, list));
        TodoItem item = new TodoItem().setId("id").setTask("task").setDone(false);
        Assertions.assertTrue(todoItemService.create(principal1, list.getId(), item));
        Optional<TodoItem> fetchedItem = todoItemService.get(principal1, list.getId(), item.getId());
        Assertions.assertTrue(fetchedItem.isPresent());
        item.setDone(true);
        Assertions.assertTrue(todoItemService.update(principal1, list.getId(), item));
        fetchedItem = todoItemService.get(principal1, list.getId(), item.getId());
        Assertions.assertTrue(fetchedItem.get().isDone());
    }

    @Test
    public void testUpdateExistsButSame() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(todoListService.create(principal1, list));
        TodoItem item = new TodoItem().setId("id").setTask("task").setDone(false);
        Assertions.assertTrue(todoItemService.create(principal1, list.getId(), item));
        Optional<TodoItem> fetchedItem = todoItemService.get(principal1, list.getId(), item.getId());
        Assertions.assertTrue(fetchedItem.isPresent());
        Assertions.assertFalse(todoItemService.update(principal1, list.getId(), item));
    }

    @Test
    public void testDeleteNotExistsTodoItem() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(todoListService.create(principal1, list));
        TodoItem item = new TodoItem().setId("id").setTask("task").setDone(false);
        Assertions.assertFalse(todoItemService.delete(principal1, list.getId(), item.getId()));
    }

    @Test
    public void testDeleteExistTodoItem() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(todoListService.create(principal1, list));
        TodoItem item = new TodoItem().setId("id").setTask("task").setDone(false);
        Assertions.assertTrue(todoItemService.create(principal1, list.getId(), item));
        Assertions.assertTrue(todoItemService.delete(principal1, list.getId(), item.getId()));
    }

    @Test
    public void testCreateThrowException() {
        TodoList list = new TodoList().setId("id").setName("name");
        TodoItem item = new TodoItem().setTask("task").setDone(false);
        Assertions.assertTrue(todoListService.create(principal1, list));
        ConstraintException exception = Assertions.assertThrows(ConstraintException.class, () -> todoItemService.create(principal1, list.getId(), item));
        Assertions.assertEquals("Failed to create todoItem: A NOT NULL constraint failed", exception.getMessage());
    }
}
