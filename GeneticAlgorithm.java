import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class GeneticAlgorithm {
	
	private Chromo[] population;
	private double totalFitness;
	
	private double crossoverRate = 0.8;
	private double mutationRate = 0.5;
	private int numElites = 2; 
	private int tournamentSize = 2;
	
	private static int numRuns=10;
	private static int timeout=300000;
	private static int populationSize;
	private static String selectionType;
	public static Point[] points = new Point[100];
	
	public GeneticAlgorithm() {
		
		/*Random rand = new Random();
		int a = 1570;
		int b = -3200;
		int c = -6053;
		for(int p=0; p<points.length; p++) {
			int x = rand.nextInt(100);
			int y = a*x*x+b*x+c;
			points[p] = new Point(x,y);
			System.out.println(x+" "+y);
		}*/
		
		population = new Chromo[populationSize];
		for (int chromo=0; chromo<populationSize; chromo++) {
			population[chromo] = new Chromo();
		}
	}
	
	public Chromo selectParent() {
		Random rand = new Random();
		
		//---------------- FITNESS PROPORTIONATE SELECTION --------------------
		if (selectionType.equals("fitnessProportionate")){
			double runningFitness = 0;
			double probability = rand.nextDouble();
			//System.out.println("totalFit: "+totalFitness+"; selection probability: "+probability);
			double worstFitness = this.population[0].getFitness();
			double adjustedTotalFitness =  populationSize*worstFitness-totalFitness;

			runningFitness=0;
			for (int c=0; c<populationSize; c++) {
				runningFitness += (worstFitness - this.population[c].getFitness());
				//System.out.println("["+c+"] running fitness: "+runningFitness + "/total = "+runningFitness/totalFitness);
				if (probability < runningFitness/adjustedTotalFitness)
					return this.population[c];			
			}
			//we'll only end up here if all fitnesses were zero, so let's pick a parent at random
			return this.population[rand.nextInt(this.populationSize)];
		}
		else if (selectionType.equals("ranked")) {
			//---------------- RANK PROPORTIONAL SELECTION --------------------
			//since chromos in current generation have been sorted, their spot+1 is their rank
			double sumRank = 0;
			double overallSumRanks = (populationSize+1)*populationSize/2.0;
			
			double probability = rand.nextDouble();
			//System.out.println("selection probability: "+probability);
			for (int c=0; c<populationSize; c++) {
				sumRank += (c+1);//ranks are not zero based
				//System.out.println(population[c] + " rank "+(c+1));
				if (probability < sumRank/overallSumRanks) {
					//System.out.println("Chosen parent: "+this.population[c]);
					return this.population[c];	
				}
			}
			//if somehow we got here without picking anyone, we could pick best
			return this.population[populationSize-1];
		}
		else if (selectionType.equals("tournament")) {
			
			int parentIndex = rand.nextInt(populationSize);
			for (int t=1; t<tournamentSize; t++) {
				int newIndex = rand.nextInt(populationSize);
				if (newIndex>parentIndex)//since they are sorted, selecting highest index is enough
					parentIndex=newIndex;
			}
			return this.population[parentIndex];
		}
		else {//RANDOM SELECTION AS BASELINE
			return this.population[rand.nextInt(populationSize)];
		}
	}
	
	public void makeNextGen() {
		
		Chromo[] tng = new Chromo[populationSize];
		int tngSize;
		
		// -------------------ELITISM----------------------------
		for (tngSize=0; tngSize<numElites; tngSize++) {//pick top, i.e. last
			tng[tngSize] = this.population[populationSize-1-tngSize];
		}
		
		Random rand = new Random();
		Chromo parent1, parent2, child;
		//----------------- MUTATION ------------
		int mutants = (int)(populationSize*mutationRate);
		for (int m=0; m<mutants; m++) {
			parent1 = selectParent();
			child = new Chromo (parent1.getPolicy());//don't point to parent in case we need changes like mutation 
			child.mutate();
			tng[tngSize++] = child;
		}
		//----------------- CROSSOVER ------------
		while (tngSize < populationSize) {
		
			parent1 = selectParent();
			parent2 = null;
			child = null;
			
			if (rand.nextDouble()<crossoverRate) {
				parent2 = selectParent();
				child = Chromo.averaging_crossover(parent1,parent2);
			}
			else {//no crossover, just copy the parent into new generation
				child = new Chromo (parent1.getPolicy());//don't point to parent in case we need changes like mutation 
				child.setID(parent1.getID());//just copy of parent1, so same id
			}
				
			tng[tngSize++] = child;
		}
		
		this.population = tng;
	}

	public static void main(String[] args)  throws Exception {

		//File file = new File("./src/Problem1_1_-4_9.txt");
		//File file = new File("./src/Problem2_19_47_-90.txt");
		//File file = new File("./src/Problem3_-100_-32_-7.txt");
		File file = new File("./src/Problem4_1570_-3200_-6053.txt");
		 Scanner sc = new Scanner (file);
		 

		//-----------------------------------------------------
		for (int p=0; p<points.length; p++) {
			points[p] = new Point(sc.nextInt(),sc.nextInt());
		}
		sc.close();
		double maxFitness = 0; //ideal sum distance from points is zero
		boolean converged;
		boolean writeGenerations=false;
		
		int numTests = 3;
		double[][] runNum = new double[numTests][numRuns];
		double[][] genSolved = new double[numTests][numRuns];
		for (int test=0; test<numTests; test++) {
			
			switch (test) {
				case 0: 
					populationSize = 30;
					selectionType = "ranked";
					break;
				case 1: 
					populationSize = 30;
					selectionType = "fitnessProportionate";
					break;
				case 2: 
					populationSize = 30;
					selectionType = "tournament";
					break;
			}
			
			FileWriter fw = new FileWriter(new File ("./src/test"+test+"_output.txt"), false);
			for (int run=0; run<numRuns; run++) {
				GeneticAlgorithm ga = new GeneticAlgorithm();
				converged=false;
				int gen=0;
				while(true && gen<timeout) {//evolve until solved or timed out
					ga.totalFitness = 0;
					
					//--------- test everyone's fitness -----------
					for (int c=0; c<populationSize; c++) {
						ga.population[c].calculateFitness();
						ga.totalFitness +=ga.population[c].getFitness();
					}
					//--------- sort, establish best, count copies of best ------------
					Arrays.sort(ga.population);
					
					int bests = 0;
					int[] bestValues = ga.population[populationSize-1].policy;
					fw.write("Run["+run+"]Gen["+gen+"] ");
					for (int c=0; c<populationSize; c++) {
						if (Arrays.equals(ga.population[c].policy,bestValues)) {
							bests++;
							if(writeGenerations) 
								fw.write("Chromo*:"+ga.population[c]);//adding asterisks to copies of best in output file
						}
						else if(writeGenerations) 
							fw.write("Chromo:"+ga.population[c]);
					}
					fw.write("\n");
					System.out.println("Test["+test+"]Run["+run+"]Gen["+gen+"] Best Chromo (x"+bests+"):"+ga.population[populationSize-1]);
					
					//--------- check if we are done searching------------
					if (ga.population[populationSize-1].getFitness()==maxFitness)
						break;
					if (ga.population[populationSize-1].policy[0]==ga.population[0].policy[0] &&
						ga.population[populationSize-1].policy[1]==ga.population[0].policy[1] &&
						ga.population[populationSize-1].policy[2]==ga.population[0].policy[2]) {
						converged=true;
						break;
					}
					//--------- breed a new generation ---------------
					ga.makeNextGen();
					gen++;
				}
				if (converged)
					System.out.printf("\n[run %3d] POPULATION IS FULLY CONVERGED in gen %d",run+1,gen);
				else
					System.out.printf("\n[run %3d] exact solution found in gen %d",run+1,gen);
				genSolved[test][run] = gen;
				runNum[test][run] = (run+1);
			}
			 fw.close();
		}
		//visualizer takes 2 2D arrays of doubles: xValues[][], yValues[][] 
				//first dimension represents the series/experiment and second dimension represents the points in that series/experiment
				//you can pass in null for the xValues[][], in which case indices will be used as xValues 
				//for example: plotting values per generation over time, we don't need to pass in anything for the generation numbers
				//visualizer also accepts two strings to label your axes
		Visualizer.visualize(runNum, genSolved, "Run#", "Gen Solution Found"); //new String[] {"experiment 1","experiment 2"
				
	}
}
