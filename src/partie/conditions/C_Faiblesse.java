package partie.conditions;

import partie.conditions.Condition.ConditionEnum;
import partie.modelPartie.PartieTimer;

public class C_Faiblesse extends Condition{
	/*
	 * Give less damage when attacking 
	 * */
	
	public C_Faiblesse(double _duree)
	{
		type=ConditionEnum.FAIBLESSE;
		DUREE= _duree*Math.pow(10, 9); //sec 
		FACTOR= 0.5; //damage x0.5  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
