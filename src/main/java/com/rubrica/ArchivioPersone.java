package com.rubrica;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Vector;

public class ArchivioPersone {
    private final File file;

    public ArchivioPersone(File file) {
        this.file = file;
    }

    public Vector<Persona> carica() throws IOException {
        Vector<Persona> persone = new Vector<>();

        // Al primo avvio il file potrebbe non esistere: la rubrica è vuota.
        if (!file.exists()) {
            return persone;
        }

        try (Scanner scanner = new Scanner(file, "UTF-8")) {
            int numeroRiga = 0;
            while (scanner.hasNextLine()) {
                String riga = scanner.nextLine();
                numeroRiga++;
                // -1 mantiene anche i campi vuoti.
                String[] campi = riga.split(";", -1);
                if (campi.length != 5) {
                    throw new IOException("La riga " + numeroRiga + " deve contenere cinque campi.");
                }
                try {
                    int eta = Integer.parseInt(campi[4]);
                    Persona persona = new Persona(campi[0], campi[1], campi[2], campi[3], eta);
                    persone.add(persona);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Dati non validi alla riga " + numeroRiga + ".", e);
                }
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
        return persone;
    }

    public void salva(Vector<Persona> persone) throws IOException {
        // PrintStream riscrive tutto il file con il contenuto aggiornato della lista.
        try (PrintStream output = new PrintStream(file, "UTF-8")) {
            for (Persona persona : persone) {
                String riga = persona.getNome() + ";"
                        + persona.getCognome() + ";"
                        + persona.getIndirizzo() + ";"
                        + persona.getTelefono() + ";"
                        + persona.getEta();
                output.println(riga);
            }
            if (output.checkError()) {
                throw new IOException("Impossibile completare la scrittura del file.");
            }
        }
    }
}
