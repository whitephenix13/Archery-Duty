package images;

import java.awt.Image;
import java.util.ArrayList;

import deplacement.Mouvement_perso;
import loading.LoadMediaThread;
import loading.OnLoadingCallback;
import personnage.Heros;

public class ImagesHeros extends LoadMediaThread{
	
	ArrayList<Image> attente = new ArrayList<Image>(); // 4
	ArrayList<Image> glissade = new ArrayList<Image>(); // 2 
	ArrayList<Image> accroche = new ArrayList<Image>(); // 4
	ArrayList<Image> saut = new ArrayList<Image>(); // 6
	ArrayList<Image> marche = new ArrayList<Image>(); // 8
	ArrayList<Image> course = new ArrayList<Image>(); // 8

	ArrayList<Image> tir_back_arm = new ArrayList<Image>();
	ArrayList<Image> tir_body = new ArrayList<Image>();
	ArrayList<Image> tir_front_arm = new ArrayList<Image>();
	ArrayList<Image> tir_head = new ArrayList<Image>();

	public ImagesHeros()
	{
	}
	
	@Override
	public void loadMedia()
	{
		if(mediaLoaded)
			return;
		
		for(int i=0; i<4;++i)
			attente.add(getIm("resources/deplacement.Attente/"+i+".png",true));
		
		setPercentage((int) Math.round((4.0*100)/76));
		for(int i=0; i<2;++i)
			glissade.add(getIm("resources/deplacement.Glissade/"+i+".png",true));
		
		setPercentage((int) Math.round((6.0*100)/76));

		for(int i=0; i<4;++i)
			accroche.add(getIm("resources/deplacement.Accroche/"+i+".png",true));
		
		setPercentage((int) Math.round((16.0*100)/76));

		for(int i=0; i<6;++i)
			saut.add(getIm("resources/deplacement.Saut/"+i+".png",true));
		
		setPercentage((int) Math.round((20.0*100)/76));

		for(int i=0; i<8;++i){
			marche.add(getIm("resources/deplacement.Marche/"+i+".png",true));
			course.add(getIm("resources/deplacement.Course/"+i+".png",true));
		}
		
		setPercentage((int) Math.round((36.0*100)/76));

		for(int i=0; i<10;++i){
			tir_back_arm.add(getIm("resources/deplacement.Tir/Back_arm/"+i+".png",true));
			tir_body.add(getIm("resources/deplacement.Tir/Body/"+i+".png",true));
			tir_front_arm.add(getIm("resources/deplacement.Tir/Front_arm/"+i+".png",true));
			tir_head.add(getIm("resources/deplacement.Tir/Head/"+i+".png",true));
		}
		
		setPercentage(100);
		mediaLoaded=true;
	}

	public ArrayList<Image> getImages(Heros heros)
	{
		ArrayList<Image> l = new ArrayList<Image>();
		if(heros.deplacement.IsDeplacement(Mouvement_perso.attente))
			l.add(attente.get(heros.anim));
		
		else if(heros.deplacement.IsDeplacement(Mouvement_perso.glissade))
			l.add(glissade.get(heros.anim));
		
		else if(heros.deplacement.IsDeplacement(Mouvement_perso.accroche))
			l.add(accroche.get(heros.anim));
		
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

	@Override
	public void loadMedia(String media_categorie, String filename) {
		// TODO Auto-generated method stub
		
	}

}
