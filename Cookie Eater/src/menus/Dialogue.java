package menus;

import java.io.*;

import ce3.*;
import entities.*;

public class Dialogue {

	private Board board;
	private Entity speaker;
	private File file;
	private final String filepath = "src/resources/dialogue/";
	
	public Dialogue(Board frame, Entity s, String filename) {
		board = frame;
		speaker = s;
		file = new File(filepath+filename);
	}
	
}
