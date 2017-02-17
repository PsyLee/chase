/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class OnlyServer extends JPanel implements Runnable{

    private BufferedImage image ;

    public OnlyServer(String filename) {
        try {
			this.image = ImageIO.read(OnlyServer.class.getResource(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, 500, 300, null);
    }

    @Override
    public void run() {

        final JFrame ramka = new JFrame("Server");
        OnlyServer panel = new OnlyServer("/gui_images/start.jpg");
        panel.setLayout(null);
        ramka.setUndecorated(true);
    
         //golemina na ramkata
        ramka.setSize(500, 300);
        ramka.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ramka.getContentPane().add(panel, BorderLayout.CENTER);
        
        //pozicija na ekranot
        Dimension screenSize;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowX = Math.max(0, (screenSize.width - ramka.getWidth()) / 2);
        int windowY = Math.max(0, (screenSize.height - ramka.getHeight()) / 2);
        ramka.setLocation(windowX, windowY);
        
        final JLabel info = new JLabel();
        info.setLocation(1, 270);
        info.setSize(500, 30);
        info.setFont(new Font("sansserif", Font.BOLD, 12));
        info.setForeground(Color.white);
        panel.add(info);
        
        //labelata za tekstot
        JLabel tekstot = new JLabel();
        tekstot.setLocation(40, 50);
        tekstot.setSize(410, 130);
        tekstot.setIcon(zaLabela("/gui_images/server_running.png", 410, 130));
        panel.add(tekstot);
                   
                   
        //labela za stop na serverot
        final JLabel stop = new JLabel();
        stop.setLocation(110, 180);
        stop.setSize(260, 100);
        stop.setIcon(zaLabela("/gui_images/stop_server.png", 260, 100));
        panel.add(stop);

        //nastani pri klik i dvizenje na maus
        stop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                stop.setIcon(zaLabela("/gui_images/stop_server_hover.png", 260, 100));
                info.setText("Stop server and exit");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                stop.setIcon(zaLabela("/gui_images/stop_server.png", 260, 100));
                info.setText("");
            }

        });
        

        //labela za gasenje na igrata
        final JLabel exit = new JLabel();
        exit.setLocation(470, 0);
        exit.setSize(30, 30);
        exit.setIcon(zaLabela("/gui_images/exit.png", 30, 30));
        panel.add(exit);

        //nastani pri klik i dvizenje na maus
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                exit.setIcon(zaLabela("/gui_images/exit_hover.png", 30, 30));
                info.setText("Exit");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exit.setIcon(zaLabela("/gui_images/exit.png", 30, 30));
                info.setText("");
            }

        });

        //labela za minimajz na igrata
        final JLabel minimize = new JLabel();
        minimize.setLocation(440, 0);
        minimize.setSize(30, 30);
        minimize.setIcon(zaLabela("/gui_images/minimize.png", 30, 30));
        panel.add(minimize);

        //nastani pri klik i dvizenje na maus
        minimize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ramka.setState(Frame.ICONIFIED);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                minimize.setIcon(zaLabela("/gui_images/minimize_hover.png", 30, 30));
                info.setText("Minimize");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                minimize.setIcon(zaLabela("/gui_images/minimize.png", 30, 30));
                info.setText("");
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
            BufferedImage originalImage = ImageIO.read(OnlyServer.class.getResource(path));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizeImageJpg = resizeImage(originalImage, type, width, height);
            resizedicon = new ImageIcon(resizeImageJpg);

        } catch (IOException ex) {
            Logger.getLogger(OnlyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resizedicon;
    }

}
