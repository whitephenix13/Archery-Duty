package music;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import debug.Debug_time;
import loading.LoadMediaThread;
import option.Config;
import principal.InterfaceConstantes;

public class MusicBruitage extends LoadMediaThread implements InterfaceConstantes{

	public static MusicBruitage me;
	double gain;
	int nombreBruitage =0 ;
	Map<String,Clip> mapClips = new HashMap<String,Clip>();

	private MusicBruitage()
	{		
		gain = Config.bruitageVolume;
	}
	public static void init()
	{			
		if(me==null)
			me = new MusicBruitage();
	}
	
	@Override
	public void loadMedia() {
		if(mediaLoaded)
			return;
		addToMap(bruitagesArray);		
		setPercentage(100);
		mediaLoaded=true;
	}
	@Override
	public void loadMedia(String media_categorie, String filename) {
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
			if(me==null || !me.mapClips.containsKey(nom)){
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

	public static void volumeControl(double nouvGain)
	{
		me.gain=nouvGain;
		Config.bruitageVolume=me.gain;

		for(Clip c : me.mapClips.values())
		{
			FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float dB = (float) (Math.log(me.gain) / Math.log(10.0) * 30.0);
			gainControl.setValue(dB);
		}
	}

	public static void startBruitage(String typeBruitage)
	{
		volumeControl(me.gain);

		Clip c = me.mapClips.get(typeBruitage);
		if(!c.isRunning() || (c.isRunning() && (c.getFramePosition()>0)) )
		{
			c.stop();
			c.setMicrosecondPosition(0);
			while(c.isRunning())
			{}
			c.start();
		}

	}

}
