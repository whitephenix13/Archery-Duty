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
	Image 			 im_slowdown =null;
	public ImagesEffect()
	{
	}
	
	@Override
	public void loadMedia()
	{
		if(mediaLoaded)
			return;
		for(int i=0; i<4; ++i){
			im_vent_effect.add(getIm("resources/fleches/effects/vent/"+i+".png",true));
		}
		setPercentage((int)(400.0/6));
		for(int i=0; i<1; ++i){
			im_grappin_effect.add(getIm("resources/fleches/effects/grappin/"+i+".png",true));
		}
		setPercentage((int)(500.0/6));
		im_slowdown=getIm("resources/slowDownFx.png",true);

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
		}
		return im;
		
	}

	@Override
	public void loadMedia(String media_categorie, String filename) {
		// TODO Auto-generated method stub
		
	}


}