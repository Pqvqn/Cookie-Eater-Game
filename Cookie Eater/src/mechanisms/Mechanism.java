package mechanisms;

import java.awt.*;
import java.awt.geom.Area;
import java.lang.reflect.InvocationTargetException;

import ce3.*;
import entities.Enemy;
import entities.EnemyBlob;
import entities.EnemyBloc;
import entities.EnemyCrawler;
import entities.EnemyGlob;
import entities.EnemyParasite;
import entities.EnemySlob;
import entities.EnemySpawner;
import entities.EnemySpawnerArena;

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
		Class[] mechtypes = {Wall.class, WallMove.class,WallCase.class,WallDoor.class};
		String thistype = sd.getString("type",0);
		if(thistype.equals(Passage.class.getName()))return null; //exclude passages
		for(int i=0; i<mechtypes.length; i++) {
			//if class type matches type from file, instantiate and return it
			if(thistype.equals(mechtypes[i].getName())){
				try {
					return (Mechanism) (mechtypes[i].getDeclaredConstructor(Game.class, Board.class, SaveData.class).newInstance(frame, gameboard, sd));
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
	
	public void remove() {
		board.mechanisms.remove(this);
	}
}
