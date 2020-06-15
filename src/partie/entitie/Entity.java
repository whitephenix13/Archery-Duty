package partie.entitie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.conditions.ConditionHandler;
import partie.effects.Effect;
import partie.effects.EffectConsequence;
import partie.effects.Grappin_effect;
import partie.entitie.heros.Heros;
import partie.modelPartie.DamageDrawer;
import partie.modelPartie.ModelPartie;
import partie.mouvement.entity.Mouvement_entity.EntityTypeMouv;
import utils.Vitesse;

public abstract class Entity extends Collidable{
	public ConditionHandler conditions;
	public float MAXLIFE ;
	public float MINLIFE ;
	protected float life;
	public HashMap<Effect,EffectConsequence> currentEffectsMap;
	
	public ArrayList<DamageDrawer> lastDamageTaken;
	
	public double last_feu_effect_update = -1;
	
	public boolean airJumping = false;
	public boolean wallJumping = false;
	public boolean groundJumping = false;
	public boolean draggable =true;

	public abstract void onAddLife();
	
	public Entity()
	{
		super();
		conditions= new ConditionHandler();
		currentEffectsMap  = new HashMap<Effect,EffectConsequence>();
		lastDamageTaken = new ArrayList<DamageDrawer>();
	}
	
	public float getLife()
	{
		return(life);
	}

	public void addLife(double add)
	{
		if(add==0)
			return;
		
		if(add<0)
			add = conditions.onDamageReceived(add);
		
		float prevLife = life;
		life += add;
		if(life>MAXLIFE){life=MAXLIFE;}
		if(life<MINLIFE){life=MINLIFE;}
		
		lastDamageTaken.add(new DamageDrawer(add));
		//used to check if entitie should die
		onAddLife();
	}

	@Override
	public void destroy(boolean destroyNow)
	{
		super.destroy(destroyNow);

		//Remove all related effects 
		for(int i=currentEffectsMap.size()-1;i>=0;--i)
		{
			unregisterEffect(currentEffectsMap.get(i));
		}

	}
	
	public boolean isGrounded()
	{
		Hitbox hit =getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp()).copy();
		assert hit.polygon.npoints==4;
		//get world hitboxes with Collision
		//lowers all points by 1 at most 
		for(int i=0; i<hit.polygon.npoints; ++i)
			hit.polygon.ypoints[i]+=1;
		boolean res =  Collision.isWorldCollision(hit,true);
		return res;
	}
	protected void setJumpSpeed(boolean isWallJumping,boolean isAirJumping, boolean isGroundJumping){
		if(isAirJumping)
			airJumping = true;
		else if(isWallJumping)
			wallJumping = true;
		else 
			groundJumping = true;
		getMouvement().setSpeed(this, getMouvIndex());
		if(isAirJumping)
			airJumping = false;
		else if(isWallJumping)
			wallJumping = false;
		else 
			groundJumping = false;
	}
	@Override 
	public void deplaceOutOfScreen()
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
			for(EffectConsequence eff: currentEffectsMap.values())
			{
				if(isDragged && ObjectTypeHelper.isTypeOf(eff.getParentEffect(), ObjectType.GRAPPIN_EFF)){
					vit = eff.getParentEffect().getModifiedVitesse(this);
					return vit;
				}
				vit =vit.add(eff.getParentEffect().getModifiedVitesse(this));
			}
			vit = vit.add(conditions.getModifiedVitesse());
		}
		return vit;
	}

	public void registerEffect(EffectConsequence eff)
	{		
		currentEffectsMap.put(eff.getParentEffect(),eff);
	}
	public void registerEffect(Effect eff)
	{	
		currentEffectsMap.put(eff,new EffectConsequence(this, eff, ModelPartie.me.getFrame()));
	}
	public void unregisterEffect(EffectConsequence eff)
	{
		currentEffectsMap.remove(eff.getParentEffect());
	}
	public void unregisterEffect(Effect eff)
	{
		currentEffectsMap.remove(eff);
	}
	public void removeEffectsThatExpired(int currentFrame){
		Iterator<Entry<Effect, EffectConsequence>> iter = currentEffectsMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Effect, EffectConsequence> entry = iter.next();
			if(entry.getValue().shouldBeRemoved(currentFrame))
				iter.remove();
		}
	}
	public boolean isDragged(){
		for(EffectConsequence eff:currentEffectsMap.values())
		{
			if(ObjectTypeHelper.isTypeOf(eff.getParentEffect(), ObjectType.GRAPPIN_EFF))
			{
				Grappin_effect grap = (Grappin_effect)eff.getParentEffect();
				if(grap.shooterDragged && this == grap.shooter)
					return true;
				if(!grap.shooterDragged && this != grap.shooter)
					return true;
			}
		}
		return false;
	}
	
	public void beforeGraphicUpdate(){
		ArrayList<DamageDrawer> toDelete = new ArrayList<DamageDrawer>();
		for(DamageDrawer damageDrawer : lastDamageTaken){
			if(!damageDrawer.shouldDraw())
				toDelete.add(damageDrawer);
		}
		for(DamageDrawer obj : toDelete)
			lastDamageTaken.remove(obj);
	}
}
