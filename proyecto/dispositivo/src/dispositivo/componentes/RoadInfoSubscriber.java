package dispositivo.componentes;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import dispositivo.utils.MySimpleLogger;

public class RoadInfoSubscriber extends MyMqttClient {

	protected PanelInformativo panelInformativo;
	
	public RoadInfoSubscriber(String clientId, PanelInformativo panelInformativo, String MQTTBrokerURL) {
		super(clientId, panelInformativo, MQTTBrokerURL);
		this.panelInformativo = panelInformativo;
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		super.messageArrived(topic, message);			// esto muestra el mensaje por pantalla ... comentar para no verlo
		String payload = new String(message.getPayload());
		
		// PROCESS THE MESSGE
		// topic - contains the topic
		// payload - contains the message
	}
}
