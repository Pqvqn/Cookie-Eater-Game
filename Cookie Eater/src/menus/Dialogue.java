package menus;

import java.io.*;
import java.util.*;
import java.awt.event.*;

import ce3.*;
import entities.*;

public class Dialogue {

	private Board board;
	private Entity speaker;
	private String text;
	private String prefix;
	private ArrayList<String> optionText; //text for every option
	private ArrayList<Dialogue> followups; //next dialogue options
	private Selection chooser;
	
	
	public Dialogue(Board frame, Entity s, String line, String pref, BufferedReader reader) {
		board = frame;
		prefix = pref;
		speaker = s;
		optionText = new ArrayList<String>();
		text = (line.contains("{")) ? line.substring(prefix.length(),line.indexOf("{")) : line.substring(prefix.length()); //extract just text
		while(line.contains("{") && line.contains("}")) { //pull out all options, which are surrounded by brackets
			optionText.add(line.substring(line.indexOf("{")+1,line.indexOf("}")));
			line = line.replaceFirst("\\{"," ");
			line = line.replaceFirst("\\}"," ");
		}
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
	public int testChoice() {
		if(chooser!=null && chooser.hasChosen()) {
			chooser.close();
			return chooser.getChosenIndex();
		}
		return -1;
	}
	public int getHover() {
		return chooser.getHoveredIndex();
	}
	public int getChoice() {
		return chooser.getChosenIndex();
	}
	public ArrayList<String> getOptions(){
		return optionText;
	}
	public Entity getSpeaker() {return speaker;}
	public void createFollowups(BufferedReader reader) throws IOException { //creates dialogues for all followup dialogues
		followups = new ArrayList<Dialogue>();
		String pref = prefix+":";
		String curr = pref;
		reader.mark(100);
		while(curr!=null && curr.length()>=pref.length() && curr.substring(0,pref.length()).equals(pref)) {
			reader.mark(100);
			curr = reader.readLine();
			if(curr!=null && curr.length()>=pref.length() && curr.substring(0,pref.length()).equals(pref))
				followups.add(new Dialogue(board,speaker,curr,pref,reader));
		}
		reader.reset();
	}
	public void display(boolean b) { //run when displayed
	  if(b) {
		  if((chooser==null || !chooser.inAction()) && !optionText.isEmpty())chooser = new Selection(board,optionText,0,-1,KeyEvent.VK_SPACE, KeyEvent.VK_ENTER);
	  }else {
		  if(chooser!=null)chooser.close();
	  }
	}
	
}
