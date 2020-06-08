package ce3;

public abstract class Entity {

	//parent class of moving, colliding objects on the stage
	
	protected double x, y; //position
	protected double x_velocity, y_velocity; //speed
	protected double mass; //weight
	
	public double getX() {return x;}
	public double getY() {return y;}
	public void setX(double xp) {x=xp;}
	public void setY(double yp) {y=yp;}
	
	public double getXVel() {return x_velocity;}
	public double getYVel() {return y_velocity;}
	public void setXVel(double a) {x_velocity = a;}
	public void setYVel(double a) {y_velocity = a;}
	
	public double getMass() {return mass;}
}
