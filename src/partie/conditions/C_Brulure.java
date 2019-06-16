package partie.conditions;

import partie.modelPartie.PartieTimer;

public class C_Brulure extends Condition{
	/*
	 * Inflicts damage on duration 
	 * */
	
	public C_Brulure(double _duree)
	{
		name=BRULURE;
		DUREE= _duree*Math.pow(10, 9); //sec 
		STEP = 1*Math.pow(10, 9); //in nano : damage every seconds
		DAMAGE = -5; //5 damage every STEP
		
		startTime=PartieTimer.me.getElapsedNano();
		lastStepTime=PartieTimer.me.getElapsedNano();
	}

}
