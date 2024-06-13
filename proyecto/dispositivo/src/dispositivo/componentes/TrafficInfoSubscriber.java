package dispositivo.componentes;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class TrafficInfoSubscriber extends MyMqttClient {
	public TrafficInfoSubscriber(String clientId, PanelInformativo panelInformativo, String MQTTBrokerURL) {
		super(clientId, panelInformativo, MQTTBrokerURL);
		this.panelInformativo = panelInformativo;
	}

	@Override
	public void connect() {
		super.connect();
		String topic = this.baseTopic + "/road/" + this.panelInformativo.getRoadPlace().getSegment() + "/traffic";
		subscribe(topic);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		super.messageArrived(topic, message);
		String payload = new String(message.getPayload());
		JSONObject jsonPayload = new JSONObject(payload);
		JSONObject msgObject = jsonPayload.getJSONObject("msg");
	}
}
