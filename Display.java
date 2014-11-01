import java.applet.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.Polygon;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

//import java.awt.Container.*;
//import java.awt.Component.*;

public class Display extends Applet implements ActionListener {

	private static final long serialVersionUID = 4689551673413905536L;

	TextField numberField;
    Button clearButton, findMWTButton;
    JPanel canvas, top, bottom;
    CenterPanel center;
    ArrayList<int[]> verticesArray;
    double[][] t;
    int[][] s;
    ArrayList<int[]> chordsToDraw;
    Polygon thisPolygon;
    JTable table;
    
    public void init() {
    	
        // layout the grid for the input interface
    	
    	verticesArray = new ArrayList<int[]>();
    	chordsToDraw = new ArrayList<int[]>();
    	
        clearButton = new Button("Clear");
        clearButton.setBackground(Color.WHITE);
        clearButton.addActionListener(this);
        findMWTButton = new Button("Find MWT");
        findMWTButton.setBackground(Color.WHITE);
        findMWTButton.addActionListener(this);
       
        canvas = new JPanel(new BorderLayout());
        
        top = new JPanel();
        top.setLayout(new FlowLayout());
        top.add(clearButton);
        top.add(findMWTButton);
        top.setBackground(Color.BLUE);

        center = new CenterPanel();
        
        center.init();
        
       center.setBackground(Color.WHITE);
        
       bottom = new JPanel();
        
       bottom.setLayout(new BorderLayout());
       
       bottom.setPreferredSize(new Dimension(200, 150));
        
       center.addMouseListener(new MouseListener(){
        	
        	public void mouseClicked(MouseEvent e) {
        				
        			int[] thisVertex = {e.getX(), e.getY()};
            		verticesArray.add(thisVertex);
            		System.out.println(Arrays.toString(thisVertex));
            		center.verticesArray.add(thisVertex);
            		
            		String[] columns = new String[verticesArray.size()];
            		
            		for (int i = 0; i < verticesArray.size(); i++) {
            			
            			columns[i] = "V" + i;
            			
            		}
            		
            	
                    //redraw the table whenever a new point is added, because this changes the dimensions of the table

            		DefaultTableModel tableModel = new DefaultTableModel(columns, verticesArray.size());
            		
            		table = new JTable(tableModel);
            		
            		MatteBorder border = new MatteBorder(1,1,1,1,Color.black);
            		table.setBorder(border);
            		table.setShowGrid(true);
            		
            		table.setSelectionModel(new DefaultListSelectionModel());;
            		
            		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            		
            		JScrollPane scrollPane = new JScrollPane(table);
            		JTable rowTable = new RowNumberTable(table);
            		scrollPane.setRowHeaderView(rowTable);
            		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
            		
            		bottom.removeAll();
            		
            		bottom.add(scrollPane, BorderLayout.CENTER); 
            		
            		bottom.invalidate();
            		
            		bottom.getTopLevelAncestor().validate();
        			
        			repaint();
        			
        			System.out.println(verticesArray);
        	}
        	
        	public void mouseEntered(MouseEvent arg0) {}
            public void mouseExited(MouseEvent arg0) {}
            public void mousePressed(MouseEvent arg0) {}
            public void mouseReleased(MouseEvent arg0) {}
        	
        });
        
        this.setLayout(new BorderLayout());
        this.add("North", top);
        this.add("Center", center);
        this.add("South", bottom);
    }

