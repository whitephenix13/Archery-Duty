package conditions;

import partie.PartieTimer;

public class C_Regeneration extends Condition{
	/*
	 * Heals HP on duration 
	 * */
	
	public C_Regeneration()
	{
		name=REGENERATION;
		DUREE= 50*Math.pow(10, 9); //sec 
		STEP = 1*Math.pow(10, 9); //damage every seconds
		DAMAGE = 5; //heals 5 every STEP
		
		startTime=PartieTimer.me.getElapsedNano();
		lastStepTime=PartieTimer.me.getElapsedNano();
	}
}
