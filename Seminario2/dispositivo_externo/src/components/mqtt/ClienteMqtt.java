package components.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import utils.MySimpleLogger;

public class ClienteMqtt {

    protected String clientId = null;
    protected MqttClient mqttClient = null;
    protected MqttConnectOptions connOpt = null;

    public ClienteMqtt(String clientId) {
        this.clientId = clientId;
    }

    /**
	 * 
	 * runClient
	 * The main functionality of this simple example.
	 * Create a MQTT client, connect to broker, pub/sub, disconnect.
	 * 
	 */
	public void connect(String broker) {
		// setup MQTT Client
		connOpt = new MqttConnectOptions();
		
		connOpt.setCleanSession(true);
		connOpt.setKeepAliveInterval(30);
//			connOpt.setUserName(M2MIO_USERNAME);
//			connOpt.setPassword(M2MIO_PASSWORD_MD5.toCharArray());
		
		// Connect to Broker
		try {
			MqttDefaultFilePersistence persistence = null;
			try {
				persistence = new MqttDefaultFilePersistence("/tmp");
			} catch (Exception e) {
			}
			if ( persistence != null )
                mqttClient = new MqttClient(broker, this.clientId, persistence);
			else
                mqttClient = new MqttClient(broker, this.clientId);

            mqttClient.connect(connOpt);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		MySimpleLogger.info(this.clientId, "Conectado al broker " + broker);

	}


    public void disconnect() {
		
		// disconnect
		try {
			// wait to ensure subscribed messages are delivered
			Thread.sleep(10000);

			mqttClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



    public void publish(String topic, String message) {
        try {
			MySimpleLogger.info(this.clientId, "Publicando mensaje: " + message + " en el topic: " + topic);
            mqttClient.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void subscribe(String topic, IMqttMessageListener listener) {
        try {
			MySimpleLogger.info(this.clientId, "Suscribiendo al topic: " + topic);
            mqttClient.subscribe(topic, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void unsubscribe(String topic) {
        try {
			MySimpleLogger.info(this.clientId, "Desuscribiendo del topic: " + topic);
            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    
}
