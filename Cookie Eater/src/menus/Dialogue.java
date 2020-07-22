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
		optionText = extractText(text,"[","]","");
		//{} -> vars
		variableText = extractText(text,"{","}","");
		//> -> jump
		if(text.contains(">")) { //signal to jump to next line
			jumpLocation = text.substring(text.indexOf(">")+1);
		}
		//## -> replace with variable
		replaceText = extractText(text, "#","#","%S%");

	}
	
	public ArrayList<String> extractText(String t, String front, String back, String marker) { //returns all strings enclosed in front string and back string after replacing with marker
		ArrayList<String> extracts = new ArrayList<String>();
		while(text.contains(front) && text.contains(back)) { //pull out Strings inside of front and back characters
			String opt = text.substring(text.indexOf(front)+1);
			opt = opt.substring(0,opt.indexOf(back));
			text = text.replace(front+opt+back,marker);
			extracts.add(opt);
		}
		return extracts;
	}
	
	public String getJump() {return jumpLocation;}
	
	public Dialogue getNext(int option){
		return followups.get(option);
	}
	public int numberOfOptions() {return followups.size();}
	
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
	public ArrayList<String> getOptions(){
		return optionText;
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
		  if((chooser==null || !chooser.inAction()) && !optionText.isEmpty())chooser = new Selection(board,optionText,0,-1,KeyEvent.VK_SPACE, KeyEvent.VK_ENTER);
	  }else {
		  if(chooser!=null)chooser.close();
	  }
	}
	
}
