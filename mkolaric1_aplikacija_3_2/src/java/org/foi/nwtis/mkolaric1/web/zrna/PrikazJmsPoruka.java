/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import org.foi.nwtis.mkolaric1.ejb.sb.JmsSingleton;
import org.foi.nwtis.mkolaric1.web.podaci.JmsPoruka;

/**
 *
 * @author Matej
 */
@Named(value = "prikazJmsPoruka")
@SessionScoped
public class PrikazJmsPoruka implements Serializable {

    private List<JmsPoruka> listaPoruka=null;
    private JmsPoruka jp;
    
    @EJB
    private JmsSingleton jmsSingleton;
    public PrikazJmsPoruka() {
    }

    public List<JmsPoruka> getListaPoruka() {
        listaPoruka=jmsSingleton.getListaPoruka();
        return listaPoruka;
    }

    public void setListaPoruka(List<JmsPoruka> listaPoruka) {
        this.listaPoruka = listaPoruka;
    }
    public void obrisiPoruku(){
        if(jp!=null){
            listaPoruka.remove(jp);
            getListaPoruka();
        }
    }

    public JmsPoruka getJp() {
        return jp;
    }

    public void setJp(JmsPoruka jp) {
        this.jp = jp;
    }

}
