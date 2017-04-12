package music;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import option.Config;
import principal.InterfaceConstantes;

public class MusicBruitage implements InterfaceConstantes{

	public static MusicBruitage me;
	double gain;
	int nombreBruitage =0 ;
	Map<String,Clip> mapClips = new HashMap<String,Clip>();

	public MusicBruitage()
	{
	}
	
	public void initMusicBruitage()
	{
		gain = Config.bruitageVolume;
		addToMap(bruitagesArray);

	}
	private void addToMap(String nom)
	{
		Clip c;
		AudioInputStream audio; 
		try {
			audio= AudioSystem.getAudioInputStream(
					getClass().getClassLoader().getResource("resources/musique/"+nom+".wav"));
			c=AudioSystem.getClip();
			c.open(audio);
			mapClips.put(nom,c);

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

	public void volumeControl(double nouvGain)
	{
		gain=nouvGain;
		Config.bruitageVolume=gain;

		for(Clip c : mapClips.values())
		{
			FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float dB = (float) (Math.log(gain) / Math.log(10.0) * 30.0);
			gainControl.setValue(dB);
		}
	}

	public void startBruitage(String typeBruitage)
	{
		volumeControl(gain);

		Clip c = mapClips.get(typeBruitage);
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
