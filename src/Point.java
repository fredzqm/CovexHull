/**
 * simplest class to represent a point
 * @author FredZhang
 *
 */
public class Point {
	int x;
	int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	public boolean equals(Object o){
		if(o.getClass()!=getClass())
			return false;
		Point n = (Point) o;
		return x==n.x && y==n.y;
	}
	
	public int hashCode(){
		return x + y * 31;
	}
	
}