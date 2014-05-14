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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GeneticAlgorithm {
	static final int NUM_ROWS = 50;
	static final int NUM_COLS = 50;
	static final double PLANT_DENSITY = 0.1;
	static final double PLANTS_PER_EVOLVER = 10;
	
	static int timeStep = 100;
	
	public static void setTimeStep(int timer) {
		timeStep = timer;
	}

	public static void main(String[] args) {
		final int PLANT_COUNT = (int)(PLANT_DENSITY*NUM_ROWS*NUM_COLS);
		final int EVOLVER_COUNT = (int)(PLANT_COUNT/PLANTS_PER_EVOLVER);
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		
		final JFrame frame = new JFrame("Genetic Algorithm");
		
		frame.setLayout(new GridBagLayout());
		frame.setPreferredSize(new Dimension(800,600));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final JPanel worldGrid = new JPanel();
		worldGrid.setLayout(new GridLayout(NUM_ROWS,NUM_COLS));
		worldGrid.setBorder(BorderFactory.createLineBorder(Color.black, 5));
		worldGrid.setSize(new Dimension(500,500));

		final World world = new World(NUM_ROWS,NUM_COLS);
		world.growPlants(PLANT_COUNT,PLANT_COUNT/10);
		world.placeEvolvers(EVOLVER_COUNT);
		
		Cell[][] cells = world.getCells();
		for (Cell[] row : cells) {
			for (Cell cell : row) {
				worldGrid.add(cell);
			}
		}

		final TextScroll scroll = new TextScroll();
		
		final JSlider slider = new JSlider(JSlider.HORIZONTAL,4,40,1000/timeStep);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					setTimeStep(1000/source.getValue());
				}
			}
		});

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(4, new JLabel("Slow"));
		labelTable.put(22, new JLabel("Medium"));
		labelTable.put(40, new JLabel("Fast"));
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true);
		
		Font labelFont = new Font("Sans", Font.BOLD, 14);
		
		JLabel speedLabel = new JLabel("Speed:");
		speedLabel.setVerticalAlignment(JLabel.TOP);
		speedLabel.setFont(labelFont);

		Checkbox yearEndOnly = new Checkbox("Show only year-end summaries", false);
		yearEndOnly.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Checkbox source = (Checkbox) e.getSource();
				slider.setEnabled(!source.getState());
				world.display(!source.getState());
			}
		});
		
		JLabel scoresLabel = new JLabel("Scores:");
		scoresLabel.setFont(labelFont);

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = GridBagConstraints.REMAINDER;
		frame.add(worldGrid,c);
		c.gridheight = 1;

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = 0;
		frame.add(speedLabel,c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = 0;
		frame.add(slider,c);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = 1;
		frame.add(yearEndOnly,c);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		frame.add(scoresLabel,c);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = GridBagConstraints.REMAINDER;
		frame.add(scroll,c);

		frame.pack();
		frame.setVisible(true);
		
		world.display(true);
		
		// Run the simulation for 200 "years".  The first 100 are not displayed,
		// but the average and maximum score (number of plants per evolver) are
		// shown for each year.
		int i = 0;
		while (true) {
			i++;
			for (int j = 0; j < 365; j++) {
				if (!yearEndOnly.getState()) {
					try {
						Thread.sleep(timeStep);
					} catch (Exception e) {}
				}
				world.step();
			}

			scroll.addText(i + ": " + String.format("%.2f",world.averageScore()) + " (max = " + world.maxScore() + ")");
			
			ArrayList<int[]> newGenomes = world.evolve();
			world.removeEvolvers();
			world.refreshWorld();
			try {
				if (yearEndOnly.getState())
					Thread.sleep(100);
				else
					Thread.sleep(10*timeStep);
			} catch (Exception e) {}
			world.placeEvolvers(EVOLVER_COUNT, newGenomes);
		}
	}
}
