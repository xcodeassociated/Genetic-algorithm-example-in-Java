import java.io.*;


public class GA_Calculator {
	
	/* Our aim is to find the best circuit 
	 * from city X and going through all other cities
	 */
	
	static String FILE = "example.txt"; // The string format should be according to format.txt
	static int totalCities, startCity;
	static int[][] dist_matrix;
	
	public GA_Calculator(String fileName){
		this.FILE = fileName;
	}
	
	// Initialize data structure, store the distance in a 2d array
	public static void initialize_ds(){
		int city1,city2;
		BufferedReader readBuffer = null;
		int totalCities;
		String xValues[] = new String[8];
		
		// Reading the given file
		try{
			
			// Initialize readBuffer
			readBuffer = new BufferedReader(new FileReader(FILE));
			
			// Check the first string for no. of cities
			String xString = readBuffer.readLine();
			int x = Integer.parseInt(xString);
			
			totalCities = x-1;
			
			// Set the size depending on total cities
			dist_matrix = new int[totalCities][totalCities+1];
			
			// Check the second string to identify starting city
			xString = readBuffer.readLine();
			x = Integer.parseInt(xString);
			
			int startCity = x;
			
			System.out.println("Total cities: "+(totalCities+1)+" | Starting city: "+startCity);
			
			// Get the distance between the cities
			city1 = 0;
			city2 = 1;
			
			// Going through the given file and getting the distance matrix
			for(int i = 0; i  < totalCities; i++){
				
				while(city1 != city2){
					
					// Read
					xString = readBuffer.readLine();
					
					// Split the values on the current line and store them into an array
					xValues = xString.split("\t");
					
					// 
					for(int j = 0; j < xValues.length; j++){
						x = Integer.parseInt(xValues[j]);
						// Store
						dist_matrix[city1][city2] = x;
						// Increment
						System.out.println(city1+","+city2+" : "+dist_matrix[city1++][city2]);
					}	
				}
				
				// Increment and reset
				city2++;
				city1 = 0;
			
			}
			
		}catch(Exception e){
			System.out.println(e);
			System.exit(0);
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		GA_Calculator GAC = new GA_Calculator("format.txt");
		
		initialize_ds();
	}

}
