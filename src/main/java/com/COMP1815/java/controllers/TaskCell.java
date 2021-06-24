package com.COMP1815.java.controllers;

import com.COMP1815.kotlin.data.RefinedTask;
import com.COMP1815.kotlin.model.Task;
import javafx.scene.control.ListCell;

import java.util.Objects;
import java.util.stream.Collectors;

// Helper class
public class TaskCell extends ListCell<RefinedTask> {
    @Override
    protected void updateItem(RefinedTask item, boolean empty) {
        super.updateItem(item, empty);
        if (empty)
            return;

        String deps = item.getTask()
                .getDependencies()
                .stream()
                .map(it -> Objects.requireNonNull(Task.Companion.findTaskByUUID(it.toString())).getName())
                .collect(Collectors.joining(" & ", "[ ", " ]"));

        this.setText(item.getTask().getName().concat(" ").concat(deps));
    }

    public static class NormalTaskCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                String deps = item.getDependencies().stream()
                        .map(it -> Objects.requireNonNull(Task.Companion.findTaskByUUID(it.toString())).getName())
                        .collect(Collectors.joining(" & ", "[ ", " ]"));
                this.setText(item.getName().concat(" ").concat(deps));
            }
        }
    }
}