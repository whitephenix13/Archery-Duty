package types;

import collision.Collidable;
import principal.InterfaceConstantes;

public abstract class Entitie extends Collidable{
	public float MAXLIFE ;
	public float MINLIFE ;
	protected float life;
	
	public float getLife()
	{
		return(life);
	}
	
	public void addLife(double add)
	{
		if(add<0)
			add = conditions.onDamageReceived(add);
		life += add;
		if(life>MAXLIFE){life=MAXLIFE;}
		if(life<MINLIFE){life=MINLIFE;}
		//used to check if entitie should die
		onAddLife();
	}
	public abstract void onAddLife();
}
