package com.rubrica;

/** Credenziali di un utente dell'applicazione. */
public class Utente {
    private final String username;
    private final String password;

    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean verificaCredenziali(String usernameInserito, String passwordInserita) {
        return username.equals(usernameInserito) && password.equals(passwordInserita);
    }
}
