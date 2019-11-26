/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.web.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matej
 */
public class Dnevnik {

    private String korisnickoIme;
    private String ipAdresa;
    private Timestamp vrijemePocetka;
    private long trajanjeObrade;
    private String url;

    public Dnevnik() {
    }

    public Dnevnik(String korisnickoIme, String ipAdresa, Timestamp vrijemePocetka, int trajanjeObrade, String url) {
        this.korisnickoIme = korisnickoIme;
        this.ipAdresa = ipAdresa;
        this.vrijemePocetka = vrijemePocetka;
        this.trajanjeObrade = trajanjeObrade;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
