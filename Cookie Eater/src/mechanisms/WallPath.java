package mechanisms;

public class WallPath {
	double[][] positions;
	int checkpoint;
	
	public double[][] getSegment(){
		double prevX = positions[checkpoint%positions.length][0], prevY = positions[checkpoint%positions.length][1];
		double nextX = positions[(checkpoint+1)%positions.length][0], nextY = positions[(checkpoint+1)%positions.length][1];
		return new double[][] {{prevX, prevY},{nextX,nextY}};
	}
	public double getSpeed() {return 0;}
}
