package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.deplacement.entity.Mouvement_entity.MouvEntityEnum;

public class ImagesHeros extends ImagesContainer{
	
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
	
	@Override
	public Image getImage(ObjectType objType, ImageInfo info1,ImageInfo info2)
	{
		return null;
	}
	/***
	 * objType: null
	 * info1 : {@link MouvEntityEnum}
	 * info2 : null
	 */
	@Override
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2, int anim)
	{
		ArrayList<Image> l = new ArrayList<Image>();
		if(info1.equals(MouvEntityEnum.ATTENTE))
			l.add(attente.get(anim));
		
		else if(info1.equals(MouvEntityEnum.GLISSADE))
			l.add(glissade.get(anim));
		
		else if(info1.equals(MouvEntityEnum.ACCROCHE))
			l.add(accroche.get(anim));
		
		else if(info1.equals(MouvEntityEnum.SAUT))
			l.add(saut.get(anim));

		else if(info1.equals(MouvEntityEnum.MARCHE))
			l.add(marche.get(anim));

		else if(info1.equals(MouvEntityEnum.COURSE))
			l.add(course.get(anim));

		else if(info1.equals(MouvEntityEnum.TIR))
		{
			l.add(tir_body.get(anim));
			l.add(tir_back_arm.get(anim));
			l.add(tir_head.get(anim));
			l.add(tir_front_arm.get(anim));
		}
		else
		{
			throw new IllegalArgumentException("Heros: GetImages deplacement inconnu "+info1 +" "+anim );
		}
		return l;
	}


}
