/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.rest;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.mkolaric1.dretve.ObradaZahtjeva;
import org.foi.nwtis.mkolaric1.dretve.PreuzimanjeAviona;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.mkolaric1.ws.Aerodrom;
import org.foi.nwtis.mkolaric1.ws.AerodromWS;
import org.foi.nwtis.mkolaric1.ws.Avion;

/**
 * REST Web Service
 *
 * @author Matej
 */
@Path("aerodromi")
public class AIRPREST {

    private Konfiguracija konf;
    private BP_Konfiguracija bpk;
    private String username;
    private String password;
    private long vrijemePocetka;
    private String korisnickoIme;
    private String lozinka;
    private String url;

    private String odgovorAutentikacijaNeuspjela = "{\"odgovor\": [], \"status\": \"ERR\", \"poruka\": \"Nepostojeći korisnik ili krivo uneseni podaci\"}";
    private String odgovorUnosFalePodaci = "{\"odgovor\": [], \"status\": \"ERR\", \"poruka\": \"neispravan icao ili neispravan naziv\"}";
    private String odgovorDodano = "{\"odgovor\": [], \"status\": \"OK\"}";
    private String odgovorAerodromID = "{\"odgovor\": [], \"status\": \"ERR\", \"poruka\": \"Aerodrom ne postoji ili neispravan naziv\"}";

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AIRPREST
     */
    public AIRPREST() {
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.mkolaric1.dretve.AIRPREST
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (AerodromWS.autenticirajGrupu(username, password)) {
            List<Aerodrom> lista = dohvatiSveAerodromeWS();
            String jsonAerodroma = dohvatiJsonUListaAerodroma(lista);
            String odgovor = ubaciJsonUFormatOdgovora(jsonAerodroma, "OK", "");
            unosUDnevnik(username, "REST", "GET", odgovor);
            return odgovor;
        } else {
            unosUDnevnik("Nepostojeći korisnik", "REST", "GET", "neispravna autentikacija");
            return odgovorAutentikacijaNeuspjela;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJson(String podaci) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (AerodromWS.autenticirajGrupu(username, password)) {
            Aerodrom a = pretvoriJsonUAerodrom(podaci);
            if (a.getIcao().equals("") || a.getNaziv().equals("") || a == null) {
                unosUDnevnik(username, "REST", "POST", odgovorUnosFalePodaci);
                return odgovorUnosFalePodaci;
            } else {
                if (!postojiAerodrom(a.getIcao())) {
                    AerodromWS.dodajAerodromGrupi(username, password, a);
                    try {
                        dodajAerodromUMyAirports(a);
                    } catch (SQLException ex) {
                        Logger.getLogger(AIRPREST.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    unosUDnevnik(username, "REST", "POST", odgovorDodano);
                    return odgovorDodano;
                }
            }
        } else {
            unosUDnevnik("Nepostojeći korisnik", "REST", "POST", "neispravna autentikacija");
            return odgovorAutentikacijaNeuspjela;
        }
        return "";
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJsonId(@PathParam("id") String icao) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (AerodromWS.autenticirajGrupu(username, password)) {
            if (postojiAerodrom(icao)) {
                Aerodrom a = dohvatiAerodrom(icao);
                String json = dohvatiJsonUAerodroma(a);
                String odgovor = ubaciJsonUFormatOdgovora(json, "OK", "");
                unosUDnevnik(username, "REST", "GET", odgovor);
                return odgovor;
            } else {
                unosUDnevnik(username, "REST", "GET", odgovorAerodromID);
                return odgovorAerodromID;
            }
        } else {
            unosUDnevnik("Nepostojeći korisnik", "REST", "POST", "neispravna autentikacija");
            return odgovorAutentikacijaNeuspjela;
        }
    }

    /**
     * PUT method for updating or creating an instance of AIRPREST
     *
     * @param podaci
     * @param content representation for the resource
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public String putJson(String podaci) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (AerodromWS.autenticirajGrupu(username, password)) {
            Aerodrom a = pretvoriJsonUAerodrom(podaci);
            if (a.getIcao().equals("") || a.getNaziv().equals("") || a == null) {
                unosUDnevnik(username, "REST", "POST", odgovorUnosFalePodaci);
                return odgovorUnosFalePodaci;
            } else {
                if (postojiAerodrom(podaci)) {
                    try {
                        AerodromWS.obrisiAerodromGrupe(username, password, a.getIcao());
                        AerodromWS.dodajAerodromGrupi(username, password, a);
                        azurirajAerodromUMyAirports(a);
                        unosUDnevnik(username, "REST", "PUT", odgovorAerodromID);
                        return odgovorAerodromID;
                    } catch (SQLException ex) {
                        Logger.getLogger(AIRPREST.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            unosUDnevnik("Nepostojeći korisnik", "REST", "PUT", "neispravna autentikacija");
            return odgovorAutentikacijaNeuspjela;
        }
        return "";
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String deleteJson(@PathParam("id") String icao) {
        ucitajKonfiguraciju();
        if (AerodromWS.autenticirajGrupu(username, password)) {
            //Aerodrom a = pretvoriJsonUAerodrom(podaci);
            if (postojiAerodrom(icao)) {
                try {
                    AerodromWS.obrisiAerodromGrupe(username, password, icao);
                    obrisiAerodromIzMyAirports(icao);
                    unosUDnevnik(username, "REST", "DELETE", odgovorAerodromID);
                } catch (SQLException ex) {
                    Logger.getLogger(AIRPREST.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                unosUDnevnik(username, "REST", "DELETE", odgovorUnosFalePodaci);
                return odgovorUnosFalePodaci;
            }
        } else {
            unosUDnevnik("Nepostojeći korisnik", "REST", "DELETE", "neispravna autentikacija");
            return odgovorAutentikacijaNeuspjela;
        }
        return "";
    }

    @Path("{id}/avion/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJsonIdAvion(@PathParam("id") String icao) {
        ucitajKonfiguraciju();
        if (AerodromWS.autenticirajGrupu(username, password)) {
            if (postojiAerodrom(icao)) {
                List<Avion> lista = AerodromWS.dajSveAvioneAerodromaGrupe(username, password, icao);
                String json = dohvatiJsonUAvion(lista);
                String odgovor = ubaciJsonUFormatOdgovora(json, "OK", "");
                unosUDnevnik(username, "REST", "GET", odgovor);
                return odgovor;
            } else {
                unosUDnevnik(username, "REST", "GET", odgovorUnosFalePodaci);
                return odgovorUnosFalePodaci;
            }
        } else {
            unosUDnevnik("Nepostojeći korisnik", "REST", "GET", "neispravna autentikacija");
            return odgovorAutentikacijaNeuspjela;
        }
    }

    @Path("{id}/avion/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJsonAvion(@PathParam("id") String icao) {
        ucitajKonfiguraciju();
        if (AerodromWS.autenticirajGrupu(username, password)) {
            if (postojiAerodrom(icao)) {
                Avion a = dohvatiAvion(icao);
                AerodromWS.dodajAvionGrupi(username, password, a);
                String json = dohvatiJsonUAerodroma(a);
                String odgovor = ubaciJsonUFormatOdgovora(json, "ERR", "");
                unosUDnevnik(username, "REST", "POST", odgovor);
                return odgovor;
            } else {
                unosUDnevnik(username, "REST", "POST", odgovorUnosFalePodaci);
                return odgovorUnosFalePodaci;
            }
        } else {
            unosUDnevnik("Nepostojeći korisnik", "REST", "POST", "neispravna autentikacija");
            return odgovorAutentikacijaNeuspjela;
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/avion/")
    public String deleteJsonAvion(@PathParam("id") String podaci) {
        ucitajKonfiguraciju();
        if (AerodromWS.autenticirajGrupu(username, password)) {
            if (postojiAerodrom(podaci)) {
                List<String> avion = obrisiAvioneAerodroma(podaci);
                AerodromWS.obrisiOdabraneAerodromeGrupe(username, password, avion);
            } else {
                unosUDnevnik(username, "REST", "DELETE", odgovorUnosFalePodaci);
                return odgovorUnosFalePodaci;
            }
        } else {
            unosUDnevnik("Nepostojeći korisnik", "REST", "DELETE", "neispravna autentikacija");
            return odgovorAutentikacijaNeuspjela;
        }
        return "";
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/avion/{aid}")
    public String deleteJsonAvionID(@PathParam("id") String podaci, @PathParam("aid") String icao24) {
        ucitajKonfiguraciju();
        if (AerodromWS.autenticirajGrupu(username, password)) {
            if (postojiAerodrom(podaci)) {

            }
        }
        return "";
    }

    private List<Aerodrom> dohvatiSveAerodromeWS() {
        List<Aerodrom> lista = AerodromWS.dajSveAerodromeGrupe(username, password);
        return lista;
    }

    /**
     * Metoda koja dohvaća podatke iz Baze i pohranjuje ih u varijable
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public boolean postojiKorisnikUBazi(String korisnickoIme, String lozinka) {
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
                ps.setString(1, korisnickoIme);
                ps.setString(2, lozinka);
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

    private void ucitajKonfiguraciju() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            SlusacAplikacije sa = new SlusacAplikacije();
            bpk = (BP_Konfiguracija) sa.getSc().getAttribute("BP_Konfig");
            url = bpk.getServerDatabase() + bpk.getUserDatabase();
            korisnickoIme = bpk.getUserUsername();
            lozinka = bpk.getUserPassword();
            konf = (Konfiguracija) sa.getSc().getAttribute("Konfiguracija");
            username = konf.dajPostavku("korisnik.ime");
            password = konf.dajPostavku("korisnik.lozinka");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreuzimanjeAviona.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public void unosUDnevnik(String korisnickoIme, String vrsta, String zahtjev, String odgovor) {
        try {
            Timestamp vrijemeUnosa = new Timestamp(System.currentTimeMillis());
            Long trajanjeObrade = System.currentTimeMillis() - vrijemePocetka;
            try (Connection con = DriverManager.getConnection(url,username,password);
                 PreparedStatement ps = con.prepareStatement("INSERT INTO DNEVNIK(korisnickoIme,ipAdresa,vrijemePocetka,trajanjeObrade,vrstaZapisa,sadrzaj,odgovor) VALUES(?,?,?,?,?,?,?)")) {
                ps.setString(1, korisnickoIme);
                ps.setString(2, "localhost");
                ps.setTimestamp(3, vrijemeUnosa);
                ps.setInt(4, trajanjeObrade.intValue());
                ps.setString(5, vrsta);
                ps.setString(6, zahtjev);
                ps.setString(7, odgovor);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<String> obrisiAvioneAerodroma(String icao) {
        List<Avion> lista = AerodromWS.dajSveAvioneAerodromaGrupe(username, password, icao);
        List<String> avioni = new ArrayList<>();
        for (Avion a : lista) {
            if (a.getEstdepartureairport().equals(icao)) {
                avioni.add(a.getIcao24());
            }
        }
        return avioni;
    }

    private Avion dohvatiAvion(String icao) {
        List<Avion> lista = AerodromWS.dajSveAvioneAerodromaGrupe(username, password, icao);
        for (Avion a : lista) {
            if (a.getEstdepartureairport().equals(icao)) {
                return a;
            }
        }
        return null;
    }

    private Aerodrom dohvatiAerodrom(String icao) {
        List<Aerodrom> lista = AerodromWS.dajSveAerodromeGrupe(username, password);
        for (Aerodrom a : lista) {
            if (icao.equals(a.getIcao())) {
                return a;
            }
        }
        return null;
    }

    private boolean postojiAerodrom(String icao) {
        List<Aerodrom> lista = AerodromWS.dajSveAerodromeGrupe(username, password);
        for (Aerodrom a : lista) {
            if (icao.equals(a.getIcao())) {
                return true;
            }
        }
        return false;
    }

    private void dodajAerodromUMyAirports(Aerodrom aerodrom) throws SQLException {
        try (Connection con = DriverManager.getConnection(url, korisnickoIme, lozinka); 
                PreparedStatement ps = con.prepareStatement("INSERT INTO MYAIRPORTS VALUES(?,?,?,?,?)")) {
            ps.setString(1, aerodrom.getIcao());
            ps.setString(2, aerodrom.getNaziv());
            ps.setString(3, aerodrom.getDrzava());
            //ps.setString(4, aerodrom.getLokacija().getLatitude() + "," + aerodrom.getLokacija().getLongitude());
            ps.setString(4, aerodrom.getLokacija().getLongitude()+ "," + aerodrom.getLokacija().getLatitude());
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));
            ps.executeUpdate();
        }
    }

    private void azurirajAerodromUMyAirports(Aerodrom aerodrom) throws SQLException {
        try {
            Class.forName(bpk.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AIRPREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (Connection con = DriverManager.getConnection(url, korisnickoIme, lozinka);
                PreparedStatement ps = con.prepareStatement("UPDATE MYAIRPORTS SET IDENT=?,NAME=?,ISO_COUNTRY=?,COORDINATES=?,STORED=?")) {
            ps.setString(1, aerodrom.getIcao());
            ps.setString(2, aerodrom.getNaziv());
            ps.setString(3, aerodrom.getDrzava());
            ps.setString(4, aerodrom.getLokacija().getLatitude() + "," + aerodrom.getLokacija().getLongitude());
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));
            ps.executeUpdate();
        }
    }

    private void obrisiAerodromIzMyAirports(String icao) throws SQLException {
        ucitajKonfiguraciju();
        try {
            Class.forName(bpk.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AIRPREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (Connection con = DriverManager.getConnection(url, korisnickoIme, lozinka);
                PreparedStatement ps = con.prepareStatement("DELETE FROM MYAIRPORTS WHERE IDENT=?")) {
            ps.setString(1, icao);
            ps.executeUpdate();
        }

    }

    private Aerodrom pretvoriJsonUAerodrom(String json) {
        Aerodrom aerodrom;
        Gson gson = new Gson();
        aerodrom = gson.fromJson(json, Aerodrom.class);
        return aerodrom;
    }

    private String dohvatiJsonUListaAerodroma(List<Aerodrom> listaAerodroma) {
        Gson gson = new Gson();
        String json = gson.toJson(listaAerodroma);
        return json;
    }

    private String dohvatiJsonUAerodroma(Avion avion) {
        Gson gson = new Gson();
        String json = gson.toJson(avion);
        return json;
    }

    private String dohvatiJsonUAerodroma(Aerodrom aerodroma) {
        Gson gson = new Gson();
        String json = gson.toJson(aerodroma);
        return json;
    }

    private String dohvatiJsonUAvion(List<Avion> avion) {
        Gson gson = new Gson();
        String json = gson.toJson(avion);
        return json;
    }

    private String ubaciJsonUFormatOdgovora(String json, String status, String greska) {
        String pocetak = "{\"odgovor\": " + json;
        String zavrsetak = ", \"status\": \"" + status + "\"";
        if (status.equals("ERR")) {
            zavrsetak += ", \"poruka\": \"" + greska + "\"}";
        } else {
            zavrsetak += "}";
        }
        return pocetak + zavrsetak;
    }
}
