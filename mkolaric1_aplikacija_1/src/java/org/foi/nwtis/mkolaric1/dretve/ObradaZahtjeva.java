/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.dretve;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.mkolaric1.ws.AerodromWS;

/**
 *
 * @author Matej
 */
public class ObradaZahtjeva extends Thread {

    private Konfiguracija konf;
    private Socket socket;
    private BP_Konfiguracija bpk;
    private String username;
    private String password;
    private String url;
    private String korisnickoIme = "";
    private String lozinka = "";
    private String zadanaNaredba = "";
    private ObradaZahtjevaServer ozs;
    private PreuzimanjeAviona pa;
    private Long vrijemePocetka;
    private String odgovor = "";
    private String svnKorime;
    private String svnLozinka;
    private static int id;
    
    public ObradaZahtjeva() {
    }

    public ObradaZahtjeva(String nazivDretve, Konfiguracija konf, BP_Konfiguracija bpk, Socket socket, ObradaZahtjevaServer ozs, PreuzimanjeAviona pa) {
        super(nazivDretve);
        this.konf = konf;
        this.bpk = bpk;
        this.socket = socket;
        this.ozs = ozs;
        this.pa = pa;
        ucitajKonfiguraciju();
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void run() {
        vrijemePocetka = System.currentTimeMillis();
        String porukaKorisnika = primiOdgovor();
        int naredba = provjeraNaredbe(porukaKorisnika);
        obradiNaredbu(naredba, porukaKorisnika);

    }

    @Override
    public synchronized void start() {
        super.start();
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
            svnKorime=konf.dajPostavku("korisnik.ime");
            svnLozinka=konf.dajPostavku("korisnik.lozinka");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreuzimanjeAviona.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    private String primiOdgovor() {
        System.out.println("USO U PRIMI ODGOVOR DRETVE!");
        String poruka = "";
        try {
            InputStream is = socket.getInputStream();
            int znak;
            StringBuilder stringBuilder = new StringBuilder();
            while ((znak = is.read()) != -1) {
                stringBuilder.append((char) znak);
            }
            poruka = stringBuilder.toString();
            System.out.println("PORUKA PRIMLJENA:" + poruka);
        } catch (IOException ex) {
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return poruka;
    }

    private void posaljiOdgovor(String poruka) {
        try {
            OutputStream os = socket.getOutputStream();
            os.write(poruka.getBytes());
            os.flush();
            socket.shutdownOutput();
            //os.close();
            //socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int provjeraNaredbe(String naredba) {
        naredba = naredba.replaceAll("\\r\\n", "");
        Pattern autentikacija = Pattern.compile("(KORISNIK) ([a-zA-Z0-9]+); LOZINKA ([^\\s]+){1,30};");
        Pattern akcijePosluzitelj = Pattern.compile("KORISNIK ([a-zA-Z0-9]+); LOZINKA .+; ((PAUZA)|(KRENI)|(PASIVNO)|(AKTIVNO)|(STANI)|(STANJE));");
        Pattern akcijeGrupe = Pattern.compile("(KORISNIK) ([a-zA-Z0-9]+); LOZINKA .+; GRUPA ((DODAJ)|(PREKID)|(KRENI)|(PAUZA)|(STANJE));");

        Matcher m = akcijePosluzitelj.matcher(naredba);
        boolean naredbaPosluzitelja = m.matches();
        if (naredbaPosluzitelja) {
            return 2;
        }
        m = akcijeGrupe.matcher(naredba);
        boolean naredbaGrupe = m.matches();
        if (naredbaGrupe) {
            return 3;
        }
        m = autentikacija.matcher(naredba);
        boolean naredbaAutentikacije = m.matches();
        if (naredbaAutentikacije) {
            return 1;
        } else {
            return 0;
        }
    }

    private void obradiNaredbu(int vrsta, String naredba) {
        if (vrsta != 0) {
            switch (vrsta) {
                case (1):
                    obradaAutentikacije(naredba);
                    break;
                case (2):
                    obradiKomandePoslužitelja(naredba);
                    break;
                case (3):
                    obradiKomandeGrupe(naredba);
                    break;
            }
        } else {
            posaljiOdgovor("Neispravna naredba");
        }
    }

    private String dohvatiKorisnickoIme(String naredba) {
        String[] naredbe = naredba.split(" ");
        korisnickoIme = naredbe[1];
        korisnickoIme = korisnickoIme.substring(0, korisnickoIme.length() - 1);
        return korisnickoIme;
    }

    private String dohvatiKorisnickuLozinku(String naredba) {
        String[] naredbe = naredba.split(" ");
        lozinka = naredbe[3];
        lozinka = lozinka.substring(0, lozinka.length() - 1);
        return lozinka;
    }

    private String dohvatiNaredbuKorisnika(String naredba) {
        String[] naredbe = naredba.split(" ");
        zadanaNaredba = naredbe[4];
        zadanaNaredba = zadanaNaredba.substring(0, zadanaNaredba.length() - 1);
        return zadanaNaredba;
    }

    private String dohvatiNaredbuGrupe(String naredba) {
        String[] naredbe = naredba.split(" ");
        zadanaNaredba = naredbe[5];
        zadanaNaredba = zadanaNaredba.substring(0, zadanaNaredba.length() - 1);
        return zadanaNaredba;
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
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM korisnici WHERE username=? and password=?")) {
                ps.setString(1, korisnickoIme);
                ps.setString(2, lozinka);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void obradaAutentikacije(String naredba) {
        korisnickoIme = dohvatiKorisnickoIme(naredba);
        lozinka = dohvatiKorisnickuLozinku(naredba);
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            posaljiOdgovor("OK 10;");
            slanjePorukaJMS(naredba);
        } else {
            posaljiOdgovor("ERR 11;");
        }
    }

    private void obradiKomandePoslužitelja(String naredba) {
        String komanda = dohvatiNaredbuKorisnika(naredba);
        korisnickoIme = dohvatiKorisnickoIme(naredba);
        lozinka = dohvatiKorisnickuLozinku(naredba);
        if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
            switch (komanda) {
                case ("PAUZA"):
                    pauzirajPosluzitelja();
                    slanjePorukaJMS(naredba);
                    break;
                case ("KRENI"):
                    pokreniPosluzitelja();
                    slanjePorukaJMS(naredba);
                    break;
                case ("PASIVNO"):
                    pasivniPosluzitelj();
                    slanjePorukaJMS(naredba);
                    break;
                case ("AKTIVNO"):
                    aktivniPosluzitelj();
                    slanjePorukaJMS(naredba);
                    break;
                case ("STANI"):
                    staniPosluzitelj();
                    slanjePorukaJMS(naredba);
                    break;
                case ("STANJE"):
                    stanjePosluzitelj();
                    slanjePorukaJMS(naredba);
                    break;
                default:
                    posaljiOdgovor("Neispravna komanda");
            }
        }
    }

    private void obradiKomandeGrupe(String naredba) {
        if (ozs.isPreuzmiKomande()) {
            String komanda = dohvatiNaredbuGrupe(naredba);
            korisnickoIme = dohvatiKorisnickoIme(naredba);
            lozinka = dohvatiKorisnickuLozinku(naredba);
            if (postojiKorisnikUBazi(korisnickoIme, lozinka)) {
                switch (komanda) {
                    case ("DODAJ"):
                        dodajGrupu();
                        break;
                    case ("PREKID"):
                        prekiniGrupu();
                        break;
                    case ("KRENI"):
                        kreniGrupu();
                        break;
                    case ("PAUZA"):
                        pauzirajGrupu();
                        break;
                    case ("STANJE"):
                        stanjeGrupe();
                        break;
                    default:
                        posaljiOdgovor("Neispravna komanda");
                }
            }
        } else {
            posaljiOdgovor("Blokirano");
        }
    }

    public void pauzirajPosluzitelja() {
        boolean pauzirajKomande = ozs.isPreuzmiKomande();
        if (pauzirajKomande) {
            ozs.setPreuzmiKomande(false);
            unosUDnevnik(korisnickoIme, "SOCKET", "OK 10;");
            posaljiOdgovor("OK 10 -PAUZA;");
        } else {
            unosUDnevnik(korisnickoIme, "SOCKET", "ERR 12;");
            posaljiOdgovor("ERR 12;");
        }
    }

    private void pokreniPosluzitelja() {
        boolean preuzmiKomnade = ozs.isPreuzmiKomande();
        if (!preuzmiKomnade) {
            ozs.setPreuzmiKomande(true);
            unosUDnevnik(korisnickoIme, "SOCKET", "OK 10;");
            posaljiOdgovor("OK 10-KRENI;");
        } else {
            unosUDnevnik(korisnickoIme, "SOCKET", "ERR 13;");
            posaljiOdgovor("ERR 13;");
        }
    }

    private void pasivniPosluzitelj() {
        boolean dretvaAerodroma = pa.isDretvaAktivna();
        if (dretvaAerodroma) {
            pa.setDretvaAktivna(false);
            unosUDnevnik(korisnickoIme, "SOCKET", "OK 10;");
            posaljiOdgovor("OK 10-PASIVNO;");
        } else {
            unosUDnevnik(korisnickoIme, "SOCKET", "ERR 14;");
            posaljiOdgovor("ERR 14;");
        }
    }

    private void aktivniPosluzitelj() {
        boolean dretvaAerodroma = pa.isDretvaAktivna();
        if (!dretvaAerodroma) {
            pa.setDretvaAktivna(true);
            unosUDnevnik(korisnickoIme, "SOCKET", "OK 10;");
            posaljiOdgovor("OK 10-AKTIVNO;");
        } else {
            unosUDnevnik(korisnickoIme, "SOCKET", "ERR 15;");
            posaljiOdgovor("ERR 15;");
        }
    }

    private void staniPosluzitelj() {
        boolean dretvaAerodroma = pa.isDretvaAktivna();
        boolean preuzimanjeKomandi = ozs.isPreuzmiKomande();
        if (dretvaAerodroma && preuzimanjeKomandi) {
            pa.interrupt();
            ozs.interrupt();
            unosUDnevnik(korisnickoIme, "SOCKET", "OK 10;");
            posaljiOdgovor("OK 10- STANI;");
        } else {
            unosUDnevnik(korisnickoIme, "SOCKET", "ERR 16;");
            posaljiOdgovor("ERR 16;");
        }
    }

    private void stanjePosluzitelj() {
        boolean dretvaAerodroma = pa.isDretvaAktivna();
        boolean preuzimanjeKomandi = ozs.isPreuzmiKomande();
        if (preuzimanjeKomandi) {
            if (dretvaAerodroma) {
                unosUDnevnik(korisnickoIme, "SOCKET", "OK 11;");
                posaljiOdgovor("OK 11;");
            } else {
                unosUDnevnik(korisnickoIme, "SOCKET", "OK 12;");
                posaljiOdgovor("OK 12");
            }
        } else {
            if (dretvaAerodroma) {
                unosUDnevnik(korisnickoIme, "SOCKET", "OK 13;");
                posaljiOdgovor("OK 13;");
            } else {
                unosUDnevnik(korisnickoIme, "SOCKET", "OK 14;");
            }
        }
    }

    private void dodajGrupu() {
        String status = AerodromWS.dajStatusGrupe(svnKorime, svnLozinka).value();
        if (!status.equals("REGISTRIRAN") || status.equals("NEPOSTOJI")) {
            AerodromWS.registrirajGrupu(svnKorime, svnLozinka);
            unosUDnevnik(korisnickoIme, "SOCKET", "OK 2O;");
            posaljiOdgovor("OK 20 - REGISTRIRANA;");
        } else{
            unosUDnevnik(korisnickoIme, "SOCKET", "ERR 20;");
            posaljiOdgovor("ERR 20;");
        }

    }

    private void prekiniGrupu() {
        String status = AerodromWS.dajStatusGrupe(svnKorime, svnLozinka).value();
        if (status.equals("REGISTRIRAN") || status.equals("AKTIVAN")) {
            AerodromWS.deregistrirajGrupu(svnKorime, svnLozinka);
            unosUDnevnik(korisnickoIme, "SOCKET", "OK 20;");
            posaljiOdgovor("OK 20 - GRUPA JE DEREGISTRIRANA;");
        } else {
            unosUDnevnik(korisnickoIme, "SOCKET", "ERR 21;");
            posaljiOdgovor("ERR 21;");
        }
    }

    private void pauzirajGrupu() {
        String status = AerodromWS.dajStatusGrupe(svnKorime, svnLozinka).value();
        if (status.equals("AKTIVAN")||status.equals("REGISTRIRAN") ) {
            AerodromWS.blokirajGrupu(svnKorime, svnLozinka);
            unosUDnevnik(korisnickoIme, "SOCKET", "OK 20;");
            posaljiOdgovor("OK 20 - PAUZIRANO;");
        } else if (status.equals("NEAKTIVAN")||status.equals("BLOKIRAN")) {
            unosUDnevnik(korisnickoIme, "SOCKET", "ERR 23;");
            posaljiOdgovor("ERR 23;");
        } else if (status.equals("NEPOSTOJI")) {
            unosUDnevnik(korisnickoIme, "SOCKET", "ERR 21;");
            posaljiOdgovor("ERR 21;");
        }else{
            posaljiOdgovor("Ne moguće izvest akciju");
        }

    }

    private void kreniGrupu() {
        String status = AerodromWS.dajStatusGrupe(svnKorime, svnLozinka).value();
        if (status.equals("NEPOSTOJI") || status.equals("DEREGISTRIRAN")) {
            unosUDnevnik(korisnickoIme, status, "ERR 21;");
            posaljiOdgovor("ERR 21;");
        }else if (status.equals("NEAKTIVAN")|| status.equals("BLOKIRAN")) {
            AerodromWS.aktivirajGrupu(svnKorime, svnLozinka);
            unosUDnevnik(korisnickoIme, status, "OK 20;");
            posaljiOdgovor("OK 20; - GRUPA AKTIVIRANA");
        } else if (status.equals("AKTIVAN")|| status.equals("REGISTRIRAN")) {
            unosUDnevnik(korisnickoIme, status, "ERR 22;");
            posaljiOdgovor("ERR 22;");
        }else{
            posaljiOdgovor("Ne moguće izvest akciju");
        }
    }

    private void stanjeGrupe() {
        String status = AerodromWS.dajStatusGrupe(svnKorime, svnLozinka).value();
        if (status.equals("AKTIVAN") || status.equals("REGISTRIRAN")) {
            unosUDnevnik(korisnickoIme, status, "OK 21;");
            posaljiOdgovor("OK 21;");
        } else if (status.equals("BLOKIRAN") || status.equals("NEAKTIVAN")) {
            unosUDnevnik(korisnickoIme, status, "OK 22;");
            posaljiOdgovor("OK 22;");
        } else if (status.equals("NEPOSTOJI")||status.equals("DEREGISTRIRAN")) {
            unosUDnevnik(korisnickoIme, status, "ERR 21;");
            posaljiOdgovor("ERR 21;");
        }else{
            posaljiOdgovor("Ne moguće izvest akciju");
        }
    }

    public void unosUDnevnik(String korisnickoIme, String vrsta, String odgovor) {
        try {
            Timestamp vrijemeUnosa = new Timestamp(System.currentTimeMillis());
            Long trajanjeObrade = System.currentTimeMillis() - vrijemePocetka;
            ucitajKonfiguraciju();
            try (Connection con = DriverManager.getConnection(url, username, password);
                    PreparedStatement ps = con.prepareStatement("INSERT INTO DNEVNIK(korisnickoIme, ipAdresa,vrijemePocetka,trajanjeObrade,vrstaZapisa,sadrzaj, odgovor) VALUES(?,?,?,?,?,?,?)")) {
                ps.setString(1, korisnickoIme);
                ps.setString(2, socket.getInetAddress().toString());
                ps.setTimestamp(3, vrijemeUnosa);
                ps.setInt(4, trajanjeObrade.intValue());
                ps.setString(5, vrsta);
                ps.setString(6, zadanaNaredba);
                ps.setString(7, odgovor);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Message createJMSMessageForjmsNWTiS_mkolaric1_1(Session session, Object messageData) throws JMSException {
        // TODO create and populate message to send
        TextMessage tm = session.createTextMessage();
        tm.setText(messageData.toString());
        return tm;
    }

    private void sendJMSMessageToNWTiS_mkolaric1_1(String messageData) throws JMSException, NamingException {
        Context c = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) c.lookup("java:comp/env/jms/NWTiS_mkolaric1_1_factory");
        javax.jms.Connection conn = null;
        Session s = null;
        try {
            conn = cf.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
            Destination destination = (Destination) c.lookup("java:comp/env/jms/NWTiS_mkolaric1_1");
            MessageProducer mp = s.createProducer(destination);
            mp.send(createJMSMessageForjmsNWTiS_mkolaric1_1(s, messageData));
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    public void slanjePorukaJMS(String naredba){
        JsonObject jo= new JsonObject();
        jo.addProperty("id",id);
        jo.addProperty("komanda", naredba);
        jo.addProperty("vrijeme",new SimpleDateFormat("dd.MM.yyyy HH.mm.ss.SSS").format(new Timestamp(System.currentTimeMillis())));
        try {
            sendJMSMessageToNWTiS_mkolaric1_1(jo.toString());
        } catch (JMSException | NamingException ex) {
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        id++;
    }
    
}
