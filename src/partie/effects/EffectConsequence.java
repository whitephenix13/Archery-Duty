package partie.effects;

import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import partie.entitie.Entity;
import partie.modelPartie.PartieTimer;

/***
 * This class is used to keep track of the effect that is currently active with several information such as when was the effect's consequence last applied
 * @author alexandre
 *
 */
public class EffectConsequence {
	
	Entity m_affectedEntity;
	Effect m_parentEffect;
	public Effect getParentEffect(){return m_parentEffect;}
	int m_lastCollisionCheckedFrame;
	
	double m_lastEffectApplied; //last time that the consequence of an effect was applied
	boolean appliedOnce = false;
	
	public EffectConsequence(Entity p_affectedEntity,Effect p_parentEffect,int currentFrame){
		m_affectedEntity = p_affectedEntity;
		m_parentEffect = p_parentEffect;
		m_lastCollisionCheckedFrame = currentFrame;
	}
	
	/***
	 * Call this method when the parentEffect is colliding with the object to which EffectConsequence is attached
	 */
	public void OnEffectColliding(int currentFrame){
		m_lastCollisionCheckedFrame = currentFrame;
	}
	public void applyConsequence(){
		if((PartieTimer.me.getElapsedNano() - m_lastEffectApplied)>m_parentEffect.getConsequenceUpdateTime()*Math.pow(10, 9)){
			m_parentEffect.applyConsequence(m_affectedEntity,appliedOnce);
			m_lastEffectApplied=PartieTimer.me.getElapsedNano();
			appliedOnce=true;
		}
	}

	public boolean shouldBeRemoved(int currentFrame){
		boolean isGrappin = ObjectTypeHelper.isTypeOf(m_parentEffect, ObjectType.GRAPPIN_EFF);
		
		if(m_parentEffect == null || m_parentEffect.getNeedDestroy() || (m_lastCollisionCheckedFrame != currentFrame && !isGrappin)){
			return true;
		}
		return false;
	}
}
