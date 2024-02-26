package com.apimodel.rest.resource.v1.items;

import com.apimodel.db.ServiceFactory;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoItem;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Optional;
import java.util.UUID;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/lists/{listId}/items")
public class PostTodoItemResource {

    private final ServiceFactory serviceFactory;

    @Inject
    public PostTodoItemResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public TodoItem postTodoItem(@Context SecurityContext securityContext,
                                 @PathParam("listId") String listId,
                                 TodoItem todoItem) {
        RapidApiPrincipal principal = (RapidApiPrincipal) securityContext.getUserPrincipal();
        todoItem.setId(Optional.of(todoItem.getId()).orElseGet(() -> UUID.randomUUID().toString()));
        todoItem.setTask(Optional.of(todoItem.getTask()).orElse(null));

        serviceFactory.getTodoListService().get(principal, listId)
                .orElseThrow(() -> new NotFoundException("list with id "+listId+ "not found"));
        if (serviceFactory.getTodoItemService().create(principal, listId, todoItem)) {
            return todoItem;
        }
        throw new BadRequestException("Invalid input, failed to insert into items");
    }

}
