package app;

import java.util.UUID;

import components.mqtt.ClienteMqtt;
import components.mqtt.Subscriber;
import utils.MySimpleLogger;

public class SubscriberApp {

    public static void main(String[] args) throws Exception {

        MySimpleLogger.level = MySimpleLogger.DEBUG;

        String clientId = args[0];
        ClienteMqtt cliente = new ClienteMqtt(clientId+UUID.randomUUID().toString());

        String broker = "tcp://tambori.dsic.upv.es:1883";;
        cliente.connect(broker);

        String topic = "dispositivo/ttmi059/funcion/f1/comandos";
        Subscriber subscriber = new Subscriber(clientId+"-pub");
        cliente.subscribe(topic, subscriber);

    }

    
}
