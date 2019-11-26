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
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.pomocno.SlanjePoruka;
import org.foi.nwtis.mkolaric1.ws.Aerodrom;
import org.foi.nwtis.mkolaric1.web.podaci.Naredbe;
import org.foi.nwtis.mkolaric1.ws.AIRPWS_Service;
import org.foi.nwtis.rest.podaci.MeteoPodaci;
import org.jboss.weld.bean.proxy.Marker;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;

/**
 *
 * @author Matej
 */
@Named(value = "pregledLetovaUzMeteo")
@SessionScoped
public class PregledLetovaUzMeteo implements Serializable {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/mkolaric1_aplikacija_1/AIRPWS.wsdl")
    private AIRPWS_Service service;

    private AIRPREST air;
    private List<Aerodrom> listaAerodroma;
    private String icao;
    private Aerodrom odabraniAerodrom;
    private Naredbe naredbe;
    private String provedenaAkcija2;
    private MeteoPodaci meteo;
    private SlanjePoruka sp;
    private BP_Konfiguracija bpk;
    private Konfiguracija konf;
    private String username;
    private String password;
    private String url;

    /**
     * Creates a new instance of pregledLetovaUzMeteo
     */
    public PregledLetovaUzMeteo() {
        air = new AIRPREST();
        sp = new SlanjePoruka();
        meteo = new MeteoPodaci();

    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public List<Aerodrom> getListaAerodroma() {
        listaAerodroma = dohvatiListuAerodroma();
        return listaAerodroma;
    }

    public void setListaAerodroma(List<Aerodrom> listaAerodroma) {
        this.listaAerodroma = listaAerodroma;
    }

    public void dohvatiAerodromIzAirports() {
        Aerodrom a = dohvatiPodatkeAerodrom(icao);
        String json = dohvatiJsonUAerodroma(a);
        String odgovor = air.postJson(json);

    }

    private List<Aerodrom> dohvatiListuAerodroma() {
        String odgovor = air.getJson();
        List<Aerodrom> lista = dohvatiJsonUListaAerodroma(odgovor);
        return lista;
    }

    public void obrisiAerodromIzListe() {
        Aerodrom a = odabraniAerodrom;
        String odgovor = air.deleteJson(a.getIcao());
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

    private String dohvatiJsonUAerodroma(Aerodrom aerodroma) {
        Gson gson = new Gson();
        String json = gson.toJson(aerodroma);
        return json;
    }

    public void kreniGrupa() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuZaGrupuKreni(korime, loz);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija2 = odgovor;
    }

    public void pauzaGrupa() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuZaGrupuPauza(korime, loz);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija2 = odgovor;
    }

    public void stanjeGrupa() {
        naredbe = new Naredbe();
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        String naredba = naredbe.kreirajNaredbuZaGrupuStanje(korime, loz);
        String odgovor = sp.posaljiPoruku(naredba);
        provedenaAkcija2 = odgovor;
    }

    public MeteoPodaci dohvatiPodatkeVremena() {
        Aerodrom a = odabraniAerodrom;
        FacesContext fc = FacesContext.getCurrentInstance();
        String korime = (String) fc.getExternalContext().getSessionMap().get("korisnickoIme");
        String loz = (String) fc.getExternalContext().getSessionMap().get("lozinka");
        org.foi.nwtis.mkolaric1.ws.MeteoPodaci privremeniMeteo = vazeciMeteoPodaci(a.getIcao(), korime, loz);
        Date lastUpdateDatum = privremeniMeteo.getLastUpdate().toGregorianCalendar().getTime();
        Date sunriseDatum = privremeniMeteo.getSunRise().toGregorianCalendar().getTime();
        Date sunsetDatum = privremeniMeteo.getSunSet().toGregorianCalendar().getTime();;
        meteo = new MeteoPodaci(sunriseDatum, sunsetDatum, privremeniMeteo.getTemperatureValue(),
                privremeniMeteo.getTemperatureMin(), privremeniMeteo.getTemperatureMax(), privremeniMeteo.getTemperatureUnit(),
                privremeniMeteo.getHumidityValue(), privremeniMeteo.getHumidityUnit(), privremeniMeteo.getPressureValue(),
                privremeniMeteo.getPressureUnit(), privremeniMeteo.getWindSpeedValue(), privremeniMeteo.getWindSpeedName(),
                privremeniMeteo.getWindDirectionValue(), privremeniMeteo.getWindDirectionCode(), privremeniMeteo.getWindDirectionName(),
                privremeniMeteo.getCloudsValue(), privremeniMeteo.getCloudsName(), privremeniMeteo.getVisibility(), privremeniMeteo.getPrecipitationValue(),
                privremeniMeteo.getPrecipitationMode(), privremeniMeteo.getPrecipitationUnit(), privremeniMeteo.getWeatherNumber(), privremeniMeteo.getWeatherValue(),
                privremeniMeteo.getWeatherIcon(), lastUpdateDatum);
        return meteo;
    }

    private org.foi.nwtis.mkolaric1.ws.Aerodrom dohvatiPodatkeAerodrom(java.lang.String arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkolaric1.ws.AIRPWS port = service.getAIRPWSPort();
        return port.dohvatiPodatkeAerodrom(arg0);
    }

    public MeteoPodaci getMeteo() {
        meteo = dohvatiPodatkeVremena();
        return meteo;
    }

    public void setMeteo(MeteoPodaci meteo) {
        this.meteo = meteo;
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

    public Aerodrom getOdabraniAerodrom() {
        return odabraniAerodrom;
    }

    public void setOdabraniAerodrom(Aerodrom odabraniAerodrom) {
        this.odabraniAerodrom = odabraniAerodrom;
    }

    public String getProvedenaAkcija2() {
        return provedenaAkcija2;
    }

    public void setProvedenaAkcija2(String provedenaAkcija2) {
        this.provedenaAkcija2 = provedenaAkcija2;
    }

    private org.foi.nwtis.mkolaric1.ws.MeteoPodaci vazeciMeteoPodaci(java.lang.String icao24, java.lang.String korisnickoIme, java.lang.String lozinka) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkolaric1.ws.AIRPWS port = service.getAIRPWSPort();
        return port.vazeciMeteoPodaci(icao24, korisnickoIme, lozinka);
    }

}
