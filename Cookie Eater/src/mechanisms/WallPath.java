package mechanisms;

public class WallPath {
	double[][] positions;
	double[] rates;
	int[] modes;
	final int SPEED=0,TIME=1;
	int checkpoint;
	
	public WallPath(int numStops) {
		positions = new double[numStops][2];
		rates = new double[numStops];
		modes = new int[numStops];
	}
	
	public double[][] getSegment(){
		double prevX = positions[checkpoint%positions.length][0], prevY = positions[checkpoint%positions.length][1];
		double nextX = positions[(checkpoint+1)%positions.length][0], nextY = positions[(checkpoint+1)%positions.length][1];
		return new double[][] {{prevX, prevY},{nextX,nextY}};
	}
	
	public double getSpeed() {
		if(modes[checkpoint] == SPEED) {
			return rates[checkpoint];
		}else if(modes[checkpoint] == TIME) {
			double[][] seg = getSegment();
			return (levels.Level.lineLength(seg[0][0],seg[0][1],seg[1][0],seg[1][1]))/rates[checkpoint];
		}else {
			return 0;
		}
	}
	public void setCheckpoint(int checkpoint, double x, double y, int mode, double rate) {
		positions[checkpoint][0] = x;
		positions[checkpoint][1] = y;
		rates[checkpoint] = rate;
		modes[checkpoint] = mode;

	}
}
