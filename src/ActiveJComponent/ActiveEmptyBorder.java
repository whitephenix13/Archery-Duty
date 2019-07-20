package ActiveJComponent;

import java.awt.Insets;

import javax.swing.border.EmptyBorder;

public class ActiveEmptyBorder extends EmptyBorder{

	public ActiveEmptyBorder(Insets borderInsets) {
		super(borderInsets);
	}
	public ActiveEmptyBorder(int top, int left, int bottom, int right)
	{
		super(top,left,bottom,right);
	}
	
	public void addBorder(int d_top, int d_left, int d_bottom, int d_right)
	{
		this.top+=d_top;
		this.left+=d_left;
		this.bottom+=d_bottom;
		this.right+=d_right;
	}
}
