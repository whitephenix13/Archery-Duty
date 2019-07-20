package debug;

import java.util.concurrent.TimeUnit;

import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.PartieTimer;

public class DebugBreak {
	public static void breakAndUpdateGraphic(AbstractModelPartie partie)
	{
		partie.forceRepaint();
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DebugBreak.breakHere();
	}
	public static void breakHere()
	{
		PartieTimer.me.freezeTime();
		System.out.print("");
		PartieTimer.me.unfreezeTime();
	}
}
