package partie.conditions;

import partie.modelPartie.PartieTimer;

public class C_Force extends Condition{
	/*
	 * Inflicts more damage when attacking
	 * */
	
	public C_Force(double _duree)
	{
		name=FORCE;
		DUREE= _duree*Math.pow(10, 9); //sec 
		FACTOR= 2; //damage x2  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
