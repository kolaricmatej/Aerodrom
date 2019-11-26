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
import javax.faces.context.FacesContext;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.mkolaric1.ws.AIRPWS_Service;
import org.foi.nwtis.mkolaric1.ws.Aerodrom;
import org.foi.nwtis.mkolaric1.ws.AvionLeti;
import org.foi.nwtis.mkolaric1.ws.MeteoPodaci;
import org.foi.nwtis.mkolaric1.zrna.PregledLetovaUzMeteo.AIRPREST;

/**
 *
 * @author Matej
 */
@Named(value = "pregledAerodromaInterval")
@SessionScoped
public class PregledAerodromaInterval implements Serializable {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/mkolaric1_aplikacija_1/AIRPWS.wsdl")
    private AIRPWS_Service service;

    private AIRPREST air;
    private List<Aerodrom> listaAerodroma;
    private String odVremena;
    private String doVremena;
    private Aerodrom odabraniAerodrom;
    private List<AvionLeti> listaLetova;
    private AvionLeti odabraniLet;
    private List<AvionLeti> listaAviona;

    public PregledAerodromaInterval() {
        air = new AIRPREST();
    }

    public AvionLeti getOdabraniLet() {
        return odabraniLet;
    }

    public void setOdabraniLet(AvionLeti odabraniLet) {
        this.odabraniLet = odabraniLet;
    }

    public List<AvionLeti> getListaAviona() {
        return listaAviona;
    }


    public void setListaAviona(List<AvionLeti> listaAviona) {
        this.listaAviona = listaAviona;
    }

    public List<AvionLeti> preuzmiLetove() {
        Aerodrom a = odabraniAerodrom;
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        listaLetova = podaciUIntervalu(a.getIcao(), korime, loz, odVremena, doVremena);
        return listaLetova;
    }

    public List<AvionLeti> preuzmiAvione() {
        AvionLeti a = odabraniLet;
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        listaAviona = podaciOAvionima(a.getIcao24(), korime, loz, odVremena, doVremena);
        return listaAviona;
    }

    public List<Aerodrom> getListaAerodroma() {
        listaAerodroma = dohvatiListuAerodroma();
        return listaAerodroma;
    }

    public void setListaAerodroma(List<Aerodrom> listaAerodroma) {
        this.listaAerodroma = listaAerodroma;
    }

    private List<Aerodrom> dohvatiListuAerodroma() {
        String odgovor = air.getJson();
        List<Aerodrom> lista = dohvatiJsonUListaAerodroma(odgovor);
        return lista;
    }

    private List<Aerodrom> dohvatiJsonUListaAerodroma(String odgovor) {
        Gson gson = new Gson();
        JsonObject jo = new JsonParser().parse(odgovor).getAsJsonObject();
        JsonArray jsonKorisnici = jo.get("odgovor").getAsJsonArray();
        List<Aerodrom> lista = new ArrayList<>();
        for (JsonElement je : jsonKorisnici) {
            Aerodrom k = gson.fromJson(je, Aerodrom.class);
            lista.add(k);
        }
        return lista;
    }

    public String getOdVremena() {
        return odVremena;
    }

    public void setOdVremena(String odVremena) {
        this.odVremena = odVremena;
    }

    public String getDoVremena() {
        return doVremena;
    }

    public void setDoVremena(String doVremena) {
        this.doVremena = doVremena;
    }

    public Aerodrom getOdabraniAerodrom() {
        return odabraniAerodrom;
    }

    public void setOdabraniAerodrom(Aerodrom odabraniAerodrom) {
        this.odabraniAerodrom = odabraniAerodrom;
    }

    public List<AvionLeti> getListaLetova() {
        return listaLetova;
    }

    public void setListaLetova(List<AvionLeti> listaLetova) {
        this.listaLetova = listaLetova;
    }

    static class AIRPREST {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8084/mkolaric1_aplikacija_1/webresources";

        public AIRPREST() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("aerodromi");
        }

        public String getJsonIdAvion(String id) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/avion", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String deleteJsonAvion(String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/avion", new Object[]{id})).request().delete(String.class);
        }

        public String putJson(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String postJson(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String getJsonId(String id) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String deleteJson(String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request().delete(String.class);
        }

        public String deleteJsonAvionID(String id, String aid) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/avion/{1}", new Object[]{id, aid})).request().delete(String.class);
        }

        public String postJsonAvion(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/avion", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String getJson() throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close() {
            client.close();
        }
    }

    private java.util.List<org.foi.nwtis.mkolaric1.ws.AvionLeti> podaciUIntervalu(java.lang.String icao24, java.lang.String korisnickoIme, java.lang.String lozinka, java.lang.String odVremena, java.lang.String doVremena) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkolaric1.ws.AIRPWS port = service.getAIRPWSPort();
        return port.podaciUIntervalu(icao24, korisnickoIme, lozinka, odVremena, doVremena);
    }

    private java.util.List<org.foi.nwtis.mkolaric1.ws.AvionLeti> podaciOAvionima(java.lang.String icao24, java.lang.String korisnickoIme, java.lang.String lozinka, java.lang.String odVremena, java.lang.String doVremena) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkolaric1.ws.AIRPWS port = service.getAIRPWSPort();
        return port.podaciOAvionima(icao24, korisnickoIme, lozinka, odVremena, doVremena);
    }

}
