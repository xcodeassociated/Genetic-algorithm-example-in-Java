import java.io.*;
import java.util.Random;


public class GA_Calculator {
	
	/* Our aim is to find the best circuit 
	 * from city X and going through all other cities
	 */
	
	static String FILE = "format.txt"; // The string format should be according to format.txt
	static int totalCities, startCity;
	static int[][] dist_matrix;				// [startCity][endCity] | returns a distance between 2 cities (Permanent) 
	static int[][] population;				// [chromosome][gene] | returns a gene of a given gene position in the chromosome (Overrides every generation)
	static int generation = 0;
	static int maxGeneration = 10;
	static int maxPopulation = 10;
	// [Generation][Chromosome] | returns a fitness of a chromosome from a generation
	static double[][] fitness = new double[maxGeneration][maxPopulation];				
	
	//Debug configs
	static boolean print_additional_info = true;
	static boolean print_dist_matrix = true;
	static boolean print_chromosome_fitness_details = true;
	
	// Constructor
	public GA_Calculator(String fileName){
		this.FILE = fileName;
	}
	
	// Initialize data structure, store the distance in a 2d array
	public static void initialize_ds(){
		int city1,city2;
		BufferedReader readBuffer = null;
		String xValues[] = new String[totalCities];
		
		// Reading the given file
		try{
			
			// Initialize readBuffer
			readBuffer = new BufferedReader(new FileReader(FILE));
			
			// Check the first string for no. of cities
			String xString = readBuffer.readLine();
			int x = Integer.parseInt(xString);
			
			totalCities = x-1;
			
			// Set the size depending on total cities
			dist_matrix = new int[totalCities+1][totalCities+1];
			
			// Check the second string to identify starting city
			xString = readBuffer.readLine();
			x = Integer.parseInt(xString);
			
			startCity = x-1;
			
			if(print_additional_info == true)
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
						dist_matrix[city2][city1] = x;
						if(print_dist_matrix == true){
							System.out.println(city2+","+city1+" : "+dist_matrix[city2][city1]);
							System.out.println(city1+","+city2+" : "+dist_matrix[city1][city2]);
						}
						// Increment
						city1++;
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

	// Initializing population
	public static void initializePopulation(){
		int chromosome = 0;
		population = new int[maxPopulation][totalCities+1];
		
		// Initialize our array of visitedCities
		int[] visitedCities = new int[totalCities+1];
		for(int i = 0; i < visitedCities.length; i++){		
			visitedCities[i] = -1;
		}
		
		while(chromosome != maxPopulation){
			
				// set the starting city (gene value) to starting city
				population[chromosome][0] = startCity;
				
				// next few cities
				for(int gene = 1; gene < totalCities; gene++){
					
					// generate a random gene value (city) for the gene
					int city = genRandom(totalCities+1);
					
					// check if the gene value already exists in one of the genes
					for(int i = 1; i < totalCities; i++){
						if(city == visitedCities[i] || city == startCity){
							city = genRandom(totalCities+1);
							i = 0;
						}
					}
					
					// Add this gene value to the gene
					population[chromosome][gene] = city;
					
					// Add to visited city to keep track
					visitedCities[gene] = city;
				}
				
				// ending cities (gene value) to 3
				population[chromosome][totalCities] = startCity;
				
				// create another chromosome
				chromosome++;
			}
		}
	
	public static void evaluatePopulation(){
		double totalDist = 0;
		double fitnessValue = 0;
		int cityA,cityB;
		int chromosome = 0;
		
		// Lets go through all the chromosome and store their fitness
		while(chromosome != maxPopulation){
			
			for(int gene = 0; gene < totalCities; gene++){
				// Get city A value
				cityA = population[chromosome][gene];
				// Get City B value
				cityB = population[chromosome][gene+1];
				// Get the distance between the cities and add em up to total distance
				totalDist += dist_matrix[cityA][cityB];
				if(print_chromosome_fitness_details == true)
					System.out.println("step "+gene+"("+cityA+")"+"->"+(gene+1)+"("+cityB+")"+":"+totalDist);
			}
			
			// Calculating fitness value, fitness value should be between 0 and 1, 1 would mean its perfect, while 0 would mean its the opposite of perfect
			fitnessValue = 1/totalDist;
			
			if( print_chromosome_fitness_details == true)
				System.out.println(chromosome+ " F: "+fitnessValue );
			
			// We quit if fitness value is calculated as 0 (which should not happen)
			if(fitnessValue == 0.00){
				System.out.println("Great job genius!");
				System.exit(0);
			}
			
			// Store the fitness
			fitness[generation][chromosome] += fitnessValue;
			// Move to next chromosome in our generation
			chromosome++;
			// Reset variables
			totalDist = 0;
		}
	}
	
	// X is the limit
	public static int genRandom(int x){
		Random output = new Random(); 
		int number = output.nextInt(x);
		return number;
	}
	
	
	public static void main(String[] args) {
		
		initialize_ds();
		
		initializePopulation();
		
		evaluatePopulation();
	}

}
