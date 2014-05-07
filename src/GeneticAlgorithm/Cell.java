/* Cell.java
 * 
 * Extends JPanel - represents a single cell in the "world", which may be empty,
 * may contain a plant, or may contain an evolver facing some direction.
 */
package GeneticAlgorithm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Cell extends JPanel {
	Color color = new Color(160, 82, 45);
	// evolverDirection = -1 represents "no evolver."
	int evolverDirection = -1;
	
	public void paintComponent (Graphics g) {
		g.setColor(color);
		g.fillRect(0,0,getWidth(),getHeight());
		
		Graphics2D g2 = (Graphics2D)g;
		if (evolverDirection >= 0) {
			g.setColor(Color.black);
			int[] xVals = new int[3];
			int[] yVals = new int[3];
			switch(evolverDirection) {
			case 0:
				xVals[0] = getWidth();
				yVals[0] = getHeight()/2;
				
				xVals[1] = 0;
				yVals[1] = 0;
				
				xVals[2] = 0;
				yVals[2] = getHeight();
				
				break;
				
			case 1:
				xVals[0] = getWidth()/2;
				yVals[0] = 0;
				
				xVals[1] = 0;
				yVals[1] = getHeight();
				
				xVals[2] = getWidth();
				yVals[2] = getHeight();
				
				break;
			
			case 2:
				xVals[0] = 0;
				yVals[0] = getHeight()/2;
				
				xVals[1] = getWidth();
				yVals[1] = getHeight();
				
				xVals[2] = getWidth();
				yVals[2] = 0;
				
				break;
				
			case 3:
				xVals[0] = getWidth()/2;
				yVals[0] = getHeight();
				
				xVals[1] = getWidth();
				yVals[1] = 0;
				
				xVals[2] = 0;
				yVals[2] = 0;
				
				break;
			}
			g.fillPolygon(xVals,yVals,3);
		}
	}
	
	public void update(int contents) {
		if (contents == 0) {
			this.color = new Color(160, 82, 45);
		} else if (contents == 1) {
			this.color = Color.green;
		}
		repaint();
	}
	
	public void addEvolver(int direction) {
		this.evolverDirection = direction;
		
		repaint();
	}
	
	public void removeEvolver() {
		this.evolverDirection = -1;
		
		repaint();
	}
}
