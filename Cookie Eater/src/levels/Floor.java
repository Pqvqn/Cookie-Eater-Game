package levels;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import ce3.*;
import mechanisms.*;

public abstract class Floor {
	
	protected Game game;
	protected Board board;
	protected Level[][] roomGrid; //grid of room locations
	//protected ArrayList<Floor> prevs;
	//protected ArrayList<Floor> nexts;
	protected ArrayList<Store> entrances;
	protected ArrayList<Store> exits;
	protected HashMap<Class,Integer> roomWeights;
	protected int numRooms;
	protected String id;

	public Floor(Game frame, Board gameboard, int wid, int hei, String floorid) {
		game = frame;
		board = gameboard;
		id = floorid;
		//prevs = new ArrayList<Floor>();
		//nexts = new ArrayList<Floor>();
		stores = new ArrayList<Store>();
		roomGrid = new Level[wid][hei];
	}
	
	//creates and connects levels in paths
	public void generateFloor() {
		//int entrances = prevs.size();
		//int exits = nexts.size();
		String id = "";
		for(int i=0; i<numRooms+2; i++)id+="0";
		//unpack weights
		ArrayList<Class> levels = new ArrayList<Class>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		Iterator<Class> it = roomWeights.keySet().iterator();
		int sum = 0;
		while(it.hasNext()) {
			Class<Level> classlvl = it.next();
			levels.add(classlvl);
			sum += roomWeights.get(classlvl);
			counts.add(sum);
		}

		for(int i=0; i<=numRooms && i<roomGrid[0].length; i++) {
			ArrayList<Level> nexture = new ArrayList<Level>();
			nexture.add(roomGrid[0][numRooms-i]);
			int chosen = (int)(Math.random()*sum);
			int find = 0;
			for(find = 0; find<counts.size() && counts.get(find)<chosen; find++);
			Class<Level> chosenlvl = levels.get(find);
			Level addition = null;
			try {
				addition = (Level)(chosenlvl.getDeclaredConstructor(Game.class, Board.class, Floor.class, String.class).newInstance(game, board, this, id.substring(0,numRooms+1-i)));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(addition!=null) {
				roomGrid[0][numRooms-i-1] = addition;
				ArrayList<Integer> dirture = new ArrayList<Integer>();
				if(numRooms-i<numRooms) {
					nexture.add(roomGrid[0][numRooms-i]);
					dirture.add(Passage.RIGHT);
				}
				addition.setNextLevels(nexture,dirture);
			}
		}
		ArrayList<Level> nexture = new ArrayList<Level>();
		ArrayList<Integer> dirture = new ArrayList<Integer>();
		nexture.add(stores.get(0));
		dirture.add(Passage.FLOOR);
		roomGrid[0][roomGrid.length-1].setNextLevels(nexture,dirture);
		
	}
	
	//return Store to use before this Floor
	public Store generateStore() {
		return new Store1(game, board, id);
	}
	
	//finds the room belonging to the given code
	public Level findRoom(String code) {
		/*(Level curr = firstLevel;
		String cid = curr.getID();
		//crawl through rooms, looking for next one based on matching id
		for(int i=1; i<code.length(); i++) {
			for(int j=0; j<curr.getPassages().size(); j++) {
				if(curr.getPassages().get(j).entranceAt(curr)) {
					Level exit = curr.getPassages().get(j).getExit();
					String oid = exit==null?cid:exit.getID();
					if(oid.substring(0,i).equals(cid)) {
						curr = curr.getPassages().get(j).getExit();
						cid = oid;
					}
				}
			}
			if(cid.length() < i)return null;
		}
		return curr;
	}*/
		for(int i=0; i<roomGrid.length; i++) {
			for(int j=0; j<roomGrid[i].length; j++) {
				if(roomGrid[i][j]!=null && roomGrid[i][j].getID().equals(code)) {
					return roomGrid[i][j];
				}
			}
		}
		return null;
	}
	
	public void addStore(Store s) {
		stores.add(s);
	}
	
	/*public void addPrev(Floor f) {prevs.add(f);}
	public void addNext(Floor f) {nexts.add(f);}
	public ArrayList<Floor> getPrev() {return prevs;}
	public ArrayList<Floor> getNext() {return nexts;}*/
}
