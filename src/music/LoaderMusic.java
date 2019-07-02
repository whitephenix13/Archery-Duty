package music;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import gameConfig.InterfaceConstantes;
import loading.LoaderItem;

public class LoaderMusic extends LoaderItem implements InterfaceConstantes{
	

	private boolean soundFound =false;
	private Map<String,Clip> mapClips = new HashMap<String,Clip>();
	
	public boolean isSoundFound(){return soundFound;}
	public Map<String,Clip> getMapClips(){return mapClips;}

	public LoaderMusic() {
		super("musics");
	}
	@Override
	public void run() {
		//fill the hash map
		if(alreadyLoaded)
			return;
		
		int num_music = 5 ;
		
		addToMap(musiqueOption);
		percentage = (int)1.0/num_music*100;
		addToMap(musiquePrincipal);
		percentage= (int)2.0/num_music*100;
		addToMap(musiquePartie);
		percentage= (int)3.0/num_music*100;
		addToMap(musiqueEditeur);
		percentage= (int)4.0/num_music*100;
		addToMap(musiqueSlow);
		
		percentage = 100;
		alreadyLoaded=true;
	}

	
	public void run(String media_categorie, String filename) {
		URL url = getClass().getClassLoader().getResource("resources/musique/"+filename+".wav");
		if(url!=null) { 
			addToMap(filename);
		}
		percentage = 100;
	}

	public void addToMap(String nom)
	{
		Clip c;
		AudioInputStream audio; 
		try {
			if(Music.me==null || mapClips.containsKey(nom))
				return;
			audio= AudioSystem.getAudioInputStream(
					getClass().getClassLoader().getResource("resources/musique/"+nom+".wav"));
			c=AudioSystem.getClip();
			c.open(audio);
			mapClips.put(nom,c);
			soundFound=true;
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
		catch(java.lang.IllegalArgumentException e)
		{
			soundFound=false;
		}

	}
	public void addToMap(String[] noms)
	{
		for(String s : noms)
		{
			addToMap(s);
		}
	}

}
