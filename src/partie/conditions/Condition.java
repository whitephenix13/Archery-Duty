package partie.conditions;

import images.ImagesContainer.ImageInfo;
import partie.modelPartie.PartieTimer;
import utils.Vitesse;

public abstract class Condition {
	public ConditionEnum type;
	public static enum ConditionEnum implements ImageInfo{BRULURE,REGENERATION,LENTEUR,VITESSE,PARALYSIE,PRECISION,DEFAILLANCE,RESISTANCE,FORCE,FAIBLESSE,MOTION};
	/*REMOVE public final static String BRULURE = "brulure";
	public final static  String REGENERATION = "regeneration";

	public final static  String LENTEUR = "lenteur";
	public final static  String VITESSE="vitesse";

	public final static  String PARALYSIE = "paralysie";
	public final static  String PRECISION = "precision";

	public final static  String DEFAILLANCE = "defaillance";
	public final static  String RESISTANCE = "resistance";

	public final static  String FORCE = "force";
	public final static  String FAIBLESSE = "faiblesse";
	
	public final static  String MOTION = "motion";*/
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
		if(DUREE > 0  && (PartieTimer.me.getElapsedNano()-startTime) >DUREE )
			return true; 
		return false;
	}
	
	/**
	 * Called when an already existing condition is applied again 
	 */
	public void onAddCondition(double _duree,Vitesse _initSpeed,int id)
	{
		double current_time = PartieTimer.me.getElapsedNano();
		
		DUREE = _duree*Math.pow(10, 9);
		startTime = current_time;
		blinkDisplay=true;
	}
	
	public void OnAttacherCollided()
	{
		
	}
	
	public void Update()
	{
		
	}
}
