# NurseSchedulingGA

GOAL: assessing what effect different genetic operators have on your evolved schedules

SETUP: given our earlier constraints and Chromo paramter values

public static int nurses = 7;		//rows
  
public static int days = 7; 		//columns
  
public static int shiftsPerDay = 3; 	//possible values in cell are [0,shiftsPerDay]
  
public static int maxShiftsInRow = 3;
	
public static int minNurses = 1; //per shift

public static int maxNurses = 3; //per shift

public static int minShifts = 5; //per nurse per week

public static int maxShifts = 5; //per nurse per week

Constraints (each violation = 1 penalty; positive or negative doesn't matter; we want 0)
- There must be at least one nurse, and at most three nurses, on each shift. 
- Each nurse should be scheduled for five shifts per week. 
- No nurse can work more than three days in a row without a day off. 
- Nurses prefer consistency - they would like to always work the same shift (days, evenings,  or nights). 

Limit your generations to 1000 (change the timeout).
Set number of runs to be at least 20 (can do more).

All tests can be shown me as graphs using the Visualizer (recall that any points at the timeout value on the vertical axis means we didn't find a solution in time); can show multiple tests per graph.
