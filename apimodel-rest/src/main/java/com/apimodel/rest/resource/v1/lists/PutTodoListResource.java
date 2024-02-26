package com.apimodel.rest.resource.v1.lists;

import com.apimodel.db.ServiceFactory;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoList;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/lists/{id}")
public class PutTodoListResource {
    private final ServiceFactory serviceFactory;


    @Inject
    public PutTodoListResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public TodoList postTodoList(@Context SecurityContext securityContext,
                                 @PathParam("id") String listId,
                                 TodoList todoList) {
        todoList.setId(listId);

        RapidApiPrincipal principal = (RapidApiPrincipal) securityContext.getUserPrincipal();
        if (serviceFactory.getTodoListService().update(principal, todoList)) {
            return todoList;
        }
        throw new NotFoundException("Todo list with id " + listId + " not found");
    }
}
