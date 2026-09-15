package com.rubrica;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class EditorPersona extends JDialog {
    private final JTextField nome = new JTextField(24);
    private final JTextField cognome = new JTextField(24);
    private final JTextField indirizzo = new JTextField(24);
    private final JTextField telefono = new JTextField(24);
    private final JTextField eta = new JTextField(24);

    public EditorPersona(JFrame owner, Rubrica rubrica, int indice, Runnable dopoSalvataggio) {
        super(owner, "editor-persona", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel campi = new JPanel(new GridLayout(5, 2, 10, 10));
        campi.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        aggiungiCampo(campi, "Nome", nome);
        aggiungiCampo(campi, "Cognome", cognome);
        aggiungiCampo(campi, "Indirizzo", indirizzo);
        aggiungiCampo(campi, "Telefono", telefono);
        aggiungiCampo(campi, "Età", eta);
        if (indice >= 0) {
            Persona p = rubrica.getPersona(indice);
            nome.setText(p.getNome());
            cognome.setText(p.getCognome());
            indirizzo.setText(p.getIndirizzo());
            telefono.setText(p.getTelefono());
            eta.setText(Integer.toString(p.getEta()));
        }
        JButton salva = new JButton("Salva");
        salva.addActionListener(event -> {
            try {
                int anni;
                try {
                    anni = Integer.parseInt(eta.getText().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Inserire un'età intera non negativa.");
                }
                Persona p = new Persona(nome.getText(), cognome.getText(), indirizzo.getText(), telefono.getText(), anni);
                if (indice < 0) rubrica.aggiungi(p);
                else rubrica.modifica(indice, p);
                dopoSalvataggio.run();
                dispose();
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Errore di salvataggio", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton annulla = new JButton("Annulla");
        annulla.addActionListener(event -> dispose());
        JPanel pulsanti = new JPanel();
        pulsanti.add(salva);
        pulsanti.add(annulla);
        add(campi, BorderLayout.CENTER);
        add(pulsanti, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(salva);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void aggiungiCampo(JPanel panel, String titolo, JTextField campo) {
        JLabel label = new JLabel(titolo);
        label.setLabelFor(campo);
        campo.setName(titolo);
        panel.add(label);
        panel.add(campo);
    }
}
