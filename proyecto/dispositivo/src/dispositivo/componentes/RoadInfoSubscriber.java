package dispositivo.componentes;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RoadInfoSubscriber extends MyMqttClient {
	public RoadInfoSubscriber(String clientId, PanelInformativo panelInformativo, String MQTTBrokerURL) {
		super(clientId, panelInformativo, MQTTBrokerURL);
		this.panelInformativo = panelInformativo;
	}

	@Override
	public void connect() {
		super.connect();
		String topic = this.baseTopic +"/road/"+ this.panelInformativo.getRoadPlace().getSegment() + "/info";
		subscribe(topic);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		super.messageArrived(topic, message);
		String payload = new String(message.getPayload());
	}
}
