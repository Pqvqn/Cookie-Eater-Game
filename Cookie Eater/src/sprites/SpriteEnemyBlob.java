package sprites;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ce3.*;
import enemies.*;

public class SpriteEnemyBlob extends Sprite{

	private EnemyBlob user;
	private Color coloration;
	private Image base;
	private double scale;
	private double radius;
	
	public SpriteEnemyBlob(Board frame, EnemyBlob e) throws IOException {
		super(frame);
		user = e;
		base = ImageIO.read(new File("Cookie Eater/src/resources/enemies/blob.png"));
		imgs.add(base);
	}
	public void setColor(Color c) {coloration = c;}
	public void prePaint() throws IOException {
		scale = board.currFloor.getScale();
		x = (int)(.5+user.getX());
		y = (int)(.5+user.getY());		
	}
	public void paint(Graphics g) {
		super.paint(g);
		try {
			prePaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		radius = user.getRadius();
		coloration = user.getColor();
		g.setColor(coloration);
		g.fillOval((int)(.5+x-radius), (int)(.5+y-radius), (int)(.5+2*radius), (int)(.5+2*radius));
		//images
		g.drawImage(base,(int)(.5+x-(base.getWidth(null)/10*scale)), (int)(.5+y-(base.getHeight(null)/10*scale)), (int)(2*(.5+base.getWidth(null)/10*scale)), (int)(2*(.5+base.getHeight(null)/10*scale)), null);
	}
}
