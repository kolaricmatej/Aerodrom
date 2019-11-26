/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.ejb.sb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 *
 * @author Matej
 */
@Stateful
@LocalBean
public class AutentikacijaKorisnika {

    public boolean provjeriPostojanjeKorisnika(String korisnickoIme, String auth) {
        try {
            RESTKorisnik rk = new RESTKorisnik();
            JsonObject jo=new JsonObject();
            jo.addProperty("lozinka", auth);
            String sadrzaj = rk.getJsonAutentikacija(korisnickoIme,jo.toString());
            if(provjeraOdgovora(sadrzaj)){
                return true;
            }else{
                return false;
            }
        } catch (ClientErrorException | UnsupportedEncodingException ex) {
            Logger.getLogger(AutentikacijaKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private boolean provjeraOdgovora(String json){
        JsonObject jo=new JsonParser().parse(json).getAsJsonObject();
        String status=jo.get("status").getAsString();
        return status.equals("OK");
    }

    static class RESTKorisnik {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8084/mkolaric1_aplikacija_3_2/webresources";

        public RESTKorisnik() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("korisnici");
        }

        public String putJson(Object requestEntity, String korisnickoIme) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{korisnickoIme})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String postJson(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String getJsonAutentikacija(String korisnickoIme, String auth) throws ClientErrorException, UnsupportedEncodingException {
            WebTarget resource = webTarget;
            if (auth != null) {
                resource = resource.queryParam("auth", URLEncoder.encode(auth, "UTF-8"));
            }
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{korisnickoIme}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String getJson() throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close() {
            client.close();
        }
    }

   








    



}
