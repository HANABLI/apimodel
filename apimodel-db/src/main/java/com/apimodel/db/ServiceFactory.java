package com.apimodel.db;

import com.apimodel.db.service.TodoItemService;
import com.apimodel.db.service.TodoListService;

public interface ServiceFactory {
    TodoListService getTodoListService();
    TodoItemService getTodoItemService();
}
