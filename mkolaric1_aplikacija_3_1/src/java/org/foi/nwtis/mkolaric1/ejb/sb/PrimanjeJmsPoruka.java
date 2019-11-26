/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkolaric1.ejb.sb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.foi.nwtis.mkolaric1.web.podaci.JmsPoruka;

/**
 *
 * @author Matej
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_mkolaric1_1"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class PrimanjeJmsPoruka implements MessageListener {

    @EJB
    private JmsSingleton jmsSingleton;

    public PrimanjeJmsPoruka() {
    }

    @Override
    public void onMessage(Message message) {
        TextMessage tm = null;
        if (message instanceof TextMessage) {
            try {
                tm = (TextMessage) message;
                JsonObject jo = new JsonParser().parse(tm.getText()).getAsJsonObject();
                JmsPoruka jp = new JmsPoruka(jo.get("id").getAsInt(), jo.get("komanda").getAsString(), jo.get("vrijeme").getAsString());
                jmsSingleton.getListaPoruka().add(jp);
            } catch (JMSException ex) {
                Logger.getLogger(PrimanjeJmsPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
