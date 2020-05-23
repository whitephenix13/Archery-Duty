package partie.input;

import java.awt.MouseInfo;
import java.awt.Point;

public class InputPartiePool implements InputPartieListener{
	class KeyStateRef{
		public KeyStateRef(){
			ref = KeyState.NONE;
		}
		public KeyState ref;
	}
	public static enum KeyState{NONE,FIRST_PRESSED,DOWN,RELEASED};
	static interface GenericInputType{};
	public static enum InputType implements GenericInputType {LEFT,LEFT_D_TAP,RIGHT,RIGHT_D_TAP,JUMP,DASH,SLOW,PAUSE}
	public static enum InputTypeArray implements GenericInputType{SHOOT,SLOT}


	private KeyStateRef _moveLeftState;
	private KeyStateRef _moveLeftDoubleTapState;
	private KeyStateRef _moveRightState;
	private KeyStateRef _moveRightDoubleTapState;
	public String printRightStates(){
		return _moveRightState.ref +" "+ _moveRightDoubleTapState.ref;
	}
	
	private KeyStateRef _jumpState;
	private KeyStateRef[] _shootState = new KeyStateRef[5];  
	private KeyStateRef[] _slotState = new KeyStateRef[4];  
	private KeyStateRef _dashState;
	private KeyStateRef _slowState;
	private KeyStateRef _pauseState;
	
	private Point mousePosWhenClicked;
	private Point mousePosWhenReleased;
	
	private InputPartie referenceInputPartie;
	
	private boolean isPlayerInputPool = false; //set to true if those are map to the players input (and not simulate for monster/used for pause)
	public void setPlayerInputPool(boolean val){
		if(isPlayerInputPool)
			resetAll();
		isPlayerInputPool=val;
	}
	public boolean getIsPlayerInputPool(){return isPlayerInputPool;}
	
	/***
	 * Use this constructor if you don't want the key state to be map to the keyboard
	 */
	public InputPartiePool(){
		this(null);
	}
	
	/***
	 * Use this constructor if you want the key state to be map to the keyboard
	 */
	public InputPartiePool(InputPartie inputPartie){
		referenceInputPartie=inputPartie;
		
		_moveLeftState = new KeyStateRef();
		_moveLeftDoubleTapState = new KeyStateRef();
		_moveRightState = new KeyStateRef();
		_moveRightDoubleTapState = new KeyStateRef();
		
		_jumpState = new KeyStateRef();
		_dashState = new KeyStateRef();
		_slowState = new KeyStateRef();
		_pauseState = new KeyStateRef();
		
		for(int i=0; i<_shootState.length;++i)
			_shootState[i] = new KeyStateRef();
		for(int i=0; i<_slotState.length;++i)
			_slotState[i] = new KeyStateRef();
		//Input partie can be null if we don't want the keyboard input to be map to this key state. This is usefull to simulate input (i.e. for ennemies)
		if(inputPartie != null)
			inputPartie.registerListener(this);
	}
	public InputPartie getReferenceInputPartie(){return referenceInputPartie;}
		
	
	//High level function to get the state of an input 
	public boolean isInputFirstPressed(GenericInputType inputType){
		return isInputFirstPressed(inputType,null);
	}
	public boolean isInputFirstPressed(GenericInputType inputType,Integer index){
		KeyStateRef keyStateRef =null;
		if(index==null)
			keyStateRef = getKeyStateRef(inputType);
		else{
			KeyStateRef[] arr = getKeyStateRefArr(inputType);
			if(index<0 || index>=arr.length)
				return false;
			keyStateRef = arr[index];
		}
		return _isFirstPressed(keyStateRef);
	}
	public boolean isInputDown(GenericInputType inputType){
		return isInputDown(inputType,null);
	}
	public boolean isInputDown(GenericInputType inputType,Integer index){
		KeyStateRef keyStateRef =null;
		if(index==null)
			keyStateRef = getKeyStateRef(inputType);
		else{
			KeyStateRef[] arr = getKeyStateRefArr(inputType);
			if(index<0 || index>=arr.length)
				return false;
			keyStateRef = arr[index];
		}
		return _isDown(keyStateRef);
	}
	public boolean isInputReleased(GenericInputType inputType){
		return isInputReleased(inputType,null);
	}
	public boolean isInputReleased(GenericInputType inputType,Integer index){
		KeyStateRef keyStateRef =null;
		if(index==null)
			keyStateRef = getKeyStateRef(inputType);
		else{
			KeyStateRef[] arr = getKeyStateRefArr(inputType);
			if(index<0 || index>=arr.length)
				return false;
			keyStateRef = arr[index];
		}
		return _isReleased(keyStateRef);
	}
	public int getInputFirstPressed(GenericInputType inputType){
		for(int i=0; i<getKeyStateRefArr(inputType).length;++i){
			if(isInputFirstPressed(inputType,i))
				return i;
		}
		return -1;
	}
	public int getInputDown(GenericInputType inputType){
		for(int i=0; i<getKeyStateRefArr(inputType).length;++i){
			if(isInputDown(inputType,i))
				return i;
		}
		return -1;
	}
	public int getInputReleased(GenericInputType inputType){
		for(int i=0; i<getKeyStateRefArr(inputType).length;++i){
			if(isInputReleased(inputType,i))
				return i;
		}
		return -1;
	}
	
