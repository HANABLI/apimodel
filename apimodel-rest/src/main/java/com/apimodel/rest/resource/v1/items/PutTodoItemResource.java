package com.apimodel.rest.resource.v1.items;

import com.apimodel.db.ServiceFactory;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoItem;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/lists/{listId}/items/{id}")
public class PutTodoItemResource {

    private final ServiceFactory serviceFactory;

    @Inject
    public PutTodoItemResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @PUT
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public TodoItem putTodoItem(@Context SecurityContext securityContext,
                                @PathParam("listId") String listId,
                                @PathParam("id") String id,
                                TodoItem todoItem) {

        RapidApiPrincipal principal = (RapidApiPrincipal) securityContext.getUserPrincipal();
        serviceFactory.getTodoItemService().get(principal, listId, id)
                .orElseThrow(() -> new NotFoundException("List with id " + listId + " not found"));
        todoItem.setId(id);
        if (serviceFactory.getTodoItemService().update(principal, listId, todoItem)) {
            return todoItem;
        }
        throw new BadRequestException("Todo Item with id " + id + "not found");
    }

}
