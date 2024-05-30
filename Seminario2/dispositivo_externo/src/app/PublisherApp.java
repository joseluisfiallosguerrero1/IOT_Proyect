package app;

import java.util.UUID;

import org.json.JSONObject;

import components.mqtt.ClienteMqtt;
import utils.MySimpleLogger;

public class PublisherApp {
    public static void main(String[] args) throws Exception {
    
        MySimpleLogger.level = MySimpleLogger.DEBUG;
        //Id de coneccion usar uuid.getFreshId()
        String clientId = args[0];
        ClienteMqtt cliente = new ClienteMqtt(clientId+UUID.randomUUID().toString());
        
        //String broker = "tcp://emqx.broker.id.1883";
        String broker = "tcp://tambori.dsic.upv.es:1883";
        
        cliente.connect(broker);

        String topic = "iot/dvallop/deportista";
        JSONObject msg = new JSONObject();
        msg.put("deportista", "Cristiano Ronaldo");
        msg.put("deporte", "football");
        cliente.publish(topic, msg.toString());
        cliente.disconnect();

    }
}
