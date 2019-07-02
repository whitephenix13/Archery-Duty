package images;

import java.awt.Image;
import java.util.ArrayList;

import loading.LoaderItem;
import partie.deplacement.entity.Mouvement_entity.TypeMouvEntitie;
import partie.entitie.heros.Heros;

public class ImagesHeros extends LoaderItem{
	
	public static String path ="resources/entitie/heros/";
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
		super("Image heros");
	}
	
	@Override
	public void run()
	{
		percentage=0;
		if(alreadyLoaded){
			percentage=100;
			return;
		}
		
		int numberOfItems = 76;
		int currentNbItems = 0 ;
		
		for(int i=0; i<4;++i)
			attente.add(getIm(path+"attente/"+i+".png",true));
		
		currentNbItems= 4;
		percentage = (int)(currentNbItems*100.0/numberOfItems);
		for(int i=0; i<2;++i)
			glissade.add(getIm(path+"glissade/"+i+".png",true));
		
		currentNbItems+= 2;
		percentage = (int)(currentNbItems*100.0/numberOfItems);

		for(int i=0; i<4;++i)
			accroche.add(getIm(path+"accroche/"+i+".png",true));
		
		currentNbItems+= 4;
		percentage = (int)(currentNbItems*100.0/numberOfItems);

		for(int i=0; i<6;++i)
			saut.add(getIm(path+"saut/"+i+".png",true));
		
		currentNbItems+= 6;
		percentage = (int)(currentNbItems*100.0/numberOfItems);

		for(int i=0; i<8;++i){
			marche.add(getIm(path+"marche/"+i+".png",true));
			course.add(getIm(path+"course/"+i+".png",true));
		}
		
		currentNbItems+= 8;
		percentage = (int)(currentNbItems*100.0/numberOfItems);

		for(int i=0; i<10;++i){
			tir_back_arm.add(getIm(path+"tir/Back_arm/"+i+".png",true));
			tir_body.add(getIm(path+"tir/Body/"+i+".png",true));
			tir_front_arm.add(getIm(path+"tir/Front_arm/"+i+".png",true));
			tir_head.add(getIm(path+"tir/Head/"+i+".png",true));
		}
		
		percentage = 100;
		alreadyLoaded=true;
	}

	public ArrayList<Image> getImages(Heros heros)
	{
		ArrayList<Image> l = new ArrayList<Image>();
		if(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Attente))
			l.add(attente.get(heros.getAnim()));
		
		else if(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Glissade))
			l.add(glissade.get(heros.getAnim()));
		
		else if(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Accroche))
			l.add(accroche.get(heros.getAnim()));
		
		else if(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Saut))
			l.add(saut.get(heros.getAnim()));

		else if(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Marche))
			l.add(marche.get(heros.getAnim()));

		else if(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Course))
			l.add(course.get(heros.getAnim()));

		else if(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Tir))
		{
			l.add(tir_body.get(heros.getAnim()));
			l.add(tir_back_arm.get(heros.getAnim()));
			l.add(tir_head.get(heros.getAnim()));
			l.add(tir_front_arm.get(heros.getAnim()));
		}
		else
		{
			throw new IllegalArgumentException("Heros: GetImages deplacement inconnu "+heros.getDeplacement().getClass().getName() +" "+heros.getAnim() );
		}
		return l;
	}


}
