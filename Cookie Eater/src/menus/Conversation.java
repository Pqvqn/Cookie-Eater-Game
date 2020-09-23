package menus;

import java.io.*;
import java.util.*;

import ce3.*;
import entities.*;

public class Conversation {

	private Board board;
	private Entity speaker;
	private File file;
	private String heading;
	private final String FILEPATH = "Cookie Eater/src/resources/dialogue/";
	private boolean displayed; //whether conversation is currently displayed
	//private ArrayList<String> lines;
	private ArrayList<Dialogue> path; //chosen dialogue, in order
	
	public Conversation(Board frame, Entity s, String filename, String placeInFile) {
		board = frame;
		speaker = s;
		heading = placeInFile;
		file = new File(FILEPATH+filename+ ".txt");
		//lines = new ArrayList<String>();
		try {
			readFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isOver() {
		return path.get(path.size()-1).getOptions().size()==0;
	}
	
	public Dialogue nextLine(int option) { //gives next line, branching from the current line depending on option chosen
		currentLine().display(false);
		option = currentLine().getOptions().indexOf(getOptions().get(option)); //this line of code is legit holy
		path.add(currentLine().getNext(option)); //go to next line
		
		currentLine().lineFunctionality();
		
		currentLine().display(true);
		speaker.speak(this); //speak line
		return currentLine();
	}/*
	public void lineFunctionality() { //operates on line for any functionality before display
		if(currentLine().getJump()!=null)skipTo(currentLine().getJump()); //if meant to jump lines, jump
		
		ArrayList<String> stateChanges = currentLine().getVariables(); //set all entity variables that this line changes
		for(int i=0; i<stateChanges.size(); i++) {
			String[] parts = stateChanges.get(i).split(";");
			speaker.setState(parts[0], parts[1]);
		}
		ArrayList<String> replace = currentLine().getReplace();
		for(int i=0; i<replace.size(); i++) {
			currentLine().setText(currentLine().getText().replaceFirst("%S%", speaker.getState(replace.get(i))));
		}
	}*/
	public void setDisplayed(boolean d) {displayed = d;}
	public void test() {
		currentLine().display(displayed);
		int t = currentLine().testChoice();
		if(t>=0)nextLine(t);
	}
	
	public Dialogue currentLine() {
		return path.get(path.size()-1);
	}
	
	public ArrayList<String> getOptions(){
		//return only the options not marked as invalid with a ~
		ArrayList<String> opts = new ArrayList<String>();
		ArrayList<String> orig = currentLine().getOptions();
		for(int i=0; i<orig.size(); i++) {
			if(!orig.get(i).substring(0,1).equals("~")) {
				opts.add(orig.get(i));
			}
		}
		return opts;
	}
	public Entity getSpeaker() {return speaker;}
	private void readFile() throws FileNotFoundException,IOException {
		path = new ArrayList<Dialogue>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String curr = "";
		while(curr!=null && !curr.equals(">"+heading)) {
			curr = reader.readLine();
		}
		path.add(new Dialogue(board,speaker,this,reader.readLine(),":",reader));
		currentLine().lineFunctionality();
		/*curr = reader.readLine();
		while(curr!=null && curr.length()>0 && !curr.substring(0,1).equals(">")){
			lines.add(curr);
			curr = reader.readLine();
		}*/
		reader.close();
	}
	public void skipTo(String line) { //skips conversation to different heading
		heading = line;
		try {
			readFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
