package com.rubrica;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class FinestraLogin extends JFrame {
    private final Utente utente;
    private final JTextField campoUtente = new JTextField(20);
    private final JPasswordField campoPassword = new JPasswordField(20);

    public FinestraLogin(Utente utente) {
        super("Login rubrica");
        this.utente = utente;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel campi = new JPanel(new GridLayout(2, 2, 10, 10));
        campi.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel labelUtente = new JLabel("Utente");
        labelUtente.setLabelFor(campoUtente);
        JLabel labelPassword = new JLabel("Password");
        labelPassword.setLabelFor(campoPassword);
        campi.add(labelUtente);
        campi.add(campoUtente);
        campi.add(labelPassword);
        campi.add(campoPassword);
        add(campi, BorderLayout.CENTER);

        JPanel pulsanti = new JPanel();
        JButton login = new JButton("LOGIN");
        login.addActionListener(event -> accedi());
        pulsanti.add(login);
        add(pulsanti, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(login);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void accedi() {
        String username = campoUtente.getText();
        String password = new String(campoPassword.getPassword());
        if (!utente.verificaCredenziali(username, password)) {
            JOptionPane.showMessageDialog(this, "login errato", "Errore", JOptionPane.ERROR_MESSAGE);
            campoPassword.setText("");
            campoPassword.requestFocusInWindow();
            return;
        }

        // La rubrica viene caricata e mostrata soltanto dopo un login corretto.
        try {
            ArchivioPersone archivio = new ArchivioPersone(new File("informazioni"));
            Rubrica rubrica = new Rubrica(archivio);
            FinestraRubrica finestra = new FinestraRubrica(rubrica);
            dispose();
            finestra.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Impossibile caricare la rubrica: " + e.getMessage(),
                    "Errore di apertura", JOptionPane.ERROR_MESSAGE);
        }
    }
}
