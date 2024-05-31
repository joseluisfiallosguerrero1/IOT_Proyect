package componentes;

import utils.MySimpleLogger;

import java.time.Instant;
import java.util.Objects;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

public class SmartCar {
	protected String brokerURL = null;

	protected String smartCarID = null;
	protected RoadPlace rp = null;	// simula la ubicación actual del vehículo
	protected SmartCar_RoadInfoSubscriber subscriber = null;
	protected SmartCar_InicidentNotifier notifier = null;
	protected MyMqttClient publisher = null;
	protected String baseTopic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna";
	protected String roadPath = "/road/";
	
	public SmartCar(String id, String brokerURL) {
		
		this.setSmartCarID(id);
		this.brokerURL = brokerURL;
		
		this.notifier = new SmartCar_InicidentNotifier(id + ".incident-notifier", this, this.brokerURL);
		this.notifier.connect();
		this.publisher = new MyMqttClient(id+".traffic",this, this.brokerURL);
		publisher.connect();
		this.subscriber = new SmartCar_RoadInfoSubscriber(id, this, brokerURL);
		subscriber.connect();

		// Shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(this::sendVehicleOut));
	}
	
	
	public void setSmartCarID(String smartCarID) {
		this.smartCarID = smartCarID;
	}
	
	public String getSmartCarID() {
		return smartCarID;
	}

	public void setCurrentRoadPlace(RoadPlace rp) {
		// 1.- Si ya teníamos algún suscriptor conectado al tramo de carretera antiguo, primero los desconectamos
		// 2.- Ahora debemos crear suscriptor/es para conocer 'cosas' de dicho tramo de carretera, y conectarlo/s
		// 3.- Debemos suscribir este/os suscriptor/es a los canales adecuados
		if (rp == null) return;

		if (this.rp == null) {
			this.rp = rp;
			try {
				this.sendVehicleIn();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
 
		if (!Objects.equals(this.rp.getRoad(), rp.getRoad())) {
			MySimpleLogger.info(this.smartCarID, "Changing road from " + this.rp.getRoad() + " to " + rp.getRoad() + " at km " + rp.getKm() + "...");
			// Send vehicle out message
			this.sendVehicleOut();
			// Send vehicle in message
			this.rp = rp;
			this.sendVehicleIn();
		}
	}

	private void sendVehicleIn() {
		if (this.rp == null) return;
		try {
			String message = buildMessage("VEHICLE_IN", "PrivateUsage");
			this.publisher.publish(this.baseTopic + roadPath + this.rp.getRoad() +"/traffic", message);
			this.subscriber.subscribe(this.baseTopic + roadPath + this.rp.getRoad()+"/info");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendVehicleOut() {
		if (this.rp == null) return;
		try {
			String message = buildMessage("VEHICLE_OUT", "PrivateUsage");
			this.publisher.publish(this.baseTopic + roadPath + this.rp.getRoad() +"/traffic", message);
			this.subscriber.unsubscribe(this.baseTopic + roadPath +this.rp.getRoad()+"/info");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String buildMessage(String action, String role) {
		JSONObject message = new JSONObject();
		JSONObject completeMessage = new JSONObject();
		Instant timestamp =  Instant.now();
		try {
			completeMessage.put("id", "MSG_" + timestamp.getEpochSecond());
			completeMessage.put("type", "TRAFFIC");
			completeMessage.put("timestamp", timestamp.getEpochSecond());
			message.put("action", action);
			message.put("road", this.rp.getRoad().split("s")[0]);
			message.put("road-segment", this.rp.getRoad());
			message.put("vehicle-id", this.smartCarID);
			message.put("position", this.rp.getKm());
			message.put("role", role);
			completeMessage.put("msg", message);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return completeMessage.toString();
	}

	public RoadPlace getCurrentPlace() {
		return rp;
	}

	public void changeKm(int km) {
		this.getCurrentPlace().setKm(km);
	}
	
	public void getIntoRoad(String road, int km) {
		this.getCurrentPlace().setRoad(road);
		this.getCurrentPlace().setKm(km);
	}
	
	public void notifyIncident(String incidentType) {
		if ( this.notifier == null )
			return;
		
		this.notifier.alert(this.getSmartCarID(), incidentType, this.getCurrentPlace());
		
	}

}
