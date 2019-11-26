/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.web.podaci;

/**
 *
 * @author Matej
 */
public class JmsPoruka {
    
    private int id;
    private String komanda;
    private String vrijeme;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKomanda() {
        return komanda;
    }

    public void setKomanda(String komanda) {
        this.komanda = komanda;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    public JmsPoruka() {
    }

    public JmsPoruka(int id, String komanda, String vrijeme) {
        this.id = id;
        this.komanda = komanda;
        this.vrijeme = vrijeme;
    }
    
    
}
