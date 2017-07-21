package types;

import collision.Collidable;
import partie.AbstractModelPartie;

public abstract class Projectile extends Collidable{

	protected float speedFactor = 1;
	@Override
	public Vitesse getGlobalVit(AbstractModelPartie partie){
		Vitesse vit = super.getGlobalVit(partie);
		return vit.times(speedFactor);
	}
}
