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
	Image 			 im_slowdown =null;
	public ImagesEffect()
	{
	}
	
	@Override
	public void loadMedia()
	{
		int nb= 15;
		if(mediaLoaded)
			return;
		if(im_slowdown==null)
			im_slowdown=getIm("resources/slowDownFX.png",true);
		setPercentage((int)(100.0/nb));

		for(int i=0; i<4; ++i){
			im_vent_effect.add(getIm("resources/fleches/effects/vent/"+i+".png",true));
		}
		setPercentage((int)(500.0/nb));
		for(int i=0; i<1; ++i){
			im_grappin_effect.add(getIm("resources/fleches/effects/grappin/"+i+".png",true));
		}
		setPercentage((int)(600.0/nb));

		for(int i=0; i<5; ++i){
			im_lumiere_effect.add(getIm("resources/fleches/effects/lumiere/"+i+".png",true));
			
		}
		setPercentage((int)(1100.0/nb));

		for(int i=0; i<4; ++i){
			im_ombre_effect.add(getIm("resources/fleches/effects/ombre/"+i+".png",true));
		}
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
		}
		return im;
		
	}

	@Override
	public void loadMedia(String media_categorie, String filename) {
		// TODO Auto-generated method stub
		
	}


}