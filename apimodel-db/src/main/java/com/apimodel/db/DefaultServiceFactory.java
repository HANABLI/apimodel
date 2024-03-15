package com.apimodel.db;

import com.apimodel.db.service.ApiKeyService;
import com.apimodel.db.service.TodoItemService;
import com.apimodel.db.service.TodoListService;
import com.apimodel.db.service.sqlite.SqliteApiKeyService;
import com.apimodel.db.service.sqlite.SqliteTodoItemService;
import com.apimodel.db.service.sqlite.SqliteTodoListService;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class DefaultServiceFactory implements ServiceFactory {
    private final TodoItemService todoItemService;
    private final TodoListService todoListService;

    private final ApiKeyService apiKeyService;

    public DefaultServiceFactory(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration")
                .load()
                .migrate();
        todoItemService = new SqliteTodoItemService(dataSource);
        todoListService = new SqliteTodoListService(dataSource);
        apiKeyService = new SqliteApiKeyService(dataSource);
    }


    @Override
    public TodoListService getTodoListService() {
        return todoListService;
    }

    @Override
    public TodoItemService getTodoItemService() {
        return todoItemService;
    }


    @Override
    public ApiKeyService getApiKeyService() {return apiKeyService; }
}
