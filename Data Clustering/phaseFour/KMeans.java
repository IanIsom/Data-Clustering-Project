package phaseFour;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class KMeans {

	//Declaration of each private data member
	private String fileName;
	private String[] firstLine;
	private String[] conversion;
	private ArrayList<Double> universalCenter = new ArrayList<Double>();
	private ArrayList<Integer> randomLineNumbers = new ArrayList<Integer>();
	private ArrayList<Centroid> centers = new ArrayList<Centroid>();
	private ArrayList<Point> points = new ArrayList<Point>();
	ArrayList<Double> sseList = new ArrayList<Double>();
	private int totalLines;
	private int dimensions;
	private int clusterNumber;
	private int iterationNum;
	private int optimalCenters;
	private double finalSSE;
	private double initialSSE;
	
	//Setters and getters
	public void setFileName(String fn) {
		fileName = fn;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setOptimalCenters(int optimal) {
		optimalCenters = optimal;
	}
	
	public int getOptimalCenters() {
		return optimalCenters;
	}
	
	public void setClusterNumber(int clusterNum) {
		clusterNumber = clusterNum;
	}
	
	public void setIterNum(int iterNum) {
		iterationNum = iterNum;
	}
	
	public int getIterNum() {
		return iterationNum;
	}
	
	public void setFinalSSE(double sse) {
		finalSSE = sse;
	}
	
	public double getFinalSSE() {
		return finalSSE;
	}
	
	public void setInitialSSE(double sse) {
		initialSSE = sse;
	}
	
	public double getInitialSSE() {
		return initialSSE;
	}
	
	public int getClusterNumber() {
		return clusterNumber;
	}
	
	public Centroid getCenter(Point p) {
		
		return centers.get(p.getClusterNumber() - 1);
		
	}
	
	public void setTotalLines(String totalLines) {
		this.totalLines = Integer.parseInt(totalLines);
	}
	
	public int getTotalLines() {
		return totalLines;
	}
	
	public void setDimensions(String dimensions) {
		this.dimensions = Integer.parseInt(dimensions);
	}
	
	public int getDimensions() {
		return dimensions;
	}
	
	public int pointsInCluster(int clusterNum) {
		int total = 0;
		for(Point p : points) {
			if(p.getClusterNumber() == clusterNum) {
				total++;
			}
		}
		return total;
	}
	
	public void printCenters() {
		for(Centroid c : centers) {
			System.out.println(c.getCoordinates().toString());
		}
	}
	
	//Randomize the numbers so that we can pick random centroids initially
	public void randomizeNumbers() {
		//Add the numbers 1 to the total number of lines to the randomLineNumbers arraylist
		for(int i = 1; i < getTotalLines(); i++) {
			randomLineNumbers.add(i);
		}
		
		//Shuffle randomLineNumbers to randomize the sequential numbers (did this to prevent duplicate numbers from being generated
		//When I used the Random class, there were runs where I would get two of the same number. This created a null point
		Collections.shuffle(randomLineNumbers);
	
	}
	
	//Read the first line of the text file. This will provide us with necessary information to continue onto the K-Means code
	public void initialRead() throws IOException {
		//Declaration of the file and extraction of first line information
		//Throws an IOException if the file name is not found
		File file = new File(fileName); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		
		firstLine = br.readLine().split("\\s");
		setTotalLines(firstLine[0]);
		setDimensions(firstLine[1]);
		
		System.out.println(firstLine.toString());
		//setOptimalCenters(Integer.parseInt(firstLine[2]));
		
		//System.out.println("Optimal Centers: " + getOptimalCenters());
		
		br.close();
	}
	
	//Read the text file, then create convert all numbers in the points from Strings to doubles
	//After point is created, add it to the ArrayList of points
	public void readPoints() throws IOException {
		File file = new File(fileName); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String line;
		br.readLine();
		
		while ((line = br.readLine()) != null) {
			Point p = new Point();
			ArrayList<Double> convertedNumbers = new ArrayList<Double>();
			conversion = line.split("\\s");
			for(String s : conversion) {
				convertedNumbers.add(Double.parseDouble(s));
			}
			
			p.setCoordinates(convertedNumbers);
			points.add(p);
				 					
		}
		
		br.close();
		
	}
	
	//min max normalization
	public void normalizeMinMax() {
		double max = 0.0;
		double min;
		for(int i = 0; i < dimensions; i++) {
			ArrayList<Double> normalizedCoords = new ArrayList<Double>();
			max = 0.0;
			//get all the coordinates from a column
			for(Point p : points) {
				normalizedCoords.add(p.getCoordinates().get(i));
			}
			
			//sort the coords and then set min to the max of the list
			ArrayList<Double> sortedCoords = new ArrayList<Double>();
			sortedCoords = normalizedCoords;
			Collections.sort(sortedCoords);
			min = sortedCoords.get(sortedCoords.size()-1);
			
			
			//Check all the numbers in the coords arraylist, set a new min and max based on each iteration
			//I could have went ahead and set the max but I already ran all my code before realizing it
			for(Double d : normalizedCoords) {
				if(d < min) {
					min = d;
				}
				else if(d > max) {
					max = d;
				}
			}
			
			//Set each coordinate to the normalized value
			//if max - min = 0, set all of those points to 0 (Source: https://stats.stackexchange.com/questions/441342/same-value-of-min-and-max-in-min-max-normalisation)
			for(Point p : points) {
				if(max - min == 0) {
					p.setSpecificCoordinate(i, 0);
				}
				else {
					p.setSpecificCoordinate(i, (p.getCoordinates().get(i) - min) / (max - min));
				}
				
				
			}
		}
	}
	
	//For bonus: Z score normalization
	public void normalizeZScore() {
		double standardDeviation = 0.0;
		double mean = 0.0;
		for(int i = 0; i < dimensions; i++) {
			
			//create an arraylist of the coordinates in a column
			ArrayList<Double> normalizedCoords = new ArrayList<Double>();
			for(Point p : points) {
				normalizedCoords.add(p.getCoordinates().get(i));
			}
			
			//find the sum of all the coords
			for(double d : normalizedCoords) {
				mean += d;
			}
			
			//create the mean by dividing the sum by the number of elements
			mean = mean / normalizedCoords.size();
			
			//Calculated standard deviation
			for(double d : normalizedCoords) {
				standardDeviation += (d - mean) * (d - mean);
			}
			
			standardDeviation = Math.sqrt(standardDeviation / normalizedCoords.size());
			
			//set each coordinate to the new normalized value
			for(Point p : points) {
				p.setSpecificCoordinate(i, (normalizedCoords.get(points.indexOf(p)) - mean) / standardDeviation);
			}
			
			standardDeviation = 0.0;
			
		}
		
	}
	
	//Read all the lines in the text file. If the line number is equal to one of my random numbers, create a 
	//new Centroid object where the line number is
	public void randomSelectionInitialization() throws IOException {	
		//Take the first n number of elements from randomLineNumbers and put them into uniqueRandomNumbers arraylist
		//n = the number of clusters the user inputs
		for(int i = 0; i < getClusterNumber(); i++) {
			Centroid c = new Centroid();
			c.setCoordinates(points.get(randomLineNumbers.get(i)).getCoordinates());
			centers.add(c);
		}
	
	}
	
	//random partition intitialization
	public void randomPartitionInitialization() {
		double mean = 0.0;
		int count = 0;
		
		//Randomly assign each point to a specific cluster number
		for(Point p : points) {
			p.setClusterNumber((int) (Math.random() * (getClusterNumber()) + 1));
		}
		
		//create the centers with this information (find the mean of each dimension and create a new center based on that)
		for(int i = 1; i <= getClusterNumber(); i++) {
			Centroid c = new Centroid();
			
			ArrayList<Double> coords = new ArrayList<Double>();
			for(int j = 0; j < getDimensions(); j++) {
				for(Point p : points) {
					if(p.getClusterNumber() == i) {
						count++;
						mean += p.getCoordinates().get(j);		
					}
				}
				coords.add(mean / count);
				mean = 0;
				count = 0;
			}
			c.setCoordinates(coords);
			centers.add(c);
		}
	}
	
	public int getPointsInCluster(int clusterNumber) {
		int count = 0;
		for(Point p : points) {
			if(p.getClusterNumber() == clusterNumber) {
				count++;
			}
		}
		return count;
	}
	
	//After creating the points and centroids, begin the K-Means clustering 
	public void kMeansRun(int maxIter, double convergenceThreshold) {
		//Variables declared for usage throughout the method
		double temp = 0.0;
		int iterationNum = 1;
		double convergence = 1.0;
		double calcDistance = 0.0;
		double totalDistance = 0.0;
		int index = 0;	
		double delta = 0.0;
		
		//While loop keeps the analysis going until convergence is less than the user inputed threshold
		//Or when the max iterations have been reached
		while((maxIter > 0) && (convergence > convergenceThreshold)) {
			//System.out.println("Iteration " + iterationNum + ": ");
			
			//Calculated the distance for each point relative to each centroid (calculate sse)
			for(Point p : points) {
				ArrayList<Double> distances = new ArrayList<Double>();
				for(Centroid c : centers) {
					for(double d : c.getCoordinates()) {
						calcDistance += (d - p.getCoordinates().get(index)) * (d - p.getCoordinates().get(index));
						index++;
					}
					distances.add(calcDistance);
					calcDistance = 0;
					index = 0;
					
				}
				
				//Assign each point to a cluster
				p.setClusterNumber(distances.indexOf(Collections.min(distances)) + 1);
				totalDistance += distances.get(distances.indexOf(Collections.min(distances)));
				
			}
			//Output the total SSE (the summation of the SSE of all points)
			//Then, add the SSE to the list of SSEs
			
			totalDistance = BigDecimal.valueOf(totalDistance)
				    .setScale(3, RoundingMode.HALF_UP)
				    .doubleValue();

			sseList.add(totalDistance);
			totalDistance = 0;
			
			//Create the place holders for the allocatedPoints ArrayList
			ArrayList<Integer> allocatedPoints = new ArrayList<Integer>();
			for(Centroid c : centers) {
				allocatedPoints.add(0);
			}

		
			//Loop that creates all of the new coordinates for for every point
			for(Centroid c : centers) {
				ArrayList<Double> newCoordinates = new ArrayList<Double>();
				
				//Create placeholderes for the new coordinates
				for(double d : c.getCoordinates()) {
					newCoordinates.add(0.0);
				}
				
				//For each dimension on each point, get the total of all the points associated with that centroid
				for(Point p : points) {
					if(p.getClusterNumber() == (centers.indexOf(c) + 1)){
						allocatedPoints.set(centers.indexOf(c), allocatedPoints.get(centers.indexOf(c)) + 1);
						for(double d : p.getCoordinates()) {
							temp = d + newCoordinates.get(p.getCoordinates().indexOf(d));
							newCoordinates.set(p.getCoordinates().indexOf(d), temp);
						}
						temp = 0.0;
					}
				}
				//Divide the previous total by the number of points to get the Mean. Those means will be the new centorid coordinates
				for(double d : newCoordinates) {
					if(allocatedPoints.get(centers.indexOf(c)) != 0) {
						newCoordinates.set(newCoordinates.indexOf(d), newCoordinates.get(newCoordinates.indexOf(d)) / allocatedPoints.get(centers.indexOf(c)));
					}
				}
				
				c.setCoordinates(newCoordinates);				
				
			};

			//Finally, calculate the convergence between the last run and this one. This will be checked when the loop goes back to the top
			if(sseList.size() > 1) {				
				convergence = Math.abs(((sseList.get(iterationNum - 2) - sseList.get(iterationNum - 1)) / sseList.get(iterationNum - 2)));
			}
			maxIter--;
			iterationNum++;
		}
		setFinalSSE(sseList.get(sseList.size()-1));
	}	
	
	public void calculateUniversalCenter() {
		//Get Center of dataset
		double mean = 0;
		for(int i = 0; i < dimensions; i++) {
			for(Point p : points) {
				mean += p.getCoordinates().get(i);
			}
			mean = mean / points.size();
			universalCenter.add(mean);
			mean = 0;
		}
	}
	
	public double calculateCH(double sse) {
		
		ArrayList<Double> traceSB = new ArrayList<Double>();
		double sb = 0.0;
		double sW = sse;
		int n = points.size();
		int k = getClusterNumber();
		double distance = 0.0;
		double ch = 0.0;
		
		//Get Center of dataset
		calculateUniversalCenter();
		
		//Calculate traceSB
		for(Centroid c : centers) {
			for(int i = 0; i < dimensions; i++) {
				
				distance += (c.getCoordinates().get(i) - universalCenter.get(i)) * (c.getCoordinates().get(i) - universalCenter.get(i));
			}
			sb = distance;			
			sb = sb * getPointsInCluster(centers.indexOf(c) + 1);
			
			traceSB.add(sb);
			distance = 0.0;

		}
		
		sb = 0;
		
		//get final value for traceSB (the summation of all diagonal values)
		for(Double d : traceSB) {
			sb += d;
		}	
		
		//Final calculation for CH
		ch = (sb / sW) * ((n - k) / (k - 1));
		

		return ch;
		
	}
	
	public int getClosestCluster(Centroid c) {
		
		ArrayList<Double> distanceList = new ArrayList<Double>();
		double distance = 0.0;
		int count = 0;
		int closestCluster = 0;
		int i = 0;
		double temp = 0.0;
				
		for(Centroid cent : centers) {
			for(Double d : c.getCoordinates()) {
				distance += (d - cent.getCoordinates().get(count)) * (d - cent.getCoordinates().get(count));
				count++;
			}

			
			distanceList.add(distance);
			distance = 0.0;
			count = 0;
		}
		
		while(distanceList.get(i) == 0) {
			i++;
		}
		
		temp = distanceList.get(i);
		
		for(Double d : distanceList) {
			if(d != 0.0 && temp > d) {
				temp = d;
			}
		}

		closestCluster = distanceList.indexOf(temp) + 1;
		
		return closestCluster;
	}
	
	
	public double calculateSC() {
		
		ArrayList<Double> scValues = new ArrayList<Double>();
		double minOut = 0.0;
		double in = 0.0;
		int closestCluster = 0;
		double finalSC = 0.0;
		
		//calculate minOut
		for(Point p: points) {
			closestCluster = getClosestCluster(getCenter(p));
			for(Point p2 : points) {
				if(p2.getClusterNumber() == closestCluster) {
					//calculate minOut
					for(Double d : p.getCoordinates()) {
						minOut += Math.abs(d - p2.getCoordinates().get(p.getCoordinates().indexOf(d)));
						
					}
				}
				else if(p2.getClusterNumber() == p.getClusterNumber()) {
					//calculate in
					for(Double d : p.getCoordinates()) {
						in += Math.abs(d - p2.getCoordinates().get(p.getCoordinates().indexOf(d)));
					}
					
				}
			}
			
			in = in / (getPointsInCluster(p.getClusterNumber()) - 1);
			minOut = minOut / getPointsInCluster(closestCluster);
			
			if(in > minOut) {
				scValues.add((minOut - in) / in);
			}
			else if(in < minOut) {
				scValues.add((minOut - in) / minOut);
			}
			in = 0.0;
			minOut = 0.0;
			
		}
		
		for(Double d : scValues) {
			finalSC += d;
		}
		
		
		return finalSC / points.size();
		
		
	}
	
	public void printResult() {
		double delta = 0.0;
		for(int i = 0; i < sseList.size(); i++) {
			System.out.println("Iteration " + (i + 1));
			if(i > 0) {
				delta = (sseList.get((i - 1)) - sseList.get(i)) / sseList.get((i - 1));
			}
			else {
				delta = 0.0;
			}
			System.out.println("Obj: " + sseList.get(i) + " ; " + "delta: " + delta);
		}
		
	}
}
