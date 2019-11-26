/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.ws;

import java.util.List;

/**
 *
 * @author Matej
 */
public class AerodromWS {

    public static boolean aktivirajAerodromGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.aktivirajAerodromGrupe(korisnickoIme, korisnickaLozinka, idAerodrom);
    }

    public static Boolean aktivirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.aktivirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static boolean aktivirajOdabraneAerodromeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<java.lang.String> odabraniAerodromi) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.aktivirajOdabraneAerodromeGrupe(korisnickoIme, korisnickaLozinka, odabraniAerodromi);
    }

    public static Boolean autenticirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.autenticirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static boolean blokirajAerodromGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.blokirajAerodromGrupe(korisnickoIme, korisnickaLozinka, idAerodrom);
    }

    public static Boolean blokirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.blokirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static boolean blokirajOdabraneAerodromeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<java.lang.String> odabranaAerodromi) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.blokirajOdabraneAerodromeGrupe(korisnickoIme, korisnickaLozinka, odabranaAerodromi);
    }

    public static int dajBrojPoruka(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajBrojPoruka(korisnickoIme, korisnickaLozinka);
    }

    public static AerodromStatus dajStatusAerodromaGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajStatusAerodromaGrupe(korisnickoIme, korisnickaLozinka, idAerodrom);
    }

    public static StatusKorisnika dajStatusGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajStatusGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static List<Aerodrom> dajSveAerodromeGrupe(String korisnickoIme, String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajSveAerodromeGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static java.util.List<Avion> dajSveAvioneAerodromaGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajSveAvioneAerodromaGrupe(korisnickoIme, korisnickaLozinka, idAerodrom);
    }

    public static int dajTrajanjeCiklusa(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajTrajanjeCiklusa(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean deregistrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.deregistrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean dodajAerodromGrupi(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, org.foi.nwtis.mkolaric1.ws.Aerodrom serodrom) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.dodajAerodromGrupi(korisnickoIme, korisnickaLozinka, serodrom);
    }

    public static boolean dodajAvionGrupi(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, org.foi.nwtis.mkolaric1.ws.Avion avionNovi) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.dodajAvionGrupi(korisnickoIme, korisnickaLozinka, avionNovi);
    }

    public static Boolean dodajNoviAerodromGrupi(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom, java.lang.String nazivAerodrom, java.lang.String drzavaAerodrom, java.lang.String latAerodrom, java.lang.String lonAerodrom) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.dodajNoviAerodromGrupi(korisnickoIme, korisnickaLozinka, idAerodrom, nazivAerodrom, drzavaAerodrom, latAerodrom, lonAerodrom);
    }

    public static boolean obrisiAerodromGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.obrisiAerodromGrupe(korisnickoIme, korisnickaLozinka, idAerodrom);
    }

    public static boolean obrisiOdabraneAerodromeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<java.lang.String> odabraniAerodromi) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.obrisiOdabraneAerodromeGrupe(korisnickoIme, korisnickaLozinka, odabraniAerodromi);
    }

    public static Boolean obrisiSveAerodromeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.obrisiSveAerodromeGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static boolean postaviAvioneGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<org.foi.nwtis.mkolaric1.ws.Avion> avioniNovi) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.postaviAvioneGrupe(korisnickoIme, korisnickaLozinka, avioniNovi);
    }

    public static void promjeniBrojPoruka(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int brojPoruka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        port.promjeniBrojPoruka(korisnickoIme, korisnickaLozinka, brojPoruka);
    }

    public static void promjeniTrajanjeCiklusa(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int trajanjeCiklusa) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        port.promjeniTrajanjeCiklusa(korisnickoIme, korisnickaLozinka, trajanjeCiklusa);
    }

    public static Boolean registrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.registrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static boolean ucitajUgradeneAerodromeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.ucitajUgradeneAerodromeGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static boolean ucitajUgradeneAvioneGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service service = new org.foi.nwtis.mkolaric1.ws.AerodromiWS_Service();
        org.foi.nwtis.mkolaric1.ws.AerodromiWS port = service.getAerodromiWSPort();
        return port.ucitajUgradeneAvioneGrupe(korisnickoIme, korisnickaLozinka);
    }

}
