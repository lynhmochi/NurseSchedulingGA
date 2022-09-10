
public class Point {
	int x; 
	int y;
	
	public Point(int x, int y) {
		this.x=x;
		this.y=y;
	}
	
	public double distance (Point other) {
		return Math.sqrt(Math.pow(this.x-other.x, 2)+Math.pow(this.y-other.y, 2));
	}
}
