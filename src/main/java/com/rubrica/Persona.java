package com.rubrica;

public final class Persona {
    private final String nome;
    private final String cognome;
    private final String indirizzo;
    private final String telefono;
    private final int eta;

    public Persona(String nome, String cognome, String indirizzo, String telefono, int eta) {
        this.nome = valida(nome);
        this.cognome = valida(cognome);
        this.indirizzo = valida(indirizzo);
        this.telefono = valida(telefono);
        if (eta < 0) throw new IllegalArgumentException("L'età deve essere un intero non negativo.");
        this.eta = eta;
    }

    private static String valida(String valore) {
        if (valore == null || valore.contains(";") || valore.contains("\n") || valore.contains("\r")) {
            throw new IllegalArgumentException("I campi non possono contenere punto e virgola o ritorni a capo.");
        }
        return valore;
    }

    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getIndirizzo() { return indirizzo; }
    public String getTelefono() { return telefono; }
    public int getEta() { return eta; }
}
