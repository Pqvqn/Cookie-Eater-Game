package ui;

import java.awt.*;

import ce3.*;

public class UIDialogueResponse extends UIElement{

	//private UIText text;
	
	public UIDialogueResponse(Board frame, String words, int x, int y) {
		super(frame,x,y);
		parts.add(new UIText(board,xPos,yPos+30,words,new Color(255,255,255,150),new Font("Arial",Font.BOLD,30))); //text
	}
	
}
