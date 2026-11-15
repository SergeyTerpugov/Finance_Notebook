package com.company;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteBook {
    private final List<Note> notes = new ArrayList<>();

    public void add(Note note) { notes.add(note); }

    public void removeAt(int index) {
        if (index < 0 || index >= notes.size()) {
            throw new IndexOutOfBoundsException("Нет записи с ID " + index);
        }
        notes.remove(index);
    }

    public List<Note> getNotes() { return Collections.unmodifiableList(notes); }

    public boolean isEmpty() { return notes.isEmpty(); }

    public int getBalance() {
        return notes.stream().mapToInt(Note::getPrice).sum();
    }

    public int getIncome() {
        return notes.stream().mapToInt(Note::getPrice).filter(p -> p > 0).sum();
    }

    public int getExpenses() {
        return notes.stream().mapToInt(Note::getPrice).filter(p -> p < 0).sum();
    }

    public void clear() { notes.clear(); }

    public void readFromFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        clear();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            try {
                add(Note.parse(trimmed));
            } catch (IllegalArgumentException e) {
                System.err.println("Пропущена некорректная строка: " + trimmed);
            }
        }
    }

    public void writeToFile(Path file) throws IOException {
        List<String> lines = notes.stream().map(Note::toRaw).toList();
        Files.write(file, lines, StandardCharsets.UTF_8);
    }
}