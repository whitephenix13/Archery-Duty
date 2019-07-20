package partie.conditions;

import partie.conditions.Condition.ConditionEnum;
import partie.modelPartie.PartieTimer;

public class C_Resistance extends Condition{
	/*
	 * Reduce damage received
	 * */

	public C_Resistance(double _duree)
	{
		type=ConditionEnum.RESISTANCE;
		DUREE= _duree*Math.pow(10, 9); //sec 
		FACTOR= 0.5; //speed x0.5  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
