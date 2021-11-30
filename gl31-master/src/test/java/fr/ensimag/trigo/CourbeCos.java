package fr.ensimag.trigo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ensimag
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import javax.swing.JComponent;

public class CourbeCos extends JComponent {

    private static final long serialVersionUID = 7800853645256601960L;

    private CurveFunction function;
    private CurveFunction function2;
    
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        
        // --- White background ---
        graphics.setColor( Color.WHITE );
        graphics.fillRect( 0, 0, getWidth(), getHeight() );
        
        // --- Draw axis ---
        graphics.setColor( Color.GRAY );
        graphics.drawLine( 0, getHeight()/2, getWidth(), getHeight()/2 );
        graphics.drawLine( getWidth()/2, 0, getWidth()/2, getHeight() );
        
        // --- Draw values ---
        graphics.setColor( Color.BLACK );
        graphics.drawString( "0,0", (int)(getWidth()*0.51), (int)(getHeight()*0.54));
        graphics.drawString( "-\u03c0", (int)(getWidth()*0.02), (int)(getHeight()*0.54));
        graphics.drawString( "\u03c0", (int)(getWidth()*0.96), (int)(getHeight()*0.54));
        
        
        // --- Draw curve ---
        double step = 0.1;
        
        Graphics2D graphics2 = (Graphics2D)graphics;
        graphics2.setStroke(new BasicStroke(3));
        
        int oldX = xToPixel( -Math.PI );
        int oldY = yToPixel( function.compute( -Math.PI ) );
        
        for( double lx=-Math.PI+step; lx<= Math.PI+step; lx+=step ) {
            int x = xToPixel( lx );
            int y = yToPixel( function.compute( lx ) );
            graphics.setColor( new Color( 255, 0, 0 ) );
            graphics.drawLine( x, y, oldX, oldY );
            
            oldX = x;
            oldY = y;
        }
        
        int oldX2 = xToPixel( -Math.PI );
        int oldY2 = yToPixel( function2.compute( -Math.PI ) );
        
        for( double lx=-Math.PI+step; lx<= Math.PI+step; lx+=step ) {
            int x = xToPixel( lx );
            int y = yToPixel( function2.compute( lx ) );
            graphics.setColor( new Color( 0, 0, 255 ) );
            graphics.drawLine( x, y, oldX2, oldY2 );
            
            oldX2 = x;
            oldY2 = y;
        }
    }
    
    public void setFunction(CurveFunction function) {
        this.function = function;
        this.repaint();
    }
    public void setFunction2(CurveFunction function) {
        this.function2 = function;
        this.repaint();
    }
    
    private int xToPixel( double x ) {
        return (int)( getWidth() * (x + Math.PI)/(2*Math.PI) );
    }

    private int yToPixel( double y ) {
        return (int)( getHeight() * (1 - (y + 1)/2.0 ) );
    }

    
    public static interface CurveFunction {
        
        public double compute( double x );
        
    }
    
}
