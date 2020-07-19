package ui;

import java.awt.*;
import java.util.*;

import ce3.*;

public class UIDialogueSelect extends UIElement{
	
	private ArrayList<UIDialogueResponse> responses;
	//private UIRectangle chosenHighlight;
	private UIRectangle hoverHighlight;
	
	public UIDialogueSelect(Board frame, ArrayList<String> options, int x, int y) {
		super(frame,x,y);
		parts.add(new UIRectangle(board, xPos, yPos, options.size()*210-10, 40, new Color(50, 50, 50, 100), true)); //backing
		//parts.add(chosenHighlight = new UIRectangle(board, xPos, yPos, 200, 40, new Color(255, 255, 255, 50), true));
		parts.add(hoverHighlight = new UIRectangle(board, xPos, yPos, 200, 40, new Color(255, 255, 255, 100), false));
		responses = new ArrayList<UIDialogueResponse>();
		for(int i=0; i<options.size(); i++) {
			UIDialogueResponse a = new UIDialogueResponse(board,options.get(i),x+210*i,y);
			responses.add(a);
			parts.add(a);
		}
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
		
		if(hovered>=0) {
			if(!parts.contains(hoverHighlight))parts.add(hoverHighlight);
			hoverHighlight.setxPos(responses.get(hovered).getxPos());
			hoverHighlight.setyPos(responses.get(hovered).getyPos());
		}else {
			parts.remove(hoverHighlight);
		}
	}

}
