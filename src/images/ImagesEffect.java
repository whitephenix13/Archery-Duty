package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.effects.Effect.EffectCollisionEnum;
import partie.mouvement.effect.Mouvement_effect.MouvEffectEnum;

public class ImagesEffect extends ImagesContainer{

	private static String path ="resources/projectile/fleches/effects/";
	
	public static enum ImEffectInfo implements ImageInfo{SLOWDOWN};
	
	ArrayList<Image> im_vent_effect= new ArrayList<Image>();
	ArrayList<Image> im_grappin_effect= new ArrayList<Image>();
	ArrayList<Image> im_lumiere_effect= new ArrayList<Image>();
	ArrayList<Image> im_ombre_effect= new ArrayList<Image>();
	
	//Ground effect at index 0, collisionEffect at index 1
	ArrayList<Image>[] im_feu_effect= (ArrayList<Image>[]) new ArrayList[2];
	ArrayList<Image>[] im_glace_effect= (ArrayList<Image>[]) new ArrayList[2];
	ArrayList<Image>[] im_roche_effect=(ArrayList<Image>[]) new ArrayList[2];
	ArrayList<Image>[] im_electrique_idle_effect= (ArrayList<Image>[]) new ArrayList[2];
	ArrayList<Image> im_electrique_appear_effect=  new ArrayList<Image>();
	ArrayList<Image> im_electrique_split_effect=  new ArrayList<Image>();

	ArrayList<Image> im_trou_noir_effect= new ArrayList<Image>();
	ArrayList<Image> im_explosive_effect= new ArrayList<Image>();

	Image im_slowdown =null;
	public ImagesEffect()
	{
		super("Image effect");
	}
	
	@Override
	public void run()
	{
		int count = 0;
		int nb= 77;
		percentage = 0;
		if(alreadyLoaded){
			percentage = 100;
			return;
		}
		if(im_slowdown==null)
			im_slowdown=getIm("resources/slowDownFX.png",true);
		count ++;
		percentage = (int)(100.0*count/nb);

		for(int i=0; i<4; ++i){
			im_vent_effect.add(getIm(path+"vent/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);

		for(int i=0; i<1; ++i){
			im_grappin_effect.add(getIm(path+"grappin/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);

		for(int i=0; i<5; ++i){
			im_lumiere_effect.add(getIm(path+"lumiere/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);

		for(int i=0; i<4; ++i){
			im_ombre_effect.add(getIm(path+"ombre/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);

		//MATERIELLE
		//TYPE 0 
		for(int i=0;i<2;i++)
		{
			im_feu_effect[i]=new ArrayList<Image>();
			im_glace_effect[i]=new ArrayList<Image>();
			im_roche_effect[i]=new ArrayList<Image>();
			im_electrique_idle_effect[i]=new ArrayList<Image>();
		}
		for(int i=0; i<6; ++i){
			im_feu_effect[0].add(getIm(path+"feu/0/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);
		for(int i=0; i<5; ++i){
			im_glace_effect[0].add(getIm(path+"glace/0/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);
		for(int i=0; i<5; ++i){
			im_roche_effect[0].add(getIm(path+"roche/0/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);
		for(int i=0; i<6; ++i){
			im_electrique_idle_effect[0].add(getIm(path+"electrique/0/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);

		//TYPE 1
		
		for(int i=0; i<4; ++i){
			im_feu_effect[1].add(getIm(path+"feu/1/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);
		for(int i=0; i<4; ++i){
			im_glace_effect[1].add(getIm(path+"glace/1/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);
		for(int i=0; i<4; ++i){
			im_roche_effect[1].add(getIm(path+"roche/1/"+i+".png",true));
			count ++;
		}

		percentage = (int)(100.0*count/nb);
		for(int i=0; i<1; ++i){
			im_electrique_appear_effect.add(getIm(path+"electrique/1/appear/"+i+".png",true));
			count ++;
		}
		for(int i=0; i<6; ++i){
			im_electrique_idle_effect[1].add(getIm(path+"electrique/1/idle/"+i+".png",true));
			count ++;
		}
		for(int i=0; i<4; ++i){
			im_electrique_split_effect.add(getIm(path+"electrique/1/split/"+i+".png",true));
			count ++;
		}
		
		//DESTRUCTRICE
		for(int i=0; i<5; ++i){
			im_trou_noir_effect.add(getIm(path+"trou_noir/"+i+".png",true));
			count ++;
		}
		percentage = (int)(100.0*count/nb);
		for(int i=0; i<12; ++i){
			im_explosive_effect.add(getIm(path+"explosive/"+i+".png",true));
			count ++;
		}
		
		percentage = 100;
		alreadyLoaded=true;
	}
	
	/***
	 * objType: null
	 * info1 : {@link ImEffectInfo}
	 * info2 : null
	 */
	@Override
	public Image getImage(ObjectType objType, ImageInfo info1,ImageInfo info2)
	{
		if(info1.equals(ImEffectInfo.SLOWDOWN))
			return im_slowdown;
		else 
			return null;
	}
	/***
	 * objType: null
	 * info1 : {@link MouvEffectEnum}}
	 * info2 : {@link EffectCollisionEnum}}
	 */
	@Override
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2, int mouv_index)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if (info1.equals(MouvEffectEnum.VENT_IDLE))
			im.add(im_vent_effect.get(mouv_index));
		if (info1.equals(MouvEffectEnum.GRAPPIN_IDLE))
			im.add(im_grappin_effect.get(mouv_index));
		if (info1.equals(MouvEffectEnum.LUMIERE_IDLE))
			im.add(im_lumiere_effect.get(mouv_index));
		if (info1.equals(MouvEffectEnum.OMBRE_IDLE))
			im.add(im_ombre_effect.get(mouv_index));
		
		if (info1.equals(MouvEffectEnum.FEU_IDLE))
			if(info2.equals(EffectCollisionEnum.GROUND))
				im.add(im_feu_effect[0].get(mouv_index));
			else
				im.add(im_feu_effect[1].get(mouv_index));			
		if (info1.equals(MouvEffectEnum.ELECTRIQUE_IDLE))
			if(info2.equals(EffectCollisionEnum.GROUND))
				im.add(im_electrique_idle_effect[0].get(mouv_index));
			else
				im.add(im_electrique_idle_effect[1].get(mouv_index));
		
		if (info1.equals(MouvEffectEnum.ELECTRIQUE_APPEAR))
			im.add(im_electrique_appear_effect.get(mouv_index));
		
		if (info1.equals(MouvEffectEnum.ELECTRIQUE_SPLIT))
			im.add(im_electrique_split_effect.get(mouv_index));
		
		if (info1.equals(MouvEffectEnum.GLACE_IDLE))
			if(info2.equals(EffectCollisionEnum.GROUND))
				im.add(im_glace_effect[0].get(mouv_index));
			else
				im.add(im_glace_effect[1].get(mouv_index));
		if (info1.equals(MouvEffectEnum.ROCHE_IDLE))
			if(info2.equals(EffectCollisionEnum.GROUND))
				im.add(im_roche_effect[0].get(mouv_index));
			else
				im.add(im_roche_effect[1].get(mouv_index));
		
		if (info1.equals(MouvEffectEnum.TROU_NOIR_IDLE))
			im.add(im_trou_noir_effect.get(mouv_index));
		if (info1.equals(MouvEffectEnum.EXPLOSIVE_IDLE))
			im.add(im_explosive_effect.get(mouv_index));
		
		return im;
		
	}



}