package com.rubrica;

import javax.swing.table.AbstractTableModel;

public final class PersoneTableModel extends AbstractTableModel {
    private final Rubrica rubrica;
    private final String[] colonne = {"Nome", "Cognome", "Telefono"};

    public PersoneTableModel(Rubrica rubrica) { this.rubrica = rubrica; }
    @Override public int getRowCount() { return rubrica.size(); }
    @Override public int getColumnCount() { return colonne.length; }
    @Override public String getColumnName(int colonna) { return colonne[colonna]; }
    @Override public Class<?> getColumnClass(int colonna) { return String.class; }
    @Override public Object getValueAt(int riga, int colonna) {
        Persona p = rubrica.getPersona(riga);
        switch (colonna) {
            case 0: return p.getNome();
            case 1: return p.getCognome();
            case 2: return p.getTelefono();
            default: throw new IndexOutOfBoundsException("Colonna: " + colonna);
        }
    }
    public void aggiorna() { fireTableDataChanged(); }
}
