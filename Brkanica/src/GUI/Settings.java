/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import Engine.Glavna;

@SuppressWarnings("serial")
public class Settings extends JPanel implements Runnable, KeyListener {

    private BufferedImage image ;
    static JFrame ramka;

    public Settings(String filename) {
        try {
			this.image = ImageIO.read(Settings.class.getResource(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    //slika i golemina na slikata za pozadina
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, 400, 190, null);
    }

    @Override
    public void run() {
        ramka = new JFrame("Settings");
        Settings panel = new Settings("/gui_images/start_settings.jpg");
        panel.setLayout(null);
        ramka.setUndecorated(true);
        ramka.addKeyListener(panel);
        
        ramka.setFocusableWindowState(false);
        ramka.setFocusable(false);
        ramka.setAlwaysOnTop(true);

        
        //golemina na ramkata
        ramka.setSize(400, 190);
        ramka.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ramka.getContentPane().add(panel, BorderLayout.CENTER);

        //pozicija na ekranot
        Dimension screenSize;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowX = Math.max(0, (screenSize.width - ramka.getWidth()) / 2);
        int windowY = Math.max(0, (screenSize.height - ramka.getHeight()) / 2);
        ramka.setLocation(windowX, windowY);

        //lista na rezolucii
        String[] rezolucii = {"640x480","800x600","1024x768","1280x600","1280x720",
        "1280x768","1360x768"};

        //drop down lista so rezoluciite
        final JComboBox rezol = new JComboBox(rezolucii);
        rezol.setLocation(40, 40);
        rezol.setSize(100, 30);
        rezol.setEnabled(false);
        panel.add(rezol);
        

        final JCheckBox fullscreen = new JCheckBox("<html><body><img src='" + Settings.class.getResource("/gui_images/fullscreen.png")+"' width=140 height=40>", true);

        fullscreen.setLocation(160, 40);
        fullscreen.setSize(160, 40);
        fullscreen.setSelected(true);
        fullscreen.setOpaque(false);
        fullscreen.setFocusPainted(false);
        panel.add(fullscreen);

        fullscreen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (fullscreen.isSelected()) {
                	//Settings.class.getResource("/gui_images/fullscreen.png");
                    fullscreen.setText("<html><body><img src='"+Settings.class.getResource("/gui_images/fullscreen_hover.png")+"' width=140 height=40>");
                    rezol.setEnabled(false);
                } else {
                    fullscreen.setText("<html><body><img src='"+Settings.class.getResource("/gui_images/fullscreen.png")+"' width=140 height=40>");
                    rezol.setEnabled(true);
                }
            }
        });
        
        final JLabel save = new JLabel();
        save.setLocation(40, 100);
        save.setSize(140, 40);
        save.setIcon(zaLabela("/gui_images/save.png", 140, 40));
        panel.add(save);
        
        
        save.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Glavna.fullscreen = fullscreen.isSelected();
                Glavna.window_size = rezol.getSelectedItem().toString();
                ramka.dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                save.setIcon(zaLabela("/gui_images/save_hover.png", 140, 40));
               // info.setText("<html>Start the server and start to play<br/>WARNING: port forward the router using port 27960 for TCP and UDP</html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                save.setIcon(zaLabela("/gui_images/save.png", 140, 40));
               // info.setText("");
            }

        });
        
        
        
        
        
        
        final JLabel cancel = new JLabel();
        cancel.setLocation(200, 98);
        cancel.setSize(140, 45);
        cancel.setIcon(zaLabela("/gui_images/cancel.png", 140, 45));
        panel.add(cancel);
        
         cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ramka.dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                cancel.setIcon(zaLabela("/gui_images/cancel_hover.png", 140, 45));
               // info.setText("<html>Start the server and start to play<br/>WARNING: port forward the router using port 27960 for TCP and UDP</html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cancel.setIcon(zaLabela("/gui_images/cancel.png", 140, 45));
               // info.setText("");
            }

        });
        
        
        
        

        //labela za gasenje na igrata
        final JLabel exit = new JLabel();
        exit.setLocation(350, 0);
        exit.setSize(30, 30);
        exit.setIcon(zaLabela("/gui_images/exit.png", 30, 30));
        panel.add(exit);

        //nastani pri klik i dvizenje na maus
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ramka.dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                exit.setIcon(zaLabela("/gui_images/exit_hover.png", 30, 30));
                // info.setText("Exit");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exit.setIcon(zaLabela("/gui_images/exit.png", 30, 30));
                // info.setText("");
            }

        });

        ramka.setVisible(true);
        ramka.setResizable(false);
    }

    //resize na slika mi trebase za vo labela
    private static BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    //sreduva slokata za dadena pateka i golemina
    private static ImageIcon zaLabela(String path, int width, int height) {
        ImageIcon resizedicon = null;
        try {
            BufferedImage originalImage = ImageIO.read(Settings.class.getResource(path));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizeImageJpg = resizeImage(originalImage, type, width, height);
            resizedicon = new ImageIcon(resizeImageJpg);

        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resizedicon;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

// pri ptitisok na escape da se izgasi igrata 
    @Override
    public void keyPressed(KeyEvent e) {

        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ESCAPE) {
            Settings.ramka.dispose();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
