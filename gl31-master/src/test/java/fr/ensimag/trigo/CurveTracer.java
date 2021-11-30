/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.trigo;

/**
 *
 * @author ensimag
 */
import static fr.ensimag.trigo.TestTrigo.*;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class CurveTracer extends JFrame {

    private static final long serialVersionUID = -3914578220391097071L;

    private CourbeCos curveCanvas = new CourbeCos();
    
    public CurveTracer() {
        super( "Curve tracer" );
        this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        
        JPanel contentPane = (JPanel) this.getContentPane();

        contentPane.add( curveCanvas, BorderLayout.CENTER );
        //Ici pour changer les fonction (sin ou cos)
        curveCanvas.setFunction( (x) -> Math.cos( x ) );
        curveCanvas.setFunction2( (x) -> cos( (float)x ) );
        this.setSize( 400, 470 );
        this.setLocationRelativeTo( null );
    }

    public static void main(String[] args) throws Exception {
        
        UIManager.setLookAndFeel( new NimbusLookAndFeel() );
        CurveTracer window = new CurveTracer();
        window.setVisible( true );
    }
}
