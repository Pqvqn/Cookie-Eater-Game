package mechanisms;

public class WallPath {
	

	final int SPEED=0,TIME=1; //mode options (timed based on constant speed or on set time)
	int checkpoint; //last passed stop on path index
	
	//arrays of attributes for each positions
	double[][] positions;
	double[] rates;
	int[] modes;
	double[][] sizes;
	
	public WallPath(int numStops) {
		positions = new double[numStops][2];
		rates = new double[numStops];
		modes = new int[numStops];
		sizes = new double[numStops][2];
	}
	
	//get line representing current movement from last checkpoint to next checkpoint
	public double[][] getSegment(){
		double prevX = positions[checkpoint()][0], prevY = positions[checkpoint()][1];
		double nextX = positions[nextpoint()][0], nextY = positions[nextpoint()][1];
		return new double[][] {{prevX, prevY},{nextX,nextY}};
	}
	
	//get speed to next point, adjusted for mode
	public double getSpeed() {
		if(modes[checkpoint()] == SPEED) {
			return rates[checkpoint()];
		}else if(modes[checkpoint()] == TIME) {
			double[][] seg = getSegment();
			return (levels.Level.lineLength(seg[0][0],seg[0][1],seg[1][0],seg[1][1]))/rates[checkpoint()];
		}else {
			return 0;
		}
	}
	//get time to next point, adjusted for mode
	public double getTime() {
		if(modes[checkpoint()] == SPEED) {
			double[][] seg = getSegment();
			return (levels.Level.lineLength(seg[0][0],seg[0][1],seg[1][0],seg[1][1]))/rates[checkpoint()];
		}else if(modes[checkpoint()] == TIME) {
			return rates[checkpoint()];
		}else {
			return 0;
		}
	}
	//get expansion to next state
	public double[] getExpansion() {
		double[] ret = new double[sizes[0].length];
		for(int i=0; i<ret.length; i++) {
			ret[i] = (sizes[nextpoint()][i] - sizes[checkpoint()][i])/getTime();
		}
		return ret;
	}
	
	//set a checkpoint's values
	public void setCheckpoint(int checkpoint, double x, double y, int mode, double rate, double sizea, double sizeb) {
		positions[checkpoint][0] = x;
		positions[checkpoint][1] = y;
		rates[checkpoint] = rate;
		modes[checkpoint] = mode;
		sizes[checkpoint][0] = sizea;
		sizes[checkpoint][1] = sizeb;
	}
	
	//last checkpoint
	public int checkpoint() {return checkpoint%positions.length;}
	//next checkpoint
	public int nextpoint() {return (checkpoint+1)%positions.length;}
	//reset to beginning of path
	public void reset() {checkpoint = 0;}
	//move to next checkpoint
	public void advance() {checkpoint++;}
}
