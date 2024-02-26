package com.apimodel.db.service.sqlite;

import com.apimodel.db.exceptions.ConflictException;
import com.apimodel.db.exceptions.ConstraintException;
import com.apimodel.db.exceptions.MissingDataException;
import com.apimodel.db.service.TodoItemService;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class SqliteTodoItemService implements TodoItemService {

    private final DataSource dataSource;

    public SqliteTodoItemService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<TodoItem> get(RapidApiPrincipal principal, String listId, String id) {
        String sql = "SELECT * FROM todo_items WHERE user_id = ? AND list_id = ? AND id = ?";
        try (Connection  connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 0;
            ps.setString(++index, principal.getUser());
            ps.setString(++index, listId);
            ps.setString(++index, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if(resultSet.next()) {
                    return Optional.of(new TodoItem().setId(resultSet.getString("id"))
                            .setDone(resultSet.getBoolean("done"))
                            .setTask(resultSet.getString("task")));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to fetch item from db" + exception.getMessage(), exception);
        }
    }

    @Override
    public List<TodoItem> getAll(RapidApiPrincipal principal, String listId) {
        String sql = "SELECT * FROM todo_items WHERE user_id = ? AND list_id = ? ORDER BY done, task";
        try (Connection connection = dataSource.getConnection();
        PreparedStatement sp = connection.prepareStatement(sql)) {
            int index = 0;
            sp.setString(++index, principal.getUser());
            sp.setString(++index, listId);
            List<TodoItem> todoItems = new ArrayList<>();
            try (ResultSet resultSet = sp.executeQuery()){
                while (resultSet.next()) {
                    todoItems.add( new TodoItem()
                            .setId(resultSet.getString("id"))
                            .setTask(resultSet.getString("task"))
                            .setDone(resultSet.getBoolean("done")));
                }
            }
            return todoItems;
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to fetch todoItems " + exception.getMessage(), exception);
        }
    }

    @Override
    public boolean create(RapidApiPrincipal principal, String listId, TodoItem todoItem) {
        String sql = "INSERT INTO todo_items (user_id, list_id, id, task, done) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 0;
            ps.setString(++index, principal.getUser());
            ps.setString(++index, listId);
            ps.setString(++index, todoItem.getId());
            ps.setString(++index, todoItem.getTask());
            ps.setBoolean(++index, todoItem.isDone());
            if (ps.executeUpdate() > 0) {
                connection.commit();
                return true;
            }
            return false;
        } catch (SQLException exception) {
            if (exception.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                throw new ConflictException("A UNIQUE constraint failed: TodoItem already existed");
            } else if (exception.getMessage().contains("SQLITE_CONSTRAINT_NOTNULL")) {
                throw new ConstraintException("Failed to create todoItem: A NOT NULL constraint failed");
            }
            throw new RuntimeException("Failed to create todoItem" + exception.getMessage(), exception);
        }
    }

    @Override
    public boolean update(RapidApiPrincipal principal, String listId, TodoItem todoItem) {
        String sql = "UPDATE todo_items SET task = ?, done = ? WHERE user_id = ? AND list_id = ? AND id = ? AND (done != ? OR task != ?)";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 0;
            ps.setString(++index, todoItem.getTask());
            ps.setBoolean(++index, todoItem.isDone());
            ps.setString(++index, principal.getUser());
            ps.setString(++index, listId);
            ps.setString(++index, todoItem.getId());
            ps.setBoolean(++index, todoItem.isDone());
            ps.setString(++index, todoItem.getTask());
            if (ps.executeUpdate() > 0) {
                connection.commit();
                return true;
            }
            return false;
        } catch (SQLException exception) {
            if (exception.getMessage().contains("SQLITE")) {
                throw new MissingDataException("A UNIQUE constraint failed: TodoItem already existed");
            }
            throw new  RuntimeException("Failed to update todoItem", exception);
        }
    }

    @Override
    public Optional<TodoItem> delete(RapidApiPrincipal principal, String listId, String id) {
        String sql = "DELETE FROM todo_items WHERE user_id = ? AND list_id = ? AND id = ?";
        Optional<TodoItem> fetchedItem = get(principal, listId, id);
        if (fetchedItem.isPresent()) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement sp = connection.prepareStatement(sql)) {
                int index = 0;
                sp.setString(++index, principal.getUser());
                sp.setString(++index, listId);
                sp.setString(++index, id);
                if (sp.executeUpdate() > 0) {
                    connection.commit();
                }
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to delete todoItem", exception);
            }
        }
        return fetchedItem;
    }

    @Override
    public int truncate() {
        String sql = "DELETE FROM todo_items";
        try (Connection connection = dataSource.getConnection();
        PreparedStatement sp = connection.prepareStatement(sql)) {
            int delete = sp.executeUpdate();
            if (delete > 0) {
                connection.commit();
            }
            return delete;
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to truncate database", exception);
        }
    }
}
