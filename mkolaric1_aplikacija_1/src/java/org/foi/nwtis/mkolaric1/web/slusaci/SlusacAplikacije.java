package org.foi.nwtis.mkolaric1.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.mkolaric1.dretve.ObradaZahtjevaServer;
import org.foi.nwtis.mkolaric1.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mkolaric1.dretve.PreuzimanjeAviona;
import org.foi.nwtis.mkolaric1.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mkolaric1.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.mkolaric1.konfiguracije.bp.BP_Konfiguracija;

@WebListener
public class SlusacAplikacije implements ServletContextListener {

    public static ServletContext sc;
    PreuzimanjeAviona preuzimanjeAviona;
    public static String putanja;
    private ObradaZahtjevaServer ozs;
@Override
    public void contextInitialized(ServletContextEvent sce) {
        sc = sce.getServletContext();
        this.putanja = sc.getRealPath("/WEB-INF").substring(0,sc.getRealPath("/WEB-INF").length()-17)+"web"+File.separator+"WEB-INF"+File.separator ;
        String datoteka = putanja + File.separator + sc.getInitParameter("konfiguracija");
        try {
            BP_Konfiguracija bpk = new BP_Konfiguracija(datoteka);
            sc.setAttribute("BP_Konfig", bpk);
            Konfiguracija konf=KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            sc.setAttribute("Konfiguracija", konf);
            System.out.println("Učitana konfiguracija");
            preuzimanjeAviona = new PreuzimanjeAviona();
            ozs=new ObradaZahtjevaServer(preuzimanjeAviona);
            ozs.start();
            preuzimanjeAviona.start();
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (preuzimanjeAviona != null) {
            preuzimanjeAviona.interrupt();
        }
        if(ozs!=null){
            ozs.interrupt();
        }
        sc = sce.getServletContext();
    }

    public ServletContext getSc() {
        return sc;
    }
   
}
