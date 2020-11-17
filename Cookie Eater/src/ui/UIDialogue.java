package ui;

import java.awt.*;
import java.util.*;

import ce3.*;
import entities.*;
import menus.*;
import sprites.*;

public class UIDialogue extends UIElement{

	private UIText name;
	private UIParagraph speech;
	private Dialogue dialogue;
	
	private UIDialogueSelect responder;
	//private UIOval base;
	//private UIImage sprite;
	private Entity speaker;
	//private UIRectangle backing;
	private Image spriteImg;
	private double ratio;
	
	public UIDialogue(Board frame, Dialogue words, ArrayList<String> options, int[] expressions) {
		super(frame,0,0);
		ratio = 10;
		dialogue = words;
		speaker = dialogue.getSpeaker();
		xPos=board.X_RESOL/2;
		yPos=board.Y_RESOL-100;
		
		parts.add(new UIOval(board,xPos-730,yPos,(int)(.5+speaker.getRadius()/board.currFloor.getScale()*ratio),(int)(.5+speaker.getRadius()/board.currFloor.getScale()*ratio),speaker.getColor(),true)); //base
		/*try {
			String speakerName = "eater";
			spriteImg = ImageIO.read(new File("Cookie Eater/src/resources/explorers/"+speakerName+"Base.png"));
			Graphics g = spriteImg.getGraphics();
			Image face = ImageIO.read(new File("Cookie Eater/src/resources/explorers/"+speakerName+"Face.png"));
			g.drawImage(face,(int)(.5+(spriteImg.getWidth(null)*.75-face.getWidth(null)/2.0)),(int)(.5+(spriteImg.getHeight(null)*.5-face.getHeight(null)/2.0)),null);
			speakerName = speaker.getName().toLowerCase();
			g.drawImage(ImageIO.read(new File("Cookie Eater/src/resources/explorers/"+speakerName+"HelmRight.png")),0,0,null);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		//display speaker by taking right-facing-neutral-expression from its sprite
		if(speaker instanceof Explorer && ((Explorer)speaker).getSprite()!=null) {
			spriteImg =  ((Explorer)speaker).getSprite().getCompiled(expressions[0], expressions[1]);
			parts.add(new UIImage(board,(int)(.5+xPos-730-((ratio/10.0)*(spriteImg.getWidth(null)/2.0))),(int)(.5+yPos-((ratio/10.0)*(spriteImg.getHeight(null)/2.0))),ratio/10.0,spriteImg)); //sprite
		}
		
		parts.add(new UIRectangle(board,xPos-500,yPos-100,1000,100,new Color(0,0,0,150),true)); //backing
		parts.add(name = new UIText(board,xPos-500,yPos-110,"",new Color(120,120,120,200),new Font("Arial",Font.BOLD,25)));
		parts.add(speech = new UIParagraph(board,xPos-490,yPos-75,null,new Color(255,255,255,150),new Font("Arial",Font.BOLD,30),30,55));
		name.setText((speaker!=null)?speaker.getName():"");
		speech.setTextLines((dialogue!=null)?dialogue.getText():"");
		parts.add(responder = new UIDialogueSelect(board,options,xPos-500,yPos));
	}
	public void update() {
		if(dialogue.getOptions().isEmpty()) {
			responder.update(-1,-1);
		}else {
			responder.update(dialogue.getChoice(),dialogue.getHover());
		}
	}
}
