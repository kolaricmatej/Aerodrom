/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.mkolaric1.dretve.ObradaZahtjeva;
import org.foi.nwtis.mkolaric1.dretve.PreuzimanjeAviona;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.web.podaci.Aerodrom;
import org.foi.nwtis.mkolaric1.web.podaci.Korisnik;
import org.foi.nwtis.mkolaric1.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.rest.klijenti.OWMKlijent;

import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

/**
 *
 * @author Matej
 */
@WebService(serviceName = "AIRPWS")
public class AIRPWS {

    private Konfiguracija konf;
    private BP_Konfiguracija bpk;
    private long vrijemePocetka;
    private String username;
    private String password;
    private String url;

    @WebMethod(operationName = "dajZadnjiAvion")
    public AvionLeti dajZadnjiAvion(@WebParam(name = "icao") String icao, @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        AvionLeti a = new AvionLeti();
        vrijemePocetka = System.currentTimeMillis();
        ucitajKonfiguraciju();
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            a = dohvatiPodatkeZaAvionLeti(icao);
            unosUDnevnik(korisnickoIme, "SOAP", "Dohvaćen avion" + a.getEstDepartureAirport());
        } else {
            unosUDnevnik("Nepostojeći korisnik", "SOAP", "ERROR prikaza zadnjeg aviona");
        }
        return a;
    }

    @WebMethod(operationName = "dajZadnjeAvione")
    public List<AvionLeti> dajZadnjeAvione(@WebParam(name = "broj") int broj, @WebParam(name = "icao") String icao, @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            List<AvionLeti> avionLeti = dohvatiPodatkeZaAvionLetiPremaBroju(broj, icao);
            unosUDnevnik(korisnickoIme, "SOAP", "Dohvaćeni posljednih n aviona");
            return avionLeti;
        } else {
            unosUDnevnik("Nepostojeći korisnik", "SOAP", "ERROR PRI DOHVAĆANJU POSLJEDNJIH N AVIONA");
            return null;
        }
    }
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @WebMethod(operationName = "podaciUIntervalu")
    public List<AvionLeti> podaciUIntervalu(@WebParam(name = "icao24") String icao24, @WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "odVremena") String odVremena, @WebParam(name = "doVremena") String doVremena) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            try {
                List<AvionLeti> al = new ArrayList<>();
                Timestamp vrijemeP = new Timestamp(sdf.parse(odVremena).getTime());
                Timestamp vrijemeKraja = new Timestamp(sdf.parse(doVremena).getTime());
                int pocetak = (int) (vrijemeP.getTime() / 1000);
                int kraj = (int) (vrijemeKraja.getTime() / 1000);
                al = provjeriJeLiPoletio(icao24, pocetak, kraj);
                if (al.isEmpty()) {
                    unosUDnevnik(korisnickoIme, "SOAP", "ERROR NIJE DOHVAĆEN NITI JEDAN ELEMENT U INTERVALU");
                    return null;
                } else {
                    unosUDnevnik(korisnickoIme, "SOAP", "Dohvaćeni podaci o avionima koji su poletjeli");
                    return al;
                }
            } catch (ParseException ex) {
                Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            unosUDnevnik("Nepostojeći korisnik", "SOAP", "ERROR podaci u intervalu");
        }
        return null;
    }

    @WebMethod(operationName = "podaciOAvionima")
    public List<AvionLeti> podaciOAvionima(@WebParam(name = "icao24") String icao24, @WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "odVremena") String odVremena, @WebParam(name = "doVremena") String doVremena) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            try {
                List<AvionLeti> al;
                Timestamp vrijemeP = new Timestamp(sdf.parse(odVremena).getTime());
                Timestamp vrijemeKraja = new Timestamp(sdf.parse(doVremena).getTime());
                int pocetak = (int) (vrijemeP.getTime() / 1000);
                int kraj = (int) (vrijemeKraja.getTime() / 1000);
                al = dohvatiInterval(icao24, pocetak, kraj);
                unosUDnevnik(korisnickoIme, "SOAP", "Dohvaćeni avioni u intervalu");
                return al;
            } catch (ParseException ex) {
                Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            unosUDnevnik("Neispravna autentikacija", "SOAP", "Nepostojeći korisnik");
        }
        return null;
    }

    @WebMethod(operationName = "pretvoriUString")
    public List<String> pretvoriUString(@WebParam(name = "icao24") String icao24, @WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "odVremena") String odVremena, @WebParam(name = "doVremena") String doVremena) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        List<String> listaAviona = new ArrayList<>();
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            try {
                Timestamp vrijemePocetka = new Timestamp(sdf.parse(odVremena).getTime());
                Timestamp vrijemeKraja = new Timestamp(sdf.parse(doVremena).getTime());
                int pocetak = (int) (vrijemePocetka.getTime() / 1000);
                int kraj = (int) (vrijemeKraja.getTime() / 1000);
                listaAviona = nazivAerodroma(icao24, pocetak, kraj);
                unosUDnevnik(korisnickoIme, "SOAP", "Dohvaćena lista aviona u stringu");
                return listaAviona;
            } catch (ParseException ex) {
                Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            unosUDnevnik("Neispravna autentikacija", "SOAP", "ERROR Nepostojeći korisnik");
        }
        return listaAviona;
    }

    @WebMethod(operationName = "vazeciMeteoPodaci")
    public MeteoPodaci vazeciMeteoPodaci(@WebParam(name = "icao24") String icao24, @WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            Aerodrom a = dohvatiPodatkeAerodrom(icao24);
            if (a == null) {
                unosUDnevnik(korisnickoIme, "SOAP", "Nema dostpunih podataka- vazeci meteo podaci");
            } else {
                MeteoPodaci mp = dohvatiMeteoPodatke(a);
                unosUDnevnik(korisnickoIme, "SOAP", "Dohvaćeni važeći meteopodaci");
                return mp;
            }
        } else {
            unosUDnevnik("Neispravna autentikacija", "SOAP", "Nepostojeći korisnik");
        }
        return null;
    }

    @WebMethod(operationName = "dodajKorisnika")
    public boolean dodajKorisnika(@WebParam(name = "korisnik") Korisnik korisnik) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (!postojiKorisnikUBazi(korisnik.getKorisnickoIme(), korisnik.getLozinka())) {
            dodajKorisnikaUBazu(korisnik.getIme(), korisnik.getPrezime(), korisnik.getKorisnickoIme(), korisnik.getLozinka());
            unosUDnevnik(korisnik.getKorisnickoIme(), "SOAP", "dodan korisnik " + korisnik.getKorisnickoIme());
            return true;
        } else {
            unosUDnevnik("NEISPRAVNA AUTENTIKACIJA", "SOAP", "Nepostojeći korisnik");
        }
        return false;
    }

    @WebMethod(operationName = "azurirajKorisnika")
    public boolean azurirajKorisnika(@WebParam(name = "korisnik") Korisnik korisnik,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            azurirajKorisnikaUBP(korisnik.getIme(), korisnik.getPrezime(), korisnik.getKorisnickoIme(), korisnik.getLozinka(), korisnik.getId());
            unosUDnevnik(korisnik.getKorisnickoIme(), "SOAP", "azuriran korisnik " + korisnik.getKorisnickoIme());
            return true;
        } else {
            unosUDnevnik("NEISPRAVNA AUTENTIKACIJA", "SOAP", "Nepostojeći korisnik");
        }
        return false;
    }

    @WebMethod(operationName = "dohvatiListuKorisnika")
    public List<Korisnik> dohvatiListuKorisnika(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            List<Korisnik> lista = new ArrayList<>();
            lista = dohvatiListuKorisnikaIzBP();
            unosUDnevnik(korisnickoIme, "SOAP", "Dohvaćena lista korisnika");
            return lista;
        } else {
            unosUDnevnik("NEISPRAVNA AUTENTIKACIJA", "SOAP", "Nepostojeći korisnik");
    }
        return null;
    }

    @WebMethod(operationName = "dodatnoUdaljenostAerodroma")
    public double dodatnoUdaljenostAerodroma(@WebParam(name = "icaoPocetni") String icaoPocetni, @WebParam(name = "icaoKraj") String icaoKraj,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            Aerodrom pocetno = preuzmiNazivAerodroma(icaoPocetni);
            Aerodrom kraj = preuzmiNazivAerodroma(icaoKraj);
            double izracun = izracunajUdaljenost(pocetno.getLokacija(), kraj.getLokacija());
            unosUDnevnik(korisnickoIme, "SOAP", "Dohvaćena udaljenost " + String.valueOf(izracun));
            return izracun;
        } else {
            unosUDnevnik("NEISPRAVNA AUTENTIKACIJA", "SOAP", "Nepostojeći korisnik");
        }
        return 0;
    }

    @WebMethod(operationName = "dodatnoUdaljenostOd")
    public List<Aerodrom> dodatnoUdaljenostOd(@WebParam(name = "icao") String icao,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "pocetnaUdaljenost") double pocetnaUdaljenost, @WebParam(name = "krajnaUdaljenost") double krajnaUdaljenost) {
        ucitajKonfiguraciju();
        vrijemePocetka = System.currentTimeMillis();
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            List<Aerodrom> listaInterval = new ArrayList<>();
            Aerodrom a = preuzmiNazivAerodroma(icao);
            List<Aerodrom> sviDodaniAerodromi = dohvatiPodatkeAerodromUListu();
            double udaljenost = 0;
            for (Aerodrom aero : sviDodaniAerodromi) {
                udaljenost = izracunajUdaljenost(a.getLokacija(), aero.getLokacija());
                if (udaljenost >= pocetnaUdaljenost && udaljenost <= krajnaUdaljenost) {
                    listaInterval.add(aero);
                }
            }
            unosUDnevnik(korisnickoIme, "SOAP", "Dohvaćeni aerodromi u određenom radijusu");
            return listaInterval;
        } else {
            unosUDnevnik("NEISPRAVNA AUTENTIKACIJA", "SOAP", "Nepostojeći korisnik");
        }
        return null;
    }

    private List<AvionLeti> provjeriJeLiPoletio(String icao, int odVremena, int doVremena) {
        try {
            List<AvionLeti> listaAviona = new ArrayList<>();
            try (Connection con = DriverManager.getConnection(url, username, password)) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT=? AND FIRSTSEEN >= ? AND LASTSEEN <=? ORDER BY FIRSTSEEN ASC");
                ps.setString(1, icao);
                ps.setInt(2, odVremena);
                ps.setInt(3, doVremena);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    AvionLeti avion = new AvionLeti();
                    avion.setIcao24(rs.getString("ICAO24"));
                    avion.setFirstSeen(rs.getInt("FIRSTSEEN"));
                    avion.setEstDepartureAirport(rs.getString("estDepartureAirport"));
                    avion.setLastSeen(rs.getInt("lastSeen"));
                    avion.setEstArrivalAirport(rs.getString("estArrivalAirport"));
                    avion.setCallsign(rs.getString("callsign"));
                    avion.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
                    avion.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
                    avion.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
                    avion.setEstDepartureAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
                    avion.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
                    avion.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));

                    listaAviona.add(avion);
                }
            }
            return listaAviona;
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private AvionLeti dohvatiPodatkeZaAvionLeti(String icao) {
        AvionLeti avionLeti = new AvionLeti();
        try {
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT=? ORDER BY STORED DESC LIMIT 1")) {
                ps.setString(1, icao);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    postaviVrijednostiIzBP(rs, avionLeti);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return avionLeti;
    }

    private List<AvionLeti> dohvatiPodatkeZaAvionLetiPremaBroju(int broj, String icao) {
        List<AvionLeti> avionLeti = new ArrayList<>();
        try {
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT=? ORDER BY STORED DESC LIMIT ?")) {
                ps.setString(1, icao);
                ps.setInt(2, broj);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    AvionLeti avion = new AvionLeti();
                    avion.setIcao24(rs.getString("ICAO24"));
                    avion.setFirstSeen(rs.getInt("FIRSTSEEN"));
                    avion.setEstDepartureAirport(rs.getString("estDepartureAirport"));
                    avion.setLastSeen(rs.getInt("lastSeen"));
                    avion.setEstArrivalAirport(rs.getString("estArrivalAirport"));
                    avion.setCallsign(rs.getString("callsign"));
                    avion.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
                    avion.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
                    avion.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
                    avion.setEstDepartureAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
                    avion.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
                    avion.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));
                    avionLeti.add(avion);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return avionLeti;
    }

    private void postaviVrijednostiIzBP(ResultSet rs, AvionLeti avion) {
        try {
            avion.setIcao24(rs.getString("ICAO24"));
            avion.setFirstSeen(rs.getInt("FIRSTSEEN"));
            avion.setEstDepartureAirport(rs.getString("estDepartureAirport"));
            avion.setLastSeen(rs.getInt("lastSeen"));
            avion.setEstArrivalAirport(rs.getString("estArrivalAirport"));
            avion.setCallsign(rs.getString("callsign"));
            avion.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
            avion.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
            avion.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
            avion.setEstDepartureAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
            avion.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
            avion.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<AvionLeti> dohvatiInterval(String icao, int odVremena, int doVremena) {
        try {
            List<AvionLeti> avioni = new ArrayList<>();

            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM AIRPLANES where estDepartureAirport=?  AND firstSeen BETWEEN ? AND ? ORDER BY FIRSTSEEN")) {
                ps.setString(1, icao);
                ps.setInt(2, odVremena);
                ps.setInt(3, doVremena);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    AvionLeti avion = new AvionLeti();
                    avion.setIcao24(rs.getString("ICAO24"));
                    avion.setFirstSeen(rs.getInt("FIRSTSEEN"));
                    avion.setEstDepartureAirport(rs.getString("estDepartureAirport"));
                    avion.setLastSeen(rs.getInt("lastSeen"));
                    avion.setEstArrivalAirport(rs.getString("estArrivalAirport"));
                    avion.setCallsign(rs.getString("callsign"));
                    avion.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
                    avion.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
                    avion.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
                    avion.setEstDepartureAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
                    avion.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
                    avion.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));
                    avioni.add(avion);
                }
            }
            return avioni;
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<String> nazivAerodroma(String icao, int odVremena, int doVremena) {
        try {
            List<String> lista = new ArrayList<>();
            List<AvionLeti> listaAviona = new ArrayList<>();
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM AIRPLANES WHERE icao24=? AND LASTSEEN BETWEEN ? AND ? ORDER BY LASTSEEN")) {
                ps.setString(1, icao);
                ps.setInt(2, odVremena);
                ps.setInt(3, doVremena);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    AvionLeti avion = new AvionLeti();
                    avion.setIcao24(rs.getString("icao24"));
                    avion.setFirstSeen(rs.getInt("firstSeen"));
                    avion.setEstDepartureAirport(rs.getString("estDepartureAirport"));
                    avion.setLastSeen(rs.getInt("lastSeen"));
                    avion.setEstArrivalAirport(rs.getString("estArrivalAirport"));
                    avion.setCallsign(rs.getString("callsign"));
                    avion.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
                    avion.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
                    avion.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
                    avion.setEstDepartureAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
                    avion.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
                    avion.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));
                    Aerodrom a = dohvatiIcaoAerodroma(avion.getEstDepartureAirport());
                    lista.add(a.getNaziv());
                }
                return lista;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Aerodrom dohvatiIcaoAerodroma(String icao) {
        try {
            Aerodrom a = null;
            ucitajKonfiguraciju();
            Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM AIRPORTS WHERE IDENT=?");
            ps.setString(1, icao);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                a = new Aerodrom(
                        rs.getString("ident"),
                        rs.getString("name"),
                        rs.getString("iso_country"),
                        null);
            }
            return a;
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private MeteoPodaci dohvatiMeteoPodatke(Aerodrom a) {
        ucitajKonfiguraciju();
        String api = konf.dajPostavku("OpenWeatherMap.apikey");
        api = api.replaceAll("(\\r|\\n)", "");
        OWMKlijent owm = new OWMKlijent(api);
        String lat = a.getLokacija().getLatitude().replaceAll(" ", "");
        String lon = a.getLokacija().getLongitude().replaceAll(" ", "");
        MeteoPodaci mp = owm.getRealTimeWeather(lat, lon);
        return mp;
    }

    @WebMethod(operationName = "dohvatiPodatkeAerodrom")
    public Aerodrom dohvatiPodatkeAerodrom(String icao) {
        try {
            Aerodrom a = new Aerodrom();
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM AIRPORTS WHERE IDENT=?")) {
                ps.setString(1, icao);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    a = postaviPodatkeAerodrom(rs);
                }
            }
            return a;
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @WebMethod(operationName = "dohvatiPodatkeAerodromUListu")
    public List<Aerodrom> dohvatiPodatkeAerodromUListu() {
        try {
            ucitajKonfiguraciju();
            List<Aerodrom> a = new ArrayList<>();
            Aerodrom aerodrom = null;
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM AIRPORTS")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String icao = rs.getString("IDENT");
                    String naziv = rs.getString("NAME");
                    String iso_country = rs.getString("ISO_COUNTRY");
                    String kordinate = rs.getString("COORDINATES");
                    String[] uredi = kordinate.split(",");
                    Lokacija lokacija = new Lokacija(uredi[0], uredi[1]);
                    aerodrom = new Aerodrom(icao, naziv, iso_country, lokacija);
                    a.add(aerodrom);
                }
                return a;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Aerodrom postaviPodatkeAerodrom(ResultSet rs) {
        Aerodrom a = new Aerodrom();
        try {
            String icao = rs.getString("IDENT");
            String naziv = rs.getString("NAME");
            String iso_country = rs.getString("ISO_COUNTRY");
            String kordinate = rs.getString("COORDINATES");
            String[] uredi = kordinate.split(",");
            Lokacija lokacija = new Lokacija(uredi[0], uredi[1]);
            a = new Aerodrom(icao, naziv, iso_country, lokacija);
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return a;
    }

    private void dodajKorisnikaUBazu(String ime, String prezime, String korisnickoIme, String lozinka) {
        try {
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("INSERT INTO korisnici(username,password,firstName,lastName) VALUES(?,?,?,?)")) {
                ps.setString(1, korisnickoIme);
                ps.setString(2, lozinka);
                ps.setString(3, ime);
                ps.setString(4, prezime);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void azurirajKorisnikaUBP(String ime, String prezime, String korisnickoIme, String lozinka, int id) {
        try {
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("UPDATE korisnici SET username=?,password=?, firstName=?, lastName=? where id=?")) {
                ps.setString(1, korisnickoIme);
                ps.setString(2, lozinka);
                ps.setString(3, ime);
                ps.setString(4, prezime);
                ps.setInt(5, id);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<Korisnik> dohvatiListuKorisnikaIzBP() {
        try {
            List<Korisnik> lista;
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM korisnici")) {
                ResultSet rs = ps.executeQuery();
                lista = new ArrayList<>();
                while (rs.next()) {
                    Korisnik k = postaviKorisnikeIzBP(rs);
                    lista.add(k);
                }
            }
            return lista;
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Korisnik postaviKorisnikeIzBP(ResultSet rs) {

        try {
            Korisnik k;
            String korisnickoIme = rs.getString("username");
            String lozinka = rs.getString("password");
            String ime = rs.getString("firstName");
            String prezime = rs.getString("lastName");
            int id = rs.getInt("id");
            k = new Korisnik(id, ime, prezime, korisnickoIme, lozinka);
            k.setId(id);
            return k;
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Aerodrom preuzmiNazivAerodroma(String icao) {
        try {
            ucitajKonfiguraciju();
            Aerodrom aerodrom = null;
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM AIRPORTS WHERE IDENT=?")) {
                ps.setString(1, icao);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    icao = rs.getString("IDENT");
                    String naziv = rs.getString("NAME");
                    String iso_country = rs.getString("ISO_COUNTRY");
                    String kordinate = rs.getString("COORDINATES");
                    String[] uredi = kordinate.split(",");
                    Lokacija lokacija = new Lokacija(uredi[0], uredi[1]);
                    aerodrom = new Aerodrom(icao, naziv, iso_country, lokacija);
                }
            }
            return aerodrom;
        } catch (SQLException ex) {
            Logger.getLogger(AIRPWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double izracunajUdaljenost(Lokacija pocetno, Lokacija kraj) {
        double lat1 = Double.parseDouble(pocetno.getLatitude());
        double lat2 = Double.parseDouble(kraj.getLatitude());
        double long1 = Double.parseDouble(pocetno.getLongitude());
        double long2 = Double.parseDouble(kraj.getLongitude());
        double racun = long1 - long2;
        double udaljenost = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(racun));
        udaljenost = Math.acos(udaljenost);
        udaljenost = rad2deg(udaljenost);
        udaljenost = udaljenost * 60 * 1.1515;
        return udaljenost;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void unosUDnevnik(String korisnickoIme, String vrsta, String odgovor) {
        try {
            Timestamp vrijemeUnosa = new Timestamp(System.currentTimeMillis());
            Long trajanjeObrade = System.currentTimeMillis() - vrijemePocetka;
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("INSERT INTO DNEVNIK(korisnickoIme,ipAdresa,vrijemePocetka,trajanjeObrade,vrstaZapisa,sadrzaj,odgovor) VALUES(?,?,?,?,?,?,?)")) {
                ps.setString(1, korisnickoIme);
                ps.setString(2, "");
                ps.setTimestamp(3, vrijemeUnosa);
                ps.setInt(4, trajanjeObrade.intValue());
                ps.setString(5, vrsta);
                ps.setString(6, "ZAHTJEV KORISNIKA");
                ps.setString(7, odgovor);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ucitajKonfiguraciju() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            SlusacAplikacije sa = new SlusacAplikacije();
            bpk = (BP_Konfiguracija) sa.getSc().getAttribute("BP_Konfig");
            url = bpk.getServerDatabase() + bpk.getUserDatabase();
            username = bpk.getUserUsername();
            password = bpk.getUserPassword();
            konf = (Konfiguracija) sa.getSc().getAttribute("Konfiguracija");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreuzimanjeAviona.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public boolean postojiKorisnikUBazi(String korisnickoIme, String lozinka) {
        try {
            ucitajKonfiguraciju();
            try {
                Class.forName(bpk.getDriverDatabase());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            try (Connection con = DriverManager.getConnection(url, username, password);
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
}
