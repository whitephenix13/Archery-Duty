package types;

import java.util.ArrayList;

import collision.Collidable;
import conditions.ConditionHandler;
import effects.Effect;
import effects.Grappin_effect;
import effects.Vent_effect;
import fleches.Fleche;
import partie.AbstractModelPartie;

public abstract class Entitie extends Collidable{
	public ConditionHandler conditions;
	public float MAXLIFE ;
	public float MINLIFE ;
	protected float life;
	public ArrayList<Effect> currentEffects;
	
	public double last_feu_effect_update = -1;

	public abstract void onAddLife();

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
	@Override 
	public void init()
	{
		super.init();
		conditions= new ConditionHandler();
		currentEffects  = new ArrayList<Effect>();
	}
	@Override
	public void destroy(AbstractModelPartie partie,boolean destroyNow)
	{
		super.destroy(partie, destroyNow);

		//Remove all related effects 
		for(int i=currentEffects.size()-1;i>=0;--i)
		{
			unregisterEffect(partie,currentEffects.get(i));
		}

	}
	@Override
	public Vitesse getGlobalVit(AbstractModelPartie partie){
		Vitesse vit = localVit.Copy().times(conditions.getSpeedFactor());
		boolean isDragged = this.isDragged();
		for(Effect eff: currentEffects)
		{
			if(isDragged && TypeObject.isTypeOf(eff, TypeObject.GRAPPIN_EFF)){
				vit = eff.getModifiedVitesse(partie, this);
				return vit;
			}
			vit =vit.add(eff.getModifiedVitesse(partie, this));
		}

		return vit;
	}
	
	public void registerEffect(Effect eff)
	{
		currentEffects.add(eff);
	}
	public void unregisterEffect(AbstractModelPartie partie, Effect eff)
	{
		currentEffects.remove(eff);
	}
	
	public boolean draggable =true;
	public boolean isDragged(){
		for(Effect eff:currentEffects)
		{
			if(TypeObject.isTypeOf(eff, TypeObject.GRAPPIN_EFF))
			{
				Grappin_effect grap = (Grappin_effect)eff;
				if(grap.shooterDragged && this == grap.shooter)
					return true;
				if(!grap.shooterDragged && this != grap.shooter)
					return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @return true if collidable has a wind arrow stick to it 
	 */
	public boolean isWindProjected(){
		for(Effect eff:currentEffects)
		{
			if(TypeObject.isTypeOf(eff, TypeObject.VENT_EFF))
			{
				Vent_effect vent = (Vent_effect)eff;
				if(vent.stickedCollidable==this)
					return true;
			}
		}
		return false;

	}
}
