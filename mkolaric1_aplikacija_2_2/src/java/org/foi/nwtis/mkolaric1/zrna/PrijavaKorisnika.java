/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.zrna;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.mkolaric1.ejb.sb.AutentikacijaKorisnika;

/**
 *
 * @author Matej
 */
@Named(value = "prijavaKorisnika")
@SessionScoped
@ManagedBean
public class PrijavaKorisnika implements Serializable {

    @EJB
    private AutentikacijaKorisnika autentikacijaKorisnika;

    private String korisnickoIme = "";
    private String lozinka = "";
    private String status;

    /**
     * Creates a new instance of PrijavaKorisnika
     */
    public PrijavaKorisnika() {
    }

    public String prijaviKorisnika() {
        boolean korisnik = autentikacijaKorisnika.provjeriPostojanjeKorisnika(korisnickoIme, lozinka);
        if (korisnik) {
            try {
                HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                sesija.setAttribute("korisnickoIme", korisnickoIme);
                sesija.setAttribute("lozinka", lozinka);
                FacesContext.getCurrentInstance().getExternalContext().redirect("pregledKorisnika.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(PrijavaKorisnika.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FacesContext fc = FacesContext.getCurrentInstance();
            String poruka = fc.getApplication().evaluateExpressionGet(fc, "Neispravno korisnicko ime ili lozinka", String.class);
            fc.addMessage(poruka, new FacesMessage(FacesMessage.SEVERITY_ERROR, poruka, null));
        }
        return null;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
