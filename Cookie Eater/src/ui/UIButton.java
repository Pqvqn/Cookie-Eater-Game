package ui;

import java.awt.*;
import java.io.*;

import javax.imageio.ImageIO;

import ce3.*;
import menus.*;

public class UIButton extends UIElement{

	private UIText text;
	private UIImage img;
	private UIRectangle backing;
	private boolean highlighted;
	
	private String[] texts;
	private Image[] images;
	
	private MenuButton button;
	
	public UIButton(Board frame, MenuButton b) {
		super(frame,(int)b.bounds().getX(),(int)b.bounds().getY());
		button = b;
		Rectangle rect = button.bounds();
		
		images = new Image[button.stateList().length];
		texts = new String[button.stateList().length];
		for(int i=0; i<button.stateList().length; i++) {
			if(button.usesImage()) {
				try {
					images[i] = ImageIO.read(new File("Cookie Eater/src/resources/ui/"+button.stateList()[i]));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				texts[i] = button.stateList()[i];
			}
		}
		
		parts.add(text = new UIText(board,xPos+5,yPos+30,   (texts[0]!=null)?texts[0]:"",
				new Color(255,255,255,150),new Font("Arial",Font.BOLD,30))); //text
		//ratio to rescale image
		double rat = (images[0]!=null)?Math.min(rect.getWidth()/images[0].getWidth(null),rect.getHeight()/images[0].getHeight(null)):1;
		parts.add(img = new UIImage(board,xPos+(int)(.5+rect.getWidth()/2-rat*(images[0]!=null?images[0].getWidth(null)/2:0)),
				yPos+(int)(.5+rect.getHeight()/2-rat*(images[0]!=null?images[0].getHeight(null)/2:0)),   
				rat, (images[0]!=null)?images[0]:null));
				
		parts.add(backing = new UIRectangle(board,(int)rect.getX(),(int)rect.getY(),(int)rect.getWidth(),(int)rect.getHeight(),new Color(0,0,0,50),true));
		highlighted = false;
	}
	
	public void trigger(int state) {
		text.setText(texts[button.currentState()]);
		img.setImage(images[button.currentState()]);
	}
	
	public void updateState(int state, String val, boolean useImage) {
		if(useImage) {
			try {
				images[state] = ImageIO.read(new File("Cookie Eater/src/resources/ui/"+val));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			texts[state] = val;
		}
		trigger(state);
	}
	
	public void highlight(boolean h) {
		highlighted = h;
		backing.setColor((highlighted)?new Color(255,255,255,50):new Color(0,0,0,50));
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}
	
	
}
