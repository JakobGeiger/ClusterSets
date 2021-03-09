package mapViewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;

public class ColorfulStringMapObject implements MapObject {

	public String content;
	public Point myPoint;
	public int fontSize;
	public Color[] myColors;
	
	public ColorfulStringMapObject(String content, Point p, int fontSize)
	{
		this.content = content;
		myPoint = p;
		this.fontSize = fontSize;
		myColors = new Color[1];
		myColors[0] = Color.BLACK;
	}

	@Override
	public void draw(Graphics2D g, Transformation t) {
		String[] lines = content.split("\n");
		
		
		Color c = g.getColor();
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
		
		for(int i = 0; i<lines.length; i++)
		{
			g.setColor(myColors[i%myColors.length]);
			g.drawString(lines[i], t.getColumn(myPoint.getX()), t.getRow(myPoint.getY()) + i*g.getFontMetrics().getHeight());
		}
		g.setColor(c);
		
	}

	@Override
	public Envelope getBoundingBox() {
		return new Envelope(myPoint.getX(),myPoint.getX(),myPoint.getY(),myPoint.getY());
	}
}
