package org.foi.nwtis.mkolaric1.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mkolaric1.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;

@WebListener
public class SlusacAplikacije implements ServletContextListener {

    public static ServletContext sc;

@Override
    public void contextInitialized(ServletContextEvent sce) {
        sc = sce.getServletContext();
        String putanja = sc.getRealPath("/WEB-INF");
        String datoteka = putanja + File.separator + sc.getInitParameter("konfiguracija");
        try {
            BP_Konfiguracija bpk = new BP_Konfiguracija(datoteka);
            sc.setAttribute("BP_Konfig", bpk);
            Konfiguracija konf=KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            sc.setAttribute("Konfiguracija", konf);
            System.out.println("Učitana konfiguracija");          
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sc = sce.getServletContext();
        sc.removeAttribute("BP_Konfig");
    }

    public ServletContext getSc() {
        return sc;
    }
   
}
