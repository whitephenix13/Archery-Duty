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

public class LoaderMusicBruitage extends LoaderItem implements InterfaceConstantes{
	
	Map<String,Clip> mapClips = new HashMap<String,Clip>();
	
	public LoaderMusicBruitage() {
		super("bruitages");
	}
	@Override
	public void run() {
		if(alreadyLoaded)
			return;
		addToMap(bruitagesArray);		
		percentage = 100;
		alreadyLoaded=true;
	}
	
	public void run(String media_categorie, String filename) {
		URL url = getClass().getClassLoader().getResource("resources/musique/"+filename+".wav");
		if(url!=null) { 
			addToMap(filename);
		}		
	}
	

	private void addToMap(String nom)
	{
		Clip c;
		AudioInputStream audio; 
		
		try {
			if(MusicBruitage.me!=null && !mapClips.containsKey(nom)){
				audio= AudioSystem.getAudioInputStream(
						getClass().getClassLoader().getResource("resources/musique/"+nom+".wav"));
				c=AudioSystem.getClip();
				c.open(audio);
				mapClips.put(nom,c);
			}

		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}

	}
	private void addToMap(String[] noms)
	{
		for(String s : noms)
		{
			addToMap(s);
		}
	}
}
