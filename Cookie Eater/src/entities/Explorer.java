package entities;

import ce3.*;
import levels.*;

public class Explorer extends Entity{

	protected Level residence; //which room this explorer is on
	protected String name;
	
	public Explorer(Board frame) {
		super(frame);
		name = "Unknown";
	}
	
	public Level getResidence() {return residence;}
	public String getName() {return name;}
	
}
