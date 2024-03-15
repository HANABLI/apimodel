package com.apimodel.rest.resource.v1.lists;

import com.apimodel.db.ServiceFactory;
import com.apimodel.db.service.TodoListService;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import com.apimodel.model.TodoList;
import com.apimodel.model.config.ConfigKey;
import com.apimodel.rest.ApiApplication;
import com.apimodel.rest.resource.v1.BaseResourceIT;
import com.apimodel.rest.security.SecurityHeader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Properties;

import static jakarta.ws.rs.core.Response.Status.*;
import static org.mockito.ArgumentMatchers.any;

public class GetTodoListResourceIT extends BaseResourceIT {

    private TodoListService todoListService;

    private RapidApiPrincipal principal = new RapidApiPrincipal("proxy-secret", "user", Subscription.BASIC);

    @Override
    protected Application configure() {
        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);
        todoListService = Mockito.mock(TodoListService.class);
        Mockito.when(serviceFactory.getTodoListService()).thenReturn(todoListService);

        Properties configProperties = new Properties();
        configProperties.setProperty(ConfigKey.RAPIDAPI_PROXY_SECRET.getKey(), "proxy-secret");
        Config config = ConfigFactory.parseProperties(configProperties);

        return new ApiApplication(config, serviceFactory);
    }

    @Test
    public void testNoProxySecretHeader() {
        Response response = target("/v1/lists/list-id").request().get();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing security header: X-RapidAPI-Proxy-Secret");
        Mockito.verify(todoListService, Mockito.times(0)).get(any(), any());
    }

    @Test
    public void testProxySecretHeader() {
        TodoList todoList = new TodoList().setId("id").setName("name");
        Mockito.when(todoListService.get(principal, "id")).thenReturn(Optional.of(todoList));
        Response response = target("/v1/lists/list-id").request().header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret").get();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing security header: X-RapidAPI-User");
        Mockito.verify(todoListService, Mockito.times(0)).get(any(), any());
    }

    @Test
    public void testProxyUserHeader() {
        Response response = target("/v1/lists/list-id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .get();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing or invalid security header: X-RapidAPI-Subscription");
        Mockito.verify(todoListService, Mockito.times(0)).get(any(), any());
    }

    @Test
    public void testInvalidProxySubscription() {
        Response response = target("/v1/lists/list-id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), "invalid")
                .get();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing or invalid security header: X-RapidAPI-Subscription");
        Mockito.verify(todoListService, Mockito.times(0)).get(any(), any());
    }

    @Test
    public void testGetNotFoundTodoList() {
        Mockito.when(todoListService.get(principal, "list-id")).thenReturn(Optional.ofNullable(null));
        Response response = target("/v1/lists/list-id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), Subscription.BASIC)
                .get();
        verifyErrorResponse(response, NOT_FOUND, "List with id list-id not found");
        Mockito.verify(todoListService, Mockito.times(1)).get(any(), any());
    }

    @Test
    public void testGetExistedTodoList() {
        TodoList list = new TodoList().setId("id").setName("Name");
        Mockito.when(todoListService.get(principal, "id")).thenReturn(Optional.of(list));
        Response response = target("/v1/lists/id").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), Subscription.BASIC)
                .get();
        Assertions.assertEquals(list, response.readEntity(TodoList.class));
        Mockito.verify(todoListService, Mockito.times(1)).get(any(), any());
    }

}
