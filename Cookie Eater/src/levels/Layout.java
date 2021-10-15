package levels;

import java.util.*;

import ce3.*;

public class Layout {

	public ArrayList<SaveData> roomGen;
	public int numRooms;
	public String store;
	public int rows, cols;
	
	public Layout(String layoutname, SaveData sd) {
		numRooms = sd.getInteger("length",0);
		rows = sd.getInteger("dimensions",0);
		cols = sd.getInteger("dimensions",1);
		store = sd.getString("store",0);
		roomGen = sd.getSaveDataList("rooms");
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		
		data.addData("length",numRooms);
		data.addData("dimensions",rows,0);
		data.addData("dimensions",cols,1);
		data.addData("store",store);
		data.addData("rooms",roomGen);
		
		return data;
	}

}
