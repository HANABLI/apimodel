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
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.logging.LogManager;
import java.util.List;
import java.util.Arrays;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.mockito.ArgumentMatchers.eq;


public class GetAllTodoListsResourceIT extends BaseResourceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllTodoListsResourceIT.class);
    static {
        LogManager.getLogManager().reset();
    }
    private TodoListService todoListService;
    RapidApiPrincipal principal = new RapidApiPrincipal("proxy-secret", "user", Subscription.BASIC);
    @Override
    protected Application configure() {
        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);
        todoListService = Mockito.mock(TodoListService.class);
        Mockito.when(serviceFactory.getTodoListService()).thenReturn(todoListService);
        return new ApiApplication(serviceFactory);
    }



    @Test
    public void testNoSecurityHeaders() {
        Response response = target("/v1/lists").request().get();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing security header: X-RapidAPI-Proxy-Secret");
    }

    @Test
    public void testProxySecretHeaders() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .get();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing security header: X-RapidAPI-User");
    }

    @Test
    public void testProxySecretAndUserHeaders() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .get();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing or invalid security header: X-RapidAPI-Subscription");
    }

    @Test
    public void testInvalidSubscriptionHeaders() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), "invalid")
                .get();
        verifyErrorResponse(response, UNAUTHORIZED, "Missing or invalid security header: X-RapidAPI-Subscription");
    }

    @Test
    public void testEmptyTodoLists() {
        Mockito.when(todoListService.getAll(eq(principal))).thenReturn(Collections.emptyList());
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), Subscription.BASIC)
                .get();
        Assertions.assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
        Assertions.assertEquals(OK.getStatusCode(), response.getStatus());
        List<TodoList> results = response.readEntity(new GenericType<>() {});
        Assertions.assertTrue(results.isEmpty());
        verifyCorsHeaders(response);
        LOGGER.info("{}", response.getHeaders());
    }

    @Test
    public void testSomeTodoLists() {
        List<TodoList> lists = Arrays.asList(
                new TodoList().setId("1").setName("List_1"),
                new TodoList().setId("2").setName("List_2")
        );

        Mockito.when(todoListService.getAll(eq(principal))).thenReturn(lists);

        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), Subscription.BASIC.name())
                .get();
        Assertions.assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
        Assertions.assertEquals(OK.getStatusCode(), response.getStatus());
        List<TodoList> results = response.readEntity(new GenericType<>() {});
        Assertions.assertEquals(results, lists);
        verifyCorsHeaders(response);
    }

    @Test void testServiceException() {
        List<TodoList> lists = Arrays.asList(
                new TodoList().setId("1").setName("List_1"),
                new TodoList().setId("2").setName("List_2")
        );

        Mockito.when(todoListService.getAll(eq(principal))).thenThrow(new RuntimeException("Failed"));

        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), Subscription.BASIC.name())
                .get();
        verifyErrorResponse(response, INTERNAL_SERVER_ERROR, "Failed");
    }

    @Test
    public void testValidationError() {
        TodoList todoList = new TodoList().setId("aaaaaaaaaaaaaaaaaaaa").setName("List Name");
        RapidApiPrincipal principal = new RapidApiPrincipal("proxy-secret", "user", Subscription.BASIC);

        Entity<TodoList> entity = Entity.entity(todoList, APPLICATION_JSON_TYPE);
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), Subscription.BASIC)
                .post(entity);

    }

//    @Test void testOneTodoList() {
//        TodoList list = new TodoList().setId("1").setName("List");
//        Mockito.when(todoListService.get(principal, "1")).thenReturn(Optional.of(list));
//        Response response = target("/v1/lists/1").request()
//                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
//                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
//                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), Subscription.BASIC.name())
//                .get();
//        Assertions.assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
//        Assertions.assertEquals(OK_200.getStatusCode(), response.getStatus());
//        TodoList result = response.readEntity(new GenericType<>() {});
//        Assertions.assertEquals(result, list);
//        verifyCorsHeaders(response);
//    }



}
