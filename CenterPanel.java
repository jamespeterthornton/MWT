
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.*;

public class CenterPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<int[]> chordsToDraw;
	ArrayList<int[]> verticesArray;
	
	
	public void init() {
		
		chordsToDraw = new ArrayList<int[]>();
		verticesArray = new ArrayList<int[]>();

		
		System.out.println("vertices array " + verticesArray);
		
	}
	
    public void clearChords() {
    	
    	chordsToDraw = new ArrayList<int[]>();
    	
    }
    
    public void clearVertices() {
    	
    	verticesArray = new ArrayList<int[]>();
    	
    }
    
     public void paintComponent(Graphics g) {
    	
    	super.paintComponent(g);
    	
    	Graphics2D g2 = (Graphics2D) g;

    	
    	g2.setColor(Color.BLACK);

  	for (int i = 0; i < verticesArray.size(); i++) {
    		
    		int[] vertex = verticesArray.get(i);
    		
    		int[] nextVertex = {0 , 0};
    		
    		if((verticesArray.size() - 1) > i) {
    			
    			nextVertex = verticesArray.get(i+1);
        		
    		} else {
    			
    			nextVertex = verticesArray.get(0);
    			
    		}
    			
        	g2.drawLine(vertex[0], vertex[1], nextVertex[0], nextVertex[1]); 
    		
        	Shape vertexCircle = new Ellipse2D.Double(vertex[0] - 5, vertex[1] - 5, 10, 10);
        	
    		g2.fill(vertexCircle);
    		
    		g2.drawString("V" + i, vertex[0]-25, vertex[1] + 20);

       
    	} 
    	
    	for (int i = 0; i < chordsToDraw.size(); i++) {
    		
    		int[] chord = chordsToDraw.get(i);
    		
    		int[] vertex1 = verticesArray.get(chord[0]);
    		int[] vertex2 = verticesArray.get(chord[1]);
    		
    		
    		//System.out.println("drawing chord from " + Arrays.toString(vertex1) + " to " + Arrays.toString(vertex2));
    		
    		g2.drawLine(vertex1[0], vertex1[1], vertex2[0], vertex2[1]);
    		
    	} 
    	
    	
    }
	
}
