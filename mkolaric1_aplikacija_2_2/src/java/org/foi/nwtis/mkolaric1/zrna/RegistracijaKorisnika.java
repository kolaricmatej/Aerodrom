/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.zrna;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.mkolaric1.ejb.sb.AutentikacijaKorisnika;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.web.podaci.Korisnik;
import org.foi.nwtis.mkolaric1.ws.AIRPWS_Service;






/**
 *
 * @author Matej
 */
@Named(value = "registracijaKorisnika")
@SessionScoped
public class RegistracijaKorisnika implements Serializable {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/mkolaric1_aplikacija_1/AIRPWS.wsdl")
    private AIRPWS_Service service;

    @EJB
    private AutentikacijaKorisnika autentikacijaKorisnika;

    private String korisnickoIme;
    private String ime;
    private String prezime;
    private String lozinka;
    private String ponovljena;
    private RESTKorisnik rk;
    private String user;
    private String pass;
    private String url;
    private List<Korisnik> listaKorisnika;
    private BP_Konfiguracija bpk;
    private Konfiguracija konf;

    /**
     * Creates a new instance of RegistracijaKorisnika
     */
    public RegistracijaKorisnika() {
        rk = new RESTKorisnik();

    }

      public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getPonovljena() {
        return ponovljena;
    }

    public void setPonovljena(String ponovljena) {
        this.ponovljena = ponovljena;
    }
    
    public void registracija() {
        if (podudaranjeLozinke()) {
            if (!korisnickoIme.isEmpty() || !lozinka.isEmpty() || !ime.isEmpty() || !prezime.isEmpty()) {
                if (!provjeraImena(korisnickoIme)) {
                    if (dodajKorisnika(korisnickoIme, lozinka, ime, prezime)) {
                        FacesContext fc = FacesContext.getCurrentInstance();
                        String poruka = fc.getApplication().evaluateExpressionGet(fc, "Korisnik uspjesno dodan", String.class);
                        fc.addMessage(poruka, new FacesMessage(FacesMessage.SEVERITY_INFO, poruka, null));
                    }
                }
            }else{
                FacesContext fc = FacesContext.getCurrentInstance();
                String poruka = fc.getApplication().evaluateExpressionGet(fc, "Nisu uneseni svi potrebni elemetni", String.class);
                fc.addMessage(poruka, new FacesMessage(FacesMessage.SEVERITY_ERROR, poruka, null));
                }
        }
    }

    public boolean podudaranjeLozinke() {
        if (!lozinka.equals(ponovljena)) {
            FacesContext fc = FacesContext.getCurrentInstance();
            String poruka = fc.getApplication().evaluateExpressionGet(fc, "Lozinke nisu jednake", String.class);
            fc.addMessage(poruka, new FacesMessage(FacesMessage.SEVERITY_ERROR, poruka, null));
            return false;
        }
        return true;
    }

    public boolean dodajKorisnika(String username, String password, String ime, String prezime) {
        Korisnik k = new Korisnik(ime, prezime, username, password);
        String json = dohvatiJsonUKorisnika(k);
        String odgovor = rk.postJson(json);
        boolean provjera = provjeraOdgovora(odgovor);
        return provjera;
    }

    private String dohvatiJsonUKorisnika(Korisnik k) {
        Gson gson = new Gson();
        String json = gson.toJson(k);
        return json;
    }

    
    public boolean provjeraImena(String korisnickoIme) { 
        List<org.foi.nwtis.mkolaric1.ws.Korisnik> lista = dohvatiListuKorisnika("pero","123456");
        for (org.foi.nwtis.mkolaric1.ws.Korisnik k : lista) {
            if (k.getKorisnickoIme().equals(korisnickoIme)) {
                FacesContext fc = FacesContext.getCurrentInstance();
                String poruka = fc.getApplication().evaluateExpressionGet(fc, "Korisnicko ime vec postoji", String.class);
                fc.addMessage(poruka, new FacesMessage(FacesMessage.SEVERITY_ERROR, poruka, null));
                return true;
            }
        }
        return false;
    }

    private boolean provjeraOdgovora(String json) {
        JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
        String status = jo.get("status").getAsString();
        return status.equals("OK");
    }

    static class RESTKorisnik {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8084/mkolaric1_aplikacija_3_2/webresources";

        public RESTKorisnik() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("korisnici");
        }

        public String putJson(Object requestEntity, String korisnickoIme) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{korisnickoIme})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String postJson(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String getJsonAutentikacija(String korisnickoIme) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{korisnickoIme}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String getJson() throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close() {
            client.close();
        }
    }

    private java.util.List<org.foi.nwtis.mkolaric1.ws.Korisnik> dohvatiListuKorisnika(java.lang.String korisnickoIme, java.lang.String lozinka) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkolaric1.ws.AIRPWS port = service.getAIRPWSPort();
        return port.dohvatiListuKorisnika(korisnickoIme, lozinka);
    }

}
