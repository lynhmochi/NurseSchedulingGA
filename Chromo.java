import java.util.*;
import java.util.stream.*;


public class Chromo  implements Comparable<Chromo>{
  public static int nurses = 7; //rows
  public static int days = 7; //columns
  public static int shiftsPerDay = 3; //possible values in cell are [0,shiftsPerDay]
  public static int maxShiftsInRow = 3;

  public static int minNurses = 1; //per shift
  public static int maxNurses = 3; //per shift
  public static int minShifts = 5; //per nurse per week
  public static int maxShifts = 5; //per nurse per week

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

  public void mutate(){                                       //shift columns to the right k times
    Random rand = new Random();
    int k = (int)rand.nextInt(days);
    System.out.println(k);
    int[][] policyShifted = new int[nurses][days];
    for (int i = 0; i < nurses; i++) {
      for (int j = 0; j < days; j++) {
        policyShifted[i][j] = this.policy[i][(j + k) % this.policy[i].length];
      }
    }
    this.policy = policyShifted;
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
