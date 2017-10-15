package partie;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ArrowSlotButton extends JButton{

	public boolean choosingArrow = false; //true if all 4 arrows type or visible , Only set for the first button of the slot 
	public int slot = -1 ; 
	public String arrowType = ""; //name of the arrow in the heros slot
	ArrowSlotButton(int _slot)
	{
		super();
		slot=_slot;
		this.setContentAreaFilled(false);
		this.setBorder(null);
	}
	public void setIcons(Image im)
	{
		ImageIcon icon =  new ImageIcon(im);
		setIcons(icon);
	}
	public void setIcons(Icon icon)
	{
		this.setIcon(icon);
		this.setRolloverIcon(icon);
		this.setPressedIcon(icon);
	}
	public static ArrowSlotButton[] setIcons(ArrowSlotButton[] buttons, Image[] images)
	{
		for(int i=0; i<buttons.length;++i)
		{
			buttons[i].setIcons(images[i]);
		}
		return buttons;
	}
	public static void setArrowType (ArrowSlotButton[] buttons, String[] names)
	{
		for(int i=0; i<buttons.length;++i)
		{
			buttons[i].arrowType=names[i];
		}
	}
	public static void switchButtonType (ArrowSlotButton button1, ArrowSlotButton button2)
	{
		Icon temp_icon = button1.getIcon();
		String temp_type = button1.arrowType;
		boolean temp_vis = button1.isVisible();
		boolean temp_ena = button1.isEnabled();
		
		button1.setIcons(button2.getIcon());
		button1.arrowType = button2.arrowType;
		button1.setVisible(button2.isVisible());
		button1.setEnabled(button2.isEnabled());
		
		button2.setIcons(temp_icon);
		button2.arrowType=temp_type;
		button2.setVisible(temp_vis);
		button2.setEnabled(temp_ena);

	}

}
