package conditions;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import partie.PartieTimer;

public class ConditionHandler {
	public Map<String,Condition> conditionsMap = new HashMap<String,Condition>();
	
	private int XDRAW_TOLERANCE = 5; 
	private int YDRAW_TOLERANCE = 10; 

	private Integer lastXDraw = null;
	private Integer lastYDraw = null;

	public ConditionHandler()
	{
		conditionsMap.put(Condition.BRULURE, null);
		conditionsMap.put(Condition.DEFAILLANCE, null);
		conditionsMap.put(Condition.FAIBLESSE, null);
		conditionsMap.put(Condition.FORCE, null);
		conditionsMap.put(Condition.LENTEUR, null);
		conditionsMap.put(Condition.PARALYSIE, null);
		conditionsMap.put(Condition.PRECISION, null);
		conditionsMap.put(Condition.REGENERATION, null);
		conditionsMap.put(Condition.RESISTANCE, null);
		conditionsMap.put(Condition.VITESSE, null);

	}
	
	/*if not working, go back to https://stackoverflow.com/questions/5245093/using-comparator-to-make-custom-sort */
	List<String> conditionOrder = Arrays.asList(Condition.BRULURE,Condition.PARALYSIE,Condition.DEFAILLANCE,Condition.FAIBLESSE,
			Condition.LENTEUR,Condition.RESISTANCE,Condition.REGENERATION,Condition.PRECISION,Condition.FORCE,Condition.VITESSE);
	
	Comparator<Condition> conditionComparator = new Comparator<Condition>(){

	    @Override
	    public int compare(final Condition o1, final Condition o2){
	        // let your comparator look up your car's color in the custom order
	        return Integer.valueOf(
	        		conditionOrder.indexOf(o1.name))
	            .compareTo(
	                Integer.valueOf(
	                	conditionOrder.indexOf(o2.name)));
	    }
	};
	
	/** Get damage from burn or healing*/
	public double conditionDamageReceived()
	{
		int damage = 0;
		Condition brulure = conditionsMap.get(Condition.BRULURE);
		if(brulure != null){
			double currentT = PartieTimer.me.getElapsedNano() ;
			if( (currentT- brulure.lastStepTime) > brulure.STEP){
				brulure.lastStepTime=currentT ;
				damage+= brulure.DAMAGE;
			}
		}
		Condition regen = conditionsMap.get(Condition.REGENERATION);
		if(regen != null){
			double currentT = PartieTimer.me.getElapsedNano() ;
			if( (currentT- regen.lastStepTime) > regen.STEP){
				regen.lastStepTime=currentT ;
				damage+= regen.DAMAGE;
			}
		}
		return damage;
	} 
	 /** Reduce or enhance damage received*/
	public double onDamageReceived(double damage)
	{
		double factor =1; 
		Condition defaillance = conditionsMap.get(Condition.DEFAILLANCE);
		Condition resistance = conditionsMap.get(Condition.RESISTANCE);
		
		if(defaillance != null)
			factor*=defaillance.FACTOR;
		
		if(resistance != null)
			factor*=resistance.FACTOR;
		
		return damage*factor;
	}
	/** Reduce or enhance damage given */
	public float getDamageFactor()
	{
		float factor =1; 
		Condition force = conditionsMap.get(Condition.FORCE);
		Condition faiblesse = conditionsMap.get(Condition.FAIBLESSE);
		
		if(force != null)
			factor*=force.FACTOR;
		
		if(faiblesse != null)
			factor*=faiblesse.FACTOR;
		
		return factor;
	} 
	
	/** Allow or not shoot */
	public float getShotSpeedFactor()
	{
		float factor =1;
		Condition paralysie = conditionsMap.get(Condition.PARALYSIE);
		if(paralysie != null)
			factor*=paralysie.FACTOR;
		
		Condition precision = conditionsMap.get(Condition.PRECISION);
		if(precision != null)
			factor*=precision.FACTOR;
		
		return factor;
		
	} 
	
