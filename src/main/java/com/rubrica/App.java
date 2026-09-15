package com.rubrica;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Utente dimostrativo per l'esercizio.
            Utente utente = new Utente("admin", "admin");
            FinestraLogin login = new FinestraLogin(utente);
            login.setVisible(true);
        });
    }
}
