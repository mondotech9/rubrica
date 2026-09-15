package com.rubrica;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                File file = new File("informazioni.txt");
                ArchivioPersone archivio = new ArchivioPersone(file);
                Rubrica rubrica = new Rubrica(archivio);
                FinestraRubrica finestra = new FinestraRubrica(rubrica);
                finestra.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Impossibile caricare la rubrica: " + e.getMessage(),
                        "Errore di apertura", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
