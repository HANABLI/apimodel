package com.apimodel.rest.resource.v1.lists;

import com.apimodel.db.ServiceFactory;
import com.apimodel.db.service.TodoListService;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import com.apimodel.model.TodoList;
import com.apimodel.rest.ApiApplication;
import com.apimodel.rest.resource.v1.BaseResourceIT;
import com.apimodel.rest.security.SecurityHeader;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.jvnet.hk2.annotations.Optional;
import org.mockito.Mockito;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class PutTodoListResourceIT extends BaseResourceIT {
    private TodoListService todoListService;
    private RapidApiPrincipal principal = new RapidApiPrincipal("proxy-secret", "user", Subscription.BASIC);

    @Override
    protected Application configure () {
        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);
        todoListService = Mockito.mock(TodoListService.class);
        Mockito.when(serviceFactory.getTodoListService()).thenReturn(todoListService);
        return new ApiApplication(serviceFactory);
    }

    @Test
    public void testNoProxyHeader() {
        TodoList todoList = new TodoList().setId("id").setName("name");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/v1/lists/id").request().put(entity);
        verifyErrorResponse(response, UNAUTHORIZED, "Missing security header: X-RapidAPI-Proxy-Secret");
        Mockito.verify(todoListService, Mockito.times(0)).update(any(), any());
    }

    @Test
    public void testProxySecretHeader() {
        TodoList todoList = new TodoList().setId("id").setName("name");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/v1/lists/id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret").put(entity);
        verifyErrorResponse(response, UNAUTHORIZED, "Missing security header: X-RapidAPI-User");
        Mockito.verify(todoListService, Mockito.times(0)).update(any(), any());
    }

    @Test
    public void testUserHeader() {
        TodoList todoList = new TodoList().setId("id").setName("name");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/v1/lists/id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .put(entity);
        verifyErrorResponse(response, UNAUTHORIZED, "Missing or invalid security header: X-RapidAPI-Subscription");
        Mockito.verify(todoListService, Mockito.times(0)).update(any(), any());
    }

    @Test
    public void testInvalidSubscriptionHeader() {
        TodoList todoList = new TodoList().setId("id").setName("name");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/v1/lists/id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), "Invalid")
                .put(entity);
        verifyErrorResponse(response, UNAUTHORIZED, "Missing or invalid security header: X-RapidAPI-Subscription");
        Mockito.verify(todoListService, Mockito.times(0)).update(any(), any());
    }

    @Test
    public void testUpdateTodoList() {
        TodoList todoList = new TodoList().setId("id").setName("name");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Mockito.when(todoListService.update(principal, todoList)).thenReturn(true);
        Response response = target("/v1/lists/id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), Subscription.BASIC)
                .put(entity);
        Assertions.assertEquals(todoList, response.readEntity(TodoList.class));
        Mockito.verify(todoListService, Mockito.times(1)).update(eq(principal), eq(todoList));
    }

    @Test
    public void testUpdateNonExistingTodoList() {
        TodoList todoList = new TodoList().setId("id").setName("name");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Mockito.when(todoListService.update(principal, todoList)).thenReturn(false);
        Response response =  target("/v1/lists/id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), Subscription.BASIC)
                .put(entity);
        verifyErrorResponse(response, NOT_FOUND, "Todo list with id id not found");
    }
}
