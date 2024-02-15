package com.apimodel.model;

import java.util.Objects;

public class TodoItem {
    private String id;
    private String tasck;
    private boolean done = false;

    public String getId() {
        return id;
    }

    public TodoItem setId(String id) {
        this.id = id;
        return this;
    }

    public String getTasck() {
        return tasck;
    }

    public TodoItem setTasck(String tasck) {
        this.tasck = tasck;
        return this;
    }

    public boolean isDone() {
        return done;
    }

    public TodoItem setDone(boolean done) {
        this.done = done;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoItem todoItem = (TodoItem) o;
        return done == todoItem.done && Objects.equals(id, todoItem.id) && Objects.equals(tasck, todoItem.tasck);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tasck, done);
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id='" + id + '\'' +
                ", tasck='" + tasck + '\'' +
                ", done=" + done +
                '}';
    }
}
