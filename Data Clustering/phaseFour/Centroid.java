package phaseFour;

import java.util.ArrayList;

public class Centroid {
	private ArrayList<Double> coordinates;
	
	public void setCoordinates(ArrayList<Double> coord) {
		coordinates = coord;
	}
	
	public ArrayList<Double> getCoordinates() {
		return coordinates;
	}

	public String toString(int clusterNumber) {
		String output = "";
		
		output += "Center " + clusterNumber + ": " + coordinates.toString() + "\n";
		
		
		return output;
	}
	

}
