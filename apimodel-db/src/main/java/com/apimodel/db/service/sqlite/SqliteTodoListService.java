package com.apimodel.db.service.sqlite;

import com.apimodel.db.exceptions.ConflictException;
import com.apimodel.db.service.TodoListService;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoList;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
public class SqliteTodoListService implements TodoListService {

    private final DataSource dataSource;

    public SqliteTodoListService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<TodoList> get(RapidApiPrincipal principal, String id) {
        String sql = "SELECT * FROM todo_lists WHERE user_id = ? AND id = ?";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 0;
            ps.setString(++index, principal.getUser());
            ps.setString(++index, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new TodoList().setId(resultSet.getString("id")).setName(resultSet.getString("name")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch list"+ e.getMessage(), e);
        }
    }

    @Override
    public List<TodoList> getAll(RapidApiPrincipal principal) {
        String sql = "SELECT * FROM todo_lists WHERE user_id = ? ORDER BY name";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 0;
            ps.setString(++index, principal.getUser());
            List<TodoList> todoLists = new ArrayList<>();
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    todoLists.add(new TodoList().setId(resultSet.getString("id")).setName(resultSet.getString("name")));
                }
            }
            return todoLists;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch lists"+ e.getMessage(), e);
        }
    }

    @Override
    public boolean create(RapidApiPrincipal principal, TodoList todoList) {
        String sql = "INSERT INTO todo_lists (user_id, id, name) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 0;
            ps.setString(++index, principal.getUser());
            ps.setString(++index, todoList.getId());
            ps.setString(++index, todoList.getName());
            if (ps.executeUpdate() > 0) {
                connection.commit();
                return true;
            }
            return false;
        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                throw new ConflictException("Todo list already exists");
            }
            throw new RuntimeException("Failed to create list"+ e.getMessage(), e);
        }
    }

    @Override
    public boolean update(RapidApiPrincipal principal, TodoList todoList) {
        String sql = "UPDATE todo_lists SET name = ? WHERE user_id = ? AND id = ? AND name != ?";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            ps.setString(++paramIndex, todoList.getName());
            ps.setString(++paramIndex, principal.getUser());
            ps.setString(++paramIndex, todoList.getId());
            ps.setString(++paramIndex, todoList.getName());
            if (ps.executeUpdate() > 0) {
                connection.commit();
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to Update list"+ e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(RapidApiPrincipal principal, String id) {
        String sql = "DELETE FROM todo_lists WHERE user_id = ? AND id = ?";
        try (Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            ps.setString(++paramIndex, principal.getUser());
            ps.setString(++paramIndex, id);
            if (ps.executeUpdate() > 0) {
                connection.commit();
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete item "+ id +" from data base:" + e.getMessage(), e);
        }
    }

    @Override
    public int truncate() {
        String sql = "DELETE FROM todo_lists";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            int deleted = ps.executeUpdate();
            if (deleted> 0) {
                connection.commit();
            }
            return deleted;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to truncate database " + e.getMessage(), e);
        }
    }
}