	/** Multiply or reduce speed */
	public double getSpeedFactor()
	{
		double factor =1; 
		Condition lenteur = conditionsMap.get(Condition.LENTEUR);
		Condition vitesse = conditionsMap.get(Condition.VITESSE);
		
		if(lenteur != null)
			factor*=lenteur.FACTOR;
		
		if(vitesse != null)
			factor*=vitesse.FACTOR;
		
		return factor;
	} 
	
	/** Add a new condition to the list or replace existing one */
	public void addNewCondition(String name)
	{
		if(name.equals(Condition.BRULURE))
			conditionsMap.put(name, new C_Brulure());
		if(name.equals(Condition.REGENERATION))
			conditionsMap.put(name, new C_Regeneration());

		else if(name.equals(Condition.LENTEUR))
			conditionsMap.put(name, new C_Lenteur());

		else if(name.equals(Condition.VITESSE))
			conditionsMap.put(name, new C_Vitesse());

		else if(name.equals(Condition.PARALYSIE))
			conditionsMap.put(name, new C_Paralysie());		
		
		else if(name.equals(Condition.PRECISION))
			conditionsMap.put(name, new C_Precision());		

		else if(name.equals(Condition.DEFAILLANCE))
			conditionsMap.put(name, new C_Defaillance());	

		else if(name.equals(Condition.FORCE))
			conditionsMap.put(name, new C_Force());	

		else if(name.equals(Condition.RESISTANCE))
			conditionsMap.put(name, new C_Resistance());	

		else if(name.equals(Condition.FAIBLESSE))
			conditionsMap.put(name, new C_Faiblesse());
		else{
			try {
				throw(new Exception("Condition not known: "+ name));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}  
	
	/** Remove conditions that expired */
	public void updateConditionState()
	{
		for(String key : conditionsMap.keySet()){
			Condition condi = conditionsMap.get(key);
			if(condi!=null && condi.ended())
				conditionsMap.put(key, null);
		}
	} 
	
	/** Returns all active conditions and update the blinkDisplay value from the conditions*/ 
	public ArrayList<Condition> getAllConditions()
	{
		ArrayList<Condition> activeCondi = new ArrayList<Condition>();
		for(String key : conditionsMap.keySet())
		{
			Condition condi = conditionsMap.get(key);
			if(condi != null){
				activeCondi.add(condi);
				//test if the object should start/continue blinking by checking the remaining time 
				double currentT = PartieTimer.me.getElapsedNano();
				if((condi.DUREE - ( currentT- condi.startTime)) < condi.START_BLINKING)
				{
					//test if the object is blink visible or blink invisible 
					if( (currentT - condi.lastBlinkTime) > condi.BLINKING_FREQUENCE )
					{
						condi.blinkDisplay=!condi.blinkDisplay;
						condi.lastBlinkTime=currentT;
					}
				}
			}
		}
		Collections.sort(activeCondi, conditionComparator);
				
		return activeCondi;
	}
	
	/**
	 * Return the x value to which the conditions should be drawn 
	 * 
	 */
	public int getXStartDraw(int xdraw, int desiredRelativeMiddle, int nb_conditions)
	{
		//Desires: xstart + range/2 = xdraw + desiredRelativeMiddle => start = xdraw + desiredRelativeMiddle - range/2
		int range = 25 * (nb_conditions-1);
		int res = xdraw + desiredRelativeMiddle - range/2;
		//apply non flickering step 	
		if(lastXDraw!=null)
		{
			if(Math.abs(res - lastXDraw)<=XDRAW_TOLERANCE)
				res=lastXDraw;
		}
		lastXDraw=res;
		return res;
	}
	public int getYStartDraw(int ydraw)
	{
		int res = ydraw;
		//apply non flickering step 	
		if(lastYDraw!=null)
		{
			//only want to keep the same drawing if it was "higher" (ie y smaller) than the previous pos
			int deltaY = res -lastYDraw; 
			if(Math.abs(deltaY)<=YDRAW_TOLERANCE && deltaY>0 )
				res=lastYDraw;
		}
		lastYDraw=res;
		return res;
	}
	
	
	
	
}
