/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;

import org.foi.nwtis.mkolaric1.web.podaci.Naredbe;
import org.foi.nwtis.mkolaric1.web.pomocno.SlanjePoruka;

/**
 *
 * @author Matej
 */
@Named(value = "pregledStanja")
@SessionScoped
public class PregledStanja implements Serializable {

    private String statusPosluzitelja = "Čekanje na zahtjev!";
    private String statusGrupe = "čekanje na zahtjev";
    private String provedenaAkcija = "";
    private String provedenaAkcija2="";
    private Naredbe naredbe;
    private String korisnickoIme;
    private String lozinka;
    private BP_Konfiguracija bpk;
    private Konfiguracija konf;
    private int port;
    private SlanjePoruka sp;

    /**
     * Creates a new instance of PregledStanja
     */
    public PregledStanja() {

        FacesContext fc = FacesContext.getCurrentInstance();
        korisnickoIme = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        lozinka = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        sp = new SlanjePoruka();
    }
    

    public String getStatusPosluzitelja() {
        return statusPosluzitelja;
    }

    public void setStatusPosluzitelja(String statusPosluzitelja) {
        this.statusPosluzitelja = statusPosluzitelja;
    }

    public void stanjePosluzitelj() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String naredba = naredbe.kreirajNaredbuStanje(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija = odgovor;
    }

     public void autentikacijaKorisnika() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String naredba = naredbe.kreirajNaredbuAutentikacija(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija = odgovor;
    }
    
    public void kreniPosluzitelj() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String naredba = naredbe.kreirajNaredbuKreni(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija = odgovor;
    }

    public void pauzaPosluzitelj() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuPauza(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija = odgovor;
    }

    public void pasivnoPosluzitelj() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuPasivno(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija = odgovor;
    }

    public void aktivnoPosluzitelj() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuAktivno(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija = odgovor;
    }

    public void staniPosluzitelj() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuStani(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija = odgovor;
    }

    public void dodajGrupa() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuZaGrupuDodaj(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija2 = odgovor;
    }

    public void prekidGrupa() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuZaGrupuPrekid(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija2 = odgovor;
    }

    public void kreniGrupa() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuZaGrupuKreni(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija2 = odgovor;
    }

    public void pauzaGrupa() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuZaGrupuPauza(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija2 = odgovor;
    }

    public void stanjeGrupa() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuZaGrupuStanje(korisnickoIme, lozinka);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija2 = odgovor;
    }

    public String getProvedenaAkcija() {
        return provedenaAkcija;
    }

    public void setProvedenaAkcija(String provedenaAkcija) {
        this.provedenaAkcija = provedenaAkcija;
    }

    public String getStatusGrupe() {
        return statusGrupe;
    }

    public void setStatusGrupe(String statusGrupe) {
        this.statusGrupe = statusGrupe;
    }

    public String getProvedenaAkcija2() {
        return provedenaAkcija2;
    }

    public void setProvedenaAkcija2(String provedenaAkcija2) {
        this.provedenaAkcija2 = provedenaAkcija2;
    }
}
