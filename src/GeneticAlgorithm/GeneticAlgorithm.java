/* GeneticAlgorithm.java
 * 
 * Simulates a population of simple "evolvers", which move around based on a
 * set of deterministic rules.  The rules take as input the contents of the 
 * cell immediately in front of the evolver and the state of the evolver, which
 * is an integer between 0 and 15.  The result of the rule is an action (move 
 * forward, turn left, turn right, or move backward) and a new state.
 * 
 * Every 365 steps (1 "year"), the evolvers evolve.  Their sets of rules are represented
 * as arrays of (16*3*2) = 96 integers (there are three possible cell contents: empty, plant,
 * or evolver).  These genomes are combined via crossover and mutated.
 */
package GeneticAlgorithm;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;

public class GeneticAlgorithm {
	static final int NUM_ROWS = 60;
	static final int NUM_COLS = 80;
	static final double PLANT_DENSITY = 0.1;
	static final double PLANTS_PER_EVOLVER = 10;

	public static void main(String[] args) {
		final int PLANT_COUNT = (int)(PLANT_DENSITY*NUM_ROWS*NUM_COLS);
		final int EVOLVER_COUNT = (int)(PLANT_COUNT/PLANTS_PER_EVOLVER);
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		
		JFrame frame = new JFrame("Genetic Algorithm");
		
		frame.setLayout(new GridLayout(NUM_ROWS,NUM_COLS));
		frame.setPreferredSize(new Dimension(800,600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		World world = new World(NUM_ROWS,NUM_COLS);
		world.growPlants(PLANT_COUNT,(int)(PLANT_COUNT/10));
		world.placeEvolvers(EVOLVER_COUNT);
		
		Cell[][] cells = world.getCells();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				frame.add(cells[i][j]);
			}
		}
		frame.pack();
		
		world.noDisplay();
		
		// Run the simulation for 200 "years".  The first 100 are not displayed,
		// but the average and maximum score (number of plants per evolver) are
		// shown for each year.
		for (int i = 0; i < 200; i++) {
			for (int j = 0; j < 365; j++) {
				if (i == 100) {
					world.display();
					frame.setVisible(true);
				}
				if (i >= 100) {
					try {
						Thread.sleep(25);
					} catch (Exception e) {}
				}
				world.step();
			}
			System.out.print(i);
			System.out.print(": ");
			System.out.format("%.2f",world.averageScore());
			System.out.print(" (max = ");
			System.out.print(world.maxScore());
			System.out.println(")");
			
			ArrayList<int[]> newGenomes = world.evolve();
			world.removeEvolvers();
			world.refreshWorld();
			try {
				if (i < 100)
					Thread.sleep(100);
				else
					Thread.sleep(1000);
			} catch (Exception e) {}
			world.placeEvolvers(EVOLVER_COUNT, newGenomes);
		}
	}
}
