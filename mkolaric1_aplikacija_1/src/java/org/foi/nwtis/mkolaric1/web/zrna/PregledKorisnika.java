/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.mkolaric1.dretve.PreuzimanjeAviona;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.web.podaci.Korisnik;
import org.foi.nwtis.mkolaric1.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matej
 */
@Named(value = "pregledKorisnika")
@SessionScoped
public class PregledKorisnika implements Serializable {

    private BP_Konfiguracija bpk;
    private Konfiguracija konf;
    private String korisnickoIme;
    private String lozinka;
    private String url;
    private List<Korisnik> listaKoriniska;
    private int brojZapisa;

    /**
     * Creates a new instance of PregledKorisnika
     */
    public PregledKorisnika() {

    }

    private List<Korisnik> dohvatiKorisnike() {
        try {
            ucitajKonfiguraciju();
            listaKoriniska = new ArrayList<>();
            try (Connection con = DriverManager.getConnection(url, korisnickoIme, lozinka);
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM korisnici"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Korisnik k = new Korisnik();
                    k.setIme(rs.getString("firstName"));
                    k.setKorisnickoIme(rs.getString("username"));
                    k.setPrezime(rs.getString("lastName"));
                    k.setLozinka(rs.getString("password"));
                    listaKoriniska.add(k);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaKoriniska;
    }

    public void ucitajKonfiguraciju() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            SlusacAplikacije sa = new SlusacAplikacije();
            bpk = (BP_Konfiguracija) sa.getSc().getAttribute("BP_Konfig");
            url = bpk.getServerDatabase() + bpk.getUserDatabase();
            korisnickoIme = bpk.getUserUsername();
            lozinka = bpk.getUserPassword();
            konf = (Konfiguracija) sa.getSc().getAttribute("Konfiguracija");
            brojZapisa = Integer.parseInt(konf.dajPostavku("korisnik.brojRedaka"));
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public List<Korisnik> getListaKoriniska() {
        listaKoriniska = dohvatiKorisnike();
        return listaKoriniska;
    }

    public void setListaKoriniska(List<Korisnik> listaKoriniska) {
        this.listaKoriniska = listaKoriniska;
    }

    public int getBrojZapisa() {
        return brojZapisa;
    }

    public void setBrojZapisa(int brojZapisa) {
        this.brojZapisa = brojZapisa;
    }
}
