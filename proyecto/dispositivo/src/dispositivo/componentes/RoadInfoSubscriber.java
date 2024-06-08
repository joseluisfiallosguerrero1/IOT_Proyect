package dispositivo.componentes;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
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
		JSONObject payload = null;
		payload = new JSONObject(entity.getText());
		String roadSituationType = payload.getString("type");
		if(roadSituationType.equals("ROAD_STATUS")){

		}else if(roadSituationType.equals("ROAD_INCIDENT")){

		}
	}
}
