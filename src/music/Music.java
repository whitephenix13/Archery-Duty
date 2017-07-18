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

import loading.LoadMediaThread;
import option.Config;
import principal.InterfaceConstantes;

public class Music extends LoadMediaThread implements InterfaceConstantes{

	public static Music me =null; 
	static double gain = Config.bruitageVolume;
	public static String musiqueEnCours ="";
	static Map<String,Clip> mapClips = new HashMap<String,Clip>();
	boolean isSlowed = false;
	boolean soundFound=true;

	public static void init()
	{
		if(me==null)
			me=new Music();
	}
	private Music()
	{}
	@Override
	public void loadMedia() {
		//fill the hash map
		if(mediaLoaded)
			return;
		
		addToMap(musiqueOption);
		setPercentage((int)100.0/5);
		addToMap(musiquePrincipal);
		setPercentage((int)200.0/5);
		addToMap(musiquePartie);
		setPercentage((int)300.0/5);
		addToMap(musiqueEditeur);
		setPercentage((int)400.0/5);
		addToMap(musiqueSlow);
		
		setPercentage(100);
		mediaLoaded=true;
	}

	@Override
	public void loadMedia(String media_categorie, String filename) {
		URL url = getClass().getClassLoader().getResource("resources/musique/"+filename+".wav");
		if(url!=null) { 
			addToMap(filename);
		}
		setPercentage(100);
	}
	public void addToMap(String nom)
	{
		Clip c;
		AudioInputStream audio; 
		try {
			if(me==null || me.mapClips.containsKey(nom))
				return;
			audio= AudioSystem.getAudioInputStream(
					getClass().getClassLoader().getResource("resources/musique/"+nom+".wav"));
			c=AudioSystem.getClip();
			c.open(audio);
			mapClips.put(nom,c);

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

	public void startMusic()
	{
		if(!soundFound)
			return;

		getClip(false).setFramePosition(0);
		volumeControl(gain);
		getClip(false).loop(Clip.LOOP_CONTINUOUSLY);
	}
	public void startNewMusic(String newMusic)
	{
		stopMusic();
		setMusic(newMusic);
		startMusic();
	}
	public void stopMusic()
	{
		if(!soundFound)
			return;

		if(getClip(false).isActive())
			getClip(false).stop();
		if(slowVersionExist())
			if(getClip(true).isActive())
				getClip(true).stop();
	}

	public void setMusic(String nomMusique)
	{
		musiqueEnCours=nomMusique;
	}
	
	Clip getClip(boolean slowedMusic)
	{
		return(slowedMusic? mapClips.get(musiqueEnCours+"_s") :mapClips.get(musiqueEnCours) );
	}

	static boolean slowVersionExist()
	{
		return(mapClips.containsKey(musiqueEnCours+"_s"));
	}
	/**
	 * Permet de régler le volume de la musique
	 *  
	 * @param nouvGain, nouvelle valeur du gain entre 0 et 1 
	 * 
	 */
	public static void volumeControl(Double nouvGain)
	{
		gain=nouvGain;
		Config.musicVolume=gain;
		float dB = (float) (Math.log(gain) / Math.log(10.0) * 30.0);

		FloatControl gainControl = (FloatControl) mapClips.get(musiqueEnCours).getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(dB);

		if(slowVersionExist())
		{
			FloatControl gainControl2 = (FloatControl) mapClips.get(musiqueEnCours+"_s").getControl(FloatControl.Type.MASTER_GAIN);
			gainControl2.setValue(dB);
		}
	}

	
	public void slowDownMusic()
	{
		if(!soundFound)
			return;
		if(isSlowed)
			return;
		Clip c = getClip(false);
		if(c==null)
			return;
		int framePos= c.getFramePosition();
		c.stop();

		getClip(true).setFramePosition(framePos*2);
		getClip(true).start();
		isSlowed=true;
	}
	public void endSlowDownMusic() 
	{
		if(!isSlowed)
			return;
		
		Clip c = getClip(true);
		if(c==null)
			return;
		int framePos= c.getFramePosition();
		c.stop();

		getClip(false).setFramePosition(framePos/2);
		getClip(false).start();
		isSlowed=false;
	}


}
