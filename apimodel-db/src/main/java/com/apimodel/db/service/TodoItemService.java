package com.apimodel.db.service;

import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoItem;
import java.util.List;
import java.util.Optional;

public interface TodoItemService {
    Optional<TodoItem> get(RapidApiPrincipal principal, String listId, String id);
    List<TodoItem> getAll(RapidApiPrincipal principal);
    boolean create(RapidApiPrincipal principal, String listId, TodoItem todoItem);
    boolean update(RapidApiPrincipal principal, String listId, TodoItem todoItem);
    boolean delete(RapidApiPrincipal principal, String listId, String id);
    int truncate();
}
