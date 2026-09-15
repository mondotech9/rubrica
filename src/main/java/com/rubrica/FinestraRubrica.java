package com.rubrica;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.*;

public final class FinestraRubrica extends JFrame {
    private final Rubrica rubrica;
    private final PersoneTableModel modello;
    private final JTable tabella;

    public FinestraRubrica(Rubrica rubrica) {
        super("Rubrica telefonica");
        this.rubrica = rubrica;
        modello = new PersoneTableModel(rubrica);
        tabella = new JTable(modello);
        tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabella.setFillsViewportHeight(true);
        tabella.setRowHeight(26);
        JScrollPane scorrimento = new JScrollPane(tabella);
        scorrimento.setPreferredSize(new Dimension(640, 400));
        add(scorrimento, BorderLayout.CENTER);
        JPanel pulsanti = new JPanel();
        JButton nuovo = new JButton("Nuovo");
        JButton modifica = new JButton("Modifica");
        JButton elimina = new JButton("Elimina");
        nuovo.addActionListener(event -> apriEditor(-1));
        modifica.addActionListener(event -> {
            int indice = selezione("modificare");
            if (indice >= 0) apriEditor(indice);
        });
        elimina.addActionListener(event -> elimina());
        pulsanti.add(nuovo);
        pulsanti.add(modifica);
        pulsanti.add(elimina);
        add(pulsanti, BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private int selezione(String azione) {
        int riga = tabella.getSelectedRow();
        if (riga < 0) {
            JOptionPane.showMessageDialog(this, "Per " + azione + " è necessario prima selezionare una persona.",
                    "Nessuna persona selezionata", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        return tabella.convertRowIndexToModel(riga);
    }

    private void apriEditor(int indice) {
        new EditorPersona(this, rubrica, indice, modello::aggiorna).setVisible(true);
    }

    private void elimina() {
        int indice = selezione("eliminare");
        if (indice < 0) return;
        Persona p = rubrica.getPersona(indice);
        int risposta = JOptionPane.showConfirmDialog(this,
                "Eliminare la persona " + p.getNome() + " " + p.getCognome() + "?",
                "Conferma eliminazione", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (risposta != JOptionPane.YES_OPTION) return;
        try {
            rubrica.elimina(indice);
            modello.aggiorna();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Errore di eliminazione", JOptionPane.ERROR_MESSAGE);
        }
    }
}
