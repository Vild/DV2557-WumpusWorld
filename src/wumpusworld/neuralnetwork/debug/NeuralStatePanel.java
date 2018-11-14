/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpusworld.neuralnetwork.debug;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Dan Printzell
 */
public class NeuralStatePanel extends JPanel {

    private JLabel label;

    public NeuralStatePanel() {
        label = new JLabel();
        update();
        add(label);
    }

    public void update() {
        System.out.println("UPDATED!!!");
        try {
            label.setIcon(new ImageIcon(ImageIO.read(new File("state.png"))));
        } catch (IOException ex) {
            Logger.getLogger(NeuralStatePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        label.revalidate();
        label.repaint();
        revalidate();
        repaint();
    }

}
