package dispositivo.componentes;

public class RoadPlace {

	protected String road = null;
	protected String segment = null;
	protected int km = 0;

	public RoadPlace(String road, String segment, int km) {
		this.road = road;
		this.segment = segment;
		this.km = km;
	}

	public void setKm(int km) {
		this.km = km;
	}

	public int getKm() {
		return km;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

}
