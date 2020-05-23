package partie.input;

public interface InputPartieListener {
	public abstract void onMove(int direction, boolean doubleTapped,boolean isReleased);
	public abstract void onJump(boolean isReleased);
	public abstract void onShoot(int type_tir,boolean isReleased);
	public abstract void onChangeSlot(int slot,boolean isReleased);
	public abstract void onDash(boolean isReleased);
	public abstract void onSlow(boolean isReleased);
	public abstract void onPause(boolean isReleased);
	
	public abstract void onResetGameTouchesFocus();
}
