package partie.conditions;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import menu.menuPrincipal.ModelPrincipal;
import partie.conditions.Condition.ConditionEnum;
import partie.modelPartie.PartieTimer;
import utils.Vitesse;

public class ConditionHandler {
	public Map<ConditionEnum,Condition> conditionsMap = new HashMap<ConditionEnum,Condition>();

	private int XDRAW_TOLERANCE = 5; 
	private int YDRAW_TOLERANCE = 10; 

	private Integer lastXDraw = null;
	private Integer lastYDraw = null;
	
	public ConditionHandler()
	{
		conditionsMap.put(ConditionEnum.BRULURE, null);
		conditionsMap.put(ConditionEnum.DEFAILLANCE, null);
		conditionsMap.put(ConditionEnum.FAIBLESSE, null);
		conditionsMap.put(ConditionEnum.FORCE, null);
		conditionsMap.put(ConditionEnum.LENTEUR, null);
		conditionsMap.put(ConditionEnum.PARALYSIE, null);
		conditionsMap.put(ConditionEnum.PRECISION, null);
		conditionsMap.put(ConditionEnum.REGENERATION, null);
		conditionsMap.put(ConditionEnum.RESISTANCE, null);
		conditionsMap.put(ConditionEnum.VITESSE, null);
		conditionsMap.put(ConditionEnum.MOTION, null);

	}

	/*if not working, go back to https://stackoverflow.com/questions/5245093/using-comparator-to-make-custom-sort */
	List<ConditionEnum> conditionOrder = Arrays.asList(ConditionEnum.MOTION,ConditionEnum.BRULURE,ConditionEnum.PARALYSIE,ConditionEnum.DEFAILLANCE,ConditionEnum.FAIBLESSE,
			ConditionEnum.LENTEUR,ConditionEnum.RESISTANCE,ConditionEnum.REGENERATION,ConditionEnum.PRECISION,ConditionEnum.FORCE,ConditionEnum.VITESSE);

	Comparator<Condition> conditionComparator = new Comparator<Condition>(){

		@Override
		public int compare(final Condition o1, final Condition o2){
			// let your comparator look up your car's color in the custom order
			return Integer.valueOf(
					conditionOrder.indexOf(o1.type))
					.compareTo(
							Integer.valueOf(
									conditionOrder.indexOf(o2.type)));
		}
	};

	/** Get damage from burn or healing*/
	public double conditionDamageReceived()
	{
		int damage = 0;
		Condition brulure = conditionsMap.get(ConditionEnum.BRULURE);
		if(brulure != null){
			double currentT = PartieTimer.me.getElapsedNano() ;
			if( (currentT- brulure.lastStepTime) > brulure.STEP){
				brulure.lastStepTime=currentT ;
				damage+= brulure.DAMAGE;
			}
		}
		Condition regen = conditionsMap.get(ConditionEnum.REGENERATION);
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
		Condition defaillance = conditionsMap.get(ConditionEnum.DEFAILLANCE);
		Condition resistance = conditionsMap.get(ConditionEnum.RESISTANCE);

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
		Condition force = conditionsMap.get(ConditionEnum.FORCE);
		Condition faiblesse = conditionsMap.get(ConditionEnum.FAIBLESSE);

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
		Condition paralysie = conditionsMap.get(ConditionEnum.PARALYSIE);
		if(paralysie != null)
			factor*=paralysie.FACTOR;

		Condition precision = conditionsMap.get(ConditionEnum.PRECISION);
		if(precision != null)
			factor*=precision.FACTOR;

		return factor;

	} 

	/** Multiply or reduce speed */
	public double getSpeedFactor()
	{
		double factor =1; 
		Condition lenteur = conditionsMap.get(ConditionEnum.LENTEUR);
		Condition vitesse = conditionsMap.get(ConditionEnum.VITESSE);

		if(lenteur != null)
			factor*=lenteur.FACTOR;
		if(vitesse != null)
			factor*=vitesse.FACTOR;
		return factor;
	} 

	/**
	 * return the localVit change induced by the condition
	 */
	public Vitesse getModifiedVitesse()
	{
		Condition motion = conditionsMap.get(ConditionEnum.MOTION);
		if(motion != null)
			return ((C_Motion)motion).getModifiedVitesse();
		else
			return new Vitesse(0,0);
	}

	/** Add a new condition to the list or replace existing one */
	public Condition addNewCondition(ConditionEnum type,double _duree, Vitesse init_speed,int id)
	{
		//Replace existing condition
		if(conditionsMap.containsKey(type))
		{
			Condition condi = conditionsMap.get(type);
			if(condi != null){
				condi.onAddCondition(_duree,init_speed,id);
				return condi ;
			}
		}
		Condition newCondi = null;
		if(type.equals(ConditionEnum.BRULURE))
			newCondi= new C_Brulure(_duree);
		else if(type.equals(ConditionEnum.REGENERATION))
			newCondi= new C_Regeneration(_duree);

		else if(type.equals(ConditionEnum.LENTEUR))
			newCondi= new C_Lenteur(_duree);

		else if(type.equals(ConditionEnum.VITESSE))
			newCondi= new C_Vitesse(_duree);

		else if(type.equals(ConditionEnum.PARALYSIE))
			newCondi= new C_Paralysie(_duree);		

		else if(type.equals(ConditionEnum.PRECISION))
			newCondi= new C_Precision(_duree);		

		else if(type.equals(ConditionEnum.DEFAILLANCE))
			newCondi= new C_Defaillance(_duree);	

		else if(type.equals(ConditionEnum.FORCE))
			newCondi= new C_Force(_duree);	

		else if(type.equals(ConditionEnum.RESISTANCE))
			newCondi= new C_Resistance(_duree);	

		else if(type.equals(ConditionEnum.FAIBLESSE))
			newCondi= new C_Faiblesse(_duree);

		else if(type.equals(ConditionEnum.MOTION))
			newCondi= new C_Motion(init_speed);
		else{
			try {
				throw(new Exception("Condition not known: "+ type));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(newCondi != null)
			conditionsMap.put(type, newCondi);
		return newCondi;
	}  
	
	public Condition addNewCondition(ConditionEnum type,double _duree,int id)
	{
		return addNewCondition(type,_duree,new Vitesse(),id);
	}  
	
	/** Remove conditions that expired */
	public void updateConditionState()
	{
		for(ConditionEnum key : conditionsMap.keySet()){
			Condition condi = conditionsMap.get(key);
			if(condi!=null){
				condi.Update();
				if(condi.ended()){
					conditionsMap.put(key, null);
				}
			}
		}
	} 
	
	public void OnAttacherCollided()
	{
		for(ConditionEnum key : conditionsMap.keySet()){
			Condition condi = conditionsMap.get(key);
			if(condi!=null){
				condi.OnAttacherCollided();
			}
		}
	}
	
	/** Returns all active conditions and update the blinkDisplay value from the conditions*/ 
	public ArrayList<Condition> getAllConditions()
	{
		ArrayList<Condition> activeCondi = new ArrayList<Condition>();
		for(ConditionEnum key : conditionsMap.keySet())
		{
			Condition condi = conditionsMap.get(key);
			if(condi != null && !condi.type.equals(ConditionEnum.MOTION)){
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
