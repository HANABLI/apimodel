package com.apimodel.rest.resource.v1;


import com.apimodel.db.ServiceFactory;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoList;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.*;


@Path("/v1/lists")
public class GetAllTodoListsResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllTodoListsResource.class);
    private final ServiceFactory serviceFactory;

    @Inject
    public GetAllTodoListsResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        LOGGER.info("Initializing service factory to: {}", serviceFactory);
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<TodoList> lists(@Context SecurityContext securityContext) {
        RapidApiPrincipal principal = (RapidApiPrincipal) securityContext.getUserPrincipal();
        return serviceFactory.getTodoListService().getAll(principal);
    }
}
