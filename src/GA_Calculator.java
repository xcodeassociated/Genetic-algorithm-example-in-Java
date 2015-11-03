/*  Contributers: Balraj Singh Bains, Mark Ericson Santos
 *  Date:   10.16.2015
 *  About:  Genetic Algorithm Coursework
 */

import java.io.*;
import java.util.Random;

public class GA_Calculator {
	
	/* Our aim is to find the best circuit 
	 * from city X and going through all other cities
	 */
	
	// variables
	static int totalCities, startCity;
	static int[][] dist_matrix;				// [startCity][endCity] | returns a distance between 2 cities (Permanent) 
	static int[][][] population;				// [generation][chromosome][gene] | returns a gene value of a given gene position in the chromosome for a generation (Overrides every generation)
	// Modifiable variables
	static String FILE = "format.txt"; // The string format should be according to format.txt
	static int generation = 0;
	static int maxGeneration = 100;
	static int maxPopulation = 50;
	static double mutationRate = 0.5;
	static double crossoverRate = 0.5;
	// elite stuff (you can change elitism to either 1 or 0)
	static int[] ePopulation = new int[maxGeneration];			// [generation] | returns the best chromosome from that generation
	static int elitism = 1;
	
	// [Generation][Chromosome] | returns a fitness of a chromosome from a generation
	static double[][] fitness = new double[maxGeneration][maxPopulation];	
	
	// Best variables, use to record the answer
	static int bestChromosome;
	static int bestGeneration;
	
	//Print configs, set it to true or false.
	static boolean print_additional_info = true;
	static boolean print_dist_matrix = false;
	static boolean print_chromosome_fitness_details = true;
	static boolean print_chromosome_fitness_details_steps = false;
	static boolean print_population = false;
	static boolean print_eFitness = true;  // elite fitness
	static boolean print_mutation = false;
	
	// Constructor (it's pretty useless because everything is in this class)
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
			
			// For the system, we have made the totalCities as totalCities-1
			totalCities = x-1;
			
			// Set the size depending on total cities
			dist_matrix = new int[totalCities+1][totalCities+1];
			
			// Check the second string to identify starting city
			xString = readBuffer.readLine();
			x = Integer.parseInt(xString);
			
			startCity = x-1;
			
			if(print_additional_info == true)
				System.out.println("Total cities: "+(totalCities+1)+" | Starting city: "+(startCity+1)+"\nNOTE: Total cities are 8, but the program reads from 0 to 7, and therefore the starting city is read as 2");
			
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
						if(city1 == 3 && city2 == 5)
							dist_matrix[city2][city1] = x-10;
						else
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
		population = new int[maxGeneration][maxPopulation][totalCities+2];
		
		// Initialize our array of visitedCities
		int[] visitedCities = new int[totalCities+1];
		for(int i = 0; i < visitedCities.length; i++){		
			visitedCities[i] = -1;
		}
		
