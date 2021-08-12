package levels;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import ce3.*;
import mechanisms.*;

public class Floor {
	
	protected Game game;
	protected Board board;
	protected Level[][] roomGrid; //grid of room locations
	protected ArrayList<Store> entrances; //entrances into the floor, located in stores
	protected ArrayList<Store> exits; //exits to other floors, located in stores

	public Floor(Game frame, Board gameboard, int wid, int hei) {
		game = frame;
		board = gameboard;
		roomGrid = new Level[wid][hei];
	}
	
	//creates and connects levels in paths
	public void generateFloor(HashMap<Class<Level>,Integer> roomWeights, int numRooms, ArrayList<Store> enter, ArrayList<Store> exit) {
		entrances = enter;
		exits = exit;
		roomGrid[0][roomGrid.length-1] = exits.get(0);
		String id = "";
		for(int i=0; i<numRooms+2; i++)id+="0";
		//unpack weights
		ArrayList<Class<Level>> levels = new ArrayList<Class<Level>>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		Iterator<Class<Level>> it = roomWeights.keySet().iterator();
		int sum = 0;
		while(it.hasNext()) {
			Class<Level> classlvl = it.next();
			levels.add(classlvl);
			sum += roomWeights.get(classlvl);
			counts.add(sum);
		}

		for(int i=1; i<=numRooms && i<roomGrid[0].length-1; i++) {
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
				nexture.add(roomGrid[0][numRooms-i]);
				ArrayList<Integer> dirture = new ArrayList<Integer>();
				dirture.add(Passage.RIGHT);
				addition.setNextLevels(nexture,dirture);
			}
		}
		roomGrid[0][0] = entrances.get(0);
		ArrayList<Level> nexture = new ArrayList<Level>();
		nexture.add(roomGrid[0][1]);
		ArrayList<Integer> dirture = new ArrayList<Integer>();
		dirture.add(Passage.RIGHT);
		roomGrid[0][0].setNextLevels(nexture,dirture);
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
}
