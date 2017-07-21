package conditions;

import partie.PartieTimer;

public class C_Brulure extends Condition{
	/*
	 * Inflicts damage on duration 
	 * */
	
	public C_Brulure()
	{
		name=BRULURE;
		DUREE= 50*Math.pow(10, 9); //sec 
		STEP = 1*Math.pow(10, 9); //damage every seconds
		DAMAGE = -5; //5 damage every STEP
		
		startTime=PartieTimer.me.getElapsedNano();
		lastStepTime=PartieTimer.me.getElapsedNano();
	}
}
