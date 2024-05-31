package componentes;

import java.time.Instant;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.json.JSONException;
import org.json.JSONObject;
import utils.MySimpleLogger;


public class SpeedLimitSign extends MyMqttClient {
    
    
    
    public SpeedLimitSign(String clientId, String brokerURL) {
        super(clientId, null, brokerURL);
    }

    public void reportSpeedLimit(String road, int currentSpeedLimit, int newSpeedLimit) {    

    if (currentSpeedLimit > newSpeedLimit) {
        String myTopic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + road + "/signals";

        MqttTopic topic = myClient.getTopic(myTopic);


		JSONObject completeMessage = new JSONObject();
        JSONObject pubMsg = new JSONObject();
        Instant timestamp =  Instant.now();
        try {
            completeMessage.put("id", "MSG_" + timestamp.getEpochSecond());
            completeMessage.put("type", "TRAFFIC_SIGNAL");
            completeMessage.put("timestamp", timestamp.getEpochSecond());
            pubMsg.put("rt","traffic-signal");
            pubMsg.put("id", this.clientId);
            pubMsg.put("road", road.split("s")[0]);
            pubMsg.put("road-segment", road);
            pubMsg.put("signal-type", "SPEED_LIMIT");
            pubMsg.put("starting-position", 0);
            pubMsg.put("ending-position", 300);
            pubMsg.put("value", newSpeedLimit);
            completeMessage.put("msg", pubMsg);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        int pubQoS = 0;
        MqttMessage message = new MqttMessage(completeMessage.toString().getBytes());
        message.setQos(pubQoS);
        message.setRetained(false);

        MySimpleLogger.trace(this.clientId, "Publishing to topic " + topic + " qos " + pubQoS);
        try {
            topic.publish(message);
            MySimpleLogger.trace(this.clientId, completeMessage.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    } else {
        // Log an error or handle the case where the new speed limit is not lower than the current speed limit
        MySimpleLogger.error(this.clientId, "New speed limit is not lower than the current speed limit");
    }
    
    }
}