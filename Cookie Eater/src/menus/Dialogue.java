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
	private ArrayList<ArrayList<String>> conditionText; //text for conditions for each option
	private ArrayList<String> variableText; //text for states
	private ArrayList<String> replaceText; //text for replacement
	private ArrayList<Dialogue> followups; //next dialogue options
	private Selection chooser;
	private String jumpLocation; //location for jump if needed
	
	public Dialogue(Board frame, Entity s, String line, String pref, BufferedReader reader) {
		board = frame;
		prefix = pref;
		speaker = s;
		optionText = new ArrayList<String>();
		conditionText = new ArrayList<ArrayList<String>>();
		variableText = new ArrayList<String>();
		replaceText = new ArrayList<String>();
		text = line.substring(prefix.length());
		translate();
		/*text = (line.contains("{")) ? line.substring(prefix.length(),line.indexOf("{")) : line.substring(prefix.length()); //extract just text
		while(line.contains("{") && line.contains("}")) { //pull out all options, which are surrounded by brackets
			optionText.add(line.substring(line.indexOf("{")+1,line.indexOf("}")));
			line = line.replaceFirst("\\{"," ");
			line = line.replaceFirst("\\}"," ");
		}*/
		try {
			createFollowups(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch blocky
			e.printStackTrace();
		}
	}
	
	public void translate() { //read function signifiers
		//[] -> options
		text = extractFromText(text,"[","]","",optionText);
		for(int i=0; i<optionText.size(); i++) {
			conditionText.add(new ArrayList<String>());
			optionText.set(i,extractFromText(optionText.get(i),"{","}","",conditionText.get(i)));
		}
		//{} -> vars
		text = extractFromText(text,"{","}","",variableText);
		//> -> jump
		if(text.contains(">")) { //signal to jump to next line
			jumpLocation = text.substring(text.indexOf(">")+1);
		}
		//## -> replace with variable
		 text = extractFromText(text,"#","#","%S%",replaceText);

	}
	
	//removes all sequences of front + anything + back with marker within fulltext, returns fulltext, deposits extracts in extracts
	public String extractFromText(String fulltext, String front, String back, String marker, ArrayList<String> extracts) {
		while(fulltext.contains(front) && fulltext.contains(back)) { //pull out Strings inside of front and back characters
			String opt = fulltext.substring(fulltext.indexOf(front)+1);
			opt = opt.substring(0,opt.indexOf(back));
			fulltext = fulltext.replace(front+opt+back,marker);
			extracts.add(opt);
		}
		return fulltext;
	}
	
	public String getJump() {return jumpLocation;}
	
	public Dialogue getNext(int option){
		
		return followups.get(optionText.indexOf(getOptions().get(option)));
	}
	
	public String getText() {
		return text;
	}
	public void setText(String t) {
		text = t;
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
	//returns options that pass all variable tests
	public ArrayList<String> getOptions(){
		ArrayList<String> validOptions = new ArrayList<String>();
		for(int i=0; i<optionText.size(); i++) {
			boolean passes = true;
			for(int j=0; j<conditionText.get(i).size(); j++) {
				String stateTest = conditionText.get(i).get(j);
				String[] parts = stateTest.split(";");
				if(!speaker.getState(parts[0]).equals(parts[1])) {
					passes=false;
				}
			}
			if(passes)validOptions.add(optionText.get(i));
		}
		return validOptions;
	}
	public ArrayList<String> getVariables(){
		return variableText;
	}
	public ArrayList<String> getReplace(){
		return replaceText;
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
		  if((chooser==null || !chooser.inAction()) && !optionText.isEmpty())chooser = new Selection(board,getOptions(),0,-1,KeyEvent.VK_SPACE, KeyEvent.VK_ENTER);
	  }else {
		  if(chooser!=null)chooser.close();
	  }
	}
	
}
