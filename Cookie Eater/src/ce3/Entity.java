package ce3;

public abstract class Entity {

	//parent class of moving, colliding objects on the stage
	
	protected double x, y; //position
	protected double x_velocity, y_velocity; //speed
	protected double mass; //weight
	protected double radius; //represents size
	protected double countVels; //number of averaged velocities in cycle
	protected boolean lock; //can entity control its movement
	protected boolean shielded; //in stun after shield use
	protected boolean ghost; //if the entity is in ghost mode
	protected double extra_radius; //area outside of the model, interacts with everything other than walls
	
	public void runUpdate() {
		countVels = 0;
	}
	
	public double getX() {return x;}
	public double getY() {return y;}
	public void setX(double xp) {x=xp;}
	public void setY(double yp) {y=yp;}
	
	public double getXVel() {return x_velocity;}
	public double getYVel() {return y_velocity;}
	public void setXVel(double a) {x_velocity = a;}
	public void setYVel(double a) {y_velocity = a;}
	
	public double getMass() {return mass;}
	
	//direction accelerating towards
	public double getAim() {return 0.0;}
	
	public void lockControl(boolean l) {lock = l;}
	
	//takes velocity changes from items and averages them
	public void averageVels(double xVel, double yVel) {
		if(countVels==0) {
			setXVel(0);
			setYVel(0);
		}
		countVels++;
		setXVel((getXVel()*(countVels-1)+xVel)/countVels);
		setYVel((getYVel()*(countVels-1)+yVel)/countVels);
	}
	
	public void setShielded(boolean s) {shielded = s;}
	public boolean getShielded() {return shielded;}
	public void setGhost(boolean g) {ghost = g;}
	public boolean getGhosted() {return ghost;}
	
	public void setExtraRadius(double er) {extra_radius=er;}
	public double getExtraRadius() {return extra_radius;}
	public void setRadius(double r) {radius=r;}
	public double getRadius() {return radius;}
}
