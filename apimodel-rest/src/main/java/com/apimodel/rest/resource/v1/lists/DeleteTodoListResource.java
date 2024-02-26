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
public class DeleteTodoListResource {

    private final ServiceFactory serviceFactory;

    @Inject
    public DeleteTodoListResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @DELETE
    @Produces(APPLICATION_JSON)
    public TodoList deleteTodoList(@Context SecurityContext securityContext, @PathParam("id") String todoListId) {

        RapidApiPrincipal principal = (RapidApiPrincipal) securityContext.getUserPrincipal();
        return serviceFactory.getTodoListService().delete(principal, todoListId)
                .orElseThrow(() -> new NotFoundException("TodoList with id: "+ todoListId +" not found"));
    }
}
