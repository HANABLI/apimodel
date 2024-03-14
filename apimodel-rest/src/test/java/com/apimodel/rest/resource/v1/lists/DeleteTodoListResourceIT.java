package com.apimodel.rest.resource.v1.lists;

import com.apimodel.db.ServiceFactory;
import com.apimodel.db.service.TodoListService;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import com.apimodel.model.TodoList;
import com.apimodel.rest.ApiApplication;
import com.apimodel.rest.resource.v1.BaseResourceIT;
import com.apimodel.rest.security.SecurityHeader;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class DeleteTodoListResourceIT extends BaseResourceIT {

    private TodoListService todoListService ;
    private RapidApiPrincipal principal = new RapidApiPrincipal("proxy-secret", "user", Subscription.BASIC);

    @Override
    protected Application configure() {
        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);
        todoListService = Mockito.mock(TodoListService.class);
        Mockito.when(serviceFactory.getTodoListService()).thenReturn(todoListService);
        return new ApiApplication(serviceFactory);
    }

    @Test
    public void testNoProxySecretHeader() {
        Response response = target("v1/lists/list-id").request().delete();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing security header: X-RapidAPI-Proxy-Secret");
        Mockito.verify(todoListService, Mockito.times(0)).delete(any(), any());
    }

    @Test
    public void testProxySecretHeader() {
        Response response = target("v1/lists/list-id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .delete();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing security header: X-RapidAPI-User");
        Mockito.verify(todoListService, Mockito.times(0)).delete(any(), any());
    }

    @Test
    public void testUserHeader() {
        Response response = target("v1/lists/list-id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .delete();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing or invalid security header: X-RapidAPI-Subscription");
        Mockito.verify(todoListService, Mockito.times(0)).delete(any(), any());
    }

    @Test
    public void testInvalidSubscription() {
        Response response = target("v1/lists/list-id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), "invalid")
                .delete();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing or invalid security header: X-RapidAPI-Subscription");
        Mockito.verify(todoListService, Mockito.times(0)).delete(any(), any());
    }

    @Test
    public void testDeleteExistingTodoList() {
        TodoList list = new TodoList().setId("list-id").setName("name");
        Mockito.when(todoListService.delete(eq(principal), eq("list-id"))).thenReturn(Optional.of(list));
        Response response = target("/v1/lists/list-id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), Subscription.BASIC)
                .delete();
        Assertions.assertEquals(list, response.readEntity(TodoList.class));
        Mockito.verify(todoListService, Mockito.times(1)).delete(any(), any());
    }

    @Test
    public void testNotFoundTodoList() {
        Mockito.when(todoListService.delete(eq(principal), eq("list-id"))).thenReturn(Optional.ofNullable(null));
        Response response = target("/v1/lists/list-id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), Subscription.BASIC)
                .delete();
        verifyErrorResponse(response, NOT_FOUND, "TodoList with id: list-id not found");
        Mockito.verify(todoListService, Mockito.times(1)).delete(any(), any());
    }
}
