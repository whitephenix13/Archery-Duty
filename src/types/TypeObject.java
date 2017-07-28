package types;

import java.util.ArrayList;
import java.util.List;

import collision.Collidable;
import effects.Effect;
import effects.Electrique_effect;
import effects.Explosive_effect;
import effects.Feu_effect;
import effects.Glace_effect;
import effects.Grappin_effect;
import effects.Lumiere_effect;
import effects.Ombre_effect;
import effects.Roche_effect;
import effects.Trou_noir_effect;
import effects.Vent_effect;
import fleches.Fleche;
import fleches.destructrice.Destructrice;
import fleches.destructrice.Fleche_bogue;
import fleches.destructrice.Fleche_explosive;
import fleches.destructrice.Fleche_foudre;
import fleches.destructrice.Fleche_trou_noir;
import fleches.materielle.Fleche_electrique;
import fleches.materielle.Fleche_feu;
import fleches.materielle.Fleche_glace;
import fleches.materielle.Fleche_roche;
import fleches.materielle.Materielle;
import fleches.rusee.Fleche_auto_teleguidee;
import fleches.rusee.Fleche_cac;
import fleches.rusee.Fleche_retard;
import fleches.rusee.Fleche_v_fleche;
import fleches.rusee.Rusee;
import fleches.sprirituelle.Fleche_grappin;
import fleches.sprirituelle.Fleche_lumiere;
import fleches.sprirituelle.Fleche_ombre;
import fleches.sprirituelle.Fleche_vent;
import fleches.sprirituelle.Spirituelle;
import monstre.Monstre;
import monstre.Spirel;
import monstre.TirMonstre;
import monstre.TirSpirel;
import personnage.Heros;

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
		
		public static String ENTITIE = Entitie.class.getName();
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
	
				//can add all different type of fleche if needed 
			public static String TIR_MONSTRE = TirMonstre.class.getName();
				public static String TIR_SPIREL = TirSpirel.class.getName();
	
	
	/** Check if the given object is of type or of subclass of obj2. Use exactMatch=true to check for same class*/
	public static boolean isTypeOf(Object obj, Object obj2,boolean exactMatch)
	{
		if(exactMatch)
			return obj.getClass().getName().equals(obj2.getClass().getName());
		try {
			return Class.forName(obj2.getClass().getName()).isInstance(obj);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}			
	/** Check if the given object is of type or of subclass of s*/
	public static boolean isTypeOf(Object obj, String s)
	{
		try {
			return Class.forName(s).isInstance(obj);
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
}
