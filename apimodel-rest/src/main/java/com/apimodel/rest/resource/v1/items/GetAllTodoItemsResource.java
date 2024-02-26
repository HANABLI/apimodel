package com.apimodel.rest.resource.v1.items;

import com.apimodel.db.ServiceFactory;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoItem;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/lists/{listId}/items")
public class GetAllTodoItemsResource {

    private final ServiceFactory serviceFactory;

    @Inject
    public GetAllTodoItemsResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<TodoItem> getAllItems(@Context SecurityContext securityContext, @PathParam("listId") String listId) {
        RapidApiPrincipal principal = (RapidApiPrincipal) securityContext.getUserPrincipal();

        return serviceFactory.getTodoItemService().getAll(principal, listId);
    }
}
