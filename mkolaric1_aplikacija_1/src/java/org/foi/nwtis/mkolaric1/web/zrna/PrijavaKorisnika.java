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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.foi.nwtis.mkolaric1.dretve.ObradaZahtjeva;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.web.podaci.Korisnik;
import org.foi.nwtis.mkolaric1.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matej
 */
@Named(value = "prijavaKorisnika")
@SessionScoped
public class PrijavaKorisnika implements Serializable {

    private String username;
    private String password;
    private Korisnik korisnik;
    private BP_Konfiguracija bpk;
    private Konfiguracija konf;
    private String status;
    private String korisnickoIme;
    private String lozinka;
    private String url;
    /**
     * Creates a new instance of PrijavaKorisnika
     */
    public PrijavaKorisnika() {
    }
    
    public String provjeraKorisnika(){
        if(postojiKorisnikUBazi(username, password)){
            FacesContext fc=FacesContext.getCurrentInstance();
            fc.getExternalContext().getSessionMap().put("korisnik", username);
            return "index.xhtml";
        }else{
            FacesContext fc = FacesContext.getCurrentInstance();
            String poruka = fc.getApplication().evaluateExpressionGet(fc, "Neispravno korisnicko ime ili lozinka", String.class);
            fc.addMessage(poruka, new FacesMessage(FacesMessage.SEVERITY_ERROR, poruka, null));
        }
        return "";
    }



    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }
    public boolean postojiKorisnikUBazi(String username, String password) {
        try {
            ucitajKonfiguraciju();
            try {
                Class.forName(bpk.getDriverDatabase());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            try (Connection con = DriverManager.getConnection(url, korisnickoIme, lozinka); 
                    PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM korisnici WHERE username=? and password=?")) {
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int broj = rs.getInt(1);
                    if (broj > 0) {
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }

    
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
