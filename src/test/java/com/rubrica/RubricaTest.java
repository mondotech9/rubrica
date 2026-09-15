package com.rubrica;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Test eseguibili con Java, senza librerie esterne. */
public class RubricaTest {
    public static void main(String[] args) throws Exception {
        Path cartella = Files.createTempDirectory("rubrica-test-");
        File file = cartella.resolve("informazioni.txt").toFile();
        try {
            Rubrica rubrica = apri(file);
            verifica(rubrica.size() == 0, "Avvio senza file");
            verifica(!file.exists(), "Il caricamento non crea il file");

            Persona steve = new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);
            rubrica.aggiungi(steve);
            PersoneTableModel tabella = new PersoneTableModel(rubrica);
            verifica(tabella.getRowCount() == 1, "Inserimento in tabella");
            verifica(tabella.getColumnCount() == 3, "Solo tre colonne");
            verifica(tabella.getValueAt(0, 2).equals("0612344"), "Zeri iniziali telefono");
            verifica(!tabella.isCellEditable(0, 0), "Modifica solo da editor");
            verificaPersona(apri(file).getPersona(0), steve);

            rubrica.aggiungi(new Persona("Bill", "Gates", "via Redmond 10", "06688989", 60));
            rubrica.aggiungi(new Persona("Babbo", "Natale", "via del Polo Nord", "00000111", 99));
            verifica(apri(file).size() == 3, "Tre persone dopo riapertura");
            rubrica.elimina(1);
            Persona modificata = new Persona("Nicolò", "Natale", "via Città 2", "+39 000111", 100);
            rubrica.modifica(1, modificata);
            verifica(tabella.getRowCount() == 2, "Eliminazione persona centrale");
            verifica(tabella.getValueAt(1, 0).equals("Nicolò"), "Modifica ultima persona");
            rubrica = apri(file);
            verificaPersona(rubrica.getPersona(0), steve);
            verificaPersona(rubrica.getPersona(1), modificata);
            String testo = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            verifica(testo.equals("Steve;Jobs;via Cupertino 13;0612344;56" + System.lineSeparator()
                    + "Nicolò;Natale;via Città 2;+39 000111;100" + System.lineSeparator()), "Codifica esatta del file");

            rubrica.elimina(1);
            rubrica.elimina(0);
            verifica(apri(file).size() == 0, "Eliminazione persistente di tutti i contatti");
            rubrica.aggiungi(new Persona("", "", "", "", 0));
            verificaPersona(apri(file).getPersona(0), new Persona("", "", "", "", 0));
            try {
                new Persona("Nome;errato", "", "", "", 10);
                throw new AssertionError("Separatore non rifiutato");
            } catch (IllegalArgumentException previsto) {
                // Il separatore non può essere inserito dentro un campo.
            }
            try {
                new Persona("", "", "", "", -1);
                throw new AssertionError("Età negativa non rifiutata");
            } catch (IllegalArgumentException previsto) {
                // Un'età deve essere non negativa.
            }
            Files.writeString(file.toPath(), "riga;non;valida", StandardCharsets.UTF_8);
            try {
                apri(file);
                throw new AssertionError("File non valido non segnalato");
            } catch (IOException previsto) {
                verifica(Files.readString(file.toPath()).equals("riga;non;valida"), "File errato non sovrascritto");
            }
            System.out.println("OK: inserimento, modifica, eliminazione, tabella, persistenza e validazione.");
        } finally {
            Files.deleteIfExists(file.toPath());
            Files.deleteIfExists(cartella);
        }
    }

    private static Rubrica apri(File file) throws IOException {
        return new Rubrica(new ArchivioPersone(file));
    }

    private static void verificaPersona(Persona attuale, Persona attesa) {
        verifica(attuale.getNome().equals(attesa.getNome()), "Nome");
        verifica(attuale.getCognome().equals(attesa.getCognome()), "Cognome");
        verifica(attuale.getIndirizzo().equals(attesa.getIndirizzo()), "Indirizzo");
        verifica(attuale.getTelefono().equals(attesa.getTelefono()), "Telefono");
        verifica(attuale.getEta() == attesa.getEta(), "Età");
    }

    private static void verifica(boolean condizione, String caso) {
        if (!condizione) throw new AssertionError(caso);
    }
}
