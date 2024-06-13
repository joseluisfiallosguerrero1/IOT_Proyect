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
    protected RoadPlace roadPlace = null;
    protected AWSIoTThingStarterI awsIot = null;

    public PanelInformativo(String deviceId, String deviceIP, String roadSegment, String mqttBroker) {
        this.deviceId = deviceId;
        String roadName = roadSegment.split("s")[0];
        this.roadPlace = new RoadPlace(roadName, roadSegment, 0);
        this.roadSubscriber = new RoadInfoSubscriber(deviceIP, this, mqttBroker);
        this.awsIot = new AWSIoTThingStarterI();

        this.roadSubscriber.connect();
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
        } else if (status.equals("No_Manouvers") || status.equals("Collapsed")) {
            this.getFuncion("f1").encender();
        }
        
    }
	public void accidenteCarretera(String status) {
        if (status.equals("Active")) {
            this.getFuncion("f2").apagar();
        } else  {
			this.getFuncion("f2").parpadear();
        }
    }

    public void vehiculoEspecial(String tipo, int posVehiculoEspecial, String roadSegment) {
        int posSmartCar = this.roadPlace.getKm();

        if( roadSegment != "R1s1"){
            return;
        }

        int distancia = posSmartCar - posVehiculoEspecial;

        if (tipo.equals("Ambulance") || tipo.equals("Police")) {
            if (distancia < 0) { //ya pasamos el vehiculo smartcar
                this.getFuncion("f3").apagar();
            } else if ((distancia) > 0 && (distancia) < 200  ) { // estamos a menos de 200 metros del vehiculo smartcar
                this.getFuncion("f3").parpadear();
            } else { // estamos a más de 200 metros del vehiculo smartcar
                this.getFuncion("f3").encender();
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