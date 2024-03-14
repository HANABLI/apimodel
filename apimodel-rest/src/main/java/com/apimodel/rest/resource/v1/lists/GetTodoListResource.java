package com.apimodel.rest.resource.v1.lists;

import com.apimodel.db.ServiceFactory;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoList;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;


@Path("/v1/lists/{listId}")
public class GetTodoListResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetTodoListResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public TodoList getTodoList(@Context SecurityContext securityContext, @PathParam("listId") String todoListId) {
        return serviceFactory.getTodoListService().get((RapidApiPrincipal) securityContext.getUserPrincipal(), todoListId)
                .orElseThrow(() -> new NotFoundException("List with id " + todoListId + " not found"));
    }

}
