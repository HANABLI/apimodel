package com.apimodel.rest.resource.v1.lists;

import com.apimodel.db.ServiceFactory;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoList;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import org.apache.commons.text.StringEscapeUtils;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;


import java.util.Optional;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;



import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/lists")
public class PostTodoListResource {
    private final Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();
    private final ServiceFactory serviceFactory;

    @Inject
    public PostTodoListResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public TodoList postTodolist(@Context SecurityContext securityContext, TodoList todoList) {
        todoList.setId(Optional.ofNullable(todoList.getId()).map(StringEscapeUtils::escapeHtml4).orElseGet(() -> UUID.randomUUID().toString()));
        todoList.setName(Optional.ofNullable(todoList.getName()).map(StringEscapeUtils::escapeHtml4).orElse(null));

        Set<ConstraintViolation<TodoList>> violations = validator.validate(todoList);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; "));
            throw new BadRequestException(errorMessage);
        }

        RapidApiPrincipal principal = (RapidApiPrincipal) securityContext.getUserPrincipal();

        if (serviceFactory.getTodoListService().create(principal, todoList)) {
            return todoList;
        }
        throw new BadRequestException("Invalid input, failed to insert into lists");
    }

}
