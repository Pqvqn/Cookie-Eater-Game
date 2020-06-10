package items;
import ce3.*;

public class ItemShield extends Item{

	private double initminrecoil;
	private double newminrecoil;
	private double initmaxrecoil;
	private double newmaxrecoil;
	private boolean canamp;
	
	public ItemShield(Board frame) {
		super(frame);
		name = "Shield";
		desc="Shields the user.`Amplify: Recoil increases";
		canamp = user.getClass().equals(Eater.class);
		if(canamp) {
			initminrecoil=((Eater)user).getMinRecoil();
			newminrecoil=initminrecoil;
			initmaxrecoil=((Eater)user).getMaxRecoil();
			newmaxrecoil=initmaxrecoil;
		}
	}

	public void initialize() {
	
	}
	
	public void execute() {
		if(checkCanceled())return;
		user.setShielded(true);
		if(canamp) {
			if(((Eater)user).getMinRecoil()!=newminrecoil)
					((Eater)user).setMinRecoil(newminrecoil);
			if(((Eater)user).getMaxRecoil()!=newmaxrecoil)
					((Eater)user).setMaxRecoil(newmaxrecoil);
		}
	}
	
	public void end(boolean interrupted) {
		if(canamp) {
			((Eater)user).setMinRecoil(initminrecoil);
			((Eater)user).setMaxRecoil(initmaxrecoil);
		}
		user.setShielded(false);
	}
	public void amplify() {
		super.amplify();
		newminrecoil+=initminrecoil;
		newmaxrecoil+=initmaxrecoil;
	}
	public void deamplify() {
		super.deamplify();
		newminrecoil-=initminrecoil;
		newmaxrecoil-=initmaxrecoil;
	}
}
