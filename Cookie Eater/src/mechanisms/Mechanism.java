package mechanisms;

import java.awt.*;
import java.awt.geom.Area;
import java.lang.reflect.InvocationTargetException;

import ce3.*;

public abstract class Mechanism {

	Game game;
	Board board;
	double x,y;
	double mass;
	
	public Mechanism(Game frame, Board gameboard, int xPos, int yPos) {
		game = frame;
		board = gameboard;
		x = xPos;
		y = yPos;
		mass = 100;
	}
	
	public Mechanism(Game frame, Board gameboard, SaveData sd) {
		game = frame;
		board = gameboard;
		randomize(sd.getSaveDataList("randomization").get(0));
		x = sd.getDouble("position",0);
		y = sd.getDouble("position",1);
		mass = sd.getDouble("mass",0);
	}
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("position",x,0);
		data.addData("position",y,1);
		data.addData("mass",mass);
		data.addData("type",this.getClass().getName());
		return data;
	}
	
	//return Mechanism created by SaveData, testing for correct type of Mechanism
	public static Mechanism loadFromData(Game frame, Board gameboard, SaveData sd) {
		//mechanism subclasses
		Class[] mechtypes = {Wall.class, WallMove.class,WallCase.class,WallDoor.class,Decoration.class};
		String thistype = sd.getString("type",0);
		if(thistype.equals(Passage.class.getName()))return null; //exclude passages
		for(int i=0; i<mechtypes.length; i++) {
			//if class type matches type from file, instantiate and return it
			if(thistype.equals(mechtypes[i].getName())){
				try {
					Mechanism m = (Mechanism) (mechtypes[i].getDeclaredConstructor(Game.class, Board.class, SaveData.class).newInstance(frame, gameboard, sd));
					return m;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		//default to wall
		return new Wall(frame, gameboard, sd);
	}
	
	//randomize all position and stats from SaveData
	public void randomize(SaveData rnd) {
		if(rnd.getData("position").size()>=4){
			double minX = rnd.getDouble("position",0);
			double minY = rnd.getDouble("position",1);
			double maxX = rnd.getDouble("position",2);
			double maxY = rnd.getDouble("position",3);
			x = (int)(Math.random()*(maxX-minX)) + minX;
			y = (int)(Math.random()*(maxY-minY)) + minY;
		}
	}
	
	public void runUpdate() {
		
	}
	public double getX() {return x;}
	public double getY() {return y;}
	public double getXVel() {return 0;}
	public double getYVel() {return 0;}
	public double getMass() {return mass;}
	public Area getArea() {
		return new Area();
	}
	
	//updates mechanism for number of cookies on the board, as mechanisms generate before cookies
	public void updateCookieTotal(int total) {
		
	}
	public void paint(Graphics g) {
		
	}
	
	//remove traces (ui) of the mechanism from board
	public void clean() {}
	public void remove() {
		clean();
		board.mechanisms().remove(this);
	}
}
