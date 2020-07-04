package ui;

import java.awt.*;

import ce3.*;
import entities.*;

public class UIDialogue extends UIElement{

	private UIText name;
	private UIParagraph speech;
	private Entity speaker;
	//private UIRectangle backing;
	
	public UIDialogue(Board frame, Entity e, String words) {
		super(frame,0,0);
		speaker = e;
		xPos=board.X_RESOL/2;
		yPos=board.Y_RESOL-100;
		parts.add(new UIRectangle(board,xPos-500,yPos-100,1000,100,new Color(0,0,0,100))); //backing
		parts.add(name = new UIText(board,xPos-500,yPos-110,"",new Color(120,120,120,200),new Font("Arial",Font.BOLD,25)));
		parts.add(speech = new UIParagraph(board,xPos-490,yPos-75,null,new Color(255,255,255,150),new Font("Arial",Font.BOLD,30),30,55));
		name.setText((speaker!=null)?speaker.name():"");
		speech.setTextLines((words!=null)?words:"");
	}
	public void update() {
	}
	public void paint(Graphics g) {
		super.paint(g);
	}
}
