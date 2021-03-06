package partie.conditions;

import partie.conditions.Condition.ConditionEnum;
import partie.modelPartie.PartieTimer;

public class C_Lenteur extends Condition{
	/*
	 * Slow down the motion of the Collidable
	 * */

	public C_Lenteur(double _duree)
	{
		type=ConditionEnum.LENTEUR;
		DUREE= _duree*Math.pow(10, 9); //sec 
		FACTOR= 0.5; //speed x0.5  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
