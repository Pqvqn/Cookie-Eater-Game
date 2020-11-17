package menus;

import java.io.*;
import java.util.*;
import java.awt.event.*;

import ce3.*;
import entities.*;
import sprites.SpriteExplorer;

public class Dialogue {

	private Board board;
	private Entity speaker;
	private Conversation convo;
	private String text;
	private String prefix;
	/*private ArrayList<String> sText; //possible texts based on conditions
	private ArrayList<ArrayList<String>> conditionText; //text for conditions for each option
	private ArrayList<ArrayList<String>> conditionOptionText; //text for conditions for each option
	private ArrayList<String> variableText; //text for states
	private ArrayList<String> replaceText; //text for replacement*/
	private ArrayList<Dialogue> followups; //next dialogue options
	private ArrayList<String> optionText; //text for every option
	private Selection chooser;
	//private String jumpLocation; //location for jump if needed
	
	public Dialogue(Board frame, Entity s,  Conversation c, String line, String pref, BufferedReader reader) {
		board = frame;
		prefix = pref;
		speaker = s;
		convo = c;
		/*conditionText = new ArrayList<ArrayList<String>>();
		optionText = new ArrayList<String>();
		conditionOptionText = new ArrayList<ArrayList<String>>();
		variableText = new ArrayList<String>();
		replaceText = new ArrayList<String>();*/
		text = line.substring(prefix.length());
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
	
	//removes all sequences of front + anything + back with marker within fulltext, returns fulltext, deposits extracts in extracts
	public String extractFromText(String fulltext, String front, String back, String marker, ArrayList<String> extracts) {
		while(fulltext.contains(front) && fulltext.contains(back)) { //pull out Strings inside of front and back characters
			//sequence to remove
			String opt = fulltext.substring(fulltext.indexOf(front)+1);
			opt = opt.substring(0,opt.indexOf(back));
			
			/*//count how many duplicates exist
			int reps = 0;
			String t = fulltext;
			String r = front+opt+back;
			while(t.contains(r)) {
				t = t.substring(t.indexOf(r)+r.length());
				reps++;
			}*/
			

			//remove first occurence of sequence
			String fullopt = front+opt+back;
			String first = fulltext.substring(0,fulltext.indexOf(fullopt));
			int v = 1+fulltext.indexOf(fullopt)+fullopt.length();
			String last = fulltext.substring(v-1);

			fulltext =  first + marker + last;
			
			/*//add to list according to number of duplicates
			for(int i=0; i<reps; i++) {
				extracts.add(opt);
			}*/
			
			extracts.add(opt);

		}
		return fulltext;
	}
	
	public void lineFunctionality() { //operates on line for any functionality before display
		
		//## -> replace with variable
		ArrayList<String> replace = new ArrayList<String>();
		text = extractFromText(text,"#","#","@S@",replace);
		for(int i=0; i<replace.size(); i++) {
			setText(getText().replaceFirst("@S@", speaker.getState(replace.get(i))));
		}
		
		//[] -> options
		optionText = new ArrayList<String>();
		text = extractFromText(text,"[","]","",optionText);
		for(int i=0; i<optionText.size(); i++) { //remove options that don't meet their conditions
			//[%%] -> options appear if conditions met / ; separates variable from state
			ArrayList<String> conditions = new ArrayList<String>();
			optionText.set(i,extractFromText(optionText.get(i),"%","%","",conditions));
			boolean passes = true;
			for(int j=0; j<conditions.size(); j++) {
				String stateTest = conditions.get(j);
				String[] parts = stateTest.split(";");
				if(!speaker.getState(parts[0]).equals(parts[1])) {
					passes=false;
				}
			}
			if(!passes) {
				optionText.set(i,"~" + optionText.get(i));
				i--;
			}
		}
		
		//%% -> conditions for dialogue to show / ; separates variable from state / | separates dialogue options in order of test priority
		String[] possible = text.split("\\|");
		boolean conditionsMet = false;
		for(int i=0; i<possible.length && !conditionsMet; i++) {
			ArrayList<String> conditions = new ArrayList<String>();
			possible[i] = extractFromText(possible[i],"%","%","",conditions);
			boolean passes = true;
			for(int j=0; j<conditions.size(); j++) {
				String stateTest = conditions.get(j);
				String[] parts = stateTest.split(";");
				if(!speaker.getState(parts[0]).equals(parts[1])) {
					passes=false;
				}
			}
			if(passes) {
				conditionsMet = true;
				text = possible[i];
			}
		}		

		//$$ -> custom functions
		ArrayList<String> functions = new ArrayList<String>();
		text = extractFromText(text,"$","$","",functions);
		for(int i=0; i<functions.size(); i++) {
			String[] parts = functions.get(i).split(";");
			//separate out arguments for function
			String[] args = new String[parts.length - 1];
			for(int j=1; j<parts.length; j++) {
				args[j-1] = parts[j];
			}

			speaker.doFunction(parts[0],args);
			
		}

		//{} -> speaker state changes / ; separates variable from state
		ArrayList<String> stateChanges = new ArrayList<String>();
		text = extractFromText(text,"{","}","",stateChanges);//set all entity variables that this line changes
		for(int i=0; i<stateChanges.size(); i++) {
			String[] parts = stateChanges.get(i).split(";");
			if(parts[1].contains("+")) {
				try {
					double total = 0;
					String[] nums = parts[1].split("\\+");
					for(int j=0; j<nums.length; j++) {
						total+=Double.parseDouble(nums[j]);
					}
					parts[1] = total+"";
				}catch(NumberFormatException e) {
					
				}
			}
			speaker.setState(parts[0], parts[1]);
		}
		
		//^^ -> speaker emotion changes / separated by ; into expression category and state
		ArrayList<String> expressionChanges = new ArrayList<String>();
		text = extractFromText(text,"^","^","",expressionChanges);
		for(int i=0; i<expressionChanges.size(); i++) {
			String[] parts = expressionChanges.get(i).split(";");
			//separate out arguments for expression
			switch(parts[0]) {
			case "Expression":
				switch(parts[1]) {
				case "Norm":
					convo.setExpression(0,SpriteExplorer.NORM);
					break;
				case "Win":
					convo.setExpression(0,SpriteExplorer.WIN);
					break;
				case "Die":
					convo.setExpression(0,SpriteExplorer.DIE);
					break;
				case "Eat":
					convo.setExpression(0,SpriteExplorer.EAT);
					break;
				case "Hit":
					convo.setExpression(0,SpriteExplorer.HIT);
					break;
				case "Special":
					convo.setExpression(0,SpriteExplorer.SPECIAL);
					break;
				default:
					convo.setExpression(0,SpriteExplorer.NORM);
					break;
				}
				break;
			case "Direction":
				switch(parts[1]) {
				case "Right":
					convo.setExpression(1,SpriteExplorer.RIGHT);
					break;
				case "Up":
					convo.setExpression(1,SpriteExplorer.UP);
					break;
				case "Down":
					convo.setExpression(1,SpriteExplorer.DOWN);
					break;
				case "Left":
					convo.setExpression(1,SpriteExplorer.LEFT);
					break;
				case "Neutral":
					convo.setExpression(1,SpriteExplorer.NEUTRAL);
					break;
				default:
					convo.setExpression(1,SpriteExplorer.RIGHT);
					break;
				}
				break;
			}
			
		}
		
		//> -> jump
		if(text.contains(">")) { //signal to jump to next line
			String jumpLocation = text.substring(text.indexOf(">")+1);
			convo.skipTo(jumpLocation); //if meant to jump lines, jump
		}
		
	}
	
	
	//public String getJump() {return jumpLocation;}
	
	public Dialogue getNext(int option){
		return followups.get(option);
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
		return optionText;
	}
	/*public ArrayList<String> getVariables(){
		return variableText;
	}
	public ArrayList<String> getReplace(){
		return replaceText;
	}*/
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
				followups.add(new Dialogue(board,speaker,convo,curr,pref,reader));
		}
		reader.reset();
	}
	public void display(boolean b) { //run when displayed
		if(b) {
			ArrayList<String> opts = new ArrayList<String>();
		  	ArrayList<String> orig = getOptions();
			for(int i=0; i<orig.size(); i++) {
				if(!orig.get(i).substring(0,1).equals("~")) {
					opts.add(orig.get(i));
				}
			}
		  if((chooser==null || !chooser.inAction()) && !optionText.isEmpty())chooser = new Selection(board,opts,0,-1,KeyEvent.VK_SPACE, KeyEvent.VK_ENTER);
		}else {
			if(chooser!=null)chooser.close();
		}
	}
	
}
