package music;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ThreadMusique implements Runnable
{
	public Music musique;
	
	public ThreadMusique (String nomMus) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		musique = new Music(nomMus);
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