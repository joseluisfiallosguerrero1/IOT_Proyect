package dispositivo.iniciador;

import dispositivo.componentes.PanelInformativo;
import dispositivo.componentes.RoadPlace;
import dispositivo.componentes.Funcion;
import dispositivo.interfaces.FuncionStatus;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.MySimpleLogger;

public class DispositivoIniciador {
	public static void main(String[] args) {

		if (args.length < 4) {
			System.out.println("Usage: java -jar dispositivo.jar device deviceIP rest-port mqttBroker");
			System.out.println(
					"Example: java -jar dispositivo.jar ttmi050 ttmi050.iot.upv.es 8182 tcp://ttmi008.iot.upv.es:1883");
			return;
		}

		String deviceId = args[0];
		String deviceIP = args[1];
		String port = args[2];
		String mqttBroker = args[3];
		String roadSegment = args[4];

		PanelInformativo panelInformativo = new PanelInformativo(deviceId, deviceIP, roadSegment, mqttBroker);

		// Añadimos funciones al dispositivo
		IFuncion f1 = Funcion.build("f1", FuncionStatus.OFF);
		panelInformativo.addFuncion(f1);

		IFuncion f2 = Funcion.build("f2", FuncionStatus.OFF);
		panelInformativo.addFuncion(f2);

		IFuncion f3 = Funcion.build("f3", FuncionStatus.OFF);
		panelInformativo.addFuncion(f3);

		// Arrancamos el dispositivo
		panelInformativo.iniciar();
	}
}
