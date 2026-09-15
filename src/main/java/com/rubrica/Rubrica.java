package com.rubrica;

import java.io.IOException;
import java.util.Vector;

public final class Rubrica {
    private final ArchivioPersone archivio;
    private Vector<Persona> persone;

    public Rubrica(ArchivioPersone archivio) throws IOException {
        this.archivio = archivio;
        persone = archivio.carica();
    }

    public int size() { return persone.size(); }
    public Persona getPersona(int indice) { return persone.get(indice); }

    public void aggiungi(Persona persona) throws IOException {
        Vector<Persona> aggiornate = new Vector<>(persone);
        aggiornate.add(persona);
        salva(aggiornate);
    }

    public void modifica(int indice, Persona persona) throws IOException {
        Vector<Persona> aggiornate = new Vector<>(persone);
        aggiornate.set(indice, persona);
        salva(aggiornate);
    }

    public void elimina(int indice) throws IOException {
        Vector<Persona> aggiornate = new Vector<>(persone);
        aggiornate.remove(indice);
        salva(aggiornate);
    }

    private void salva(Vector<Persona> aggiornate) throws IOException {
        archivio.salva(aggiornate);
        persone = aggiornate;
    }
}
