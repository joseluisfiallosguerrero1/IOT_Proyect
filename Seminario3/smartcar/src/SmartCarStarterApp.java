import java.util.UUID;

import componentes.RoadPlace;
import componentes.SmartCar;
import componentes.SpeedLimitSign;

public class SmartCarStarterApp {
    public static void main(String[] args) throws Exception {

		if ( args.length < 2 )
		{
			System.out.println("Usage: SmartCarStarterApp <smartCarID> <brokerURL>");
			System.exit(1);
		}

		String smartCarID = args[0];
		String brokerURL = args[1];

        SmartCar sc1 = new SmartCar(smartCarID, brokerURL);
		sc1.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

        SmartCar sc2 = new SmartCar(smartCarID + "2", brokerURL);
		sc2.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

        SmartCar sc3 = new SmartCar(smartCarID + "2", brokerURL);
		sc3.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

        SmartCar sc4 = new SmartCar(smartCarID + "4", brokerURL);
		sc4.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

        SmartCar sc5 = new SmartCar(smartCarID + "5", brokerURL);
		sc5.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

        SmartCar sc6 = new SmartCar(smartCarID + "6", brokerURL);
		sc6.setCurrentRoadPlace(new RoadPlace("R1s1", 0));

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

		//sc1.getIntoRoad(...);  // indicamos que el SmartCar está en tal segmento
		//sc1.notifyIncident(...);


		//Ejercicio 5.3
		String speedlimitID = "SpeedLimitSign1" +UUID.randomUUID().toString();

		// Indicate that the SmartCar is on road "R5s1" at km 50
		int currentSpeed = 50;
		sc1.getIntoRoad("R5s5", currentSpeed);  // indicamos que el SmartCar está en tal segmento
		//sc1.notifyIncident("INCIDENT");
		sc1.notifyIncident("alert");

		// Create and connect a SpeedLimitSign
		SpeedLimitSign speedLimitSign = new SpeedLimitSign(speedlimitID, brokerURL);
		speedLimitSign.connect();
		
		// Set a new speed limit on a specific road segment
		speedLimitSign.reportSpeedLimit("R5s1", currentSpeed, 40); 
    }
}
