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

import principal.InterfaceConstantes;

public class Music implements InterfaceConstantes{
static double gain = valeurSonInit;
static String musiqueEnCours ="";
static Map<String,Clip> mapClips = new HashMap<String,Clip>();
boolean soundFound=true;

	/**
	 * Instantie une Music pour pouvoir accéder aux fonctions statics
	 * 
	 */
	public Music() 
	{
		
	}

	/**
	 * Instantie une Music
	 * 
	 * @param nomMusique, nom de la musique à jouer
	 * 
	 */
	public Music(String nomMusique) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		
		
		//fill the hash map
		addToMap(musiqueOption);
		addToMap(musiquePrincipal);
		addToMap(musiquePartie);
		addToMap(musiqueEditeur);
		addToMap(musiqueSlow);
		if(soundFound)
		{
			musiqueEnCours=nomMusique;
					
			FloatControl gainControl = (FloatControl) mapClips.get(musiqueEnCours).getControl(FloatControl.Type.MASTER_GAIN);
			float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
			gainControl.setValue(dB);
		}
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
		float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
		
		FloatControl gainControl = (FloatControl) mapClips.get(musiqueEnCours).getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(dB);
		
		if(slowVersionExist())
		{
			FloatControl gainControl2 = (FloatControl) mapClips.get(musiqueEnCours+"_s").getControl(FloatControl.Type.MASTER_GAIN);
			gainControl2.setValue(dB);
		}
	}
	
	
	public void stopMusique()
	{
		if(!soundFound)
			return;
		
		if(getClip(false).isActive())
			getClip(false).stop();
		if(slowVersionExist())
			if(getClip(true).isActive())
				getClip(true).stop();
	}
	public void startMusique()
	{
		if(!soundFound)
			return;
		
		getClip(false).setFramePosition(0);
		volumeControl(gain);
		getClip(false).loop(Clip.LOOP_CONTINUOUSLY);
	}
	public void setMusic(String nomMusique, Music music) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		musiqueEnCours=nomMusique;
	}
	public void slowDownMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		if(!soundFound)
			return;
		
		Clip c = getClip(false);
		if(c==null)
			return;
		int framePos= c.getFramePosition();
		c.stop();
		
		getClip(true).setFramePosition(framePos*2);
		getClip(true).start();
	}
	public void endSlowDownMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{

		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
				getClass().getClassLoader().getResource("resources/musique/"+musiqueEnCours+".wav"));
		Clip c = getClip(true);
		if(c==null)
			return;
		int framePos= c.getFramePosition();
		c.stop();

		getClip(false).setFramePosition(framePos/2);
		getClip(false).start();
	}
}
