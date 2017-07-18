package types;

import partie.AbstractModelPartie;

public abstract class Destroyable {
	protected boolean needDestroy = false;
	public boolean getNeedDestroy(){return needDestroy;}
	public void setNeedDestroy(boolean val){needDestroy=val;}
	public long TEMPS_DESTRUCTION = (long) Math.pow(10, 9);//nanos, 1sec 
	//timer pour savoir quand est ce que l'effet doit disparaitre 
	public long tempsDetruit = 0;
	public void timer()
	{
		tempsDetruit=System.nanoTime();
	}
	public abstract void destroy(AbstractModelPartie partie,boolean destroyNow);
	public abstract void onDestroy(AbstractModelPartie partie);

	
}
