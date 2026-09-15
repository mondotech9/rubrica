package com.rubrica;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Vector;

/* Un file Persona1.txt, Persona2.txt, ecc. per ogni contatto. */
public class ArchivioPersone {
    private final File cartella;

    public ArchivioPersone(File cartella) {
        this.cartella = cartella;
    }

    public Vector<Persona> carica() throws IOException {
        Vector<Persona> persone = new Vector<>();
        if (!cartella.exists()) {
            // Compatibilità con la versione precedente: il vecchio file resta intatto.
            File vecchioFile = new File(cartella.getAbsoluteFile().getParentFile(), "informazioni.txt");
            if (vecchioFile.exists()) {
                persone = leggiFile(vecchioFile);
            }
            return persone;
        }

        for (File file : elencaFile()) {
            Vector<Persona> contenuto = leggiFile(file);
            if (contenuto.size() != 1) {
                throw new IOException(file.getName() + " deve contenere una sola persona.");
            }
            persone.add(contenuto.get(0));
        }
        return persone;
    }

    private File[] elencaFile() throws IOException {
        File[] files = cartella.listFiles((directory, nome) -> nome.matches("Persona[1-9][0-9]*\\.txt"));
        if (files == null) {
            throw new IOException("Impossibile leggere la cartella " + cartella);
        }
        // Prima i nomi più corti: Persona2 deve precedere Persona10.
        Arrays.sort(files, Comparator.comparingInt((File file) -> file.getName().length())
                .thenComparing(File::getName));
        return files;
    }

    private Vector<Persona> leggiFile(File file) throws IOException {
        Vector<Persona> persone = new Vector<>();
        try (Scanner scanner = new Scanner(file, "UTF-8")) {
            int numeroRiga = 0;
            while (scanner.hasNextLine()) {
                String riga = scanner.nextLine();
                numeroRiga++;
                String[] campi = riga.split(";", -1);
                if (campi.length != 5) {
                    throw new IOException("Cinque campi richiesti in " + file.getName() + ", riga " + numeroRiga);
                }
                try {
                    int eta = Integer.parseInt(campi[4]);
                    persone.add(new Persona(campi[0], campi[1], campi[2], campi[3], eta));
                } catch (IllegalArgumentException e) {
                    throw new IOException("Dati non validi in " + file.getName() + ", riga " + numeroRiga, e);
                }
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
        return persone;
    }

    public void salva(Vector<Persona> persone) throws IOException {
        if (!cartella.exists() && !cartella.mkdirs()) {
            throw new IOException("Impossibile creare la cartella " + cartella);
        }
        File[] vecchiFiles = elencaFile();
        Vector<String> nomiUsati = new Vector<>();

        for (int i = 0; i < persone.size(); i++) {
            String nomeFile = "Persona" + (i + 1) + ".txt";
            File file = new File(cartella, nomeFile);
            Persona persona = persone.get(i);
            try (PrintStream output = new PrintStream(file, "UTF-8")) {
                output.println(persona.getNome() + ";" + persona.getCognome() + ";"
                        + persona.getIndirizzo() + ";" + persona.getTelefono() + ";" + persona.getEta());
                if (output.checkError()) {
                    throw new IOException("Impossibile completare la scrittura di " + nomeFile);
                }
            }
            nomiUsati.add(nomeFile);
        }

        // Dopo un'eliminazione si rimuovono solo i file PersonaN.txt rimasti in più.
        for (File file : vecchiFiles) {
            if (!nomiUsati.contains(file.getName()) && !file.delete()) {
                throw new IOException("Impossibile eliminare " + file.getName());
            }
        }
    }
}
