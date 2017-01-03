package music;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ThreadMusique implements Runnable
{
	public String nomMusique ;
	public Music musique;
	
	public ThreadMusique (String nomMus) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
	nomMusique=nomMus;	
	 musique = new Music(nomMusique);
	}
	
	public void run() 
	{
		musique.startMusique();
	}
	public Music getMusic()
	{
		return(musique);
	}

}