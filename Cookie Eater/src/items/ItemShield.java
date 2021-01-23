package items;
import ce3.*;

public class ItemShield extends Item{

	private double initminrecoil;
	private double initmaxrecoil;
	private double mult;
	
	public ItemShield(Game frame) {
		super(frame);
		name = "Shield";
		desc="Shields the user.`Amplify: Recoil increases";
		mult = 1;
	}

	public void prepare() {
		initminrecoil=user.getMinRecoil(); //get starting recoil
		initmaxrecoil=user.getMaxRecoil();
	}
	public void initialize() {
		user.setMinRecoil(initminrecoil*mult); //multiple recoil
		user.setMaxRecoil(initmaxrecoil*mult);
	}
	
	public void execute() {
		if(checkCanceled())return;
		user.setShielded(true); //shield on
		/*if(user.getMinRecoil()!=newminrecoil)
				user.setMinRecoil(newminrecoil);
		if(user.getMaxRecoil()!=newmaxrecoil)
				user.setMaxRecoil(newmaxrecoil);*/
	}
	
	public void end(boolean interrupted) {

		user.setMinRecoil(initminrecoil); //reset recoil to original
		user.setMaxRecoil(initmaxrecoil);
		user.setShielded(false);
	}
	public void amplify() {
		super.amplify();
		mult*=2;
	}
	public void deamplify() {
		super.deamplify();
		mult/=2;
	}
}
