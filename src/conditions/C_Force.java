package conditions;

import partie.PartieTimer;

public class C_Force extends Condition{
	/*
	 * Inflicts more damage when attacking
	 * */
	
	public C_Force()
	{
		name=FORCE;
		DUREE= 30*Math.pow(10, 9); //sec 
		FACTOR= 2; //damage x2  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
