/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.web.pomocno;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Matej
 */
public class SlanjePoruka {

    private BP_Konfiguracija bpk;
    private Konfiguracija konf;
    private int port;

    public SlanjePoruka() {
    }

    public String posaljiPoruku(String poruka) {
        ucitajKonfiguraciju();
        String odgovor = "";
        try {
            Socket socket = new Socket("localhost", port);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            os.write(poruka.getBytes());
            os.flush();
            socket.shutdownOutput();
            int znak = 0;

            StringBuilder stringBuilder = new StringBuilder();
            while ((znak = is.read()) != -1) {
                stringBuilder.append((char) znak);
            }
            odgovor = stringBuilder.toString();
            //socket.shutdownInput();
            //socket.shutdownOutput();
            //socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SlanjePoruka.class.getName()).log(Level.SEVERE, null, ex);
            odgovor = "ERROR";
        }
        return odgovor;
    }

    private void ucitajKonfiguraciju() {
        SlusacAplikacije sa = new SlusacAplikacije();
        bpk = (BP_Konfiguracija) sa.getSc().getAttribute("BP_Konfig");
        konf = (Konfiguracija) sa.getSc().getAttribute("Konfiguracija");
        port = Integer.parseInt(konf.dajPostavku("port"));

    }
}
