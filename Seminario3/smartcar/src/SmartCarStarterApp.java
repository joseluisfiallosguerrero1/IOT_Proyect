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

		// Ejericio 5.1 Juan Camilo
		// Ejercicio 5.2 Jose Fiallos
		// Ejercicio 5.3 Gary Alarcon
		// Ejercicio 5.4 Victor Euceda
		// Ejercicio 5.5 Luis Martinez

		String firstRoadSegment = "R1s1";
		int firstRoadSegmentCapacity = 6;
        SmartCar sc1 = new SmartCar(smartCarID + UUID.randomUUID().toString(), brokerURL);
		sc1.setCurrentRoadPlace(new RoadPlace(firstRoadSegment, 0));

		SmartCar[] smartCars = new SmartCar[firstRoadSegmentCapacity];
		for (int i = 0; i < 7; i++) {
			SmartCar sc = new SmartCar(smartCarID + UUID.randomUUID().toString(), brokerURL);
			sc.setCurrentRoadPlace(new RoadPlace(firstRoadSegment, 0));
			smartCars[i] = sc;
		}

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		//Ejercicio 5.3
		String speedlimitID = "SpeedLimitSign1" + UUID.randomUUID().toString();

		// Indicate that the SpeedLimitSign is on secondRoadSegment
		String secondRoadSegment = "R5s1";
		int currentSpeed = 50;
		sc1.getIntoRoad(secondRoadSegment, currentSpeed);
		//sc1.notifyIncident("INCIDENT");
		sc1.notifyIncident("alert");

		// Create and connect a SpeedLimitSign
		SpeedLimitSign speedLimitSign = new SpeedLimitSign(speedlimitID, brokerURL);
		speedLimitSign.connect();
		
		// Set a new speed limit on a specific road segment
		speedLimitSign.reportSpeedLimit(secondRoadSegment, currentSpeed, 40); 
    }
}
