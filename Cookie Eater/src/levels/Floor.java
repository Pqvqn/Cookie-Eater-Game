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
		entrances = new ArrayList<Store>();
		exits = new ArrayList<Store>();
		roomGrid = new Level[wid][hei];
	}
	
	//creates and connects levels in paths
	public void generateFloor() {
		//int entrances = prevs.size();
		//int exits = nexts.size();
		int[] currCoord = {0,0};
		String id = "";
		for(int i=0; i<Math.min(numRooms,roomGrid.length*roomGrid[0].length); i++)id+="0";
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
		for(int i=Math.min(numRooms,roomGrid.length*roomGrid[0].length)-1; i>=0; i--) {
			int chosen = (int)(Math.random()*sum);
			int find = 0;
			for(find = 0; find<counts.size() && counts.get(find)<chosen; find++);
			Class<Level> chosenlvl = levels.get(find);
			Level addition = null;
			try {
				addition = (Level)(chosenlvl.getDeclaredConstructor(Game.class, Board.class, Floor.class, String.class).newInstance(game, board, this, id.substring(0,i+1)));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(addition!=null) {
				ArrayList<Integer> dirs = new ArrayList<Integer>();
				for(int d=-1; d<4; d++)dirs.add(d);
				if(currCoord[0]==0)dirs.remove(dirs.indexOf(Passage.LEFT));
				if(currCoord[1]==0)dirs.remove(dirs.indexOf(Passage.TOP));
				if(currCoord[0]==roomGrid[0].length-1)dirs.remove(dirs.indexOf(Passage.RIGHT));
				if(currCoord[1]==roomGrid.length-1)dirs.remove(dirs.indexOf(Passage.BOTTOM));
				int chosenDir = dirs.get((int)(Math.random()*dirs.size()));
				
				roomGrid[0][i] = addition;
				ArrayList<Level> nexture = new ArrayList<Level>();
				ArrayList<Integer> dirture = new ArrayList<Integer>();
				if(i<Math.min(numRooms,roomGrid[0].length)-1) {
					nexture.add(roomGrid[0][i+1]);
					dirture.add(chosenDir);
				}
				addition.setNextLevels(nexture,dirture);
			}
		}
		if(!exits.isEmpty()) {
			ArrayList<Level> nexture = new ArrayList<Level>();
			ArrayList<Integer> dirture = new ArrayList<Integer>();
			nexture.add(exits.get(0));
			dirture.add(Passage.FLOOR);
			roomGrid[0][roomGrid.length-1].setNextLevels(nexture,dirture);
		}
		
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
	
	public void addEntrance(Store s) {
		entrances.add(s);
	}
	public void addExit(Store s) {
		exits.add(s);
	}
	public void buildExit(Store s) {
		exits.add(s);
		int dir = Passage.BOTTOM;
		Passage p = new Passage(game,board,roomGrid[0][roomGrid.length-1],s,Passage.FLOOR,0,50);
		roomGrid[0][roomGrid.length-1].passageways.add(p);
		s.addEntrance(p);
	}
	public int numExits() {return exits.size();}
	public String getID() {return id;}
	/*public void addPrev(Floor f) {prevs.add(f);}
	public void addNext(Floor f) {nexts.add(f);}
	public ArrayList<Floor> getPrev() {return prevs;}
	public ArrayList<Floor> getNext() {return nexts;}*/
}
