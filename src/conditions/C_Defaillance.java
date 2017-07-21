package conditions;

import partie.PartieTimer;

public class C_Defaillance extends Condition{
	/*
	 * Received more damage from an attack 
	 * */
	
	public C_Defaillance()
	{
		name=DEFAILLANCE;
		DUREE= 50*Math.pow(10, 9); //sec 
		FACTOR= 2; //damage x2  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
