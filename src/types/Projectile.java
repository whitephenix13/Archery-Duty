package types;

import collision.Collidable;
import partie.AbstractModelPartie;

public abstract class Projectile extends Collidable{
	
	protected float speedFactor = 1;
	@Override
	public Vitesse getGlobalVit(AbstractModelPartie partie){
		Vitesse vit = localVit.Copy();
		return vit.times(speedFactor);
	}
	
}
