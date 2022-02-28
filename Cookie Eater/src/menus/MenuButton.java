package menus;

import java.awt.*;
import java.awt.event.*;

import ce3.*;
import ui.*;

public class MenuButton implements MouseListener, MouseMotionListener{

	private Game game;
	private OnClick onClick;
	private Rectangle bounding;
	private UIButton ui;
	private boolean visible;
	private boolean usesImage;
	private String[] states;
	private int currState;
	private UIElement uiManager;
	
	public MenuButton(Game frame, UIElement manager, OnClick oc, String[] statelist, boolean img, int x, int y, int w, int h) {
		game = frame;
		onClick = oc;
		bounding = new Rectangle(x,y,w,h);
		states = statelist;
		currState = 0;
		visible = false;
		usesImage = img;
		uiManager = manager;
		ui = new UIButton(game,this);

	}
	
	public void setClick(OnClick oc) {onClick = oc;}
	
	//clicks button until has the desired state
	public void clickTo(String s) {
		for(int i=0; i<states.length && !getState().equals(s); i++) {
			currState++;
			if(currState >= states.length)currState = 0;
			ui.trigger(currState);
			onClick.click();
		}
	}
	
	public void show(boolean s) {
		if(s!=visible) {
			if(s) {
				uiManager.addElement(ui);
				game.addMouseListener(this);
				game.addMouseMotionListener(this);
			}else {
				uiManager.removeElement(ui);
				game.removeMouseListener(this);
				game.removeMouseMotionListener(this);
			}
		}
		visible = s;
	}
	
	public void show() {
		show(true);
	}
	
	public void hide() {
		show(false);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(bounding.contains(game.draw.convertPoint(e.getLocationOnScreen(),true))) {
			currState++;
			if(currState >= states.length)currState = 0;
			ui.trigger(currState);
			onClick.click();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(bounding.contains(game.draw.convertPoint(e.getLocationOnScreen(),true))) {
			ui.highlight(true);
		}else {
			if(ui.isHighlighted()) {
				ui.highlight(false);
			}
		}

	}
	
	
	public Rectangle bounds() {return bounding;}
	public int currentState() {return currState;}
	public String getState() {return states[currState];}
	public String[] stateList() {return states;}
	public boolean usesImage() {return usesImage;}
	public void resetState() {currState=0;ui.trigger(currState);}
	public void setCurrStateValue(String s) {states[currState]=s;ui.updateState(currState,states[currState],usesImage());}
	public void setStateList(String[] list) {states = list;}
	
	public interface OnClick{
		public void click();
	}
}
