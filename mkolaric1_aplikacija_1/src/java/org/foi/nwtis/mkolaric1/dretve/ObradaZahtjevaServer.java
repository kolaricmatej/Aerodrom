/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.dretve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkolaric1.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matej
 */
public class ObradaZahtjevaServer extends Thread {

    ServletContext sc;
    private ServerSocket serverSocket;
    private Konfiguracija konf;
    private BP_Konfiguracija bpk;
    private int port;
    private PreuzimanjeAviona preuzimanjeAviona;
    private boolean kraj=false;
    
    private boolean preuzmiKomande=true;
    
    public ObradaZahtjevaServer(PreuzimanjeAviona pa) {
        SlusacAplikacije sa = new SlusacAplikacije();
        sc=sa.getSc();
        konf=(Konfiguracija) sc.getAttribute("Konfiguracija");
        bpk=(BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        port=Integer.parseInt(konf.dajPostavku("port"));
        preuzimanjeAviona=pa;
    }

    @Override
    public void interrupt() {
        this.kraj=true;
        super.interrupt(); 
        if(!serverSocket.isClosed()&&serverSocket!=null){
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(ObradaZahtjevaServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void run() {
        try {
            serverSocket=new ServerSocket(port);
            while(!kraj){
                if(serverSocket!=null&& !serverSocket.isClosed()){
                    System.out.println("OBRADA ZAHTJEVA SERVER STIGO!");
                   Socket socket=serverSocket.accept();
                    System.out.println("PROSLA LINIJA ACCEPT");
                   ObradaZahtjeva oz= new ObradaZahtjeva("Dretva obrade zahjteva", konf, bpk, socket, this, preuzimanjeAviona);
                   oz.start();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ObradaZahtjevaServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public synchronized void start() {
        super.start(); 
    }

    public boolean isPreuzmiKomande() {
        return preuzmiKomande;
    }

    public void setPreuzmiKomande(boolean preuzmiKomande) {
        this.preuzmiKomande = preuzmiKomande;
    }
    
    
}
