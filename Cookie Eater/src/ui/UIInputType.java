package ui;

import java.awt.*;
import java.awt.event.*;

import ce3.*;

public class UIInputType extends UIElement implements KeyListener{

	private String submittedText;
	private String writtenText;
	private UIText textDisp;
	private UIText promptDisp;
	
	public UIInputType(Game frame, int x, int y) {
		super(frame,x,y);
		submittedText = null;
		writtenText = "";
		parts.add(new UIRectangle(game,xPos-300,yPos-50,600,100,new Color(0,0,0,100),true)); //backing
		parts.add(textDisp = new UIText(game,xPos,yPos+25,writtenText,new Color(255,255,255,200),new Font("Arial",Font.BOLD,70)));
		parts.add(promptDisp = new UIText(game,xPos,yPos-70,"",new Color(255,255,255,255),new Font("Arial",Font.ITALIC,20)));
		update();
	}
	
	public String getSubmission() {return submittedText;}
	
	public void startText(String prompt) {
		submittedText = null;
		writtenText = "";
		promptDisp.setText(prompt);
	}
	
	
	public void update() {
		textDisp.setText(writtenText);
		textDisp.center(xPos);
		promptDisp.center(xPos);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		switch(code) {
		case KeyEvent.VK_ENTER: //submit with enter
			submittedText = writtenText;
			break;
		case KeyEvent.VK_BACK_SPACE: //undo last char
			if(writtenText.length()>0)
				writtenText = writtenText.substring(0,writtenText.length()-1);
			break;
		default:
			if(e.getKeyChar()!=KeyEvent.CHAR_UNDEFINED)writtenText += e.getKeyChar(); //add typed letter to text
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
}
