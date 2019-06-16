package partie.conditions;

import partie.modelPartie.PartieTimer;

public class C_Regeneration extends Condition{
	/*
	 * Heals HP on duration 
	 * */
	
	public C_Regeneration(double _duree)
	{
		name=REGENERATION;
		DUREE= _duree*Math.pow(10, 9); //sec 
		STEP = 1*Math.pow(10, 9); //damage every seconds
		DAMAGE = 5; //heals 5 every STEP
		
		startTime=PartieTimer.me.getElapsedNano();
		lastStepTime=PartieTimer.me.getElapsedNano();
	}
}
