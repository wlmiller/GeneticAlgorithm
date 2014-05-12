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
import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class GeneticAlgorithm {
	static final int NUM_ROWS = 50;
	static final int NUM_COLS = 50;
	static final double PLANT_DENSITY = 0.1;
	static final double PLANTS_PER_EVOLVER = 10;

	public static void main(String[] args) {
		final int PLANT_COUNT = (int)(PLANT_DENSITY*NUM_ROWS*NUM_COLS);
		final int EVOLVER_COUNT = (int)(PLANT_COUNT/PLANTS_PER_EVOLVER);
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		
		JFrame frame = new JFrame("Genetic Algorithm");
		
		frame.setLayout(new GridBagLayout());
		frame.setPreferredSize(new Dimension(800,600));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel worldGrid = new JPanel();
		worldGrid.setLayout(new GridLayout(NUM_ROWS,NUM_COLS));
		worldGrid.setBorder(BorderFactory.createLineBorder(Color.black, 5));
		worldGrid.setSize(new Dimension(500,500));

		World world = new World(NUM_ROWS,NUM_COLS);
		world.growPlants(PLANT_COUNT,PLANT_COUNT/10);
		world.placeEvolvers(EVOLVER_COUNT);
		
		Cell[][] cells = world.getCells();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				worldGrid.add(cells[i][j]);
			}
		}

		TextScroll scroll = new TextScroll();

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		frame.add(worldGrid,c);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = 0;
		frame.add(scroll,c);

		frame.pack();
        world.display();
		frame.setVisible(true);
		
		world.noDisplay();
		
		// Run the simulation for 200 "years".  The first 100 are not displayed,
		// but the average and maximum score (number of plants per evolver) are
		// shown for each year.
		for (int i = 0; i < 200; i++) {
			for (int j = 0; j < 365; j++) {
				if (i >= 100) {
					try {
						Thread.sleep(25);
					} catch (Exception e) {}
				}
				world.step();
			}

			scroll.addText(i + ": " + String.format("%.2f",world.averageScore()) + " (max = " + world.maxScore() + ")");
			
			ArrayList<int[]> newGenomes = world.evolve();
			world.removeEvolvers();
            if (i == 99) {
                world.display();
            }
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
