package org.example.api.controllers;

import org.example.api.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractController<T> {

    protected abstract String getFilePath();

    protected abstract Class<T> getType();

    protected abstract int getID(T item);

    protected List<T> getAll() {
        try {
            Path path = Path.of(getFilePath());

            if (!Files.exists(path)) {
                createEmptyFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<T> items = JsonParser.deserializeList(json, getType());
            return items != null ? items : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading file.");
            return new ArrayList<>();
        }
    }

    protected void writeAll(List<T> items) {
        try {
            Path path = Path.of(getFilePath());

            if (!Files.exists(path)) {
                createEmptyFile(path);
            }

            String json = JsonParser.serialize(items);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
    }

    protected int getNextID(List<T> items) {
        int maxID = 0;

        for (T item : items) {
            if (getID(item) > maxID) {
                maxID = getID(item);
            }
        }

        return maxID + 1;
    }

    private void createEmptyFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}