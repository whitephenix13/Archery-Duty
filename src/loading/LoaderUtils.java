package loading;

import java.lang.ref.WeakReference;

import gameConfig.InterfaceConstantes;
import menu.menuPrincipal.AbstractModelPrincipal;
import music.Music;
import music.MusicBruitage;
import partie.bloc.Bloc;
import partie.modelPartie.AbstractModelPartie;
import serialize.Serialize;

public class LoaderUtils {
	
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
	public static LoaderItem loadWorld(final String nomFichier, final AbstractModelPartie partie)
	{
		Serialize.loadPercentage=0;
		return new LoaderItem("Load world"){
			private int startPartieLoadPercentage = 0;
			@Override
			public void run()
			{
				Bloc[][] oldniveau = partie.monde.name.equals(nomFichier)? partie.monde.niveau :null;
				partie.monde = Serialize.charger(nomFichier,oldniveau);
				
				//Consider that loading is done at 95% from here 
				startPartieLoadPercentage=0;
				partie.startPartie(InterfaceConstantes.SPAWN_PROGRAMME);//SPAWN_ALEATOIRE, SPAWN_PROGRAMME
				startPartieLoadPercentage=100;
			}
			@Override
			public int getProgress()
			{
				return (int)Math.round(0.95*Serialize.loadPercentage+0.05*startPartieLoadPercentage);
			}
		};
	}
	
	public static LoaderItem waitForLoaderToEnd(final Loader loader)
	{
		return new LoaderItem("Wait for loader to end "){
			@Override
			public void run()
			{
				loader.waitToEnd(null);
			}
			@Override
			public int getProgress()
			{
				return loader.getProgress();
			}
		};
	}
	
	public static void waitForGarbageCollectorToEnd()
	{
		Object obj = new Object();
		WeakReference<Object> ref = new WeakReference<Object>(obj);
		obj = null;
		while(ref.get() != null)
		{
			System.gc();
			try {Thread.sleep(16);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	public static LoaderItem waitForGarbageCollectorToEndInALoader()
	{
		return new LoaderItem("Wait for GC"){
			private int percentage = 0;
			@Override
			public void run()
			{
				percentage=0;
				waitForGarbageCollectorToEnd();
				percentage=100;
			}
			@Override
			public int getProgress()
			{
				return percentage;
			}
		};
	}
	public static LoaderItem loadAllImagesAndSounds(final AbstractModelPrincipal princip, final AbstractModelPartie partie)
	{
		return new LoaderItem("Load all images and sounds "){
			@Override
			public void run()
			{
				//Load Heros, Monstre, TirMonstre, Fleche
				princip.imPrincipal.run();
				princip.imBackground.run();
				princip.imHeros.run();
				princip.imMonstre.run();
				princip.imTirMonstre.run();
				princip.imFleches.run();
				princip.imEffect.run();
				princip.imMonde.run();
				princip.imConditions.run();
				princip.imFlecheIcon.run();
				
				if(!InterfaceConstantes.IGNORE_SOUND){
					Music.me.loaderMusic.run();
	
					MusicBruitage.me.loaderMusicBruitage.run();
				}
			}
			@Override
			public int getProgress()
			{
				int res = (int) Math.round((
						princip.imPrincipal.getProgress()+
						princip.imBackground.getProgress()+
						princip.imHeros.getProgress() + 
						princip.imMonstre.getProgress() + 
						princip.imTirMonstre.getProgress() + 
						princip.imFleches.getProgress() + 
						princip.imEffect.getProgress() +
						princip.imMonde.getProgress() + 
						princip.imConditions.getProgress() +
						princip.imFlecheIcon.getProgress() +
						(InterfaceConstantes.IGNORE_SOUND?0:(Music.me.loaderMusic.getProgress() +MusicBruitage.me.loaderMusicBruitage.getProgress()))
						)/(InterfaceConstantes.IGNORE_SOUND?10.0:12.0)) ;
				return res;
			}
		};
	}
	
	public static LoaderItem load(final String media_type, final String media_categorie, final String filename, final AbstractModelPrincipal princip,final AbstractModelPartie partie )
	{
		return new LoaderItem("Loader single media"){
			private int percentage;
			private LoaderItem media;
			@Override
			public void run()
			{
				percentage=0;
				if(media_type.equals(MT_IMAGE)){
					if(media_categorie.equals(C_PRINCIPAL)){
						media=princip.imPrincipal;
						media.run();
						percentage=100;
					}
					else 
						throw new UnsupportedOperationException();
					/*else if(filename.equals(C_EFFECT)){
						
					}else if(filename.equals(C_FLECHE)){
						
					}else if(filename.equals(C_HEROS)){
						
					}else if(filename.equals(C_SPIREL)){
						
					}else if(filename.equals(C_TIR_SPIREL)){
						
					}*/
				}
				else if (media_type.equals(MT_SOUND))
				{
					if(media_categorie.equals(C_MUSIC))
					{
						media=Music.me.loaderMusic;
						Music.me.loaderMusic.run(media_categorie, filename);
						percentage=100;
					}
					else if(media_categorie.equals(C_BRUITAGE))
					{
						media=MusicBruitage.me.loaderMusicBruitage;
						MusicBruitage.me.loaderMusicBruitage.run(media_categorie, filename);
						percentage=100;
					}
				}						
			}
			@Override
			public int getProgress()
			{
				if(media==null)
					return 0;
				else
					return percentage; //do not use media.getProgress() as this is for global loading
			}
		};
		
	}
	
}
