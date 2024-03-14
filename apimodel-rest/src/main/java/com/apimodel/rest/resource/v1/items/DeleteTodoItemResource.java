package com.apimodel.rest.resource.v1.items;

import com.apimodel.db.ServiceFactory;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.TodoItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/v1/lists/{listId}/items/{id}")
public class DeleteTodoItemResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public DeleteTodoItemResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            tags = "Todo Items",
            summary = "Delete Item",
            description = """
                 Delete the specified `TodoItem` from the `TodoList`.\n
                 This endpoint:
                 * Fetches the specified `TodoItem`.
                 * Deletes the `TodoItem` if found.
                 * Returns the Deleted `TodoItem` to the caller.\n
                 This example shows how to use `curl` to perform the deletion:\n
                 ```bash
                 curl -sk https://localhost:8443/api/lists/list-id/items/item-id -XDELETE \\
                      -H "X-RapidAPI-Proxy-Secret: secret" \\
                      -H "X-RapidAPI-User: user" \\
                      -H "X-RapidAPI-Subscription: BASIC"
                 ```\n
                 After successfully deleting a `TodoItem`, this endpoint will return the `TodoItem` that was
                 deleted:\n
                 ```json
                 {
                    "id": "item-id",
                    "task": "the item task",
                    "done": true
                 }
                 ```
                 If a list with the specified `listId` does not exist, or if the item with the specified `id` does
                 not exist inside the list, this endpoint will return a `404 Not Found` response like this example:
                 ```json
                 {
                    "status": 404,
                    "message": "Item with id item-id not found in list with id list-id"
                 }
                 ```
                """,
            operationId = "delete-item",
            parameters = {
              @Parameter(
                      name = "listId",
                      in = ParameterIn.PATH,
                      description = "the unique id of the list containing the item to delete",
                      required = true,
                      schema = @Schema(implementation = String.class),
                      example = "list-id"
              ),
              @Parameter(
                      name = "todoItemId",
                      in = ParameterIn.PATH,
                      description = "the unique id of the item to delete",
                      schema = @Schema(implementation = String.class),
                      example = "item-id"
              )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "response description",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "Example TodoItem returned after successful item deletion",
                                    value = "{\"id\":my-item-id\", \"task\":\"Bla Bla Bla\", \"done\":true}",
                                    description = "Provides an example Error Response when the item is not found"
                            )
                            ,
                            schema = @Schema(
                                    implementation = TodoItem.class,
                                    name = "TodoItem",
                                    title = "The deleted TodoItem",
                                    description = "The deleted TodoItem as returned"
                            ))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "TodoItem not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            examples = @ExampleObject(
                                    name = "not found",
                                    summary = "Example ErrorResponse",
                                    value = "{\"status\":404, \"message\":\"The requested TodoItem was not found\"}",
                                    description = "Provides an example Error Response when the item is not found"
                            )
                            ,
                            schema = @Schema(
                                    implementation = TodoItem.class,
                                    name = "ErrorResponse",
                                    title = "The ErrorResponse",
                                    description = "The ErrorResponse indicating the problem"
                            ))),
            })
    public TodoItem deleteTodoItem(@Context SecurityContext securityContext,
                                   @PathParam("listId") String listId,
                                   @PathParam("id") String id) {
        RapidApiPrincipal principal = (RapidApiPrincipal) securityContext.getUserPrincipal();

        return serviceFactory.getTodoItemService().delete(principal, listId, id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found in list with id " + listId));

    }

}