		while(chromosome != maxPopulation){
			
				// set the starting city (gene value) to starting city
				population[generation][chromosome][0] = startCity;
				
				if(print_population == true)
					System.out.print(generation+"-"+chromosome+" : "+population[generation][chromosome][0]);
				
				// next few cities
				for(int gene = 1; gene <= totalCities; gene++){
					
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
					population[generation][chromosome][gene] = city;

					// Add to visited city to keep track
					visitedCities[gene] = city;
					
					if(print_population == true)
						System.out.print(" "+city+" ");
				}
				
				// ending cities (gene value) to 3
				population[generation][chromosome][totalCities+1] = startCity;
				
				if(print_population == true)
					System.out.println(" "+population[generation][chromosome][totalCities+1]);
				
				// create another chromosome
				chromosome++;
			}
		}
	
	// Evaluate the population to get their fitness
	public static void evaluatePopulation(){
		double totalDist = 0;
		double fitnessValue = 0;
		int cityA,cityB;
		int chromosome = 0;
		int eChromosome = 0;
		double eFitness = 0;
		
		// Lets go through all the chromosome and store their fitness
		while(chromosome != maxPopulation){
			
			for(int gene = 0; gene <= totalCities; gene++){
				// Get city A value
				cityA = population[generation][chromosome][gene];
				// Get City B value
				cityB = population[generation][chromosome][gene+1];
				// Get the distance between the cities and add em up to total distance
				totalDist += dist_matrix[cityA][cityB];
				if(print_chromosome_fitness_details_steps == true)
					System.out.println("step "+gene+"("+cityA+")"+"->"+(gene+1)+"("+cityB+")"+":"+totalDist);
			}
			
			// Calculating fitness value, fitness value should be between 0 and 1, 1 would mean its perfect, while 0 would mean its the opposite of perfect
			fitnessValue = 1/totalDist;
			
			// FITNESS PRINT METHOD, This is the best way of having a look at the chromosome and their fitness
			if( print_chromosome_fitness_details == true){
				System.out.print(generation+"-"+chromosome+" | C:");
				
				for(int gene = 0 ; gene <= totalCities+1; gene++){
					System.out.print(" "+population[generation][chromosome][gene]+" ");
				}
				
				System.out.println("| D: "+totalDist+" | F: "+fitnessValue );
			}
			
			// We quit if fitness value is calculated as 0 (which should not happen)
			if(fitnessValue == 0.00){
				System.out.println("Great job genius!");
				System.exit(0);
			}
			
			// Store the fitness
			fitness[generation][chromosome] += fitnessValue;
			if(fitnessValue > eFitness){
				eFitness = fitnessValue;
				eChromosome = chromosome;
			}
			
			// Move to next chromosome in our generation
			chromosome++;
			// Reset variables
			totalDist = 0;
		}
		
		if(print_eFitness == true)
			System.out.println("Champion of this gen "+generation+"-"+eChromosome+" : "+eFitness);

		// adding the finest one to ePopulation
		ePopulation[generation]=eChromosome;
		
		// At the end we get the best generation and the best chromosome
		if(generation == maxGeneration-1){
			// some print commands
			System.out.println("\nFinal Results:");
			// find the best stuff
			for(int i = 0; i < maxGeneration; i++){
				for(int j = 0; j < maxPopulation; j++){
					if(fitness[i][j] > fitness[bestGeneration][bestChromosome]){	
						fitness[bestGeneration][bestChromosome] = fitness[i][j];
						bestChromosome = j;
						bestGeneration = i;
					}
				}
			}
			// print the best stuff
			System.out.print(bestGeneration+"-"+bestChromosome+" : C: ");
			for(int gene = 0; gene <= totalCities+1; gene++){
				System.out.print(" "+population[bestGeneration][bestChromosome][gene]+" ");
				// Get the best distance again
				if(gene < totalCities+1){
					// Get city A value
					cityA = population[bestGeneration][bestChromosome][gene];
					// Get City B value
					cityB = population[bestGeneration][bestChromosome][gene+1];
					// Get the distance between the cities and add em up to total distance
					totalDist += dist_matrix[cityA][cityB];
				}
			}
			// print the fitness and distances
			System.out.print(" | D: "+totalDist+" | F: "+fitness[bestGeneration][bestChromosome]);
		}
	}
	
	// Create next generation with the help of previous generation
	public static void createNextGen(){
		int elitismOffset = 0;
		int parentA,parentB;
		int [] usedGenes = new int[totalCities+2];
		
		// Elitism is on, then one BEST chromosome gets a free pass to the next generation
		if(elitism == 1){
			
			for(int chromosome = 0; chromosome < elitism; chromosome++){
				
				if(print_population == true)
					System.out.print(generation+"-"+chromosome+" :");
				
				for(int gene = 0; gene <= totalCities+1; gene++){

					population[generation][chromosome][gene] = population[generation-1][ePopulation[generation-1]][gene];
					if(print_population == true)
						System.out.print(" "+population[generation][chromosome][gene]+" ");
				}
				
				System.out.println();
			}
			
			elitismOffset++;
		}
		
		// We start here, to create our new population
		for(int chromosome = elitismOffset; chromosome < maxPopulation; chromosome++){
			
			// Set parents
			parentA = selectParent();
			parentB = selectParent();
			// To have different parents 
			while(parentB == parentA){
				parentB = selectParent();
			}
			
			// Setting our first and last genes as 3 for all the chromosome
			population[generation][chromosome][0] = startCity;
			population[generation][chromosome][totalCities+1] = startCity;
			
			if(print_population == true)
				System.out.print(parentA+"+"+parentB+" | "+generation+"-"+chromosome+" : "+population[generation][chromosome][0]);
			
			// Creating a child with cross over 
			for(int gene = 1; gene <= totalCities; gene++){
				
				// parent random selection
				double pSelect = genRandomDouble();
				
				// Record the parent in a string for display purpose
				String sParent;
				
				// if parent is A else B. (We take a gene from the selected parent)
				if(pSelect > crossoverRate){
					population[generation][chromosome][gene] = population[generation-1][parentA][gene];
					usedGenes[gene] = population[generation-1][parentA][gene];
					sParent = "A";
				}else{
					population[generation][chromosome][gene] = population[generation-1][parentB][gene];	
					sParent = "B";
					usedGenes[gene] = population[generation-1][parentA][gene];
				}
				
				// Check if this gene is repeated, if yes, then replace this gene with a random unique gene
				for(int i = 1; i < gene; i++){
					if(population[generation][chromosome][gene] == usedGenes[i] || population[generation][chromosome][gene] == startCity){
						// check if the gene value already exists in one of the genes
						population[generation][chromosome][gene] = genRandom(totalCities+1);
						sParent = "R";
						i = 0;
					}
				}
				
				usedGenes[gene] = population[generation][chromosome][gene];
				
				if(print_population == true)
					System.out.print(" "+population[generation][chromosome][gene]+"("+sParent+")"+" ");
			}
			
			// Few print methods
			if(print_population == true)
				System.out.println(" "+startCity);
			
			if(print_mutation)
				System.out.print("M@");
			
			// Gene position (select from 1 to 6 (inclusive))
			int gPosition = genRandom(totalCities)-1;
			
			if(print_mutation)
				System.out.print(gPosition+ " : "+population[generation][chromosome][0]);
			
			// MUTATION
			// Mutation takes 2 genes and swap em
			double doMutation = genRandomDouble();
			if(doMutation <= mutationRate){
				for(int gene = 1; gene <= totalCities; gene++){
					
					int tGene;
					
					if(gene == gPosition){
						tGene = population[generation][chromosome][gene];
						population[generation][chromosome][gene] = population[generation][chromosome][gene+1];
						population[generation][chromosome][gene+1] = tGene;
						if(print_mutation)
							System.out.print("(S)");
					}
					
					if(print_mutation)
						System.out.print(" "+population[generation][chromosome][gene]+" ");
				}
			}
			if(print_mutation)
				System.out.println(" "+population[generation][chromosome][totalCities+1]);
		}
			
	}
	
	private static int selectParent(){
		double totalFitness = 0.0;
		double random;
		int chromosome;
		double [][] parentChance = new double[2][maxPopulation];
		double pChance;
		
		for(chromosome = 0; chromosome < maxPopulation; chromosome++){
			totalFitness += fitness[generation-1][chromosome];
		}
		
		for(chromosome = 0; chromosome < maxPopulation; chromosome++){
			pChance = fitness[generation-1][chromosome]/totalFitness;
			parentChance[0][chromosome] = pChance;
			if(chromosome > 0){
				parentChance[1][chromosome] = parentChance[1][chromosome-1]+parentChance[0][chromosome-1];
			}else{
				parentChance[1][chromosome] = 0.0;
			}
		}
		
		random = genRandomDouble();
		
		for(chromosome = 0; chromosome < maxPopulation; chromosome++){
			if(random >= parentChance[0][chromosome] && random < (parentChance[1][chromosome]+parentChance[0][chromosome]))
				return chromosome;
		}
		
		return 0;
	}
	
	// RANDOM FUNCTIONS--------------------------------------------

	// X is the exclusive limit, if you want 0 or 1, put x = 2
	public static int genRandom(int x){
		Random output = new Random(); 
		int number = output.nextInt(x);
		return number;
	}
	
	// Generate a random of type double, the generated value is between 0.00 and 1.00
	public static double genRandomDouble(){
		Random output = new Random();
		double number = output.nextDouble();
		return number;
	}
	
	// RANDOM FUNCTIONS END-------------------------------------------
	
	public static void main(String[] args) {
		
		// This method reads from the format file to get the distance between cities
		initialize_ds();
		
		// Our 0 generation, this is where it begins
		System.out.println("\nGen: 0");
		initializePopulation();
		
		// We perform this function after creating every generation to evaluate their fitness
		evaluatePopulation();
		
		// We then start a loop to create our next generations
		while(++generation < maxGeneration){
			// Creates next set of generation with crossover and mutation (also elitism if its on)
			createNextGen();
			// Evaluate the generation again
			System.out.println("Gen: " + generation);
			evaluatePopulation();
		}
		
		// Fin.
	}

}
