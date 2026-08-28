package com.company;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AppGUI extends JFrame {

    private JLabel folderLabel;
    private JLabel fileLabel;
    private JTextArea mainArea;

    private NoteBook noteBook = new NoteBook();
    private String currentFullPath = "";
    private String currentFolderPath = "";

    public AppGUI() {
        setTitle("Финансовый Блокнот");
        setSize(860, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 246, 248));
        setLayout(null);

        Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

        JButton chooseFileBtn = new JButton("Выбрать файл");
        chooseFileBtn.setBounds(40, 25, 160, 35);
        chooseFileBtn.setFont(boldFont);
        chooseFileBtn.setBackground(new Color(52, 152, 219));
        chooseFileBtn.setForeground(Color.WHITE);
        chooseFileBtn.setFocusPainted(false);
        chooseFileBtn.setBorder(BorderFactory.createEmptyBorder());
        chooseFileBtn.addActionListener(e -> handleChooseFile());
        add(chooseFileBtn);

        fileLabel = new JLabel("Месяц: [Не выбран]");
        fileLabel.setBounds(230, 25, 220, 35);
        fileLabel.setFont(mainFont);
        fileLabel.setForeground(new Color(44, 62, 80));
        add(fileLabel);

        folderLabel = new JLabel("Год: [Не выбран]");
        folderLabel.setBounds(470, 25, 300, 35);
        folderLabel.setFont(mainFont);
        folderLabel.setForeground(new Color(44, 62, 80));
        add(folderLabel);

        JButton addButton = new JButton("Добавить");
        addButton.setBounds(40, 85, 160, 35);
        addButton.setFont(boldFont);
        addButton.setBackground(Color.WHITE);
        addButton.setForeground(new Color(46, 204, 113));
        addButton.setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 1));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> handleAddNote());
        add(addButton);

        JButton deleteButton = new JButton("Удалить");
        deleteButton.setBounds(40, 135, 160, 35);
        deleteButton.setFont(boldFont);
        deleteButton.setBackground(Color.WHITE);
        deleteButton.setForeground(new Color(231, 76, 60));
        deleteButton.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 1));
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> handleDeleteNote());
        add(deleteButton);

        JButton balanceButton = new JButton("Показать баланс");
        balanceButton.setBounds(40, 185, 160, 35);
        balanceButton.setFont(boldFont);
        balanceButton.setBackground(new Color(155, 89, 182));
        balanceButton.setForeground(Color.WHITE);
        balanceButton.setFocusPainted(false);
        balanceButton.setBorder(BorderFactory.createEmptyBorder());
        balanceButton.addActionListener(e -> handleShowBalance());
        add(balanceButton);

        mainArea = new JTextArea(" Пожалуйста, выберите файл месяца для начала работы...");
        mainArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        mainArea.setEditable(false);
        mainArea.setBackground(Color.WHITE);
        mainArea.setForeground(new Color(52, 73, 94));

        mainArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scrollPane = new JScrollPane(mainArea);
        scrollPane.setBounds(220, 85, 580, 265);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 223, 230), 1));
        add(scrollPane);

        JButton readButton = new JButton("Чтение");
        readButton.setBounds(460, 375, 160, 38);
        readButton.setFont(boldFont);
        readButton.setBackground(new Color(149, 165, 166));
        readButton.setForeground(Color.WHITE);
        readButton.setFocusPainted(false);
        readButton.setBorder(BorderFactory.createEmptyBorder());
        readButton.addActionListener(e -> handleRead());
        add(readButton);

        JButton writeButton = new JButton("Запись");
        writeButton.setBounds(640, 375, 160, 38);
        writeButton.setFont(boldFont);
        writeButton.setBackground(new Color(39, 174, 96));
        writeButton.setForeground(Color.WHITE);
        writeButton.setFocusPainted(false);
        writeButton.setBorder(BorderFactory.createEmptyBorder());
        writeButton.addActionListener(e -> handleWrite());
        add(writeButton);
    }

    private void handleChooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setCurrentDirectory(new File("."));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt");
        chooser.setFileFilter(filter);

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();

            currentFullPath = selectedFile.getAbsolutePath();
            currentFolderPath = selectedFile.getParent();

            String parentFolderName = selectedFile.getParentFile().getName();
            folderLabel.setText("Год (папка):  " + parentFolderName);

            String fileName = selectedFile.getName();
            if (fileName.endsWith(".txt")) {
                fileName = fileName.substring(0, fileName.length() - 4);
            }
            fileLabel.setText("Месяц (файл):  " + fileName);

            handleRead();
        }
    }

    private void refreshUI() {
        mainArea.setText(noteBook.printAll());
    }

    private void handleRead() {
        if (currentFullPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Сначала выберите файл месяца!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            noteBook.readFromFile(currentFullPath);
            refreshUI();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка чтения файла!\nОписание: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleWrite() {
        if (currentFullPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нечего сохранять. Выберите файл!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Files.createDirectories(Paths.get(currentFolderPath));
            noteBook.loadToFile(currentFullPath);
            JOptionPane.showMessageDialog(this, "Данные успешно сохранены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Не удалось записать данные:\n" + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddNote() {
        if (currentFullPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Сначала выберите рабочий файл!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userInput = JOptionPane.showInputDialog(this,
                "Введите заметку в формате 'Цена:Описание':",
                "Новая запись", JOptionPane.QUESTION_MESSAGE);
        if (userInput != null && !userInput.trim().isEmpty()) {
            noteBook.addNote(userInput.trim());
            refreshUI();
        }
    }

    private void handleDeleteNote() {
        if (noteBook.getBook().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Список пуст!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String inputId = JOptionPane.showInputDialog(this, "Введите ID строки для удаления:", "Удаление", JOptionPane.QUESTION_MESSAGE);
        try {
            if (inputId != null) {
                int id = Integer.parseInt(inputId.trim());
                noteBook.deleteNote(id);
                refreshUI();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Введите корректное число!", "Ошибка формата", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleShowBalance() {
        if (currentFullPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Выберите файл для расчета баланса.", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String message = String.format(
                "📈 Прибыль за месяц:  +%d\n📉 Траты за месяц:  %d\n─────────────────────────\n💰 Общий баланс:  %d",
                noteBook.getPositiveBalance(), noteBook.getNegativeBalance(), noteBook.getBalance()
        );
        JOptionPane.showMessageDialog(this, message, "Финансовый Отчет", JOptionPane.INFORMATION_MESSAGE);
    }
}