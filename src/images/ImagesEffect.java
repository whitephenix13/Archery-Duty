package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.TypeObject;
import loading.LoaderItem;
import partie.effects.Effect;

public class ImagesEffect extends LoaderItem{

	private static String path ="resources/projectile/fleches/effects/";
	public static String SLOWDOWN = "slowdown";
	ArrayList<Image> im_vent_effect= new ArrayList<Image>();
	ArrayList<Image> im_grappin_effect= new ArrayList<Image>();
	ArrayList<Image> im_lumiere_effect= new ArrayList<Image>();
	ArrayList<Image> im_ombre_effect= new ArrayList<Image>();
	
	//Ground effect at index 0, collisionEffect at index 1
	@SuppressWarnings("unchecked")
	ArrayList<Image>[] im_feu_effect= (ArrayList<Image>[]) new ArrayList[2];
	@SuppressWarnings("unchecked")
	ArrayList<Image>[] im_glace_effect= (ArrayList<Image>[]) new ArrayList[2];
	@SuppressWarnings("unchecked")
	ArrayList<Image>[] im_roche_effect=(ArrayList<Image>[]) new ArrayList[2];
	@SuppressWarnings("unchecked")
	ArrayList<Image>[] im_electrique_effect= (ArrayList<Image>[]) new ArrayList[2];

	ArrayList<Image> im_trou_noir_effect= new ArrayList<Image>();
	ArrayList<Image> im_explosive_effect= new ArrayList<Image>();

	Image im_slowdown =null;
	public ImagesEffect()
	{
	}
	
	@Override
	public void run()
	{
		int count = 0;
		int nb= 15;
		if(alreadyLoaded)
			return;
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
			im_electrique_effect[i]=new ArrayList<Image>();
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
			im_electrique_effect[0].add(getIm(path+"electrique/0/"+i+".png",true));
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

		percentage = (int)(100.0*count/nb);
		for(int i=0; i<4; ++i){
			im_electrique_effect[1].add(getIm(path+"electrique/1/"+i+".png",true));
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
	
	public Image getImage(String s)
	{
		if(s.equals(SLOWDOWN))
			return im_slowdown;
		else 
			return null;
	}
	public ArrayList<Image> getImage(Effect effect)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if (TypeObject.isTypeOf(effect, TypeObject.VENT_EFF))
			im.add(im_vent_effect.get(effect.getAnim()));
		if (TypeObject.isTypeOf(effect, TypeObject.GRAPPIN_EFF))
			im.add(im_grappin_effect.get(effect.getAnim()));
		if (TypeObject.isTypeOf(effect, TypeObject.LUMIERE_EFF))
			im.add(im_lumiere_effect.get(effect.getAnim()));
		if (TypeObject.isTypeOf(effect, TypeObject.OMBRE_EFF))
			im.add(im_ombre_effect.get(effect.getAnim()));
		
		if (TypeObject.isTypeOf(effect, TypeObject.FEU_EFF))
			im.add(im_feu_effect[effect.groundEffect?0:1].get(effect.getAnim()));
		if (TypeObject.isTypeOf(effect, TypeObject.ELECTRIQUE_EFF))
			im.add(im_electrique_effect[effect.groundEffect?0:1].get(effect.getAnim()));
		if (TypeObject.isTypeOf(effect, TypeObject.GLACE_EFF))
			im.add(im_glace_effect[effect.groundEffect?0:1].get(effect.getAnim()));
		if (TypeObject.isTypeOf(effect, TypeObject.ROCHE_EFF))
			im.add(im_roche_effect[effect.groundEffect?0:1].get(effect.getAnim()));
		
		if (TypeObject.isTypeOf(effect, TypeObject.TROU_NOIR_EFF))
			im.add(im_trou_noir_effect.get(effect.getAnim()));
		if (TypeObject.isTypeOf(effect, TypeObject.EXPLOSIVE_EFF))
			im.add(im_explosive_effect.get(effect.getAnim()));
		
		return im;
		
	}



}