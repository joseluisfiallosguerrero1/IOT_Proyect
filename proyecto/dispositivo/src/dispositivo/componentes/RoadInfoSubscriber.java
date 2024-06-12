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
		String topic = this.baseTopic + "/road/" + this.panelInformativo.getRoadPlace().getSegment() + "/info";
		subscribe(topic);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		super.messageArrived(topic, message);
		String payload = new String(message.getPayload());
		JSONObject jsonPayload = new JSONObject(payload);
		String roadSituationType = jsonPayload.getString("type");
		if (roadSituationType.equals("ROAD_STATUS")) {
			JSONObject msgObject = jsonPayload.getJSONObject("msg");
			String roadStatus = msgObject.getString("status");
			this.panelInformativo.congestionCarretera(roadStatus);
		} else if (roadSituationType.equals("ROAD_INCIDENT")) {

		} else if (roadSituationType.equals("TRAFFIC")) {
			JSONObject msgObject = jsonPayload.getJSONObject("msg");
			String vehicleType = msgObject.getString("vehicle-role");
			int position = msgObject.getInt("position");
			String roadSegment = msgObject.getString("road-segment");
			this.panelInformativo.vehiculoEspecial(vehicleType, position, roadSegment) ;
		}
	}
}
