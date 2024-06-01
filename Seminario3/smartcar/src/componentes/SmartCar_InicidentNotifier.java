package componentes;

import java.util.Random;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.json.JSONException;
import org.json.JSONObject;
import utils.MySimpleLogger;

public class SmartCar_InicidentNotifier extends MyMqttClient {
	
	public SmartCar_InicidentNotifier(String clientId, SmartCar smartcar, String brokerURL) {
		super(clientId, smartcar, brokerURL);
	}
	
	public void alert(String smartCarID, String notificationType, RoadPlace place) {

		String myTopic =  "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + place.getRoad() + "/alerts";
		long timestamp = System.currentTimeMillis();

		MqttTopic topic = myClient.getTopic(myTopic);


		// Generar un ID aleatorio compuesto solo de números directamente
		Random random = new Random();
		StringBuilder randomNumericIdBuilder = new StringBuilder();
		for (int i = 0; i < 10; i++) { // Generar un ID de 10 dígitos
			randomNumericIdBuilder.append(random.nextInt(10));
		}
		String msgID = randomNumericIdBuilder.toString();

		// JSON para envio de alertas
		JSONObject pubMsg = new JSONObject();
		JSONObject msgDetails = new JSONObject();
		try {
			// Llenamos el mensaje principal
			pubMsg.put("id", "MSG_" + timestamp);
			pubMsg.put("type", "ROAD_INCIDENT"); 
			pubMsg.put("timestamp", timestamp );
			pubMsg.put("msg", msgDetails);	

			// Llenamos los detalles del mensaje
            msgDetails.put("rt", "traffic::"+notificationType); 
			
			// Incident type y descripcion segun tipo de alerta
			if ("alert".equalsIgnoreCase(notificationType)) {
                msgDetails.put("incident-type", "TRAFFIC_ACCIDENT");
                msgDetails.put("description", "Vehicle Crash");
            } else if ("incident".equalsIgnoreCase(notificationType)) {
                msgDetails.put("incident-type", "incident");
                msgDetails.put("description", "Working Area");
            }

			// resto de mensaje
            msgDetails.put("id", msgID);
            msgDetails.put("road", place.getRoad().split("s")[0]);
            msgDetails.put("road-segment", place.getRoad()); 
            msgDetails.put("starting-position", place.getKm());
            msgDetails.put("ending-position", place.getKm());
            msgDetails.put("status", "Active");
            msgDetails.put("link", "/incident/"+ msgID);

	   		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
   		int pubQoS = 0;
		MqttMessage message = new MqttMessage(pubMsg.toString().getBytes());
    	message.setQos(pubQoS);
    	message.setRetained(false);

    	// Publish the message
    	MySimpleLogger.trace(this.clientId, "Publishing to topic " + topic + " qos " + pubQoS);
    	MqttDeliveryToken token = null;
    	try {
    		// publish message to broker
			token = topic.publish(message);
			MySimpleLogger.trace(this.clientId, pubMsg.toString());
	    	// Wait until the message has been delivered to the broker
			token.waitForCompletion();
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    		    	

	}
	
}