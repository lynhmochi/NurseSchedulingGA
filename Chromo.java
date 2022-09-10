import java.util.Arrays;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

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
    System.out.println(Arrays.toString(weekly5Check));
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
    a.calculateFitness();
    System.out.println(a.fitness);
    System.out.println(Arrays.deepToString(a.policy));

  }
}
