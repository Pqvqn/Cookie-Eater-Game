package ui;

import java.awt.*;
import java.util.*;

import ce3.*;

public class UIDialogueSelect extends UIElement{
	
	private ArrayList<UIDialogueResponse> responses;
	//private UIRectangle chosenHighlight;
	private UIRectangle hoverHighlight;
	private UIRectangle backing;
	
	public UIDialogueSelect(Game frame, ArrayList<String> options, int x, int y) {
		super(frame,x,y);
		parts.add(backing = new UIRectangle(game, xPos, yPos, options.size()*210-10, 40, new Color(50, 50, 50, 100), true)); //backing
		//parts.add(chosenHighlight = new UIRectangle(board, xPos, yPos, 200, 40, new Color(255, 255, 255, 50), true));
		responses = new ArrayList<UIDialogueResponse>();
		int wid = 0;
		for(int i=0; i<options.size(); i++) {
			UIDialogueResponse a = new UIDialogueResponse(game,options.get(i),x+wid,y);
			responses.add(a);
			parts.add(a);
			wid += a.getWidth() + 10;
		}
		parts.add(hoverHighlight = new UIRectangle(game, xPos, yPos, 200, 40, new Color(255, 255, 255, 100), false));
		backing.setwLen(wid-10);
		
	}
	public void update(int chosen, int hovered) {
		for(int i=0; i<responses.size(); i++) {
			responses.get(i).select(i == chosen);
		}
		/*if(chosen>=0) {
			if(!parts.contains(chosenHighlight))parts.add(chosenHighlight);
			chosenHighlight.setxPos(responses.get(chosen).getxPos());
			chosenHighlight.setyPos(responses.get(chosen).getyPos());
		}else {
			parts.remove(chosenHighlight);
		}*/
		
		if(hovered>=0 && !responses.isEmpty()) {
			if(!parts.contains(hoverHighlight))parts.add(hoverHighlight);
			hoverHighlight.setxPos(responses.get(hovered).getxPos());
			hoverHighlight.setyPos(responses.get(hovered).getyPos());
			hoverHighlight.setwLen(responses.get(hovered).getWidth());
		}else {
			parts.remove(hoverHighlight);
		}
	}

}
