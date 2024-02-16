package com.apimodel.db;

import com.apimodel.db.service.TodoItemService;
import com.apimodel.db.service.TodoListService;
import com.apimodel.db.service.sqlite.SqliteTodoItemService;
import com.apimodel.db.service.sqlite.SqliteTodoListService;

import javax.sql.DataSource;

public class DefaultServiceFactory implements ServiceFactory {
    private final TodoItemService todoItemService;
    private final TodoListService todoListService;

    public DefaultServiceFactory(DataSource dataSource) {

        todoItemService = new SqliteTodoItemService(dataSource);
        todoListService = new SqliteTodoListService(dataSource);
    }


    @Override
    public TodoListService getTodoListService() {
        return todoListService;
    }

    @Override
    public TodoItemService getTodoItemService() {
        return todoItemService;
    }
}