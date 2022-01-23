package sprites;

import java.awt.*;
import java.io.*;

import javax.imageio.*;

import ce3.*;
import mechanisms.*;

public class SpriteMechanism extends Sprite{

	private Mechanism user;
	private Image img;
	private double scale;
	private File file;
	
	public SpriteMechanism(Board frame, Mechanism m, String fileName) throws IOException {
		super(frame);
		user = m;
		file = new File("Cookie Eater/src/resources/level/"+fileName+".png");
		img = ImageIO.read(file);
		//imgs.add(img);
	}
	
	public void redraw(String fileName) {
		//imgs.remove(img);
		file = new File("Cookie Eater/src/resources/level/"+fileName+".png");
		try {
			img = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//imgs.add(img);
	}
	
	public double radius() {
		return (Math.sqrt(Math.pow(img.getWidth(null),2)+Math.pow(img.getHeight(null),2)))/10*board.currLevel.getScale();
	}
	
	public void paint(Graphics g){
	
		scale = board.currLevel.getScale();
		x = (int)user.getX();
		y = (int)user.getY();
		g.drawImage(img,(int)(.5+x-(img.getWidth(null)/10*scale)), (int)(.5+y-(img.getHeight(null)/10*scale)), (int)(2*(.5+img.getWidth(null)/10*scale)), (int)(2*(.5+img.getHeight(null)/10*scale)), null);

	}
}
