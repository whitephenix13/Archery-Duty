package personnage;

import java.awt.Image;
import java.awt.Toolkit;

import deplacement.Mouvement_perso;

public class ImagesHeros {
	Image attente0;
	Image attente1;

	Image glissade0;
	Image glissade1;

	Image saut0;
	Image saut1;
	Image saut2;
	Image saut3;
	Image saut4;
	Image saut5;

	Image marche0;
	Image marche1;
	Image marche2;
	Image marche3;
	Image marche4;
	Image marche5;
	Image marche6;
	Image marche7;

	Image course0;
	Image course1;
	Image course2;
	Image course3;
	Image course4;
	Image course5;
	Image course6;
	Image course7;

	Image tir0;
	Image tir1;
	Image tir2;
	Image tir3;
	Image tir4;
	Image tir5;
	Image tir6;
	Image tir7;
	
	public ImagesHeros()
	{
		chargerImages();
	}
	
	public void chargerImages()
	{
		attente0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Attente/0.gif"));
		attente1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Attente/1.gif"));

		glissade0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Glissade/0.gif"));
		glissade1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Glissade/1.gif"));

		saut0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/0.gif"));
		saut1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/1.gif"));
		saut2= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/2.gif"));
		saut3= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/3.gif"));
		saut4= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/4.gif"));
		saut5= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/5.gif"));

		marche0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/0.gif"));
		marche1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/1.gif"));
		marche2= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/2.gif"));
		marche3= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/3.gif"));
		marche4= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/4.gif"));
		marche5= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/5.gif"));
		marche6= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/6.gif"));
		marche7= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/7.gif"));

		course0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/0.gif"));
		course1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/1.gif"));
		course2= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/2.gif"));
		course3= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/3.gif"));
		course4= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/4.gif"));
		course5= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/5.gif"));
		course6= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/6.gif"));
		course7= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/7.gif"));

		tir0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/0.gif"));
		tir1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/1.gif"));
		tir2= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/2.gif"));
		tir3= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/3.gif"));
		tir4= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/4.gif"));
		tir5= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/5.gif"));
		tir6= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/6.gif"));
		tir7= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/7.gif"));
	}
	
	public Image getImages(Heros heros)
	{
		if(heros.deplacement.IsDeplacement(Mouvement_perso.attente))
		{
			switch(heros.anim)
			{
			case 0: return(attente0);
			case 1: return(attente1);
			default: return(attente0);
			}
		}
		else if(heros.deplacement.IsDeplacement(Mouvement_perso.glissade))
		{
			switch(heros.anim)
			{
			case 0: return(glissade0);
			case 1: return(glissade1);
			default: return(glissade0);
			}
		}
		else if(heros.deplacement.IsDeplacement(Mouvement_perso.saut))
		{
			switch(heros.anim)
			{
			case 0: return(saut0);
			case 1: return(saut1);
			case 2: return(saut2);
			case 3: return(saut3);
			case 4: return(saut4);
			case 5: return(saut5);
			default: return(saut0);
			}
		}
		else if(heros.deplacement.IsDeplacement(Mouvement_perso.marche))
		{
			switch(heros.anim)
			{
			case 0: return(marche0);
			case 1: return(marche1);
			case 2: return(marche2);
			case 3: return(marche3);
			case 4: return(marche4);
			case 5: return(marche5);
			case 6: return(marche6);
			case 7: return(marche7);
			default: return(marche0);
			}
		}
		else if(heros.deplacement.IsDeplacement(Mouvement_perso.course))
		{
			switch(heros.anim)
			{
			case 0: return(course0);
			case 1: return(course1);
			case 2: return(course2);
			case 3: return(course3);
			case 4: return(course4);
			case 5: return(course5);
			case 6: return(course6);
			case 7: return(course7);
			default: return(course0);
			}
		}
		else if(heros.deplacement.IsDeplacement(Mouvement_perso.tir))
		{
			switch(heros.anim)
			{
			case 0: return(tir0);
			case 1: return(tir1);
			case 2: return(tir2);
			case 3: return(tir3);
			case 4: return(tir4);
			case 5: return(tir5);
			case 6: return(tir6);
			case 7: return(tir7);
			default: return(tir0);
			}
		}
		else
		{
			throw new IllegalArgumentException("Heros: GetImages deplacement inconnu "+heros.deplacement.getClass().getName() +" "+heros.anim );
		}
	}
}
