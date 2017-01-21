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

	static double gain;
	AudioInputStream audioInputStream;

	static int nombreBruitage =0 ;


	static Map<String,Clip> mapClips = new HashMap<String,Clip>();

	String typeBruitage;

	long startTime;
	long time;


	public void initMusicBruitage()
	{
		gain = Config.bruitageVolume;
		addToMap(bruitagesArray);

	}
	public void addToMap(String nom)
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
	public void addToMap(String[] noms)
	{
		for(String s : noms)
		{
			addToMap(s);
		}
	}
	public MusicBruitage()
	{
	}

	public MusicBruitage(String bruitage) {

		typeBruitage=bruitage;
		volumeControl(gain);
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

	public void startBruitage(long _time)
	{
		time=_time*1000;

		startTime=System.nanoTime();
		volumeControl(gain);

		Clip c = mapClips.get(typeBruitage);
		c.stop();
		c.setMicrosecondPosition(0);
		c.start();

	}
	public void setBruitage(String bruitage, Music music) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		typeBruitage=bruitage;
	}
	public boolean doitDetruire()
	{
		if((System.nanoTime()- startTime)*Math.pow(10, -6) > time )
		{
			return(true);
		}
		else
		{
			return(false);
		}
	}

}
