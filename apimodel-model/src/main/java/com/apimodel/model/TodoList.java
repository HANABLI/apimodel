package com.apimodel.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class TodoList {
    @NotEmpty(message = "Todo list id cannot be empty, it should have at least one character")
    @NotNull(message = "Todo list id cannot be null")
    @Size(min = 1, max = 36, message = "Todo list id max length is 36 characters")
    private String id;
    @NotEmpty(message = "Todo list name cannot be empty, it should have at least one character")
    @NotNull(message = "Todo list name cannot be null")
    @Size(min = 1, max = 200, message = "Todo list name max length is 200 characters")
    private String name;

    @Schema(
            name = "id",
            title = "ID",
            description = """
                    The identifier used to uniquely represent this `TodoList`.
                    """,
            example = """
                    my-list-id\n
                    18d8cb73-bf95-479a-bb7e-2b2d7e0ac256
                    """
    )
    public String getId() {
        return id;
    }

    public TodoList setId(String id) {
        this.id = id;
        return this;
    }

    @Schema(
            name = "name",
            title = "Name",
            description = """
                    The name used to describe the contents of this `TodoList`.
                    """,
            example = """
                Sends Emails to costumers\n
                Go shopping
            """
    )
    public String getName() {
        return name;
    }

    public TodoList setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoList todoList = (TodoList) o;
        return Objects.equals(id, todoList.id) && Objects.equals(name, todoList.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "TodoList{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
