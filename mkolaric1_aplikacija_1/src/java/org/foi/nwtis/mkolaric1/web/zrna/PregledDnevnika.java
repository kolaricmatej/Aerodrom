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
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.web.podaci.DnevnikZahtjeva;
import org.foi.nwtis.mkolaric1.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matej
 */
@Named(value = "pregledDnevnika")
@SessionScoped
public class PregledDnevnika implements Serializable {

    private BP_Konfiguracija bpk;
    private Konfiguracija konf;
    private int brojZapisa;
    private List<DnevnikZahtjeva> listaDnevnika;
    private String korisnickoIme;
    private String lozinka;
    private String url;

    /**
     * Creates a new instance of PregledDnevnika
     */
    public PregledDnevnika() {
        
    }
    private List<DnevnikZahtjeva> dohvatiZapiseDnevnika(){
        try {
            ucitajKonfiguraciju();
            listaDnevnika=new ArrayList<>();
            try (Connection con = DriverManager.getConnection(url, korisnickoIme, lozinka); 
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM dnevnik");
                    ResultSet rs=ps.executeQuery();) {
                while(rs.next()){
                    DnevnikZahtjeva dz=new DnevnikZahtjeva();
                    dz.setKorisnickoIme(rs.getString("korisnickoIme"));
                    dz.setIpAdresa(rs.getString("ipAdresa"));
                    dz.setVrijemePocetka(rs.getTimestamp("vrijemePocetka"));
                    dz.setTrajanjeObrade(rs.getLong("trajanjeObrade"));
                    dz.setVrstaZapisa(rs.getString("vrstaZapisa"));
                    dz.setSadrzaj(rs.getString("sadrzaj"));
                    dz.setOdgovor(rs.getString("odgovor"));
                    listaDnevnika.add(dz);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaDnevnika;
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
    public int getBrojZapisa() {
        return brojZapisa;
    }

    public void setBrojZapisa(int brojZapisa) {
        this.brojZapisa = brojZapisa;
    }

    public List<DnevnikZahtjeva> getListaDnevnika() {
        listaDnevnika=dohvatiZapiseDnevnika();
        return listaDnevnika;
    }

    public void setListaDnevnika(List<DnevnikZahtjeva> listaDnevnika) {
        this.listaDnevnika = listaDnevnika;
    }
}
