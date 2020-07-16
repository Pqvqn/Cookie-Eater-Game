package menus;

import java.io.*;
import java.util.*;

import ce3.*;

public class Dialogue {

	private String text;
	private String prefix;
	private Board board;
	private ArrayList<Dialogue> followups; //next dialogue options
	
	public Dialogue(Board frame, String line, String pref, BufferedReader reader) {
		board = frame;
		prefix = pref;
		text = line.substring(prefix.length());
		try {
			createFollowups(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch blocky
			e.printStackTrace();
		}
	}
	
	public Dialogue getNext(int option){
		return followups.get(option);
	}
	public int numberOfOptions() {return followups.size();}
	
	public String getText() {
		return text;
	}
	
	public void createFollowups(BufferedReader reader) throws IOException { //creates dialogues for all followup dialogues
		followups = new ArrayList<Dialogue>();
		String pref = prefix+":";
		String curr = pref;
		reader.mark(100);
		while(curr!=null && curr.length()>=pref.length() && curr.substring(0,pref.length()).equals(pref)) {
			reader.mark(100);
			curr = reader.readLine();
			if(curr!=null && curr.length()>=pref.length() && curr.substring(0,pref.length()).equals(pref))
				followups.add(new Dialogue(board,curr,pref,reader));
		}
		reader.reset();
	}
	
}
