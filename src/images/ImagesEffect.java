package images;

import java.awt.Image;
import java.util.ArrayList;

import effects.Effect;
import fleches.Fleche;
import loading.LoadMediaThread;
import loading.OnLoadingCallback;

public class ImagesEffect extends LoadMediaThread{

	public static String SLOWDOWN = "slowdown";
	ArrayList<Image> im_vent_effect= new ArrayList<Image>();
	ArrayList<Image> im_grappin_effect= new ArrayList<Image>();
	ArrayList<Image> im_lumiere_effect= new ArrayList<Image>();
	ArrayList<Image> im_ombre_effect= new ArrayList<Image>();
	
	@SuppressWarnings("unchecked")
	ArrayList<Image>[] im_feu_effect= (ArrayList<Image>[]) new ArrayList[2];
	@SuppressWarnings("unchecked")
	ArrayList<Image>[] im_glace_effect= (ArrayList<Image>[]) new ArrayList[2];
	@SuppressWarnings("unchecked")
	ArrayList<Image>[] im_roche_effect=(ArrayList<Image>[]) new ArrayList[2];
	@SuppressWarnings("unchecked")
	ArrayList<Image>[] im_electrique_effect= (ArrayList<Image>[]) new ArrayList[2];

	ArrayList<Image> im_trou_noir_effect= new ArrayList<Image>();

	Image im_slowdown =null;
	public ImagesEffect()
	{
	}
	
	@Override
	public void loadMedia()
	{
		int count = 0;
		int nb= 15;
		if(mediaLoaded)
			return;
		if(im_slowdown==null)
			im_slowdown=getIm("resources/slowDownFX.png",true);
		count ++;
		setPercentage((int)(100.0*count/nb));

		for(int i=0; i<4; ++i){
			im_vent_effect.add(getIm("resources/fleches/effects/vent/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));

		for(int i=0; i<1; ++i){
			im_grappin_effect.add(getIm("resources/fleches/effects/grappin/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));

		for(int i=0; i<5; ++i){
			im_lumiere_effect.add(getIm("resources/fleches/effects/lumiere/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));

		for(int i=0; i<4; ++i){
			im_ombre_effect.add(getIm("resources/fleches/effects/ombre/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));

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
			im_feu_effect[0].add(getIm("resources/fleches/effects/feu/0/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));
		for(int i=0; i<5; ++i){
			im_glace_effect[0].add(getIm("resources/fleches/effects/glace/0/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));
		for(int i=0; i<10; ++i){
			im_roche_effect[0].add(getIm("resources/fleches/effects/roche/0/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));
		for(int i=0; i<6; ++i){
			im_electrique_effect[0].add(getIm("resources/fleches/effects/electrique/0/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));

		//TYPE 1
		
		for(int i=0; i<4; ++i){
			im_feu_effect[1].add(getIm("resources/fleches/effects/feu/1/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));
		for(int i=0; i<4; ++i){
			im_glace_effect[1].add(getIm("resources/fleches/effects/glace/1/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));
		for(int i=0; i<4; ++i){
			im_roche_effect[1].add(getIm("resources/fleches/effects/roche/1/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));

		setPercentage((int)(100.0*count/nb));
		for(int i=0; i<4; ++i){
			im_electrique_effect[1].add(getIm("resources/fleches/effects/electrique/1/"+i+".png",true));
			count ++;
		}
		
		//DESTRUCTRICE
		for(int i=0; i<5; ++i){
			im_trou_noir_effect.add(getIm("resources/fleches/effects/trou_noir/"+i+".png",true));
			count ++;
		}
		setPercentage((int)(100.0*count/nb));
		setPercentage(100);
		mediaLoaded=true;
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
		if(!effect.name.equals(""))
		{
			if (effect.name.equals(Fleche.SPIRITUELLE.VENT))
				im.add(im_vent_effect.get(effect.anim));
			if (effect.name.equals(Fleche.SPIRITUELLE.GRAPPIN))
				im.add(im_grappin_effect.get(effect.anim));
			if (effect.name.equals(Fleche.SPIRITUELLE.LUMIERE))
				im.add(im_lumiere_effect.get(effect.anim));
			if (effect.name.equals(Fleche.SPIRITUELLE.OMBRE))
				im.add(im_ombre_effect.get(effect.anim));
			
			if (effect.name.equals(Fleche.MATERIELLE.FEU))
				im.add(im_feu_effect[effect.typeEffect].get(effect.anim));
			if (effect.name.equals(Fleche.MATERIELLE.ELECTRIQUE))
				im.add(im_electrique_effect[effect.typeEffect].get(effect.anim));
			if (effect.name.equals(Fleche.MATERIELLE.GLACE))
				im.add(im_glace_effect[effect.typeEffect].get(effect.anim));
			if (effect.name.equals(Fleche.MATERIELLE.ROCHE))
				im.add(im_roche_effect[effect.typeEffect].get(effect.anim));
			
			if (effect.name.equals(Fleche.DESTRUCTRICE.TROU_NOIR))
				im.add(im_trou_noir_effect.get(effect.anim));
		}
		return im;
		
	}

	@Override
	public void loadMedia(String media_categorie, String filename) {
		// TODO Auto-generated method stub
		
	}


}