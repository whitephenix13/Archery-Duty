package types;

import java.awt.event.KeyEvent;

public class Touches {

public static final int ERROR = -10;
public static final int RIGHT_MOUSE = -1;	
public static final int LEFT_MOUSE = -2;	
public static final int MIDDLE_MOUSE = -3;	
	
public static int t_droite = KeyEvent.VK_D;
public static int t_gauche = KeyEvent.VK_Q;
public static int t_saut =KeyEvent.VK_SPACE;
public static int t_tir =LEFT_MOUSE;
public static int t_slow=KeyEvent.VK_Z;
public static int t_pause= KeyEvent.VK_ESCAPE;



public static String ToString(int _touche)
	{
		if(_touche==KeyEvent.VK_CONTROL)
			return("CTRL");
		if(_touche==KeyEvent.VK_SHIFT)
			return("SHIFT");
		if(_touche==KeyEvent.VK_SPACE)
			return("ESPACE");
		if(_touche==KeyEvent.VK_BACK_SPACE)
			return("RETOUR");
		if(_touche==KeyEvent.VK_ENTER)
			return("ENTER");
		if(_touche==KeyEvent.VK_ESCAPE)
			return("ESC");
		if(_touche==KeyEvent.VK_F1)
			return("F1");
		if(_touche==KeyEvent.VK_F2)
			return("F2");
		if(_touche==KeyEvent.VK_F3)
			return("F3");
		if(_touche==KeyEvent.VK_F4)
			return("F4");
		if(_touche==KeyEvent.VK_F5)
			return("F5");
		if(_touche==KeyEvent.VK_F6)
			return("F6");
		if(_touche==KeyEvent.VK_F7)
			return("F7");
		if(_touche==KeyEvent.VK_F8)
			return("F8");
		if(_touche==KeyEvent.VK_F9)
			return("F9");
		if(_touche==KeyEvent.VK_F10)
			return("F10");
		if(_touche==KeyEvent.VK_F11)
			return("F11");
		if(_touche==KeyEvent.VK_F12)
			return("F12");
		
		if(_touche==KeyEvent.VK_UP)
			return("FLECHE HAUT");
		if(_touche==KeyEvent.VK_DOWN)
			return("FLECHE BAS");
		if(_touche==KeyEvent.VK_RIGHT)
			return("FLECHE DROIT");
		if(_touche==KeyEvent.VK_LEFT)
			return("FLECHE GAUCHE");
		
		if(_touche==RIGHT_MOUSE)
			return("SOURIS DROITE");
		if(_touche==LEFT_MOUSE)
			return("SOURIS GAUCHE");
		if(_touche==MIDDLE_MOUSE)
			return("SOURIS MILIEU");
		
		if( ((_touche >= KeyEvent.VK_A) && (_touche <= KeyEvent.VK_Z)) || ((_touche >= KeyEvent.VK_0) && (_touche <= KeyEvent.VK_9)) )
		{
			return String.valueOf((char)_touche);
		}
		
		
		return "";
	}
/*
public static String touchesToString(String _touche)
{
	String touche= _touche.toLowerCase();
	if(touche.equals(Character.toString( Character.toLowerCase(cliqueDroit))))
	{
		return("Clique droit");
	}
	else if(touche.equals(Character.toString(Character.toLowerCase(cliqueGauche))))
	{
		return("Clique gauche");
	}
	else if(touche.equals(Character.toString(Character.toLowerCase(cliqueMilieu))))
	{
		return("Clique centre");
	}
	else if(touche.matches("^\\s*$"))
	{
		return("Espace");
	}
	else 
	{
		return(touche);
	}
}*/
/*
public static char stringToTouches(String nomTouche)
{
		return(nomTouche.charAt(0));
}

public boolean toucheCliqueDroit (Touches touches)
	{
		if(Character.toLowerCase(Touches.marcheDroite)	==Character.toLowerCase(cliqueDroit)|| 
			Character.toLowerCase(Touches.marcheGauche)	==Character.toLowerCase(cliqueDroit)|| 
			Character.toLowerCase(Touches.saut)			==Character.toLowerCase(cliqueDroit)||
			Character.toLowerCase(Touches.toucheTir)	==Character.toLowerCase(cliqueDroit)||
			Character.toLowerCase(Touches.toucheSlow)	==Character.toLowerCase(cliqueDroit)||
			Character.toLowerCase(Touches.touchePause)	==Character.toLowerCase(cliqueDroit))
		{
			return (true);
		}
		else
		{
			return(false);
		}
	}

public boolean toucheCliqueGauche (Touches touches)
	{
		if(Character.toLowerCase(Touches.marcheDroite)	==Character.toLowerCase(cliqueGauche)|| 
			Character.toLowerCase(Touches.marcheGauche)	==Character.toLowerCase(cliqueGauche)|| 
			Character.toLowerCase(Touches.saut)			==Character.toLowerCase(cliqueGauche)||
			Character.toLowerCase(Touches.toucheTir)	==Character.toLowerCase(cliqueGauche)||
			Character.toLowerCase(Touches.toucheSlow)	==Character.toLowerCase(cliqueGauche)||
			Character.toLowerCase(Touches.touchePause)	==Character.toLowerCase(cliqueGauche))
		{
			return (true);
		}
		else
		{
			return(false);
		}
	}
	
public boolean toucheCliqueMilieu (Touches touches)
	{
		if( Character.toLowerCase(Touches.marcheDroite)	==Character.toLowerCase(cliqueMilieu)|| 
			Character.toLowerCase(Touches.marcheGauche)	==Character.toLowerCase(cliqueMilieu)|| 
			Character.toLowerCase(Touches.saut)			==Character.toLowerCase(cliqueMilieu)||
			Character.toLowerCase(Touches.toucheTir)	==Character.toLowerCase(cliqueMilieu)||
			Character.toLowerCase(Touches.toucheSlow)	==Character.toLowerCase(cliqueMilieu)||
			Character.toLowerCase(Touches.touchePause)	==Character.toLowerCase(cliqueMilieu))
		{
			return (true);
		}
		else
		{
			return(false);
		}
	}
	*/
}
