package conditions;

import partie.PartieTimer;

public class C_Precision extends Condition{
	/*
	 * Augment shot speed
	 * */

	public C_Precision()
	{
		name=PRECISION;
		DUREE= 50*Math.pow(10, 9); //sec 
		FACTOR= 0.25; //speed x2  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
