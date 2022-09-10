//code adapted from https://www.javatpoint.com/java-plot PlotExample.java

//import require classes and packages  
import java.awt.*;  
import javax.swing.*;  
import java.awt.geom.*;  
  
//Extends JPanel class  
public class Visualizer extends JPanel{  
	
    //initialize coordinates  
	private static String xAxisLabel;
	private static String yAxisLabel;
    private double[][] xCoords;   
    private double[][] yCoords;
    private int marg = 75;  
      
    public Visualizer (double[][] xCoords, double[][] yCoords) {
    	this.xCoords = xCoords;
    	this.yCoords = yCoords;
    }

    protected void paintComponent(Graphics grf){  
        //create instance of the Graphics to use its methods  
        super.paintComponent(grf);  
        Graphics2D graph = (Graphics2D)grf;  
          
        //Sets the value of a single preference for the rendering algorithms.  
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
          
        // get width and height  
        int width = getWidth();  
        int height = getHeight();  
          
        // draw graph axes 
        graph.setColor(Color.LIGHT_GRAY);
        graph.draw(new Line2D.Double(marg, marg, marg, height-marg));  
        graph.draw(new Line2D.Double(marg, height-marg, width-marg, height-marg));  
          
        //find value of x and scale to plot points  
        //double x = (double)(width-2*marg)/(yCoords.length-1);  
        double scaleX = (double)(width-2*marg)/getMax(xCoords); 
        double scaleY = (double)(height-2*marg)/getMax(yCoords);  
        
          
        //let's draw some  axis ticks 
        int tickWidth = 6;
       
        
      //--------- now let's handle the y ticks -------------
        double[] goodTickLabels = new double[] {100000, 50000, 10000, 5000, 1000,500,200,100,50,20,10,5, 2, 1, 0.5, 0.2, 0.1, 0.05,0.01,0.005, 0.001};
        
       
        double upperBound = getMax(xCoords)/5.0;
        double tickMarker=1;
        for (int i=0; i<goodTickLabels.length; i++) {
        	if (goodTickLabels[i]<=upperBound) {
        		tickMarker=goodTickLabels[i];
        		break;
        	}	
        }
        int xticks = (int)((width-2*marg)/scaleX/tickMarker);
        //System.out.printf("xticks %d scalex %f xticks %d, upperbound %f, tickMarker %f\n",xticks, scaleX, xticks, upperBound, tickMarker);
        for (int t=0; t<=xticks; t++) {
        	graph.draw(new Line2D.Double(marg+t*tickMarker*scaleX, height-marg+tickWidth, marg+t*tickMarker*scaleX, height-marg-tickWidth));
        	String tickLabel = String.valueOf(t*tickMarker);
        	int length = tickLabel.length();
        	graph.drawString(tickLabel,(int)(marg+t*tickMarker*scaleX-3), (int)(height-marg+3.5*tickWidth)); //horizontal tick labels
        }
        //--------- now let's handle the y ticks -------------
        int yticks = (int)((height-2*marg)/scaleY);
        upperBound = yticks/5.0;
        tickMarker=1;
       
        for (int i=0; i<goodTickLabels.length; i++) {
        	if (goodTickLabels[i]<=upperBound) {
        		tickMarker=goodTickLabels[i];
        		break;
        	}	
        }
        
        for (int t=0; t<=yticks; t++) {
       	 if (t%tickMarker==0) {
           	 graph.draw(new Line2D.Double(marg-tickWidth, height-marg-t*scaleY, marg+tickWidth, height-marg-t*scaleY));
       		 String tickLabel = String.valueOf(t);
       		 int length = tickLabel.length();
       		 graph.drawString(tickLabel,(int)(marg-3*tickWidth-4*length), (int)(height-marg-t*scaleY+3)); //vertical tick labels
       	 }
       }
        
        //----------------- let's label our axes ----------------
        Font font = new Font("Arial", Font.PLAIN, 15);
        FontMetrics metrics = graph.getFontMetrics(font);
        graph.setFont(font);
        graph.drawString(xAxisLabel, (int)((width-marg)/2-metrics.stringWidth(xAxisLabel)/2), height-marg/4);
        drawRotate(graph, marg/3, (height + metrics.stringWidth(yAxisLabel)) / 2, 270, yAxisLabel);
        
        
        
        //let's draw our points
        Color [] colors = new Color[] { Color.MAGENTA, Color.CYAN, Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.YELLOW, Color.WHITE, Color.BLACK};
        double x1, y1;
        for (int series=0; series<yCoords.length; series++) {
        	graph.setPaint(colors[series]); //set color for points   
	        // set points to the graph  
	        if (xCoords==null || xCoords[series]==null){
		        for(int i=0; i<yCoords[series].length; i++){  
		        	x1 = marg+i*scaleX;  
		            y1 = height-marg-scaleY*yCoords[series][i];  
		            graph.fill(new Ellipse2D.Double(x1-2, y1-2, 4, 4));  
		        }  
	        }
	        else {
	        	 for(int i=0; i<yCoords[series].length; i++){  
	 	        	x1 = marg+xCoords[series][i]*scaleX;  
	 	            y1 = height-marg-scaleY*yCoords[series][i];  
	 	            graph.fill(new Ellipse2D.Double(x1-2, y1-2, 4, 4));  
	 	        }  
	        }
        }
        
    }  
    public void drawRotate(Graphics2D graph, double x, double y, int angle, String text) {
    	graph.translate((float) x, (float) y);
    	graph.rotate(Math.toRadians(angle));
        graph.drawString(text, 0, 0);
        graph.rotate(-Math.toRadians(angle));
        graph.translate(-(float) x, -(float) y);
    }

      
    //create getMax() method to find maximum value  
    private double getMax(double [][] array){  
        double max = -Double.MAX_VALUE;  
        for(int i=0; i<array.length; i++){  
	        for(int j=0; j<array[i].length; j++){  
	            if(array[i][j]>max)  
	                max = array[i][j];  
	             
	        }
        } 
        return max;  
    }    
    
    public static void visualize(double [][] xCoords, double[][] yCoords, String xLabel, String yLabel) {
    	
    	xAxisLabel = xLabel;
    	yAxisLabel = yLabel;
    	 //create an instance of JFrame class  
        JFrame frame = new JFrame();  
        // set size, layout and location for frame.  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        JPanel mainPanel = new Visualizer(xCoords, yCoords);  
        frame.add(mainPanel); 
        frame.setContentPane(mainPanel); //necessary for next line to actually change frame color 
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setSize(400, 400);  
        frame.setLocation(200, 200);  
        frame.setVisible(true);  
    }
      
    //main() method start  
    public static void main(String args[]){  
    	
    	xAxisLabel = "x axis";
    	yAxisLabel = "y axis";
    	
    	 //create an instance of JFrame class  
        JFrame frame = new JFrame();  
        // set size, layout and location for frame.  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        JPanel mainPanel = new Visualizer(new double [][] {{0.1,0.2,0.3,0.4,5}, {0.1,0.2,0.3,0.4,0.5}}, new double [][] {{1,2,3,4,5}, {2,4,6,8,10}});
        frame.add(mainPanel); 
        frame.setContentPane(mainPanel); //necessary for next line to actually change frame color 
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setSize(400, 400);  
        frame.setLocation(200, 200);  
        frame.setVisible(true);  
    }  
}  