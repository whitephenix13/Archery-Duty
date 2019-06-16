package gameConfig;

import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.PartieTimer;

public abstract class Destroyable {
	protected boolean needDestroy = false;
	public boolean getNeedDestroy(){return needDestroy;}
	public long TEMPS_DESTRUCTION = (long) Math.pow(10, 9);//nanos, 1sec 
	//timer to know when the object should be destroyed
	public double tempsDetruit = 0;
	public void timer()
	{
		tempsDetruit=PartieTimer.me.getElapsedNano();
	}
	public abstract void destroy(AbstractModelPartie partie,boolean destroyNow);
	public abstract void onDestroy(AbstractModelPartie partie);

	
}
