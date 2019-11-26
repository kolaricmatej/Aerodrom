/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.web.podaci;

import java.sql.Timestamp;

/**
 *
 * @author Matej
 */
public class DnevnikZahtjeva {
    private String korisnickoIme;
    private String ipAdresa;
    private Timestamp vrijemePocetka;
    private long trajanjeObrade;
    private String vrstaZapisa;
    private String sadrzaj;
    private String odgovor;

    public DnevnikZahtjeva(String korisnickoIme, String ipAdresa, Timestamp vrijemePocetka, int trajanjeObrade, String vrstaZapisa, String sadrzaj, String odgovor) {
        this.korisnickoIme = korisnickoIme;
        this.ipAdresa = ipAdresa;
        this.vrijemePocetka = vrijemePocetka;
        this.trajanjeObrade = trajanjeObrade;
        this.vrstaZapisa = vrstaZapisa;
        this.sadrzaj = sadrzaj;
        this.odgovor=odgovor;
    }

    
    public DnevnikZahtjeva() {
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getIpAdresa() {
        return ipAdresa;
    }

    public void setIpAdresa(String ipAdresa) {
        this.ipAdresa = ipAdresa;
    }

    public Timestamp getVrijemePocetka() {
        return vrijemePocetka;
    }

    public void setVrijemePocetka(Timestamp vrijemePocetka) {
        this.vrijemePocetka = vrijemePocetka;
    }

    public long getTrajanjeObrade() {
        return trajanjeObrade;
    }

    public void setTrajanjeObrade(long trajanjeObrade) {
        this.trajanjeObrade = trajanjeObrade;
    }

    public String getVrstaZapisa() {
        return vrstaZapisa;
    }

    public void setVrstaZapisa(String vrstaZapisa) {
        this.vrstaZapisa = vrstaZapisa;
    }

    public String getSadrzaj() {
        return sadrzaj;
    }

    public void setSadrzaj(String sadrzaj) {
        this.sadrzaj = sadrzaj;
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }
    
    
}
