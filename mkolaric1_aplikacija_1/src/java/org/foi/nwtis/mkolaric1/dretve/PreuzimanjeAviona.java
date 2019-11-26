/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.dretve;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mkolaric1.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 * Klasa koja pokreće dretvu te služi za preuzimanje aerodroma iz tablice
 * MYAIRPORTS u tablicu AIRPLANES
 *
 * @author nwtis_1
 */
public class PreuzimanjeAviona extends Thread {

    public boolean kraj = false;
    public static Konfiguracija konf;
    private BP_Konfiguracija bpk;
    int inicijalniPocetakIntervala;
    int pocetakIntervala;
    int krajIntervala;
    int trajanjeIntervala;
    int ciklusDretve;
    int redniBrojCiklusa;
    private String korisnickoIme;
    private String lozinka;
    private String url;
    private boolean dretvaAktivna = true;

    @Override
    public void interrupt() {
        dretvaAktivna = false;
        super.interrupt();
    }

    @Override
    public void run() {
        while (dretvaAktivna) {
            BufferedWriter bw = null;
            ucitajKonfiguraciju();
            String k = konf.dajPostavku("OpenSkyNetwork.korisnik");
            String l = konf.dajPostavku("OpenSkyNetwork.lozinka");
            OSKlijent oSKlijent = new OSKlijent(k, l);
            try (Connection con = DriverManager.getConnection(url, korisnickoIme, lozinka);
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM MYAIRPORTS");
                    ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    List<AvionLeti> departures = oSKlijent.getDepartures(rs.getString("IDENT"), pocetakIntervala, krajIntervala);
                    Collections.sort(departures, (AvionLeti a1, AvionLeti a2) -> {
                        if (a1.getFirstSeen() == a2.getFirstSeen()) {
                            return 0;
                        } else {
                            return a1.getFirstSeen() < a2.getFirstSeen() ? -1 : 1;
                        }
                    });
                    for (AvionLeti a : departures) {
                        if (!provjeriPodatkeIzAirplanes(a.getIcao24(), a.getFirstSeen())) {
                            unesiPodatkeAvionLeti(a);
                        }
                    }
                }

                bw = provjeraPostojanjaDatotekeNadzora();
                krajIntervala = pocetakIntervala + (trajanjeIntervala * 60 * 60);
                if (pocetakIntervala >= ((int) (new Date().getTime() / 1000))) {
                    pocetakIntervala = (int) (new Date().getTime() / 1000) - (inicijalniPocetakIntervala * 60 * 60);
                    krajIntervala = pocetakIntervala + (trajanjeIntervala * 60 * 60);
                    this.redniBrojCiklusa = 0;
                }
                pisiUDatotekuNadzora(bw);
                Thread.sleep(ciklusDretve * 60 * 1000);
            } catch (InterruptedException | SQLException ex) {
                Logger.getLogger(PreuzimanjeAviona.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
        ucitajKonfiguraciju();
        inicijalniPocetakIntervala = Integer.parseInt(konf.dajPostavku("preuzimanje.pocetak"));
        pocetakIntervala = (int) (new Date().getTime() / 1000) - (inicijalniPocetakIntervala * 60 * 60);
        trajanjeIntervala = Integer.parseInt(konf.dajPostavku("preuzimanje.trajanje"));
        ciklusDretve = Integer.parseInt(konf.dajPostavku("preuzimanje.ciklus"));
        krajIntervala = pocetakIntervala + (trajanjeIntervala * 60 * 60);
    }

    private boolean provjeriPodatkeIzAirplanes(String icao, int firstSeen) {
        try {
            ucitajKonfiguraciju();
            Connection con = DriverManager.getConnection(url, korisnickoIme, lozinka);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ICAO24=? AND FIRSTSEEN=?");
            ps.setString(1, icao);
            ps.setInt(2, firstSeen);
            ResultSet rs = ps.executeQuery();
            if (rs.next() == false) {
                ps.close();
                con.close();
                rs.close();
                return false;
            } else {
                ps.close();
                con.close();
                rs.close();
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreuzimanjeAviona.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void unesiPodatkeAvionLeti(AvionLeti a) throws SQLException {
        ucitajKonfiguraciju();
        if (a.getEstArrivalAirport() == null) {
            return;
        }
        String upit = "INSERT INTO AIRPLANES (ICAO24,FIRSTSEEN,ESTDEPARTUREAIRPORT,LASTSEEN,ESTARRIVALAIRPORT,CALLSIGN,estDepartureAirportHorizDistance,estDepartureAirportVertDistance,estArrivalAirportHorizDistance,estArrivalAirportVertDistance,departureAirportCandidatesCount,arrivalAirportCandidatesCount,STORED) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DriverManager.getConnection(url, korisnickoIme, lozinka);
                PreparedStatement ps = con.prepareStatement(upit)) {
            ps.setString(1, a.getIcao24());
            ps.setInt(2, a.getFirstSeen());
            ps.setString(3, a.getEstDepartureAirport());
            ps.setInt(4, a.getLastSeen());
            ps.setString(5, a.getEstArrivalAirport());
            ps.setString(6, a.getCallsign());
            ps.setInt(7, a.getEstDepartureAirportHorizDistance());
            ps.setInt(8, a.getEstDepartureAirportVertDistance());
            ps.setInt(9, a.getEstArrivalAirportHorizDistance());
            ps.setInt(10, a.getEstArrivalAirportVertDistance());
            ps.setInt(11, a.getDepartureAirportCandidatesCount());
            ps.setInt(12, a.getArrivalAirportCandidatesCount());
            ps.setTimestamp(13, new Timestamp(new Date().getTime()));
            ps.executeUpdate();
        }
    }

    public void ucitajKonfiguraciju() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            SlusacAplikacije sa = new SlusacAplikacije();
            bpk = (BP_Konfiguracija) sa.getSc().getAttribute("BP_Konfig");
            url = bpk.getServerDatabase() + bpk.getUserDatabase();
            korisnickoIme = bpk.getUserUsername();
            lozinka = bpk.getUserPassword();
            konf = (Konfiguracija) sa.getSc().getAttribute("Konfiguracija");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreuzimanjeAviona.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public BufferedWriter provjeraPostojanjaDatotekeNadzora() {
        ucitajKonfiguraciju();
        boolean postoji = true;
        BufferedWriter bw = null;
        try {
            InputStream datoteka = new FileInputStream(SlusacAplikacije.putanja + konf.dajPostavku("datoteka.nadzora"));

        } catch (FileNotFoundException ex) {
            postoji = false;
        } catch (IOException ex) {
            Logger.getLogger(PreuzimanjeAviona.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (postoji) {
            try {
                String putanja = SlusacAplikacije.sc.getRealPath("/WEB-INF") + File.separator + konf.dajPostavku("datoteka.nadzora");
                Konfiguracija datotekaJson = KonfiguracijaApstraktna.preuzmiKonfiguraciju(putanja);
                this.pocetakIntervala = Integer.parseInt(datotekaJson.dajPostavku("pocetakIntervala"));

                this.redniBrojCiklusa = Integer.parseInt(datotekaJson.dajPostavku("redniBrojCiklusa"));
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SlusacAplikacije.putanja + konf.dajPostavku("datoteka.nadzora"))));
            } catch (FileNotFoundException | NemaKonfiguracije | NeispravnaKonfiguracija ex) {
                Logger.getLogger(PreuzimanjeAviona.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.redniBrojCiklusa = 0;
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SlusacAplikacije.putanja + konf.dajPostavku("datoteka.nadzora"))));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PreuzimanjeAviona.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        return bw;
    }

    /**
     * Metoda zadužena za pisanje u datoteku u formatiranom zapisu koja prolazi
     * kroz 2 vrste dretvi: korisničke i servisne dretve
     *
     * @param bw parametar koji sprema u buffer i nakon toga vrši upis u
     * datoteku
     */
    public void pisiUDatotekuNadzora(BufferedWriter bw) {
        try {
            this.redniBrojCiklusa++;
            bw.write("{\"pocetakIntervala\": " + krajIntervala + ", " + "\"redniBrojCiklusa\": " + redniBrojCiklusa + "}" + "\n");
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(PreuzimanjeAviona.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public boolean isDretvaAktivna() {
        return dretvaAktivna;
    }

    public void setDretvaAktivna(boolean dretvaAktivna) {
        this.dretvaAktivna = dretvaAktivna;
    }

}
