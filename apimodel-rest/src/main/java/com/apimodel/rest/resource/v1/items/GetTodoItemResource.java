package com.apimodel.rest.resource.v1.items;

import com.apimodel.db.ServiceFactory;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoItem;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("v1/lists/{listId}/items/{id}")
public class GetTodoItemResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetTodoItemResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public TodoItem getTodoItem(@Context SecurityContext securityContext,
                                @PathParam("listId") String todoListId,
                                @PathParam("id") String todoItemId) {
        RapidApiPrincipal principal = (RapidApiPrincipal) securityContext.getUserPrincipal();
        serviceFactory.getTodoListService().get(principal, todoListId)
                .orElseThrow(() -> new NotFoundException("List with id " + todoListId + " not found, item neither "));
        return serviceFactory.getTodoItemService().get(principal, todoListId, todoItemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + todoItemId + " not found in list with id " + todoListId + "."));
    }

}
