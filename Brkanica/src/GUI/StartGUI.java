/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Server.Server.ServerProgram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class StartGUI extends JPanel implements KeyListener {

    private BufferedImage image ;

    public StartGUI(String filename) {
        try {
			this.image = ImageIO.read(StartGUI.class.getResource(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    //slika i golemina na slikata za pozadina
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, 500, 300, null);
    }

    public static void main(String[] args) {

        final JFrame ramka = new JFrame("Start");
        StartGUI panel = new StartGUI("/gui_images/start.jpg");
        panel.setLayout(null);
        ramka.setUndecorated(true);
        ramka.addKeyListener(panel);

        //labela za info
        final JLabel info = new JLabel();
        info.setLocation(1, 270);
        info.setSize(500, 30);
        info.setFont(new Font("sansserif", Font.BOLD, 12));
        info.setForeground(Color.white);
        panel.add(info);

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
        
    
        //labela za start na igrata so server
        final JLabel withServer = new JLabel();
        withServer.setLocation(40, 50);
        withServer.setSize(210, 130);
        withServer.setIcon(zaLabela("/gui_images/serv_play.png", 210, 130));
        panel.add(withServer);

        //nastani pri klik i dvizenje na maus
        withServer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ServerProgram server = new ServerProgram();
                new Thread(server).start();
                ramka.dispose();
                (new Thread(new WithServer(""))).start();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                withServer.setIcon(zaLabela("/gui_images/serv_play_hover.png", 210, 130));
                info.setText("<html>Start the server and start to play<br/>WARNING: port forward the router using port 27960 for TCP and UDP</html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                withServer.setIcon(zaLabela("/gui_images/serv_play.png", 210, 130));
                info.setText("");
            }

        });

        //labela za start na igrata bez server
        final JLabel withoutServer = new JLabel();
        withoutServer.setLocation(280, 60);
        withoutServer.setSize(170, 100);
        withoutServer.setIcon(zaLabela("/gui_images/play.png", 170, 100));
        panel.add(withoutServer);

        //nastani pri klik i dvizenje na maus
        withoutServer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               
                ramka.dispose();
                (new Thread(new WithoutServer(""))).start();

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                withoutServer.setIcon(zaLabela("/gui_images/play_hover.png", 170, 100));
                info.setText("Start the game");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                withoutServer.setIcon(zaLabela("/gui_images/play.png", 170, 100));
                info.setText("");
            }

        });

        //labela za start samo na serverot
        final JLabel onlyServer = new JLabel();
        onlyServer.setLocation(135, 180);
        onlyServer.setSize(210, 90);
        onlyServer.setIcon(zaLabela("/gui_images/server.png", 210, 90));
        panel.add(onlyServer);

        //nastani pri klik i dvizenje na maus
        onlyServer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ServerProgram server = new ServerProgram();
                new Thread(server).start();
                ramka.dispose();
                (new Thread(new OnlyServer(""))).start();

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                onlyServer.setIcon(zaLabela("/gui_images/server_hover.png", 210, 90));
                info.setText("<html>Start the server only<br/>WARNING: port forward the router using port 27960 for TCP and UDP</html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                onlyServer.setIcon(zaLabela("/gui_images/server.png", 210, 90));
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
            BufferedImage originalImage = ImageIO.read(StartGUI.class.getResource(path));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizeImageJpg = resizeImage(originalImage, type, width, height);
            resizedicon = new ImageIcon(resizeImageJpg);

        } catch (IOException ex) {
            Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
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
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
