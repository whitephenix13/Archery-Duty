package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.TypeObject;
import loading.LoaderItem;
import partie.deplacement.entity.Mouvement_entity.TypeMouvEntitie;
import partie.entitie.Entity;

public class ImagesMonstre extends LoaderItem{

	public static String path ="resources/entitie/monstre/";
	ArrayList<Image> im_SP_attente= new ArrayList<Image>();
	ArrayList<Image> im_SP_marche= new ArrayList<Image>();
	ArrayList<Image> im_SP_saut= new ArrayList<Image>();
	
	public ImagesMonstre()
	{
		super("Image monstre");
	}
	
	@Override
	public void run()
	{
		percentage=0;
		if(alreadyLoaded){
			percentage=100;
			return;
		}
		
		for(int i=0; i<2; ++i)
			im_SP_attente.add(getIm(path+"spirel/attente/"+i+".gif",true));
		percentage =(int)(2*100.0/8);

		for(int i=0; i<4; ++i)
			im_SP_marche.add(getIm(path+"spirel/marche/"+i+".gif",true));
		percentage =(int)(6*100.0/8);
				
		for(int i=0; i<2; ++i)
			im_SP_saut.add(getIm(path+"spirel/attente/"+i+".gif",true));
		percentage =100;
		alreadyLoaded=true;
		
	}
	/**
	 * Renvoie la bonne image pour un monstre donné
	 * 
	 * @param monstre, le monstre a afficher
	 * 
	 * @return l'image a afficher
	 */
	public ArrayList<Image> getImage(Entity monstre)
	{
		ArrayList<Image> im = new ArrayList<Image>();
//				im.add(this.im_electrique_aura.get(fleche.anim));

		if (TypeObject.isTypeOf(monstre, TypeObject.SPIREL))
		{
			if(monstre.getDeplacement().IsDeplacement(TypeMouvEntitie.Attente))
				im.add(im_SP_attente.get(monstre.getAnim()));
			else if (monstre.getDeplacement().IsDeplacement(TypeMouvEntitie.Marche))
				im.add(im_SP_marche.get(monstre.getAnim()));
			else if(monstre.getDeplacement().IsDeplacement(TypeMouvEntitie.Saut))
				im.add(im_SP_saut.get(monstre.getAnim()));
		}
		return im;
	}


}
