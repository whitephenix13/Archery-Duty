package partie.entitie;

import java.util.ArrayList;

import debug.DebugStack;
import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.conditions.ConditionHandler;
import partie.effects.Effect;
import partie.effects.Grappin_effect;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.entity.Mouvement_entity.EntityTypeMouv;
import utils.Vitesse;

public abstract class Entity extends Collidable{
	public ConditionHandler conditions;
	public float MAXLIFE ;
	public float MINLIFE ;
	protected float life;
	public ArrayList<Effect> currentEffects;

	public double last_feu_effect_update = -1;

	public abstract void onAddLife();
	
	public Entity()
	{
		super();
		conditions= new ConditionHandler();
		currentEffects  = new ArrayList<Effect>();
	}
	
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
	public void destroy(AbstractModelPartie partie,boolean destroyNow)
	{
		super.destroy(partie, destroyNow);

		//Remove all related effects 
		for(int i=currentEffects.size()-1;i>=0;--i)
		{
			unregisterEffect(partie,currentEffects.get(i));
		}

	}
	
	public boolean isGrounded(AbstractModelPartie partie)
	{
		Hitbox hit =getHitbox(partie.INIT_RECT,partie.getScreenDisp()).copy();
		assert hit.polygon.npoints==4;
		//get world hitboxes with Collision
		//lowers all points by 1 at most 
		for(int i=0; i<hit.polygon.npoints; ++i)
			hit.polygon.ypoints[i]+=1;
		boolean res =  Collision.isWorldCollision(partie,hit,true);
		return res;
	}
	
	@Override 
	public void deplaceOutOfScreen(AbstractModelPartie partie)
	{
		//do nothing
		if(this instanceof Heros)
			System.out.println("ENTITY: Out of screen "+getPos()+" "+ this.getGlobalVit());
	}
	
	@Override
	public Vitesse getGlobalVit(){
		Vitesse vit = localVit.Copy().times(conditions.getSpeedFactor());
		boolean isDragged = this.isDragged();
		//Do not apply any effect when accroche 
		boolean applyEffects = true;
		if(this instanceof Heros)
		{
			applyEffects = !((Heros)this).getMouvement().isMouvement(EntityTypeMouv.ACCROCHE);
		}
		if(applyEffects){
			for(Effect eff: currentEffects)
			{
				if(isDragged && ObjectTypeHelper.isTypeOf(eff, ObjectType.GRAPPIN_EFF)){
					vit = eff.getModifiedVitesse(this);
					return vit;
				}
				vit =vit.add(eff.getModifiedVitesse(this));
			}
			vit = vit.add(conditions.getModifiedVitesse());
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
			if(ObjectTypeHelper.isTypeOf(eff, ObjectType.GRAPPIN_EFF))
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
}
