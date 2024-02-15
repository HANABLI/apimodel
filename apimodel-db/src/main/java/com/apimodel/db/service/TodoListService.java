package com.apimodel.db.service;

import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoList;

import java.util.List;
import java.util.Optional;

public interface TodoListService {
    Optional<TodoList> get(RapidApiPrincipal principal, String id);
    List<TodoList> getAll(RapidApiPrincipal principal);
    boolean create(RapidApiPrincipal principal, TodoList todoList);
    boolean update(RapidApiPrincipal principal, TodoList todoList);
    boolean delete(RapidApiPrincipal principal, String id);
    int truncate();

}
