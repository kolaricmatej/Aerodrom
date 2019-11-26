/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.web.podaci;

/**
 *
 * @author Matej
 */
public class Naredbe {

   public String kreirajNaredbuAutentikacija(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + ";";
        return naredba;
    }
    public String kreirajNaredbuStanje(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; STANJE;";
        return naredba;
    }

    public String kreirajNaredbuKreni(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; KRENI;";
        return naredba;
    }

    public String kreirajNaredbuPauza(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; PAUZA;";
        return naredba;
    }

    public String kreirajNaredbuPasivno(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; PASIVNO;";
        return naredba;
    }

    public String kreirajNaredbuAktivno(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; AKTIVNO;";
        return naredba;
    }

    public String kreirajNaredbuStani(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; STANI;";
        return naredba;
    }

    public String kreirajNaredbuZaGrupuDodaj(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; GRUPA DODAJ;";
        return naredba;
    }

    public String kreirajNaredbuZaGrupuPrekid(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; GRUPA PREKID;";
        return naredba;
    }

    public String kreirajNaredbuZaGrupuKreni(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; GRUPA KRENI;";
        return naredba;
    }

    public String kreirajNaredbuZaGrupuPauza(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; GRUPA PAUZA;";
        return naredba;
    }

    public String kreirajNaredbuZaGrupuStanje(String korisnickoIme, String lozinka) {
        String naredba = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; GRUPA STANJE;";
        return naredba;
    }
}
