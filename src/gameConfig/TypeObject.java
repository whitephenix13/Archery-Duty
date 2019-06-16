package gameConfig;

import java.util.ArrayList;
import java.util.List;

import partie.bloc.Bloc;
import partie.collision.Collidable;
import partie.effects.Effect;
import partie.effects.Electrique_effect;
import partie.effects.Explosive_effect;
import partie.effects.Feu_effect;
import partie.effects.Glace_effect;
import partie.effects.Grappin_effect;
import partie.effects.Lumiere_effect;
import partie.effects.Ombre_effect;
import partie.effects.Roche_effect;
import partie.effects.Trou_noir_effect;
import partie.effects.Vent_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.entitie.monstre.Monstre;
import partie.entitie.monstre.Spirel;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;
import partie.projectile.fleches.destructrice.Destructrice;
import partie.projectile.fleches.destructrice.Fleche_bogue;
import partie.projectile.fleches.destructrice.Fleche_explosive;
import partie.projectile.fleches.destructrice.Fleche_foudre;
import partie.projectile.fleches.destructrice.Fleche_trou_noir;
import partie.projectile.fleches.materielle.Fleche_electrique;
import partie.projectile.fleches.materielle.Fleche_feu;
import partie.projectile.fleches.materielle.Fleche_glace;
import partie.projectile.fleches.materielle.Fleche_roche;
import partie.projectile.fleches.materielle.Materielle;
import partie.projectile.fleches.rusee.Fleche_auto_teleguidee;
import partie.projectile.fleches.rusee.Fleche_cac;
import partie.projectile.fleches.rusee.Fleche_retard;
import partie.projectile.fleches.rusee.Fleche_v_fleche;
import partie.projectile.fleches.rusee.Rusee;
import partie.projectile.fleches.sprirituelle.Fleche_grappin;
import partie.projectile.fleches.sprirituelle.Fleche_lumiere;
import partie.projectile.fleches.sprirituelle.Fleche_ombre;
import partie.projectile.fleches.sprirituelle.Fleche_vent;
import partie.projectile.fleches.sprirituelle.Spirituelle;
import partie.projectile.tirMonstre.TirMonstre;
import partie.projectile.tirMonstre.TirSpirel;

public abstract class TypeObject {
	
	public static String COLLIDABLE = Collidable.class.getName();
		
		public static String EFFECT = Effect.class.getName();
			public static String ELECTRIQUE_EFF = Electrique_effect.class.getName();
			public static String EXPLOSIVE_EFF = Explosive_effect.class.getName();
			public static String FEU_EFF = Feu_effect.class.getName();
			public static String GLACE_EFF = Glace_effect.class.getName();
			public static String GRAPPIN_EFF = Grappin_effect.class.getName();
			public static String LUMIERE_EFF = Lumiere_effect.class.getName();
			public static String OMBRE_EFF = Ombre_effect.class.getName();
			public static String ROCHE_EFF = Roche_effect.class.getName();
			public static String TROU_NOIR_EFF = Trou_noir_effect.class.getName();
			public static String VENT_EFF = Vent_effect.class.getName();
			
		public static String BLOC = Bloc.class.getName();
		
		public static String ENTITIE = Entity.class.getName();
			public static String HEROS = Heros.class.getName();
			public static String MONSTRE = Monstre.class.getName();
				public static String SPIREL = Spirel.class.getName();
		
		public static String PROJECTILE = Projectile.class.getName();
			public static String FLECHE = Fleche.class.getName();
				public static String DESTRUCTRICE = Destructrice.class.getName();
					public static String FOUDRE=Fleche_foudre.class.getName();
					public static String EXPLOSIVE=Fleche_explosive.class.getName();
					public static String TROU_NOIR=Fleche_trou_noir.class.getName();
					public static String BOGUE=Fleche_bogue.class.getName();
					
				public static String MATERIELLE = Materielle.class.getName();
					public static String FEU=Fleche_feu.class.getName();
					public static String ELECTRIQUE=Fleche_electrique.class.getName();
					public static String GLACE=Fleche_glace.class.getName();
					public static String ROCHE=Fleche_roche.class.getName();
					
				public static String RUSEE = Rusee.class.getName();
					public static String AUTO_TELEGUIDEE=Fleche_auto_teleguidee.class.getName();
					public static String RETARD=Fleche_retard.class.getName();
					public static String V_FLECHE=Fleche_v_fleche.class.getName();
					public static String CAC=Fleche_cac.class.getName();
				
				public static String SPRIRITUELLE = Spirituelle.class.getName();
					public static String LUMIERE=Fleche_lumiere.class.getName();
					public static String OMBRE=Fleche_ombre.class.getName();
					public static String VENT=Fleche_vent.class.getName();
					public static String GRAPPIN=Fleche_grappin.class.getName();
				
				public static String[] DESTRUCTRICE_CLASS = {FOUDRE,EXPLOSIVE,TROU_NOIR,BOGUE};
				public static String[] MATERIELLE_CLASS = {FEU,ELECTRIQUE,GLACE,ROCHE};
				public static String[] RUSEE_CLASS = {AUTO_TELEGUIDEE,RETARD,V_FLECHE,CAC};
				public static String[] SPRIRITUELLE_CLASS = {LUMIERE,OMBRE,VENT,GRAPPIN};

				
			public static String TIR_MONSTRE = TirMonstre.class.getName();
				public static String TIR_SPIREL = TirSpirel.class.getName();
	
	
	
	/** Check if the given object is of type or of subclass of obj2. Use exactMatch=true to check for same class*/
	public static boolean isTypeOf(Object obj, Object obj2,boolean exactMatch)
	{
		if(exactMatch)
			return obj.getClass().getName().equals(obj2.getClass().getName());
		try {
			return Class.forName(obj2.getClass().getName()).isAssignableFrom(obj.getClass());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}			
	/** Check if the given object is of type or of subclass of s*/
	public static boolean isTypeOf(Object obj, String s)
	{
		try {
			return Class.forName(s).isAssignableFrom(obj.getClass());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static boolean isTypeOf(Class<?> cl, String s)
	{
		try {
			return Class.forName(s).isAssignableFrom(cl);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	/** Check if the given object is a subclass of any class name in list*/
	public static boolean isMemberOf(Object obj, List<String>l)
	{
		for(String s : l)
		{
			if(isTypeOf(obj,s))
				return true;
		}
		return false;
	}
	/** Check if the given objectName is a subclass of any class name in list*/
	public static boolean isMemberOf(String objName, List<String>l)
	{
		Class<?> objClass=null;
		try {
			objClass = Class.forName(objName);
		} catch (ClassNotFoundException e) {e.printStackTrace();}
		
		if(objClass==null)
			return false;
		
		for(String s : l)
		{
			if(isTypeOf(objClass,s))
				return true;
		}
		return false;
	}
	
}
