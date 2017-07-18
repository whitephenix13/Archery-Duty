package loading;

import java.util.ArrayList;
import java.util.List;

import menuPrincipal.AbstractModelPrincipal;
import music.Music;
import music.MusicBruitage;
import observer.Observable;
import partie.AbstractModelPartie;
import serialize.Serialize;

public class LoadAllMedias {

	//Media type
	public static String MT_SOUND = "SOUND"; 
	public static String MT_IMAGE = "IMAGE"; 
	public static String MT_WORLD = "WORLD"; 

	//Media Categorie
	public static String C_MUSIC = "MUSIC";
	public static String C_BRUITAGE = "BRUITAGE";
	
	public static String C_PRINCIPAL = "PRINCIPAL";
	public static String C_EFFECT = "EFFECT"; 
	public static String C_FLECHE = "FLECHE"; 
	public static String C_HEROS = "HEROS"; 
	public static String C_SPIREL = "SPIREL"; 
	public static String C_TIR_SPIREL = "TSPIREL";

	public CustomLoad mainLoader = null;
	private List<CustomLoad> pendingDownloads = new ArrayList<CustomLoad>();
	
	public class CustomLoad implements Runnable 
	{
		LoadMediaThread media = null;
		boolean loadedDone = false;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		public int getPercentage(){return 0;};
		public void printPercentage(){System.out.println(getPercentage()+" %");}

		
	}
	public void start()
	{
		mainLoader = new CustomLoad(){
			@Override
			public void run()
			{
				for(int i =0; i< pendingDownloads.size(); ++i)
					pendingDownloads.get(i).run();
				loadedDone=true;
			}
			@Override
			public int getPercentage()
			{
				int res = 0;
				int num = pendingDownloads.size();
				for(int i =0; i< num; ++i){
					res+=pendingDownloads.get(i).getPercentage();
				}
				res= (int) (((float) res )/ num);
				
				return res;
			}
		};
		
		Thread t = new Thread(mainLoader);
		t.start();
	}
	public void load(final AbstractModelPrincipal princip, final AbstractModelPartie partie)
	{
		CustomLoad custom = new CustomLoad(){
			@Override
			public void run()
			{
				//Load Heros, Monstre, TirMonstre, Fleche
				princip.imPrincipal.loadMedia();
				partie.imHeros.loadMedia();
				partie.imMonstre.loadMedia();
				partie.imTirMonstre.loadMedia();
				partie.imFleches.loadMedia();
				partie.imEffect.loadMedia();
				partie.imMonde.loadMedia();
				
				Music.me.loadMedia();
				MusicBruitage.me.loadMedia();
				loadedDone=true;
			}
			@Override
			public int getPercentage()
			{
				int res = (int) Math.round((
						princip.imPrincipal.getPercentage()+
						partie.imHeros.getPercentage() + 
						partie.imMonstre.getPercentage() + 
						partie.imTirMonstre.getPercentage() + 
						partie.imFleches.getPercentage() + 
						partie.imEffect.getPercentage() +
						partie.imMonde.getPercentage() + 
						Music.me.getPercentage() +
						MusicBruitage.me.getPercentage()
						)/9.0) ;
				return res;
			}
		};
		pendingDownloads.add(custom);
		
	}
	public void loadNiveau(final String nomFichier, final AbstractModelPartie partie)
	{
		Serialize.loadPercentage=0;

		CustomLoad custom = new CustomLoad(){
			@Override
			public void run()
			{
				partie.monde=Serialize.charger(nomFichier);
				loadedDone=true;
			}
			@Override
			public int getPercentage()
			{
				return Serialize.loadPercentage;
			}
		};
		pendingDownloads.add(custom);
	}
	public void load(final String media_type, final String media_categorie, final String filename, final AbstractModelPrincipal princip,final AbstractModelPartie partie )
	{
		CustomLoad custom = new CustomLoad(){
			@Override
			public void run()
			{
				if(media_type.equals(MT_IMAGE)){
					if(media_categorie.equals(C_PRINCIPAL)){
						media=princip.imPrincipal;
						princip.imPrincipal.loadMedia(C_PRINCIPAL, filename);
					}
					else if(filename.equals(C_EFFECT)){
						
					}else if(filename.equals(C_FLECHE)){
						
					}else if(filename.equals(C_HEROS)){
						
					}else if(filename.equals(C_SPIREL)){
						
					}else if(filename.equals(C_TIR_SPIREL)){
						
					}
				}
				else if (media_type.equals(MT_SOUND))
				{
					if(media_categorie.equals(C_MUSIC))
					{
						media=Music.me;
						Music.me.loadMedia(media_categorie, filename);
					}
					else if(media_categorie.equals(C_BRUITAGE))
					{
						media=MusicBruitage.me;
						MusicBruitage.me.loadMedia(media_categorie, filename);
					}
				}
				loadedDone=true;
						
			}
			@Override
			public int getPercentage()
			{
				if(media==null)
					return 0;
				else
					return media.getPercentage();
			}
		};
		
		pendingDownloads.add(custom);
				
		//WARNING NOT IMPLEMENTED FOR IMAGES, ONLY FOR MAIN MENU BACKGROUND 
	}
	public void wait(DisplayLoader modelToUpdate, Observable modelObsToUpdate,boolean setAllMediaLoaded)
	{
		while(!mainLoader.loadedDone)
		{
			if(modelToUpdate!=null)
				modelToUpdate.loadPercentage=mainLoader.getPercentage();
			//slow down loop with sleep 
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(modelObsToUpdate!=null){
				modelObsToUpdate.notifyMainObserver();
			}
			
			continue;
		}
		if(modelToUpdate!=null && setAllMediaLoaded)
			modelToUpdate.all_media_loaded=true;
	}
}
