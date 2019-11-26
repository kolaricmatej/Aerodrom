package org.foi.nwtis.mkolaric1.zrna;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import javax.faces.context.FacesContext;

/**
 * Klasa koju koristimo kod prijevoda aplikacije za promjenu jezika
 * @author Matej
 */
@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable {

    private Locale locale;
    private String language;

    public Lokalizacija() {
        this.locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();

    }

    public Locale getLocale() {
        return locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public void setLanguage(String language) {
        this.locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

}