	public void updateInputState(){
		for (InputType input : InputType.values()) {
			_updateFirstPressed(getKeyStateRef(input));
			_updateReleased(getKeyStateRef(input));
		}
		for (InputTypeArray input : InputTypeArray.values()) {
			for(int i=0; i<getKeyStateRefArr(input).length;++i){
				_updateFirstPressed(getKeyStateRefArr(input)[i]);
				_updateReleased(getKeyStateRefArr(input)[i]);
			}
		}
	}
	public void releaseIfDown(boolean resetOnlyGameTouches){
		for (InputType input : InputType.values()) {
			if(resetOnlyGameTouches && input.equals(InputType.PAUSE))
				continue;
			_releaseIfDown(getKeyStateRef(input));
		}
		for (InputTypeArray input : InputTypeArray.values()) {
			for(int i=0; i<getKeyStateRefArr(input).length;++i)
				_releaseIfDown(getKeyStateRefArr(input)[i]);
		}
	}
	
	public void reset(GenericInputType inputType){
		reset(inputType,null);
	}
	public void reset(GenericInputType inputType,Integer index){
		if(index==null)
			_reset(getKeyStateRef(inputType));
		else
			_reset(getKeyStateRefArr(inputType)[index]);
	}
	public void resetAll(){
		for (InputType input : InputType.values()) {
			_reset(getKeyStateRef(input));
		}
		for (InputTypeArray input : InputTypeArray.values()) {
			for(int i=0; i<getKeyStateRefArr(input).length;++i)
				_reset(getKeyStateRefArr(input)[i]);
		}
	}
	
	//Use this function when changing from one input pool to another for an entity to ensure continuities of the actions
	public void copyValues(InputPartiePool copy){
		for (InputType input : InputType.values()) {
			if(copy.isInputFirstPressed(input))
				getKeyStateRef(input).ref = KeyState.FIRST_PRESSED;
			else if(copy.isInputDown(input))
				getKeyStateRef(input).ref = KeyState.DOWN;
			else if(copy.isInputReleased(input))
				getKeyStateRef(input).ref = KeyState.RELEASED;
			else
				getKeyStateRef(input).ref = KeyState.NONE;
		}
		for (InputTypeArray input : InputTypeArray.values()) {
			for(int i=0; i<getKeyStateRefArr(input).length;++i)
				if(copy.isInputFirstPressed(input,i))
					getKeyStateRefArr(input)[i].ref = KeyState.FIRST_PRESSED;
				else if(copy.isInputDown(input,i))
					getKeyStateRefArr(input)[i].ref = KeyState.DOWN;
				else if(copy.isInputReleased(input,i))
					getKeyStateRefArr(input)[i].ref = KeyState.RELEASED;
				else
					getKeyStateRefArr(input)[i].ref = KeyState.NONE;
		}
	}
	
	@Override
	public String toString(){
		String res="";
		for (InputType input : InputType.values()) {
			res+=input.toString() +": " +getKeyStateRef(input).ref.toString()+"\n";
		}
		for (InputTypeArray input : InputTypeArray.values()) {
			for(int i=0; i<getKeyStateRefArr(input).length;++i)
				res+=input.toString() +" "+i+" : " +getKeyStateRefArr(input)[i].ref.toString()+"\n";
		}
		if(!res.equals(""))
			res=res.substring(0,res.length()-2);
		return res;
	}
	public String getActiveInputsAsString(){
		String res="";
		for (InputType input : InputType.values()) {
			if(getKeyStateRef(input).ref != KeyState.NONE)
				res+=input.toString() +": " +getKeyStateRef(input).ref.toString()+"\n";
		}
		for (InputTypeArray input : InputTypeArray.values()) {
			for(int i=0; i<getKeyStateRefArr(input).length;++i)
				if(getKeyStateRefArr(input)[i].ref != KeyState.NONE)
					res+=input.toString() +" "+i+" : " +getKeyStateRefArr(input)[i].ref.toString()+"\n";
		}
		if(!res.equals(""))
			res=res.substring(0,res.length()-1);
		return res;
	}
	
