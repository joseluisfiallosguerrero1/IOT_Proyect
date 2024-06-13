package dispositivo.awsiotthing;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

import dispositivo.utils.MySimpleLogger;

public class AWSIoT_TopicHandler extends AWSIotTopic {

	public AWSIoT_TopicHandler(String topic, AWSIotQos qos) {
		super(topic, qos);
	}
	
	@Override
	public void onMessage(AWSIotMessage message) {
		//super.onMessage(message);
		System.out.println(message.getTopic());
		if (message.getTopic().endsWith("f2")){
			byte[] payloadBytes = message.getPayload();

			String payloadString = new String(payloadBytes);

			JSONObject jsonPayload;
			try {
				jsonPayload = new JSONObject(payloadString);
				String roadSituationType = jsonPayload.getString("type");
				if (roadSituationType.equals("ROAD_INCIDENT")) {
					String text = message.getStringPayload();
					MySimpleLogger.info(AWSIoTThingStarter.loggerId + "-topicHandler", "RECEIVED: " + text);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		
			
		}
		
	}

}
