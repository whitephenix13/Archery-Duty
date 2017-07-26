package conditions;

import partie.PartieTimer;

public class C_Paralysie extends Condition{

	/*
	 * Disable the ability to shoot for the affected person 
	 * */

	public C_Paralysie(double _duree)
	{
		name=PARALYSIE;
		DUREE= _duree*Math.pow(10, 9); //sec 
		FACTOR= 0; //speed x0 
		startTime=PartieTimer.me.getElapsedNano();
	}

}
