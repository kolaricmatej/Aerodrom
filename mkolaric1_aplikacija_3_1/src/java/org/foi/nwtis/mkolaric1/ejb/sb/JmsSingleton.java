/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.ejb.sb;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import org.foi.nwtis.mkolaric1.web.podaci.JmsPoruka;

/**
 *
 * @author Matej
 */
@Startup
@Singleton
@LocalBean
public class JmsSingleton {

    private List<JmsPoruka> listaPoruka = new ArrayList<>();

    public List<JmsPoruka> getListaPoruka() {
        return listaPoruka;
    }

    public void setListaPoruka(List<JmsPoruka> listaPoruka) {
        this.listaPoruka = listaPoruka;
    }
    
}
