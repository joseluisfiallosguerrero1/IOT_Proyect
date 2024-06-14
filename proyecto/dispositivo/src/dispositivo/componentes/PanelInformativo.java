package dispositivo.componentes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import dispositivo.awsiotthing.AWSIoTThingStarterI;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;

public class PanelInformativo implements IDispositivo {
    protected String deviceId = null;
    protected Map<String, IFuncion> functions = null;
    protected RoadInfoSubscriber roadSubscriber = null;
    protected TrafficInfoSubscriber trafficSubscriber = null;
    protected RoadPlace roadPlace = null;
    protected AWSIoTThingStarterI awsIot = null;

    public PanelInformativo(String deviceId, String deviceIP, String roadSegment, String mqttBroker) {
        this.deviceId = deviceId;
        String roadName = roadSegment.split("s")[0];
        this.roadPlace = new RoadPlace(roadName, roadSegment, 0);
        this.roadSubscriber = new RoadInfoSubscriber(deviceIP, this, mqttBroker);
        this.awsIot = new AWSIoTThingStarterI();

        this.roadSubscriber.connect();
        this.trafficSubscriber = new TrafficInfoSubscriber("deviceIP", this, mqttBroker);
        this.trafficSubscriber.connect();
    }

    protected Map<String, IFuncion> getFunctions() {
		return this.functions;
	}

	public void setFunctions(Map<String, IFuncion> fs) {
		this.functions = fs;
	}

    public void setRoadPlace(RoadPlace rp) {
        this.roadPlace = rp;
    }

    public RoadPlace getRoadPlace() {
        return this.roadPlace;
    }

    public void congestionCarretera(String status) throws JSONException {
        System.out.println(status);
        if (status.equals("Free_Flow") || status.equals("Mostly_Free_Flow")) {
            this.getFuncion("f1").apagar();
            JSONObject message = new JSONObject();
            message.put("type", "apagado");
            this.awsIot.publish("f1", message.toString());
        } else if (status.equals("Limited_Manouvers")) {
            this.getFuncion("f1").parpadear();
            JSONObject message = new JSONObject();
            message.put("type", "parpadear");
            this.awsIot.publish("f1", message.toString());
        } else if (status.equals("No_Manouvers") || status.equals("Collapsed")) {
            this.getFuncion("f1").encender();
            JSONObject message = new JSONObject();
            message.put("type", "encender");
            this.awsIot.publish("f1", message.toString());
        }
        
    }
	public void accidenteCarretera(String status) throws JSONException {
        if (!status.equals("Active")) {
            this.getFuncion("f2").apagar();
            JSONObject message = new JSONObject();
            message.put("type", "apagar");
            this.awsIot.publish("f2", message.toString());
        } else  {
            JSONObject message = new JSONObject();
            message.put("type", "parpadear");
            this.awsIot.publish("f2", message.toString());
			this.getFuncion("f2").parpadear();
        }
    }

    public void vehiculoEspecial(String tipo, int posVehiculoEspecial, String roadSegment) throws JSONException {
        int posSmartCar = this.roadPlace.getKm();
        String panelRoadSegment = this.roadPlace.getSegment();
        if(!roadSegment.equals(panelRoadSegment)){
            System.out.println("El vehiculo especial no está en el mismo segmento de carretera que el panel informativo");
            return;
        }

        int distancia = posSmartCar - posVehiculoEspecial;

        if (tipo.equals("Ambulance") || tipo.equals("Police")) {
            if (distancia < 0) { //ya pasamos el vehiculo smartcar
                this.getFuncion("f3").apagar();
                JSONObject message = new JSONObject();
                message.put("type", "apagar");
                this.awsIot.publish("f3", message.toString());
            } else if ((distancia) > 0 && (distancia) < 200  ) { // estamos a menos de 200 metros del vehiculo smartcar
                this.getFuncion("f3").parpadear();
                JSONObject message = new JSONObject();
                message.put("type", "parpadear");
                this.awsIot.publish("f3", message.toString());
            } else { // estamos a más de 200 metros del vehiculo smartcar
                this.getFuncion("f3").encender();
                JSONObject message = new JSONObject();
                message.put("type", "encender");
                this.awsIot.publish("f3", message.toString());
            }
        } 
        
    }
    
	@Override
	public String getId() {
		return this.deviceId;
	}

	@Override
	public IDispositivo iniciar() {
		for (IFuncion f : this.getFunciones()) {
			f.iniciar();
		}

		return this;
	}

	@Override
	public IDispositivo detener() {
		for (IFuncion f : this.getFunciones()) {
			f.detener();
		}
		return this;
	}

    @Override
    public IDispositivo addFuncion(IFuncion f) {
        if (this.getFunctions() == null)
            this.setFunctions(new HashMap<String, IFuncion>());
        this.getFunctions().put(f.getId(), f);
        return this;
    }

    @Override
    public IFuncion getFuncion(String funcionId) {
        if (this.getFunctions() == null)
            return null;
        return this.getFunctions().get(funcionId);
    }

	@Override
	public Collection<IFuncion> getFunciones() {
		if (this.getFunctions() == null)
			return null;
		return this.getFunctions().values();
	}
}