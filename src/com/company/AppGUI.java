package com.company;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class AppGUI extends JFrame {

    private final NoteBook noteBook = new NoteBook();
    private final JTextArea mainArea = new JTextArea();
    private final JLabel fileLabel = new JLabel("Месяц: [Не выбран]");
    private final JLabel folderLabel = new JLabel("Год: [Не выбран]");
    private Path currentFile;

    public AppGUI() {
        setTitle("Финансовый Блокнот");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(880, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildActionPanel(), BorderLayout.EAST);
        add(buildBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.add(styledButton("Выбрать файл", new Color(52, 152, 219), true, this::handleChooseFile));
        panel.add(fileLabel);
        panel.add(folderLabel);
        return panel;
    }

    private JScrollPane buildCenterPanel() {
        mainArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        mainArea.setEditable(false);
        mainArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        mainArea.setText("Пожалуйста, выберите файл месяца для начала работы...");
        return new JScrollPane(mainArea);
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 10));
        panel.add(styledButton("Добавить", new Color(46, 204, 113), false, this::handleAddNote));
        panel.add(styledButton("Удалить", new Color(231, 76, 60), false, this::handleDeleteNote));
        panel.add(styledButton("Показать баланс", new Color(155, 89, 182), true, this::handleShowBalance));
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.add(styledButton("Чтение", new Color(149, 165, 166), true, this::handleRead));
        panel.add(styledButton("Запись", new Color(39, 174, 96), true, this::handleWrite));
        return panel;
    }

    private JButton styledButton(String text, Color color, boolean filled, Runnable onClick) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        if (filled) {
            b.setBackground(color);
            b.setForeground(Color.WHITE);
            b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        } else {
            b.setBackground(Color.WHITE);
            b.setForeground(color);
            b.setBorder(BorderFactory.createLineBorder(color));
        }
        b.addActionListener(e -> onClick.run());
        return b;
    }

    private void handleChooseFile() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setFileFilter(new FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File selected = chooser.getSelectedFile();
        currentFile = selected.toPath();

        folderLabel.setText("Год (папка): " + selected.getParentFile().getName());
        fileLabel.setText("Месяц (файл): " + selected.getName().replaceFirst("\\.txt$", ""));
        handleRead();
    }

    private void handleRead() {
        if (currentFile == null) { warn("Сначала выберите файл месяца!"); return; }
        try {
            noteBook.readFromFile(currentFile);
            refresh();
        } catch (IOException e) {
            error("Ошибка чтения файла", e);
        }
    }

    private void handleWrite() {
        if (currentFile == null) { warn("Нечего сохранять. Выберите файл!"); return; }
        try {
            noteBook.writeToFile(currentFile);
            info("Данные успешно сохранены!");
        } catch (IOException e) {
            error("Не удалось записать данные", e);
        }
    }

    private void handleAddNote() {
        if (currentFile == null) { warn("Сначала выберите рабочий файл!"); return; }
        String input = JOptionPane.showInputDialog(this,
                "Введите заметку в формате 'Цена:Описание':", "Новая запись", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.isBlank()) return;
        try {
            noteBook.add(Note.parse(input.trim()));
            refresh();
        } catch (IllegalArgumentException e) {
            warn(e.getMessage());
        }
    }

    private void handleDeleteNote() {
        if (noteBook.isEmpty()) { warn("Список пуст!"); return; }
        String input = JOptionPane.showInputDialog(this, "Введите ID строки для удаления:", "Удаление", JOptionPane.QUESTION_MESSAGE);
        if (input == null) return;
        try {
            noteBook.removeAt(Integer.parseInt(input.trim()));
            refresh();
        } catch (NumberFormatException e) {
            warn("Введите корректное число!");
        } catch (IndexOutOfBoundsException e) {
            warn(e.getMessage());
        }
    }

    private void handleShowBalance() {
        if (currentFile == null) { warn("Выберите файл для расчета баланса."); return; }
        String message = String.format(
                "📈 Прибыль за месяц: +%d%n📉 Траты за месяц: %d%n─────────────────────────%n💰 Общий баланс: %d",
                noteBook.getIncome(), noteBook.getExpenses(), noteBook.getBalance());
        JOptionPane.showMessageDialog(this, message, "Финансовый Отчет", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refresh() {
        if (noteBook.isEmpty()) { mainArea.setText("Поле заметок пусто!"); return; }
        StringBuilder sb = new StringBuilder();
        int id = 0;
        for (Note note : noteBook.getNotes()) {
            sb.append('[').append(id++).append("] ").append(note.toRaw()).append('\n');
        }
        mainArea.setText(sb.toString());
    }

    private void warn(String msg) { JOptionPane.showMessageDialog(this, msg, "Внимание", JOptionPane.WARNING_MESSAGE); }
    private void info(String msg) { JOptionPane.showMessageDialog(this, msg, "Успех", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String title, Exception e) {
        JOptionPane.showMessageDialog(this, title + ":\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}