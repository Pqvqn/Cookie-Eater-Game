package menus;

import java.io.*;
import java.util.*;

import ce3.*;
import entities.*;

public class Dialogue {

	private Board board;
	private Entity speaker;
	private File file;
	private String heading;
	private final String filepath = "Cookie Eater/src/resources/dialogue/";
	private ArrayList<String> lines;
	
	public Dialogue(Board frame, Entity s, String filename, String placeInFile) {
		board = frame;
		speaker = s;
		heading = placeInFile;
		file = new File(filepath+filename+ ".txt");
		lines = new ArrayList<String>();
		try {
			readFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String l : lines) {
			System.out.println(l);
		}
	}
	
	private void readFile() throws FileNotFoundException,IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String curr = "";
		while(curr!=null && !curr.equals(">"+heading)) {
			curr = reader.readLine();
		}
		curr = reader.readLine();
		while(curr!=null && curr.length()>0 && !curr.substring(0,1).equals(">")){
			lines.add(curr);
			curr = reader.readLine();
		}
		reader.close();
	}
	
}
