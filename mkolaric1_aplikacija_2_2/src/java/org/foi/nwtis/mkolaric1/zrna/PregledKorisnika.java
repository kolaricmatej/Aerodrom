/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.zrna;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.foi.nwtis.mkolaric1.web.podaci.Korisnik;

/**
 *
 * @author Matej
 */
@Named(value = "pregledKorisnika")
@SessionScoped
public class PregledKorisnika implements Serializable {

    private String korisnickoIme;
    private String lozinka;
    private String ponovljena;
    private String ime;
    private String prezime;
    private List<Korisnik> listaKoriniska;
    private int idKorisnika;
    private RESTKorisnik rk;
    private String korime;
    private String loz;

    /**
     * Creates a new instance of PregledKorisnika
     */
    public PregledKorisnika() {
        rk = new RESTKorisnik();
          FacesContext fc = FacesContext.getCurrentInstance();
        korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");

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

    public List<Korisnik> getListaKoriniska() {
        
        listaKoriniska = dohvatiKorisnike();
        return listaKoriniska;
    }

    public void setListaKoriniska(List<Korisnik> listaKoriniska) {
        this.listaKoriniska = listaKoriniska;
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

    public String getPonovljena() {
        return ponovljena;
    }

    public void setPonovljena(String ponovljena) {
        this.ponovljena = ponovljena;
    }

    private List<Korisnik> dohvatiKorisnike() {
        List<Korisnik> lista = new ArrayList<>();
        HttpSession sesija=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        String json = rk.getJson((String) sesija.getAttribute("korisnickoIme"),(String)sesija.getAttribute("lozinka"));
        lista = dohvatiJsonUListaKorisnika(json);
        return lista;
    }

    private List<Korisnik> dohvatiJsonUListaKorisnika(String odgovor) {
        Gson gson = new Gson();
        JsonObject jo = new JsonParser().parse(odgovor).getAsJsonObject();
        JsonArray jsonKorisnici = jo.get("odgovor").getAsJsonArray();
        List<Korisnik> lista = new ArrayList<>();
        for (JsonElement je : jsonKorisnici) {
            Korisnik k = gson.fromJson(je, Korisnik.class);
            lista.add(k);
        }
        return lista;
    }

    
    
    public void provediAzuriranje(){
         if (!korisnickoIme.isEmpty() || !lozinka.isEmpty() || !ime.isEmpty() || !prezime.isEmpty()) {

             if(!provjeraImena(korisnickoIme)){
                 if(azurirajKorisnika(idKorisnika,ime, prezime, korisnickoIme, lozinka)){
                      FacesContext fc = FacesContext.getCurrentInstance();
                        String poruka = fc.getApplication().evaluateExpressionGet(fc, "Korisnik uspjesno promjenjen", String.class);
                        fc.addMessage(poruka, new FacesMessage(FacesMessage.SEVERITY_INFO, poruka, null));
                        HttpSession sesija=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                        sesija.setAttribute("korisnickoIme", korisnickoIme);
                        sesija.setAttribute("lozinka", lozinka);
                        this.listaKoriniska=dohvatiKorisnike();
                 }else{
                        FacesContext fc = FacesContext.getCurrentInstance();
                        String poruka = fc.getApplication().evaluateExpressionGet(fc, "Korisnik nije promjenjen", String.class);
                        fc.addMessage(poruka, new FacesMessage(FacesMessage.SEVERITY_ERROR, poruka, null));
                 }
             }else{
                  FacesContext fc = FacesContext.getCurrentInstance();
                String poruka = fc.getApplication().evaluateExpressionGet(fc, "Nisu uneseni svi potrebni elemetni", String.class);
                fc.addMessage(poruka, new FacesMessage(FacesMessage.SEVERITY_ERROR, poruka, null));
             }
         }
         
    }

    public boolean provjeraImena(String username) {
        String odgovor = rk.getJson(korime,loz);
        List<Korisnik> lista = dohvatiJsonUListaKorisnika(odgovor);
        for (Korisnik k : lista) {
            if (k.getKorisnickoIme().equals(this.korisnickoIme)) {
                FacesContext fc = FacesContext.getCurrentInstance();
                String poruka = fc.getApplication().evaluateExpressionGet(fc, "Korisnicko ime vec postoji", String.class);
                fc.addMessage(poruka, new FacesMessage(FacesMessage.SEVERITY_ERROR, poruka, null));
                return true;
            }
        }
        return false;
    }

    public boolean azurirajKorisnika(int id,String ime, String prezime, String korisnickoIme, String lozinka) {
        Korisnik k = new Korisnik(id,ime, prezime, korisnickoIme, lozinka);
        String json = dohvatiJsonUKorisnika(k);
        String odgovor = rk.putJson(json, this.korisnickoIme,korime,loz);
        boolean uspjesno = provjeraOdgovora(odgovor);
        return uspjesno;
    }

    private boolean provjeraOdgovora(String json) {
        JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
        String status = jo.get("status").getAsString();
        return status.equals("OK");
    }

    private String dohvatiJsonUKorisnika(Korisnik k) {
        Gson gson = new Gson();
        String json = gson.toJson(k);
        return json;
    }

    public void onPageLoad(ComponentSystemEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        String korisnik = (String) context.getExternalContext().getSessionMap().get("korisnickoIme");
        List<Korisnik> lista=dohvatiKorisnike();
        for(Korisnik k: lista){
            if(k.getKorisnickoIme().equals(korisnik)){
                this.korisnickoIme=korisnik;
                this.ime=k.getIme();
                this.prezime=k.getPrezime();
                this.idKorisnika=k.getId();
            }
        }
    }

    static class RESTKorisnik {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8084/mkolaric1_aplikacija_3_2/webresources";

        public RESTKorisnik() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("korisnici");
        }

        public String putJson(Object requestEntity, String korisnickoIme, String korisnik, String lozinka) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{korisnickoIme})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header("korisnickoIme", korisnik).header("lozinka", lozinka).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String postJson(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String getJsonAutentikacija(String korisnickoIme, String auth) throws ClientErrorException {
            WebTarget resource = webTarget;
            if (auth != null) {
                resource = resource.queryParam("auth", auth);
            }
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{korisnickoIme}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String getJson(String korisnickoIme, String lozinka) throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header("korisnickoIme", korisnickoIme).header("lozinka", lozinka).get(String.class);
        }

        public void close() {
            client.close();
        }
    }

}
