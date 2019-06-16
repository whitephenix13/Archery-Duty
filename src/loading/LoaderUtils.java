package loading;

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
		
		return new LoaderItem(){
			@Override
			public void run()
			{
				Bloc[][] oldniveau = partie.monde.name.equals(nomFichier)? partie.monde.niveau :null;
				partie.monde = Serialize.charger(nomFichier,oldniveau);
			}
			@Override
			public int getProgress()
			{
				return Serialize.loadPercentage;
			}
		};
	}
	
	public static LoaderItem loadAllImagesAndSounds(final AbstractModelPrincipal princip, final AbstractModelPartie partie)
	{
		return new LoaderItem(){
			@Override
			public void run()
			{
				//Load Heros, Monstre, TirMonstre, Fleche
				princip.imPrincipal.run();
				partie.imHeros.run();
				partie.imMonstre.run();
				partie.imTirMonstre.run();
				partie.imFleches.run();
				partie.imEffect.run();
				partie.imMonde.run();
				partie.imConditions.run();
				partie.imFlecheIcon.run();
				
				Music.me.loaderMusic.run();

				MusicBruitage.me.loaderMusicBruitage.run();
			}
			@Override
			public int getProgress()
			{
				int res = (int) Math.round((
						princip.imPrincipal.getProgress()+
						partie.imHeros.getProgress() + 
						partie.imMonstre.getProgress() + 
						partie.imTirMonstre.getProgress() + 
						partie.imFleches.getProgress() + 
						partie.imEffect.getProgress() +
						partie.imMonde.getProgress() + 
						partie.imConditions.getProgress() +
						partie.imFlecheIcon.getProgress() +
						Music.me.loaderMusic.getProgress() +
						MusicBruitage.me.loaderMusicBruitage.getProgress()
						)/11.0) ;
				return res;
			}
		};
	}
	
	public static LoaderItem load(final String media_type, final String media_categorie, final String filename, final AbstractModelPrincipal princip,final AbstractModelPartie partie )
	{
		return new LoaderItem(){
			private LoaderItem media;
			@Override
			public void run()
			{
				if(media_type.equals(MT_IMAGE)){
					if(media_categorie.equals(C_PRINCIPAL)){
						media=princip.imPrincipal;
						media.run();
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
					}
					else if(media_categorie.equals(C_BRUITAGE))
					{
						media=MusicBruitage.me.loaderMusicBruitage;
						MusicBruitage.me.loaderMusicBruitage.run(media_categorie, filename);
					}
				}						
			}
			@Override
			public int getProgress()
			{
				if(media==null)
					return 0;
				else
					return media.getProgress();
			}
		};
		
	}
	
}
