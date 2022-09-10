import java.util.*;
import java.util.stream.*;


public class Chromo  implements Comparable<Chromo>{
	private int populationSize = 500;
	private static double crossoverRate = 0.9;
	private static double mutationRate = 0.5;
	private int numElites = 2;
	private int tournamentSize = 2;

	private static int numRuns=20;
	private static int timeout=1000;
  //selectionType = "ranked" or "tournament"
	private static String selectionType = "ranked";
  //crossoverType = "CrissCross" or "Horizontal"
	private static String crossoverType = "Horizontal";
  //mutationType = 1 or 2
	private static int mutationType;

  public int[][] policy;
	private double fitness;
	private int id;
  public static int numChromos = 0;

  public Chromo() {
    this.policy = policyInit();
    this.fitness = -1;
    numChromos++;
    this.id = numChromos;
  }

  public int compareTo (Chromo other) {
    if (this.fitness<other.fitness)
      return 1;
    else if (this.fitness>other.fitness)
      return -1;
    else {
      if (this.id<other.id)
        return 1;
      else if (this.id>other.id)
        return -1;
      return 0;
    }
  }
  
  public static int individualRowPriority(int[] a){                             //the last 3 constraints but for 1D array
    int violations = 0;
    int k = 0;
    HashMap<Integer, Integer> shift = new HashMap<Integer, Integer>();
    for (int i = 0; i < days; i++){
      if (a[i] != 0){
        k++;
        if (k > 3){
          violations++;
        }
        if (shift.get(a[i]) != null){
          shift.put(a[i],shift.get(a[i])+1);
        } else {
          shift.put(a[i], 1);
        }
      } else {
        k = 0;
      }
    }
    violations += Math.abs(1-shift.size());
    int n = 0;
    for (int f : shift.values()) {
      n += f;
    }
    violations += Math.abs(5-n);
    return violations;
  }

  public static Chromo horizontal_priority_crossover(Chromo c1, Chromo c2){
    Chromo child = new Chromo();
    for (int i = 0; i < nurses; i++){
      int c1_row = individualRowPriority(c1.policy[i]);
      int c2_row = individualRowPriority(c2.policy[i]);
      int bestrow = Math.min(c1_row,c2_row);
      if (bestrow == c1_row){
        child.policy[i] = c1.policy[i].clone();
      } else {
        child.policy[i] = c2.policy[i].clone();
      }
    }
    return child;
  }

  public static Chromo uniform_crossover(Chromo c1, Chromo c2){
    Chromo child = new Chromo();
    for (int i = 0; i < nurses; i++){
      for (int j = 0; j < days; j++){
        if (Math.random() < 0.5){
          child.policy[i][j] = c1.policy[i][j];
        } else {
        child.policy[i][j] = c2.policy[i][j];
        }
      }
    }
    return child;
  }

  public static Chromo singlepoint_crossover(Chromo c1, Chromo c2){
    Chromo child = new Chromo();
    Random rand = new Random();
    int crossover_point = (int)rand.nextInt(days);
    int j = 0;
    while (j < crossover_point){
      for (int i = 0; i < nurses; i++){
        child.policy[i][j] = c1.policy[i][j];
      }
      j++;
    }
    while (j < days){
      for (int i = 0; i < nurses; i++){
        child.policy[i][j] = c2.policy[i][j];
      }
      j++;
    }
    return child;
  }

  public static Chromo crisscrossover(Chromo c1, Chromo c2){
    Chromo child = new Chromo();
    for (int i = 0; i < nurses; i++){
      for (int j = 0; j < days; j++){
        if (j % 2 == 1){
          child.policy[i][j] = c1.policy[i][j];
        } else {
          child.policy[i][j] = c2.policy[i][j];
        }
      }
    }
    return child;
  }

  public void mutate(){     //shift columns to the right k times
    Random rand = new Random();
    int k = (int)rand.nextInt(days);
    int[][] policyShifted = new int[nurses][days];
    for (int i = 0; i < nurses; i++) {
      for (int j = 0; j < days; j++) {
        policyShifted[i][j] = this.policy[i][(j + k) % this.policy[i].length];
      }
    }
    this.policy = policyShifted;
  }

  public void mutate2(){       //random shuffling each row’s elements
    Random rand = new Random();
    for (int j = 0; j < nurses; j++) {
      for(int i = days - 1; i > 0; i--){
        int index = (int)rand.nextInt(i + 1);
        int temp = this.policy[j][index];
        this.policy[j][index] = this.policy[j][i];
        this.policy[j][i] = temp;
      }
    }
  }

  public void calculateFitness(){
    this.fitness = 0;
    int[] weekly5Check = new int[nurses];                     //each nurse no <5 shifts
    for (int j = 0; j < days; j++) {
      int[] shiftCheck = new int[shiftsPerDay+1];             //each shift >1 nurses <3
      for (int i = 0; i < nurses; i++) {
        if (this.policy[i][j] > 0){
          weekly5Check[i]++;
        }
        shiftCheck[this.policy[i][j]]++;
        if (shiftCheck[this.policy[i][j]] > 3){
          this.fitness--;
        }
      }
      for (int i = 0; i < shiftCheck.length; i ++){
        if (shiftCheck[i] == 0){
          this.fitness--;
        }
      }
      for (int i = 0; i < weekly5Check.length; i++){
        if (weekly5Check[i] < 5) {
          this.fitness--;
        }
      }
    }
    for (int i = 0; i < nurses; i++){                           //nurse prefers to work the same shift
      Set<Integer> sameShiftCheck = IntStream.of(this.policy[i]).boxed().collect(Collectors.toSet());
      if (sameShiftCheck.contains(0)){
        this.fitness += (2 - sameShiftCheck.size());
      } else {
        this.fitness += (1 - sameShiftCheck.size());
      }
      int k = 0;
      for (int j = 0; j < days; j++){                           //no more than 3 consecutive days of work
        if (this.policy[i][j] != 0){                            //Kadane's algo
          k++;
          if (k > 3){
            this.fitness--;
          }
        } else {
          k = 0;
        }
      }
    }
  }


  public int[][] policyInit(){
    Random rand = new Random();
    int[][] res = new int[nurses][days];
    for (int i = 0; i < nurses; i++) {
      for (int j = 0; j < days; j++) {
        res[i][j] = rand.nextInt(shiftsPerDay+1);
      }
    }
    return res;
  }

  public static void main(String[] args) {
    Chromo a = new Chromo();
    // Chromo b = new Chromo();
    // Chromo c = crisscrossover(a,b);
    //
    a.calculateFitness();
    // b.calculateFitness();
    // c.calculateFitness();

    System.out.println(Arrays.deepToString(a.policy));
    System.out.println(a.fitness);
    // System.out.println(Arrays.deepToString(b.policy));
    // System.out.println(Arrays.deepToString(c.policy));
    a.mutate();
    System.out.println(Arrays.deepToString(a.policy));
  }
}
