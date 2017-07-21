package conditions;

import partie.PartieTimer;

public abstract class Condition {
	public String name;
	public static String BRULURE = "brulure";
	public static String REGENERATION = "regeneration";

	public static String LENTEUR = "lenteur";
	public static String VITESSE="vitesse";

	public static String PARALYSIE = "paralysie";
	public static String PRECISION = "precision";

	public static String DEFAILLANCE = "defaillance";
	public static String RESISTANCE = "resistance";

	public static String FORCE = "force";
	public static String FAIBLESSE = "faiblesse";
	
	//Time during which the condition is applied 
	protected double DUREE = 0;//in nanos
	protected double startTime = 0;
	
	//Time at which the effect of the condition is applied (used for brulure)
	protected double STEP = 0;
	protected double lastStepTime = 0;
	
	protected double DAMAGE = 0;//damage for brulure ...
	protected double FACTOR = 0;//slow factor for lenteur ...
	
	
	//Blink time (when time left <1sec)
	protected double START_BLINKING = 1*Math.pow(10, 9); //start blinking when this few time is left 
	protected double BLINKING_FREQUENCE = 0.05 *Math.pow(10, 9); //frequence of blink 
	public boolean blinkDisplay =true; //switch between true and false when blinking 
	protected double lastBlinkTime=0;
	
	public boolean ended()
	{
		if((PartieTimer.me.getElapsedNano()-startTime) >DUREE )
			return true; 
		return false;
	}
	
}