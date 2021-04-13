package phaseFour;

import java.util.ArrayList;

public class Point {
	private ArrayList<Double> coordinates;
	private int clusterNumber = -1;
	
	public void setCoordinates(ArrayList<Double> coord) {
		coordinates = coord;
	}
	
	public void setSpecificCoordinate(int index, double value) {
		coordinates.set(index, value);
	}
	
	public void setClusterNumber(int clusterNum) {
		clusterNumber = clusterNum;
	}
	
	public ArrayList<Double> getCoordinates(){
		return coordinates;
	}
	
	public int getClusterNumber() {
		return clusterNumber;
	}
	
	public String toString() {
		String output = "";
		
		if(getClusterNumber() != -1) {
			output += "Assigned Cluster Number: " + getClusterNumber() + "\n";
		}
		
		output += "Coordinates: " + coordinates.toString();
			
		
		return output;
	}

}
