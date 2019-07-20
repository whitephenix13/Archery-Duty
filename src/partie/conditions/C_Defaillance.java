package partie.conditions;

import partie.conditions.Condition.ConditionEnum;
import partie.modelPartie.PartieTimer;

public class C_Defaillance extends Condition{
	/*
	 * Received more damage from an attack 
	 * */
	
	public C_Defaillance(double _duree)
	{
		type=ConditionEnum.DEFAILLANCE;
		DUREE= _duree*Math.pow(10, 9); //sec 
		FACTOR= 2; //damage x2  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
