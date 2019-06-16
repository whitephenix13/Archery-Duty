package partie.projectile.fleches.sprirituelle;

import java.util.List;

import partie.entitie.heros.Heros;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;

public class Spirituelle extends Fleche{
	public Spirituelle(List<Projectile> tabFleche, int current_frame,
			Heros _shooter, float damageMultiplier, float speedFactor) {
		super(tabFleche, current_frame, _shooter, damageMultiplier, speedFactor);
	}

	public Spirituelle(List<Projectile> tabFleche, int current_frame,
			Heros _shooter, boolean add_to_list, float damageMult,
			float speedFactor) {
		super(tabFleche, current_frame, _shooter, add_to_list, damageMult,speedFactor);
	}
}