	//Low level function to map input type with the correct variables
	private KeyStateRef getKeyStateRef(GenericInputType inputType){
		if(inputType.equals(InputType.LEFT))
			return _moveLeftState;
		if(inputType.equals(InputType.LEFT_D_TAP))
			return _moveLeftDoubleTapState;
		if(inputType.equals(InputType.RIGHT))
			return _moveRightState;
		if(inputType.equals(InputType.RIGHT_D_TAP))
			return _moveRightDoubleTapState;
		if(inputType.equals(InputType.JUMP))
			return _jumpState;
		if(inputType.equals(InputType.DASH))
			return _dashState;
		if(inputType.equals(InputType.SLOW))
			return _slowState;
		if(inputType.equals(InputType.PAUSE))
			return _pauseState;
		try{throw new Exception(inputType+" is not handled");}catch(Exception e){e.printStackTrace();}
		return null;
	} 
	private KeyStateRef[] getKeyStateRefArr(GenericInputType inputType){
		if(inputType.equals(InputTypeArray.SLOT))
			return _slotState;
		if(inputType.equals(InputTypeArray.SHOOT))
			return _shootState;

		try{throw new Exception(inputType+" is not handled");}catch(Exception e){e.printStackTrace();}
		return null;
	}

	//Low level function to actually determine the type of the key state 
	private boolean _isFirstPressed(KeyStateRef keyState){
		return keyState.ref.equals(KeyState.FIRST_PRESSED);
	}
	private boolean _isDown(KeyStateRef keyState){
		return keyState.ref.equals(KeyState.DOWN) || keyState.ref.equals(KeyState.FIRST_PRESSED);
	}
	private boolean _isReleased(KeyStateRef keyState){
		return keyState.ref.equals(KeyState.RELEASED);
	}

	private void _reset(KeyStateRef keyState){
		keyState.ref = KeyState.NONE;
	}
	private void _updateFirstPressed(KeyStateRef keyState){
		if(keyState.ref.equals(KeyState.FIRST_PRESSED))
			keyState.ref = KeyState.DOWN;
	}
	private void _updateReleased(KeyStateRef keyState){
		if(keyState.ref.equals(KeyState.RELEASED))
			keyState.ref = KeyState.NONE;
	}
	private void _releaseIfDown(KeyStateRef keyState){
		if(keyState.ref.equals(KeyState.DOWN))
			keyState.ref = KeyState.RELEASED;
	}
	
	public Point getMouseClickedPos(){return mousePosWhenClicked;}
	public Point getMouseReleasedPos(){return mousePosWhenReleased;}

	@Override
	public void onResetGameTouchesFocus(){
		if(isPlayerInputPool)
			releaseIfDown(true);
	}
		
	@Override
	public void onMove(int direction, boolean doubleTapped, boolean isReleased) {
		if(!isReleased){
			if(direction==-1){
				_moveLeftState.ref = KeyState.FIRST_PRESSED;
				if(doubleTapped) _moveLeftDoubleTapState.ref = KeyState.FIRST_PRESSED;
			}
			else{
				_moveRightState.ref = KeyState.FIRST_PRESSED;
				if(doubleTapped) _moveRightDoubleTapState.ref = KeyState.FIRST_PRESSED;
			}
		} else {
			if(direction==-1){
				_moveLeftDoubleTapState.ref = KeyState.RELEASED;_moveLeftState.ref = KeyState.RELEASED;}
			else{
				_moveRightDoubleTapState.ref = KeyState.RELEASED;_moveRightState.ref = KeyState.RELEASED;}
		}
	}
	@Override
	public void onJump(boolean isReleased) {
		_jumpState.ref = isReleased? KeyState.RELEASED:KeyState.FIRST_PRESSED;
	}
	@Override
	public void onShoot(int type_tir, boolean isReleased) {
		_shootState[type_tir].ref = isReleased? KeyState.RELEASED:KeyState.FIRST_PRESSED;
		if(!isReleased)
			mousePosWhenClicked = MouseInfo.getPointerInfo().getLocation();
		else
			mousePosWhenReleased = MouseInfo.getPointerInfo().getLocation();
	}
	@Override
	public void onChangeSlot(int slot, boolean isReleased) {
		_slotState[slot].ref = isReleased? KeyState.RELEASED:KeyState.FIRST_PRESSED;
	}
	@Override
	public void onDash(boolean isReleased) {
		_dashState.ref = isReleased? KeyState.RELEASED:KeyState.FIRST_PRESSED;
	}
	@Override
	public void onSlow(boolean isReleased) {
		_slowState.ref = isReleased? KeyState.RELEASED:KeyState.FIRST_PRESSED;
	}
	@Override
	public void onPause(boolean isReleased) {
		System.out.println("Pause pressed");
		_pauseState.ref = isReleased? KeyState.RELEASED:KeyState.FIRST_PRESSED;
	}
}
