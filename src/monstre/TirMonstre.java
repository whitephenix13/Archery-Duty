package monstre;

import collision.Collidable;
import partie.AbstractModelPartie;
import types.Hitbox;

public abstract class TirMonstre extends Collidable{
	
	public int dommage;
		
	public abstract Hitbox getWorldHitbox(AbstractModelPartie partie);

}
