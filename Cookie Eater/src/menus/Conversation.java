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
		return path.get(path.size()-1).numberOfOptions()==0;
	}
	
	public Dialogue nextLine(int option) { //gives next line, branching from the current line depending on option chosen
		path.add(currentLine().getNext(option));
		return currentLine();
	}
	
	public Dialogue currentLine() {
		return path.get(path.size()-1);
	}
	public Entity getSpeaker() {return speaker;}
	private void readFile() throws FileNotFoundException,IOException {
		path = new ArrayList<Dialogue>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String curr = "";
		while(curr!=null && !curr.equals(">"+heading)) {
			curr = reader.readLine();
		}
		path.add(new Dialogue(board,speaker,reader.readLine(),":",reader));
		/*curr = reader.readLine();
		while(curr!=null && curr.length()>0 && !curr.substring(0,1).equals(">")){
			lines.add(curr);
			curr = reader.readLine();
		}*/
		reader.close();
	}
	
}
