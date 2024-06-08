package components.mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import utils.MySimpleLogger;

public class Subscriber implements IMqttMessageListener {

    protected String subscriberId = null;

    public Subscriber(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        String payload = new String(message.getPayload());
		
		MySimpleLogger.info(this.subscriberId, "-------------------------------------------------");
		MySimpleLogger.info(this.subscriberId, "| Topic:" + topic);
		MySimpleLogger.info(this.subscriberId, "| Message: " + payload);
		MySimpleLogger.info(this.subscriberId, "-------------------------------------------------");
        this.actualizar_estados(payload);
        
    }

    public void actualizar_estados(String msg) {
         //Id de coneccion usar uuid.getFreshId()
        String clientId = "subscriber";
        ClienteMqtt cliente = new ClienteMqtt(clientId+UUID.randomUUID().toString());
        
        //String broker = "tcp://emqx.broker.id.1883";
        String broker = "tcp://tambori.dsic.upv.es:1883";
        
        cliente.connect(broker);

        String topic = "dispositivo/ttmi050/funcion/f1/comandos";
        
        cliente.publish(topic, msg.toString());
        //cliente.disconnect();
    }
}
