package graphen;

public class Element {
	private int knoten;
	private int wert;
	
	public Element(int knoten, int wert) {
		this.knoten = knoten;
		this.wert = wert;
	}

	public int getKnoten() {
		return knoten;
	}

	public void setKnoten(int knoten) {
		this.knoten = knoten;
	}

	public int getWert() {
		return wert;
	}

	public void setWert(int wert) {
		this.wert = wert;
	}
	
	public String toString() {
		return knoten + " " + wert;
	}
}
