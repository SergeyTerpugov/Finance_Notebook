package com.company;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppGUI app = new AppGUI();
            app.setVisible(true);
        });
    }
}
