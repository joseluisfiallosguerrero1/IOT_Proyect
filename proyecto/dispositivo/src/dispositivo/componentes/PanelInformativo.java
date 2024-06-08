package dispositivo.componentes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;

public class PanelInformativo implements IDispositivo {
    protected String deviceId = null;
    protected Map<String, IFuncion> functions = null;
    protected RoadInfoSubscriber roadSubscriber = null;

    public PanelInformativo(String deviceId, String deviceIP, int port, String mqttBroker) {
        this.deviceId = deviceId;
        this.roadSubscriber = new RoadInfoSubscriber(deviceIP, this, mqttBroker);
    }

    protected Map<String, IFuncion> getFunctions() {
		return this.functions;
	}

	protected void setFunctions(Map<String, IFuncion> fs) {
		this.functions = fs;
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