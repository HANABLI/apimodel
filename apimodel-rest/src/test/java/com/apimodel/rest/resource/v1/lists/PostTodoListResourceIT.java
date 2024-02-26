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
import org.mockito.Mockito;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class PostTodoListResourceIT extends BaseResourceIT {
    private TodoListService todoListService;
    private RapidApiPrincipal principal = new RapidApiPrincipal("proxy-secret", "user", Subscription.BASIC);

    @Override
    protected Application configure() {
        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);
        todoListService = Mockito.mock(TodoListService.class);
        Mockito.when(serviceFactory.getTodoListService()).thenReturn(todoListService);
        return new ApiApplication(serviceFactory);
    }

    @Test
    public void testNoSecurityHeaders() {
        TodoList todoList = new TodoList().setId("list-id").setName("listName");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/v1/lists").request().post(entity);
        verifyErrorResponse(response, UNAUTHORIZED,
                "Missing security header: X-RapidAPI-Proxy-Secret");
        Mockito.verify(todoListService, Mockito.times(0)).create(any(), any());
    }

    @Test
    public void testProxySecretHeader() {
        TodoList todoList = new TodoList().setId("list-id").setName("listName");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .post(entity);
        verifyErrorResponse(response, UNAUTHORIZED, "Missing security header: X-RapidAPI-User");
        Mockito.verify(todoListService, Mockito.times(0)).create(any(), any());
    }

    @Test
    public void testProxyUserHeader() {
        TodoList todoList = new TodoList().setId("list-id").setName("listName");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .post(entity);
        verifyErrorResponse(response, UNAUTHORIZED, "Missing or invalid security header: X-RapidAPI-Subscription");
        Mockito.verify(todoListService, Mockito.times(0)).create(any(), any());
    }

    @Test
    public void testProxyInvalidSubscription() {
        TodoList todoList = new TodoList().setId("list-id").setName("listName");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), "invalid")
                .post(entity);
        verifyErrorResponse(response, UNAUTHORIZED, "Missing or invalid security header: X-RapidAPI-Subscription");
        Mockito.verify(todoListService, Mockito.times(0)).create(any(), any());
    }

    @Test
    public void testTodoListCreateFalse() {
        TodoList todoList = new TodoList().setId("list-id").setName("list Name");
        Mockito.when(todoListService.create(eq(principal), eq(todoList))).thenReturn(false);
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), Subscription.BASIC.name())
                .post(entity);
        verifyErrorResponse(response, BAD_REQUEST, "Invalid input, failed to insert into lists");
        Mockito.verify(todoListService, Mockito.times(1)).create(eq(principal), eq(todoList));
    }

    @Test
    public void testTodoListCreateTrue() {
        TodoList todoList = new TodoList().setId("list-id").setName("list Name");
        Entity<TodoList> entity = Entity.entity(todoList, MediaType.APPLICATION_JSON_TYPE);
        Mockito.when(todoListService.create(principal, todoList)).thenReturn(true);
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), Subscription.BASIC)
                .post(entity);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        Assertions.assertEquals(todoList ,response.readEntity(TodoList.class));
        Mockito.verify(todoListService, Mockito.times(1)).create(eq(principal), eq(todoList));
    }


}
