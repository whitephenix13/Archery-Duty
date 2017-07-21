package monstre;

import partie.AbstractModelPartie;
import types.Hitbox;
import types.Projectile;

public abstract class TirMonstre extends Projectile{
	
	public float dommage;
		
	public abstract Hitbox getWorldHitbox(AbstractModelPartie partie);

}
