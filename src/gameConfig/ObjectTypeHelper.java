package gameConfig;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

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
import partie.projectile.fleches.destructrice.Fleche_barrage;
import partie.projectile.fleches.destructrice.Fleche_explosive;
import partie.projectile.fleches.destructrice.Fleche_faucon;
import partie.projectile.fleches.destructrice.Fleche_trou_noir;
import partie.projectile.fleches.materielle.Fleche_electrique;
import partie.projectile.fleches.materielle.Fleche_feu;
import partie.projectile.fleches.materielle.Fleche_glace;
import partie.projectile.fleches.materielle.Fleche_roche;
import partie.projectile.fleches.materielle.Materielle;
import partie.projectile.fleches.rusee.Fleche_absorption;
import partie.projectile.fleches.rusee.Fleche_leurre;
import partie.projectile.fleches.rusee.Fleche_marque_mortelle;
import partie.projectile.fleches.rusee.Fleche_ninja;
import partie.projectile.fleches.rusee.Rusee;
import partie.projectile.fleches.sprirituelle.Fleche_grappin;
import partie.projectile.fleches.sprirituelle.Fleche_lumiere;
import partie.projectile.fleches.sprirituelle.Fleche_ombre;
import partie.projectile.fleches.sprirituelle.Fleche_vent;
import partie.projectile.fleches.sprirituelle.Spirituelle;
import partie.projectile.tirMonstre.TirMonstre;
import partie.projectile.tirMonstre.TirSpirel;

public abstract class ObjectTypeHelper {
		
	public static enum ObjectType{
		COLLIDABLE,
			
			EFFECT,
				ELECTRIQUE_EFF,
				EXPLOSIVE_EFF,
				FEU_EFF,
				GLACE_EFF,
				GRAPPIN_EFF,
				LUMIERE_EFF,
				OMBRE_EFF,
				ROCHE_EFF,
				TROU_NOIR_EFF,
				VENT_EFF,
			
			BLOC,
			
			ENTITIE,
				HEROS,
				MONSTRE,
					SPIREL,
					
			PROJECTILE,
				FLECHE,
					DESTRUCTRICE,
						FAUCON,
						EXPLOSIVE,
						TROU_NOIR,
						BARRAGE,
					MATERIELLE,
						FEU,
						GLACE,
						ROCHE,
						ELECTRIQUE,
					RUSEE,
						MARQUE_MORTELLE,
						LEURRE,
						NINJA,
						ABSORPTION,
					SPRIRITUELLE,
						LUMIERE,
						OMBRE,
						VENT,
						GRAPPIN,
				TIR_MONSTRE,
					TIR_SPIREL
	};
	public static HashMap<ObjectType,Class<?>> objectTypeToClass = createMap();
	private static HashMap<ObjectType, Class<?>> createMap() {
		HashMap<ObjectType,Class<?>> myMap = new HashMap<ObjectType,Class<?>>();
		myMap.put(ObjectType.COLLIDABLE, Collidable.class);
		myMap.put(ObjectType.EFFECT, Effect.class);
		myMap.put(ObjectType.ELECTRIQUE_EFF, Electrique_effect.class);
		myMap.put(ObjectType.EXPLOSIVE_EFF , Explosive_effect.class);
		myMap.put(ObjectType.FEU_EFF , Feu_effect.class);
		myMap.put(ObjectType.GLACE_EFF , Glace_effect.class);
		myMap.put(ObjectType.GRAPPIN_EFF , Grappin_effect.class);
		myMap.put(ObjectType.LUMIERE_EFF , Lumiere_effect.class);
		myMap.put(ObjectType.OMBRE_EFF , Ombre_effect.class);
		myMap.put(ObjectType.ROCHE_EFF , Roche_effect.class);
		myMap.put(ObjectType.TROU_NOIR_EFF , Trou_noir_effect.class);
		myMap.put(ObjectType.VENT_EFF , Vent_effect.class);
		myMap.put(ObjectType.BLOC , Bloc.class);
		myMap.put(ObjectType.ENTITIE , Entity.class);
		myMap.put(ObjectType.HEROS , Heros.class);
		myMap.put(ObjectType.MONSTRE , Monstre.class);
		myMap.put(ObjectType.SPIREL , Spirel.class);
		myMap.put(ObjectType.PROJECTILE , Projectile.class);
		myMap.put(ObjectType.FLECHE , Fleche.class);
		myMap.put(ObjectType.DESTRUCTRICE , Destructrice.class);
		myMap.put(ObjectType.FAUCON,Fleche_faucon.class);
		myMap.put(ObjectType.EXPLOSIVE,Fleche_explosive.class);
		myMap.put(ObjectType.TROU_NOIR,Fleche_trou_noir.class);
		myMap.put(ObjectType.BARRAGE,Fleche_barrage.class);
		myMap.put(ObjectType.MATERIELLE , Materielle.class);
		myMap.put(ObjectType.FEU,Fleche_feu.class);
		myMap.put(ObjectType.ELECTRIQUE,Fleche_electrique.class);
		myMap.put(ObjectType.GLACE,Fleche_glace.class);
		myMap.put(ObjectType.ROCHE,Fleche_roche.class);
		myMap.put(ObjectType.RUSEE , Rusee.class);
		myMap.put(ObjectType.MARQUE_MORTELLE,Fleche_marque_mortelle.class);
		myMap.put(ObjectType.LEURRE,Fleche_leurre.class);
		myMap.put(ObjectType.NINJA,Fleche_ninja.class);
		myMap.put(ObjectType.ABSORPTION,Fleche_absorption.class);
		myMap.put(ObjectType.SPRIRITUELLE , Spirituelle.class);
		myMap.put(ObjectType.LUMIERE,Fleche_lumiere.class);
		myMap.put(ObjectType.OMBRE,Fleche_ombre.class);
		myMap.put(ObjectType.VENT,Fleche_vent.class);
		myMap.put(ObjectType.GRAPPIN,Fleche_grappin.class);
		myMap.put(ObjectType.TIR_MONSTRE , TirMonstre.class);
		myMap.put(ObjectType.TIR_SPIREL , TirSpirel.class);
		return myMap;
	}
	
	public static ObjectType getTypeObject(Object obj)
	{
		for(ObjectType t : objectTypeToClass.keySet())
		{
			if(objectTypeToClass.get(t).equals(obj.getClass()))
				return t;
		}
		return null;
	}
	
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
	/** Check if the given object is of type or of subclass of type*/
	public static boolean isTypeOf(Object obj, ObjectType type)
	{
		return objectTypeToClass.get(type).isAssignableFrom(obj.getClass());
	}
	public static boolean isTypeOf(Class<?> cl, ObjectType type)
	{
		return objectTypeToClass.get(type).isAssignableFrom(cl);
	}
	/** Check if the given object is a subclass of any typeObject in list*/
	public static boolean isMemberOf(Object obj, List<ObjectType>l)
	{
		for(ObjectType type : l)
		{
			if(isTypeOf(obj,type))
				return true;
		}
		return false;
	}
	/** Check if the given objectType is a subclass of any typeObject in list*/
	public static boolean isMemberOf(ObjectType type, List<ObjectType>l)
	{
		for(ObjectType typel : l)
		{
			if(isTypeOf(objectTypeToClass.get(type),typel))
				return true;
		}
		return false;
	}
	
}