    public boolean isNumber(String string) {
        char[] c = string.toCharArray();
        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(c[i])) {
                return false;
            }
        }
        return true;
    }

    
    
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == clearButton) {

            // get the length of the text, so we can remove it, and rewrite
            // the sorted assignment schedule into the textArea
        	
        	bottom.removeAll();
        	
        	center.clearVertices();
        	
        	verticesArray = new ArrayList<int[]>();
        	chordsToDraw = new ArrayList<int[]> ();
        	repaint();
        	

        } else if (e.getSource() == findMWTButton) {
        	
        	
        	findMWT();
        	
        	
        	this.repaint();
        	
        	
        }
    } 
    
    
    public void findMWT() {
    	
    	
    	
    	makePolygon();
    	
    	int n = verticesArray.size()-1;
    	
       	t = new double[n+1][n+1];
    	s = new int[n+1][n+1];
    	
    	for (int i = 0; i < n; i++) {
    		
    		t[i][i+1] = 0;
    		
    		table.setValueAt(0, i, i+1);
    		
    		
    		Rectangle rect = table.getBounds();
    		
    		
    		
    		System.out.println(table.getBounds());
    		
    		//rect.y += top.getSize().height;
    		
    		table.paintImmediately(rect);

    		
    	}
    	
    	//first, fill out the table for length 2 subchains
    	//then for length 3
    	//and so on
    	
    	for (int gap = 2; gap <= n; gap++) {
    		
   // 		System.out.println("gap is " + gap);
    		
    		//iterate through all of the i's as starting points
    		//up until the last possible i with this subchain length
    		
    		for(int i = 0; i<=n-gap; i++) {
    			
    			//set j to the index this subchain's length away
    			//from i
    			
    			int j = i + gap;
    			
    			//set by default an impossibly high value for
    			//the mwt from i to j
    			
    			t[i][j] = 10000000;
    			
    			//this is where we calculate the MWT of the subpolygon from i to j
    			
    			for( int k = i+1; k < j; k++) {
    				
                    //check if the triangle is good before processing this set of i, k, and j
                    //if any of the triangle's constituent line segments exit the polygon,
                    //then we do not process the triangle and thus avoid faulty triangulations				

    				if (isTriangleGood(i, k, j)) {
		    				
		    				double q = t[i][k] + t[k][j] + weight(i, k, j);
		    				
		    				if (q < t[i][j]) {
		    					
                                //if we have found a new minimum value for q, update our arrays and the table

		    					t[i][j] = q;
		    					s[i][j] = k;
		    					table.setValueAt(Math.round(q*100)/100, i, j);
		    					table.paintImmediately(table.getBounds());
		    		    		System.out.println(table.getBounds());
		
		    					
		    				}

                            //draw the chords for animation
		    				
		    				clearChords();
		    				
		    				int[] chord1 = {i, k};
		    				int[] chord2 = {j, k};
		    			
		    				center.chordsToDraw.add(chord1);
		    				center.chordsToDraw.add(chord2);
		    				
		    				center.paintImmediately(center.getBounds());
		    				
		    				try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
		    				
		    				center.clearChords();
    				
    				}
    				
    			}
    			
    		}
    	}
    	
    	System.out.println(Arrays.deepToString(t));
    	System.out.println(Arrays.deepToString(s));
    	System.out.println("minimum cost is " + t[0][n] + " and decision is " + s[0][n]);
    	
    	
    	center.clearChords();
    	drawChords(0, n);
    	
    }    
    
    public void makePolygon() {
    	
    	
  	    List<Integer> xCoordinates = new ArrayList<Integer>();
	    
	    List<Integer> yCoordinates = new ArrayList<Integer>();
	    
	    for (int i = 0; i < verticesArray.size(); i++) {
	    	
	    	int[] vertex = verticesArray.get(i);
	    	
	    	xCoordinates.add(vertex[0]);
	    	yCoordinates.add(vertex[1]);
	    		
	    }
	    
	    thisPolygon = new Polygon();
	    thisPolygon.npoints = verticesArray.size();
	    thisPolygon.xpoints = listToArray(xCoordinates);
	    thisPolygon.ypoints = listToArray(yCoordinates);
    	
    }
    
    
    public boolean isTriangleGood(int i, int k, int j) {
    	
    	
    	if(isLineGood(i, k) && isLineGood(k,j) && isLineGood(i,j)) return true;
    	
    	System.out.println("triangle is nooo good");
    	
    	return false;
    	
    }
    
    public boolean isLineGood(int a, int b) {
    	
    	
    	if((Math.abs(a-b) == 1) || a==b || ((a==0) && (b==verticesArray.size()-1))) return true;
    	
    	System.out.println("testing a " + a + " and b " + b);
    	
    	int[] vertex1 = verticesArray.get(a);
    	int[] vertex2 = verticesArray.get(b);
    	
    	int midX = (vertex1[0] + vertex2[0])/2;
    	int midY = (vertex1[1] + vertex2[1])/2;
    	
    	if(!thisPolygon.contains(midX, midY)) {
    		
    		System.out.println("this polygon does not contain midpoint with x " + midX + " and Y " + midY);
    		
    		return false;
    	}
    	
    	
    	
    	Line2D line1 = new Line2D.Float(vertex1[0], vertex1[1], vertex2[0], vertex2[1]);
    	
    	
    	System.out.println("now checking a " + a + " and b " + b);
    	
    	for (int i =0 ; i < verticesArray.size(); i ++ ) {
    	
    		
    		if(i!=a && i!=b && (i+1)%verticesArray.size()!=a && (i+1)%verticesArray.size()!=b) {
    		
    			System.out.println("checking i " + i + " a " + a + " and b " + b);
    			
	    		int[] vertex3 = verticesArray.get(i);
	    		int[] vertex4 = verticesArray.get((i+1)%verticesArray.size());
	    		
	    		Line2D line2 = new Line2D.Float(vertex3[0], vertex3[1], vertex4[0], vertex4[1]);
	    		
	    		if(line2.intersectsLine(line1)) return false;
	    		
    		}
    	}
    	
    	
    	System.out.println("returning truuuuueeeee");
    	
    	return true;
    	
    }
    
    
    
    public ActionListener highlightChords(int i, int k, int j) {
    	
		clearChords();
		
		int[] chord1 = {i, k};
		int[] chord2 = {j, k};
	
		chordsToDraw.add(chord1);
		chordsToDraw.add(chord2);
		
		repaint();
		return null;
		
    }
    
    
    public void drawChords(int i, int j) {
    	
    	if(Math.abs(i-j) > 1) {
    		
    		System.out.println("i is " + i+ " and j is " + j + " and s is " + Arrays.deepToString(s));
    		
    		int[] chord1 = {i, s[i][j]};
    		int[] chord2 = {j, s[i][j]};
    	
    		chordsToDraw.add(chord1);
    		chordsToDraw.add(chord2);
    	
    		drawChords(i, s[i][j]);
    		drawChords(s[i][j], j);
    	
    	}
    }
    
    public void clearChords() {
    	
    	chordsToDraw = new ArrayList<int[]>();
    	
    }
    
    public double weight(int i, int k, int j) {
    	
    	double weight = distance(i,k) + distance(k, j) + distance(j, i); 
    	
    	return weight;
    	
    }
    
    public double distance(int x, int y) {
    	
    	int[] vertex1 = verticesArray.get(x);
    	int[] vertex2 = verticesArray.get(y);
    	
    	double x1 = Math.abs(vertex1[0] - vertex2[0]);
    	double x2 = Math.abs(vertex1[1] - vertex2[1]);
    	
    	double x3 = (x1*x1) + (x2*x2);
    	
    	double thisDistance = Math.pow(x3, 0.5);
    	
    	return thisDistance;
    	
    }
    
    
    public int[] listToArray(List<Integer> listToConvert) {
    	
    	int[] retArray = new int[listToConvert.size()];
    	
    	int i = 0;
    	
    	for (int e : listToConvert) {
    		
    		retArray[i++] = e;
    		
    	}
    	
    	return retArray;
    	
    }
    
    public void paint(Graphics g) {
    	
    	super.paint(g);
    	
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
    			
        	g2.drawLine(vertex[0], vertex[1] + top.getSize().height, nextVertex[0], nextVertex[1] + top.getSize().height); 
    		
        	Shape vertexCircle = new Ellipse2D.Double(vertex[0] - 5, vertex[1] + top.getSize().height - 5, 10, 10);
        	
    		g2.fill(vertexCircle);
    		
    		g2.drawString("V" + i, vertex[0]-25, vertex[1]+top.getSize().height + 20);
    		
    	} 
    	
    	for (int i = 0; i <chordsToDraw.size(); i++) {
    		
    		int[] chord = chordsToDraw.get(i);
    		
    		int[] vertex1 = verticesArray.get(chord[0]);
    		int[] vertex2 = verticesArray.get(chord[1]);
    		
    		
    		//System.out.println("drawing chord from " + Arrays.toString(vertex1) + " to " + Arrays.toString(vertex2));
    		
    		g2.drawLine(vertex1[0], vertex1[1] + top.getSize().height, vertex2[0], vertex2[1] + top.getSize().height);
    		
    	}
    	
    }

}


