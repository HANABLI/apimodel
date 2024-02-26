package com.apimodel.db.service.sqlite;


import com.apimodel.db.DataSourceExtension;
import com.apimodel.db.exceptions.ConflictException;
import com.apimodel.db.service.TodoListService;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import com.apimodel.model.TodoList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.List;

@ExtendWith(DataSourceExtension.class)
public class SqliteTodoListServiceIT {

    private final TodoListService service;
    private final RapidApiPrincipal principal1 = new RapidApiPrincipal("proxy-secret", "user1", Subscription.BASIC);
    private final RapidApiPrincipal principal2 = new RapidApiPrincipal("proxy-secret", "user2", Subscription.BASIC);
    public SqliteTodoListServiceIT(DataSource dataSource) {
        this.service = new SqliteTodoListService(dataSource);
    }

    @Test
    public void testGetWithNoList() {
        Optional<TodoList> fetchedData = service.get(principal1, "id");
        Assertions.assertTrue(fetchedData.isEmpty());
    }

    @Test
    public void testGetListWrongId() {
        TodoList todoList = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(service.create(principal1, todoList));
        Optional<TodoList> fetchedData = service.get(principal1, "wrongId");
        Assertions.assertTrue(fetchedData.isEmpty());
    }

    @Test
    public void testGetWithWrongPrincipal() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(service.create(principal1, list));
        Optional<TodoList> fetched = service.get(principal2, list.getId());
        Assertions.assertTrue(fetched.isEmpty());
    }

    @Test
    public void testGetWithOneList() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(service.create(principal1, list));

        Optional<TodoList> fetched = service.get(principal1, list.getId());
        Assertions.assertTrue(fetched.isPresent());
        Assertions.assertEquals(list, fetched.get());
    }

    @Test
    public void testCreateConflict() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(service.create(principal1, list));
        ConflictException exception = Assertions.assertThrows(ConflictException.class,
                () -> service.create(principal1, list));
        Assertions.assertEquals("Todo list already exists", exception.getMessage());
    }

    @Test
    public void testGetAllNoLists() {
        List<TodoList> fetchedData = service.getAll(principal1);
        Assertions.assertTrue(fetchedData.isEmpty());
    }

    @Test
    public void testGetAllWithWrongPrincipal() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(service.create(principal1, list));

        List<TodoList> fetched = service.getAll(principal2);
        Assertions.assertTrue(fetched.isEmpty());
    }

    @Test
    public void testGetAllWithOneList() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(service.create(principal1, list));

        List<TodoList> fetched = service.getAll(principal1);
        Assertions.assertEquals(1, fetched.size());
        Assertions.assertEquals(list, fetched.get(0));
    }

    @Test
    public void testGetWithMultipleLists() {
        TodoList list1 = new TodoList().setId("id1").setName("name1");
        TodoList list2 = new TodoList().setId("id2").setName("name2");
        Assertions.assertTrue(service.create(principal1, list1));
        Assertions.assertTrue(service.create(principal1, list2));

        List<TodoList> fetched = service.getAll(principal1);
        Assertions.assertTrue(fetched.contains(list1));
        Assertions.assertTrue(fetched.contains(list2));
    }

    @Test
    public void testCreateConflictDifferentName() {
        TodoList list1 = new TodoList().setId("id").setName("name1");
        TodoList list2 = new TodoList().setId("id").setName("name2");
        Assertions.assertTrue(service.create(principal1, list1));
        ConflictException exception = Assertions.assertThrows(ConflictException.class,
                () -> service.create(principal1, list2));
        Assertions.assertEquals("Todo list already exists", exception.getMessage());
    }

    @Test
    public void testCreateConflictDifferentUser() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(service.create(principal1, list));
        Assertions.assertTrue(service.create(principal2, list));

        Optional<TodoList> fetched1 = service.get(principal1, list.getId());
        Optional<TodoList> fetched2 = service.get(principal2, list.getId());
        Assertions.assertTrue(fetched1.isPresent());
        Assertions.assertTrue(fetched2.isPresent());
    }

    @Test
    public void testUpdateMissingTodoList() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertFalse(service.update(principal1, list));
    }

    @Test
    public void testUpdateExistsButSame() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(service.create(principal1, list));
        Assertions.assertFalse(service.update(principal1, list));
    }

    @Test
    public void testUpdateDifferentName() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(service.create(principal1, list));
        list.setName("updatedName");
        Assertions.assertTrue(service.update(principal1, list));

        Optional<TodoList> fetched = service.get(principal1, list.getId());
        Assertions.assertTrue(fetched.isPresent());
        Assertions.assertEquals(list.getName(), fetched.get().getName());
    }

    @Test
    public void testDeleteExistedTodoList() {
        TodoList list = new TodoList().setId("id").setName("name");
        Assertions.assertTrue(service.create(principal1, list));
        Optional<TodoList> deleted = service.delete(principal1, list.getId());
        Assertions.assertTrue(deleted.isPresent());
        Optional<TodoList> fetched = service.get(principal1, list.getId());
        Assertions.assertTrue(fetched.isEmpty());
    }

    @Test
    public void testDeleteUnExistingToDoList() {
        TodoList list = new TodoList().setId("id").setName("name");

        Optional<TodoList> deleted = service.delete(principal1, list.getId());
        Assertions.assertTrue(deleted.isEmpty());
    }
}
