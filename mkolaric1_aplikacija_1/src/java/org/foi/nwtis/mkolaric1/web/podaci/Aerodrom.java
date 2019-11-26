/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.web.podaci;

import org.foi.nwtis.rest.podaci.Lokacija;

/**
 * Klasa za dohvaćanje i pohranu podataka vezanih uz Aerodrom koju koristimo kao glavna klasa za pohranu u objekt
 * @author nwtis_1
 */
public class Aerodrom {
    private String icao;
    private String naziv;
    private String drzava;
    private Lokacija lokacija;

    public Aerodrom(String icao, String naziv, String drzava, Lokacija lokacija) {
        this.icao = icao;
        this.naziv = naziv;
        this.drzava = drzava;
        this.lokacija = lokacija;
    }

    public Aerodrom() {
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getDrzava() {
        return drzava;
    }

    public void setDrzava(String drzava) {
        this.drzava = drzava;
    }

    public Lokacija getLokacija() {
        return lokacija;
    }

    public void setLokacija(Lokacija lokacija) {
        this.lokacija = lokacija;
    }
    
}
