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
        File file = cartella.resolve("informazioni").toFile();
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
            String primo = Files.readString(new File(file, "Persona1.txt").toPath(), StandardCharsets.UTF_8);
            String secondo = Files.readString(new File(file, "Persona2.txt").toPath(), StandardCharsets.UTF_8);
            verifica(primo.equals("Steve;Jobs;via Cupertino 13;0612344;56" + System.lineSeparator()), "Primo file");
            verifica(secondo.equals("Nicolò;Natale;via Città 2;+39 000111;100" + System.lineSeparator()), "Secondo file");
            verifica(!new File(file, "Persona3.txt").exists(), "File in eccesso eliminato");

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
            Path fileErrato = new File(file, "Persona1.txt").toPath();
            Files.writeString(fileErrato, "riga;non;valida", StandardCharsets.UTF_8);
            try {
                apri(file);
                throw new AssertionError("File non valido non segnalato");
            } catch (IOException previsto) {
                verifica(Files.readString(fileErrato).equals("riga;non;valida"), "File errato non sovrascritto");
            }
            // Ripristina il file per verificare omonimi e ordinamento oltre Persona9.
            Files.writeString(fileErrato, "Mario;Rossi;via Roma;000;20" + System.lineSeparator());
            rubrica = apri(file);
            for (int i = 1; i < 12; i++) {
                rubrica.aggiungi(new Persona("Mario", "Rossi", "via Roma", "00" + i, 20 + i));
            }
            rubrica = apri(file);
            verifica(rubrica.size() == 12, "Omonimi in file separati");
            for (int i = 0; i < 12; i++) {
                verifica(rubrica.getPersona(i).getEta() == 20 + i, "Ordine numerico dei file");
            }
            Path nota = new File(file, "note.txt").toPath();
            Files.writeString(nota, "Da conservare");
            while (rubrica.size() > 0) rubrica.elimina(0);
            verifica(apri(file).size() == 0, "Tutti i file persona eliminati");
            verifica(Files.readString(nota).equals("Da conservare"), "File estranei conservati");
            Files.delete(nota);
            Files.delete(file.toPath());

            Path precedente = cartella.resolve("informazioni.txt");
            Files.writeString(precedente, "Steve;Jobs;via Cupertino 13;0612344;56" + System.lineSeparator());
            rubrica = apri(file);
            verificaPersona(rubrica.getPersona(0), steve);
            rubrica.aggiungi(new Persona("Mario", "Rossi", "", "001", 25));
            verifica(apri(file).size() == 2, "Passaggio dal vecchio file alla cartella");
            verifica(Files.exists(precedente), "Vecchio file conservato");
            rubrica.elimina(1);
            rubrica.elimina(0);
            verifica(apri(file).size() == 0, "Non reimporta vecchi contatti dopo eliminazione");
            Files.delete(precedente);

            Utente utente = new Utente("admin", "admin");
            verifica(utente.verificaCredenziali("admin", "admin"), "Login corretto");
            verifica(!utente.verificaCredenziali("admin", "errata"), "Password errata");
            verifica(!utente.verificaCredenziali("altro", "admin"), "Username errato");
            verifica(!utente.verificaCredenziali("", ""), "Credenziali vuote");
            System.out.println("OK: contatti, file separati, omonimi, ordine, eliminazione, compatibilità e login.");
        } finally {
            if (file.isDirectory()) {
                for (File contatto : file.listFiles()) Files.deleteIfExists(contatto.toPath());
            }
            Files.deleteIfExists(file.toPath());
            Files.deleteIfExists(cartella.resolve("informazioni.txt"));
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
