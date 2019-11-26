package org.foi.nwtis.mkolaric1.web.podaci;

/**
 *
 * @author Matej Kolarić
 *
 * Klasa koja je zadužena za unos korisnika
 */
public class Korisnik {

    private int id;
    private String ime;
    private String prezime;
    private String korisnickoIme;
    private String lozinka;

    /**
     * Konstruktor klase korisnika
     *
     * @param korisnicko_ime korisničko ime korisnika
     * @param lozinka lozinka kojom se dopušta pristup serveru
     */
    public Korisnik(String ime, String prezime, String korisnickoime, String lozinka) {
        this.korisnickoIme = korisnickoime;
        this.lozinka = lozinka;
        this.ime = ime;
        this.prezime = prezime;
    }

    public Korisnik(int id, String ime, String prezime, String korisnickoime, String lozinka) {
        this.id = id;
        this.korisnickoIme = korisnickoime;
        this.lozinka = lozinka;
        this.ime = ime;
        this.prezime = prezime;
    }

    public Korisnik() {
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
