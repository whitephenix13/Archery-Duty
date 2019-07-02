package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.TypeObject;
import loading.LoaderItem;
import partie.deplacement.projectile.Mouvement_projectile.TypeTir;
import partie.projectile.Projectile;

public class ImagesTirMonstre extends LoaderItem{
	
	private static String path ="resources/projectile/tirMonstre/";
	ArrayList<Image> im_SP_tir= new ArrayList<Image>();
	
	public ImagesTirMonstre()
	{
		super("Image tir monstre");
	}
	
	@Override
	public void run()
	{
		percentage=0;
		if(alreadyLoaded){
			percentage=100;
			return;
		}
		
		for(int i=0; i<3; ++i)
			im_SP_tir.add(getIm(path+"spirel/"+i+".png",true));
		percentage = 100;;
		alreadyLoaded=true;
	}
	public ArrayList<Image> getImage(Projectile tir)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if(TypeObject.isTypeOf(tir, TypeObject.TIR_SPIREL))
		{
			if(tir.getDeplacement().IsDeplacement(TypeTir.T_normal))
				im.add(im_SP_tir.get(tir.getAnim()));
		}

		return im;
	}


}
