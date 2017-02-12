package personnage;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;

import deplacement.Mouvement_perso;

public class ImagesHeros {
	
	ArrayList<Image> attente = new ArrayList<Image>(); // 4
	ArrayList<Image> glissade = new ArrayList<Image>(); // 2 
	ArrayList<Image> saut = new ArrayList<Image>(); // 6
	ArrayList<Image> marche = new ArrayList<Image>(); // 8
	ArrayList<Image> course = new ArrayList<Image>(); // 8

	ArrayList<Image> tir_back_arm = new ArrayList<Image>();
	ArrayList<Image> tir_body = new ArrayList<Image>();
	ArrayList<Image> tir_front_arm = new ArrayList<Image>();
	ArrayList<Image> tir_head = new ArrayList<Image>();

	public ImagesHeros()
	{
		chargerImages();
	}

	public void chargerImages()
	{
		for(int i=0; i<4;++i)
			attente.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Attente/"+i+".png")));

		for(int i=0; i<2;++i)
			glissade.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Glissade/"+i+".png")));
		
		for(int i=0; i<6;++i)
			saut.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Saut/"+i+".png")));
		
		for(int i=0; i<8;++i){
			marche.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Marche/"+i+".png")));
			course.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Course/"+i+".png")));
		}
		for(int i=0; i<10;++i){
			tir_back_arm.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/Back_arm/"+i+".png")));
			tir_body.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/Body/"+i+".png")));
			tir_front_arm.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/Front_arm/"+i+".png")));
			tir_head.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/deplacement.Tir/Head/"+i+".png")));
		}
	}

	public ArrayList<Image> getImages(Heros heros)
	{
		ArrayList<Image> l = new ArrayList<Image>();
		if(heros.deplacement.IsDeplacement(Mouvement_perso.attente))
			l.add(attente.get(heros.anim));
		
		else if(heros.deplacement.IsDeplacement(Mouvement_perso.glissade))
			l.add(glissade.get(heros.anim));

		else if(heros.deplacement.IsDeplacement(Mouvement_perso.saut))
			l.add(saut.get(heros.anim));

		else if(heros.deplacement.IsDeplacement(Mouvement_perso.marche))
			l.add(marche.get(heros.anim));

		else if(heros.deplacement.IsDeplacement(Mouvement_perso.course))
			l.add(course.get(heros.anim));

		else if(heros.deplacement.IsDeplacement(Mouvement_perso.tir))
		{
			l.add(tir_body.get(heros.anim));
			l.add(tir_back_arm.get(heros.anim));
			l.add(tir_head.get(heros.anim));
			l.add(tir_front_arm.get(heros.anim));
		}
		else
		{
			throw new IllegalArgumentException("Heros: GetImages deplacement inconnu "+heros.deplacement.getClass().getName() +" "+heros.anim );
		}
		return l;
	}
}
