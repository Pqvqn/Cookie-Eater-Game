package mechanisms;

import java.util.*;

import ce3.*;

public class Path {
	

	public static final int SPEED=0,TIME=1; //mode options (timed based on constant speed or on set time)
	int checkpoint; //last passed stop on path index
	
	//arrays of attributes for each positions
	double[][] positions;
	double[] rates;
	int[] modes;
	double[][] sizes;
	double[] angles;
	
	//counts updates to advance to next checkpoint
	int updates;
	
	public Path(int numStops) {
		positions = new double[numStops][2];
		rates = new double[numStops];
		modes = new int[numStops];
		sizes = new double[numStops][2];
		angles = new double[numStops];
		updates = 0;
	}
	
	public Path(SaveData sd) {
		checkpoint = sd.getInteger("checkpoint",0);
		updates = 0;
		
		ArrayList<Object> pos = sd.getData("positions");
		positions = new double[pos.size()/2][2];
		for(int i=0; i<pos.size(); i++) {
			positions[i/2][i%2] = Double.parseDouble(pos.get(i).toString());
		}
		ArrayList<Object> rat = sd.getData("rates");
		rates = new double[rat.size()];
		for(int i=0; i<rat.size(); i++) {
			rates[i] = Double.parseDouble(rat.get(i).toString());
		}
		ArrayList<Object> mod = sd.getData("modes");
		modes = new int[mod.size()];
		for(int i=0; i<mod.size(); i++) {
			modes[i] = Integer.parseInt(mod.get(i).toString());
		}
		ArrayList<Object> siz = sd.getData("sizes");
		sizes = new double[siz.size()/2][2];
		for(int i=0; i<siz.size(); i++) {
			sizes[i/2][i%2] = Double.parseDouble(siz.get(i).toString());
		}
		ArrayList<Object> ang = sd.getData("angles");
		angles = new double[ang.size()];
		for(int i=0; i<ang.size(); i++) {
			angles[i] = Double.parseDouble(ang.get(i).toString());
		}
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("checkpoint",checkpoint);
		for(int i=0; i<positions.length; i++) {
			data.addData("positions",positions[i][0],2*i);
			data.addData("positions",positions[i][1],2*i+1);
		}
		for(int i=0; i<rates.length; i++)data.addData("rates",rates[i],i);
		for(int i=0; i<modes.length; i++)data.addData("modes",modes[i],i);
		for(int i=0; i<sizes.length; i++) {
			data.addData("sizes",sizes[i][0],2*i);
			data.addData("sizes",sizes[i][1],2*i+1);
		}
		for(int i=0; i<angles.length; i++)data.addData("angles",angles[i],i);
		return data;
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
			if(rates[checkpoint()]==0)return 0;
			return (levels.Level.lineLength(seg[0][0],seg[0][1],seg[1][0],seg[1][1]))/rates[checkpoint()];
		}else {
			return 0;
		}
	}
	//get time to next point, adjusted for mode
	public double getTime() {
		if(modes[checkpoint()] == SPEED) {
			double[][] seg = getSegment();
			if(rates[checkpoint()]==0)return 1;
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
	//gets rotation amount
	public double getRotation() {
		double ret = (angles[nextpoint()] - angles[checkpoint()])/getTime();
		return ret;
	}
	
	//set a checkpoint's values
	public void setCheckpoint(int checkpoint, double x, double y, int mode, double rate, double sizea, double sizeb, double angle) {
		positions[checkpoint][0] = x;
		positions[checkpoint][1] = y;
		rates[checkpoint] = rate;
		modes[checkpoint] = mode;
		sizes[checkpoint][0] = sizea;
		sizes[checkpoint][1] = sizeb;
		angles[checkpoint] = angle;
	}
	
	public void update() {
		updates++;
		if(updates>=getTime()) {
			updates=0;
			advance();
		}
	}
	
	//last checkpoint
	public int checkpoint() {return checkpoint%positions.length;}
	//next checkpoint
	public int nextpoint() {return (checkpoint+1)%positions.length;}
	//reset to beginning of path
	public void reset() {checkpoint = 0;}
	//move to next checkpoint
	public void advance() {checkpoint++;}

	public double[] position(){return positions[checkpoint()];}
	public double rate(){return rates[checkpoint()];}
	public int mode(){return modes[checkpoint()];}
	public double[] size(){return sizes[checkpoint()];}
	public double angle() {return angles[checkpoint()];}

}
