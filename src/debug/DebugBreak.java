package debug;

import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.ModelPartie;
import partie.modelPartie.PartieTimer;

public class DebugBreak {
	public static void breakAndUpdateGraphic()
	{
		ModelPartie.me.forceRepaint();
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
		System.out.print("");//break here 
		PartieTimer.me.unfreezeTime();
	}
}
