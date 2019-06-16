package partie.conditions;

import partie.modelPartie.PartieTimer;

public class C_Precision extends Condition{
	/*
	 * Augment shot speed
	 * */

	public C_Precision(double _duree)
	{
		name=PRECISION;
		DUREE= _duree*Math.pow(10, 9); //sec 
		FACTOR= 0.25; //speed x2  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
