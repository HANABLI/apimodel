package com.apimodel.db.service.sqlite;

import com.apimodel.db.service.TodoListService;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoList;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class SqliteTodoListService implements TodoListService {

    private final DataSource dataSource;

    public SqliteTodoListService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<TodoList> get(RapidApiPrincipal principal, String id) {
        return Optional.empty();
    }

    @Override
    public List<TodoList> getAll(RapidApiPrincipal principal) {
        return null;
    }

    @Override
    public boolean create(RapidApiPrincipal principal, TodoList todoList) {
        return false;
    }

    @Override
    public boolean update(RapidApiPrincipal principal, TodoList todoList) {
        return false;
    }

    @Override
    public boolean delete(RapidApiPrincipal principal, String id) {
        return false;
    }

    @Override
    public int truncate() {
        return 0;
    }
}
