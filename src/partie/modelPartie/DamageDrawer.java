package partie.modelPartie;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;

import javax.swing.JLabel;

public class DamageDrawer {
	private final Font FONT = new JLabel().getFont();
	private static final double MAX_TIME =  1* Math.pow(10, 9); // nano
	private static final Point RANDOM_SHIFT_SCALE =  new Point(50,20);
	private static final int MIN_FONT =  10;
	private static final int MAX_FONT =  20; 
	
	private String m_message;
	private double m_damage; 
	private long m_startTime;
	private Point m_randomShift; //add randomness to the draw position
	private Point m_drawPos;
	
	private Point m_originalPixelSize;
	public DamageDrawer(double p_damage){
		m_damage=p_damage;
		m_startTime = System.nanoTime();
		m_message = ""+Math.abs(m_damage);
		m_originalPixelSize = getMessagePixelSize(MIN_FONT);
	}
	
	public boolean shouldDraw(){
		return System.nanoTime() - m_startTime <MAX_TIME;
	}
	public int getFontSize(){
		double ratio = (System.nanoTime() - m_startTime)/MAX_TIME;
		return (int)Math.round((1-ratio) * MIN_FONT + ratio * MAX_FONT);
	}
	public Color getColor(){
		if(m_damage>0)
			return Color.GREEN;
		else if(m_damage<0)
			return Color.RED;
		else
			return Color.BLACK;
		
	}
	public String getMessage(){
		return m_message;
	}
	/***
	 * 
	 * @param currentPos: current position of the object that took damage. This is the draw position if it has not been initialized already
	 * @param randomDrawStrength: scaling of the random shift vector to avoid drawing all damages at the same position
	 * @return the position to which the object has to be drawn (takes into account the random shift)
	 */
	public Point getWorldDrawPos(Point p_defaultPos){
		if(m_drawPos==null){
			m_randomShift = new Point((int)Math.round((Math.random()-0.5)*RANDOM_SHIFT_SCALE.x),(int)Math.round((Math.random()-0.5)*RANDOM_SHIFT_SCALE.y));
			m_drawPos = new Point(p_defaultPos.x+m_randomShift.x,p_defaultPos.y+m_randomShift.y);
			System.out.println("Random shift " + m_randomShift+" draw pos "+ m_drawPos);
		}
		Point newPixelSize = getMessagePixelSize(getFontSize());
		return new Point(m_drawPos.x +(m_originalPixelSize.x-newPixelSize.x)/2,m_drawPos.y +(m_originalPixelSize.y-newPixelSize.y)/2);
	}
	private Point getMessagePixelSize(int fontSize){
		String message = getMessage();
		Font  defaultFont = new Font(FONT.getName(), FONT.getStyle(), fontSize);
		Canvas c = new Canvas();
		FontMetrics fontMetrics = c.getFontMetrics(defaultFont);
		return new Point(fontMetrics.stringWidth(message),fontMetrics.getHeight());
	}

}
