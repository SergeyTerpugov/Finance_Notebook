package com.company;

import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NoteBook {
    private ArrayList<Note> book;

    public NoteBook() {
        this.book = new ArrayList<>();
    }

    public ArrayList<Note> getBook() {
        return this.book;
    }

    public void readFromFile(String nameFile) throws IOException { //чтение из файла
        Path path = Paths.get(nameFile);
        List<String> lines = Files.readAllLines(path);

        this.book.clear();
        for (String el : lines){
            if (!el.trim().isEmpty()){
                this.book.add(new Note(el));
            }
        }
    }

    public void loadToFile(String nameFile) throws IOException { //запись в файл
        Path path = Paths.get(nameFile);
        List<String> linesToSave = new ArrayList<>();

        for (Note el : this.book){
            linesToSave.add(el.getText());
        }

        Files.write(path, linesToSave);
    }

    public void addNote(String text){ //добавление заметки в книгу
        Note newNote = new Note(text);
        this.book.add(newNote);
    }

    public void deleteNote(int id){ //удаление заметки
        this.book.remove(id);
    }

    public String printAll(){
        if (this.book.isEmpty()) return "Поле заметок пусто!";

        StringBuilder allText = new StringBuilder();
        int id = 0;
        for (Note el : this.book){
            allText.append("[").append(id).append("] ").append(el.getText()).append("\n");
            id ++;
        }

        return allText.toString();
    }

    public int getBalance(){ //вывод итогового баланса
        int balance = 0;

        for (Note el : this.book){
            balance += el.getPrice();
        }

        return balance;
    }

    public int getNegativeBalance(){ //вывод трат
        int negBalance = 0;

        for (Note el : this.book){
            if (el.getPrice() < 0){
                negBalance += el.getPrice();
            }
        }

        return negBalance;
    }

    public int getPositiveBalance(){ //вывод прибыли
        int posBalance = 0;

        for (Note el : this.book){
            if (el.getPrice() > 0){
                posBalance += el.getPrice();
            }
        }

        return posBalance;
    }
}
