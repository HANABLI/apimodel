package com.apimodel.db.service.sqlite;

import com.apimodel.db.service.TodoItemService;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoItem;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class SqliteTodoItemService implements TodoItemService {

    private final DataSource dataSource;

    public SqliteTodoItemService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<TodoItem> get(RapidApiPrincipal principal, String listId, String id) {
        return Optional.empty();
    }

    @Override
    public List<TodoItem> getAll(RapidApiPrincipal principal) {
        return null;
    }

    @Override
    public boolean create(RapidApiPrincipal principal, String listId, TodoItem todoItem) {
        return false;
    }

    @Override
    public boolean update(RapidApiPrincipal principal, String listId, TodoItem todoItem) {
        return false;
    }

    @Override
    public boolean delete(RapidApiPrincipal principal, String listId, String id) {
        return false;
    }

    @Override
    public int truncate() {
        return 0;
    }
}
