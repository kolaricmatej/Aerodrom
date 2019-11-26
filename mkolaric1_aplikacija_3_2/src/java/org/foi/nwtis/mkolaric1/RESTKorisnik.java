/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.mkolaric1.ws.Korisnik;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.web.pomocno.SlanjePoruka;

/**
 * REST Web Service
 *
 * @author Matej
 */
@Path("korisnici")
public class RESTKorisnik {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RESTKorisnik
     */
    public RESTKorisnik() {
    }

    private String odgovorNeispravnoDodano = "{\"status\": \"ERR\", \"poruka\": \"neuspješno dodavanje u tablicu Korisnik\"}";
    private String odgovorDodanoUKorisnike = "{\"odgovor\": [], \"status\": \"OK\"}";
    private String odgovorNePostojiUBazi = "{\"status\": \"ERR\", \"poruka\": \"Korisnik ne postoje ili neispravan naziv\"}";
    private String odgovorUnosFalePodaci = "{\"odgovor\": [], \"status\": \"ERR\", \"poruka\": \"neispravan id ili neispravan naziv\"}";

    private BP_Konfiguracija bpk;
    private Konfiguracija konf;
    private int port;
    //private String korisnickoIme;
    //private String lozinka;
    private SlanjePoruka sp=new SlanjePoruka();
    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.mkolaric1.RESTKorisnik
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String getJson(@HeaderParam("korisnickoIme")String korisnickoIme, @HeaderParam("lozinka")String lozinka) {
        ucitajKonfiguraciju();
        List<Korisnik> lista =dohvatiListuKorisnika(korisnickoIme, lozinka);
        for (Korisnik k : lista) {
            k.setLozinka("");
        }
        String json = dohvatiJsonUListaKorisnika(lista);
        String odgovor = ubaciJsonUFormatOdgovora(json, "OK", "");
        sp.posaljiPoruku(odgovor);
        return odgovor;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJson(String podaci) {
        Korisnik k = pretvoriJsonUKorisnik(podaci);
        if (dodajKorisnika(k)) {
            sp.posaljiPoruku(odgovorDodanoUKorisnike);
            return odgovorDodanoUKorisnike;
        } else {
            sp.posaljiPoruku(odgovorNeispravnoDodano);
            return odgovorNeispravnoDodano;
        }
    }
 
    @Path("{korisnickoIme}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public String getJsonAutentikacija(@PathParam("korisnickoIme") String username,@QueryParam("auth") String auth, @HeaderParam("korisnickoIme")String korisnickoIme, @HeaderParam("lozinka")String lozinka) {
        ucitajKonfiguraciju();
        if(auth==null){
            List<Korisnik> lista = dohvatiListuKorisnika(korisnickoIme, lozinka);
            if(lista.size()<1){
                return "{\"status\": \"ERR\", \"poruka\": \"Nepostojeći korisnik ili krivo uneseni podaci\"}";
            }else{
            Korisnik k = dohvatiKorisnikaPremaID(lista, username);
            String json = dohvatiJsonUKorisnik(k);
            String odgovor = ubaciJsonUFormatOdgovora(json, "OK", "");
            return odgovor;
            }
        }else{
            JsonObject jo=new JsonParser().parse(auth).getAsJsonObject();
            String passoword=jo.get("lozinka").getAsString();
            String naredba="KORISNIK "+username+"; LOZINKA "+passoword+";";
            String odgovor=sp.posaljiPoruku(naredba);
            if(odgovor.contains("ERR")){
                return "{\"status\": \"ERR\", \"poruka\": \"Nepostojeći korisnik ili krivo uneseni podaci\"}";
            }else{
                return "{\"odgovor\": [], \"status\": \"OK\"}";
            }
        }
    }

    @Path("{korisnickoIme}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public String putJson(String podaci,@PathParam("korisnickoIme") String username,@HeaderParam("korisnickoIme")String korisnickoIme, @HeaderParam("lozinka")String lozinka) {
        ucitajKonfiguraciju();
        Korisnik k = pretvoriJsonUKorisnik(podaci);
        if (azurirajKorisnika(k, korisnickoIme, lozinka)) {
            sp.posaljiPoruku(odgovorDodanoUKorisnike);
            return odgovorDodanoUKorisnike;
        } else {
            sp.posaljiPoruku(odgovorNeispravnoDodano);
            return odgovorNeispravnoDodano;
        }

    }
    private Korisnik dohvatiKorisnikaBezLozinke(List<Korisnik> korisnici, String username) {
        for (Korisnik k : korisnici) {
            if (k.getKorisnickoIme().equals(username)) {
                    k.setLozinka("");
            }
            return k;
        }
        return null;
    }

    private Korisnik dohvatiKorisnikaPremaID(List<Korisnik> korisnici, String username) {
        for (Korisnik k : korisnici) {
            if (k.getKorisnickoIme().equals(username)) {
                    return k;
            }
        }
        return null;
    }

    private void ucitajKonfiguraciju() {
        SlusacAplikacije sa = new SlusacAplikacije();
        bpk = (BP_Konfiguracija) sa.getSc().getAttribute("BP_Konfig");
        konf = (Konfiguracija) sa.getSc().getAttribute("Konfiguracija");
        //korisnickoIme = konf.dajPostavku("korisnickoIme");
        //lozinka = konf.dajPostavku("korisnickaLozinka");
    }

  

    private String dohvatiJsonUKorisnik(Korisnik korisnik) {
        Gson gson = new Gson();
        String json = gson.toJson(korisnik);
        return json;
    }

    private String dohvatiJsonUListaKorisnika(List<Korisnik> listaKorisnika) {
        Gson gson = new Gson();
        String json = gson.toJson(listaKorisnika);
        return json;
    }

    private Korisnik pretvoriJsonUKorisnik(String json) {
        Korisnik korisnik = new Korisnik();
        Gson gson = new Gson();
        korisnik = gson.fromJson(json, Korisnik.class);
        return korisnik;
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

    private static boolean dodajKorisnika(org.foi.nwtis.mkolaric1.ws.Korisnik korisnik) {
        org.foi.nwtis.mkolaric1.ws.AIRPWS_Service service = new org.foi.nwtis.mkolaric1.ws.AIRPWS_Service();
        org.foi.nwtis.mkolaric1.ws.AIRPWS port = service.getAIRPWSPort();
        return port.dodajKorisnika(korisnik);
    }

    private static boolean azurirajKorisnika(org.foi.nwtis.mkolaric1.ws.Korisnik korisnik, java.lang.String korisnickoIme, java.lang.String lozinka) {
        org.foi.nwtis.mkolaric1.ws.AIRPWS_Service service = new org.foi.nwtis.mkolaric1.ws.AIRPWS_Service();
        org.foi.nwtis.mkolaric1.ws.AIRPWS port = service.getAIRPWSPort();
        return port.azurirajKorisnika(korisnik, korisnickoIme, lozinka);
    }

    private static java.util.List<org.foi.nwtis.mkolaric1.ws.Korisnik> dohvatiListuKorisnika(java.lang.String korisnickoIme, java.lang.String lozinka) {
        org.foi.nwtis.mkolaric1.ws.AIRPWS_Service service = new org.foi.nwtis.mkolaric1.ws.AIRPWS_Service();
        org.foi.nwtis.mkolaric1.ws.AIRPWS port = service.getAIRPWSPort();
        return port.dohvatiListuKorisnika(korisnickoIme, lozinka);
    }

   

}
