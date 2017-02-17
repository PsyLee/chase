/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Engine.Glavna;
import Player.Network;

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
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class WithoutServer extends JPanel implements Runnable {

	private BufferedImage image ;

    public WithoutServer(String filename) {
    	try {
			this.image = ImageIO.read(WithoutServer.class.getResource(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    //slika i golemina na slikata za pozadina
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, 530, 250, null);
    }

    @Override
    public void run() {
        final JFrame ramka = new JFrame("With server");
        WithoutServer panel = new WithoutServer("/gui_images/start.jpg");
        panel.setLayout(null);
        ramka.setUndecorated(true);

        //golemina na ramkata
        ramka.setSize(530, 250);
        ramka.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ramka.getContentPane().add(panel, BorderLayout.CENTER);

        //pozicija na ekranot
        Dimension screenSize;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowX = Math.max(0, (screenSize.width - ramka.getWidth()) / 2);
        int windowY = Math.max(0, (screenSize.height - ramka.getHeight()) / 2);
        ramka.setLocation(windowX, windowY);

        final JLabel info = new JLabel();
        info.setLocation(1, 220);
        info.setSize(500, 30);
        info.setFont(new Font("sansserif", Font.BOLD, 12));
        info.setForeground(Color.white);
        panel.add(info);

        //labela za gasenje na igrata
        final JLabel exit = new JLabel();
        exit.setLocation(0, 0);
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
        minimize.setLocation(30, 0);
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

        //labela za nickname
        JLabel nickname = new JLabel();
        nickname.setLocation(30, 45);
        nickname.setSize(120, 60);
        nickname.setIcon(zaLabela("/gui_images/nickname.png", 120, 60));
        panel.add(nickname);

        final JTextField nick = new JTextField();
        nick.setLocation(160, 55);
        nick.setSize(145, 35);
        nick.setHorizontalAlignment(JTextField.CENTER);
        nick.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 20));
        nick.setBackground(Color.GRAY);
        nick.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        panel.add(nick);

        //labela za serverIP
        JLabel serverIP = new JLabel();
        serverIP.setLocation(30, 95);
        serverIP.setSize(120, 60);
        serverIP.setIcon(zaLabela("/gui_images/serverIP.png", 120, 60));
        panel.add(serverIP);

        final JTextField ipServer = new JTextField();
        ipServer.setLocation(160, 105);
        ipServer.setSize(145, 35);
        ipServer.setHorizontalAlignment(JTextField.CENTER);
        ipServer.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 18));
        ipServer.setBackground(Color.GRAY);
        ipServer.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        panel.add(ipServer);

        //dugme za brkanje
        final JRadioButton chase = new JRadioButton("<html><body><img src='"+WithoutServer.class.getResource("/gui_images/chase.png")+"' width=105 height=35>", true);
        chase.setLocation(360, 55);
        chase.setSize(125, 35);
        chase.setOpaque(false);
        chase.setFocusPainted(false);
        panel.add(chase);

        //dugle za beganje
        final JRadioButton run = new JRadioButton("<html><body><img src='"+WithoutServer.class.getResource("/gui_images/run.png")+"' width=105 height=30>", false);
        run.setLocation(360, 85);
        run.setSize(125, 30);
        run.setOpaque(false);
        run.setFocusPainted(false);
        panel.add(run);

        //grupiranje na dugminjata
        ButtonGroup group = new ButtonGroup();
        group.add(chase);
        group.add(run);

        //nastani pri klik i dvizenje na maus
        chase.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                chase.setText("<html><body><img src='"+WithoutServer.class.getResource("/gui_images/chase_hover.png")+"' width=105 height=35>");
//                info.setText("<html>Start the server and start to play<br/>WARNING: port forward the router using port 27960 for TCP and UDP</html>");
//            
            }

            @Override
            public void mouseExited(MouseEvent e) {
                chase.setText("<html><body><img src='"+WithoutServer.class.getResource("/gui_images/chase.png")+"' width=105 height=35>");
//                info.setText("");
            }

        });

        //nastani pri klik i dvizenje na maus
        run.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                run.setText("<html><body><img src='"+WithoutServer.class.getResource("/gui_images/run_hover.png")+"' width=105 height=30>");
//                info.setText("<html>Start the server and start to play<br/>WARNING: port forward the router using port 27960 for TCP and UDP</html>");
//            
            }

            @Override
            public void mouseExited(MouseEvent e) {
                run.setText("<html><body><img src='"+WithoutServer.class.getResource("/gui_images/run.png")+"' width=105 height=30>");
//                info.setText("");
            }

        });

        //labela za choose
        JLabel choose = new JLabel();
        choose.setLocation(340, 15);
        choose.setSize(125, 55);
        choose.setIcon(zaLabela("/gui_images/choose.png", 125, 55));
        panel.add(choose);

        //labela za settings
        final JLabel settings = new JLabel();
        settings.setLocation(60, 150);
        settings.setSize(150, 90);
        settings.setIcon(zaLabela("/gui_images/settings.png", 150, 90));
        panel.add(settings);

        settings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                (new Thread(new Settings(""))).start();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                settings.setIcon(zaLabela("/gui_images/settings_hover.png", 150, 90));
//                info.setText("<html>Start the server and start to play<br/>WARNING: port forward the router using port 27960 for TCP and UDP</html>");
//            
            }

            @Override
            public void mouseExited(MouseEvent e) {
                settings.setIcon(zaLabela("/gui_images/settings.png", 150, 90));
//                info.setText("");
            }

        });

        //labela za connect
        final JLabel connect = new JLabel();
        connect.setLocation(250, 150);
        connect.setSize(160, 90);
        connect.setEnabled(true);
        connect.setIcon(zaLabela("/gui_images/connect.png", 160, 90));
        panel.add(connect);

        //nastani pri klik i dvizenje na maus
        connect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (nick.getText().trim().length() == 0) {
                    JOptionPane.showMessageDialog(ramka, "Nickname minimum one character", "Nickname error", JOptionPane.ERROR_MESSAGE);
                } else {

                    Network network = new Network();
                    network.setIP(ipServer.getText().trim());
                    network.connect();

                    if (network.moze == false) {
                        JOptionPane.showMessageDialog(ramka, "Can't connect to " + ipServer.getText().trim(), "Connecting error", JOptionPane.ERROR_MESSAGE);
                    } else {
 
                    	Glavna.network = network;
                        Glavna.player.nickname = nick.getText().trim();

                        if (chase.isSelected()) {
                            Glavna.player.chase = true;
                        } else {
                            Glavna.player.chase = false;
                        }

                        Glavna g = new Glavna();
                        new Thread(g).start();
                        ramka.dispose();
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                connect.setIcon(zaLabela("/gui_images/connect_hover.png", 160, 90));
//                info.setText("<html>Start the server and start to play<br/>WARNING: port forward the router using port 27960 for TCP and UDP</html>");
//            
            }

            @Override
            public void mouseExited(MouseEvent e) {
                connect.setIcon(zaLabela("/gui_images/connect.png", 160, 90));
//                info.setText("");
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
            BufferedImage originalImage = ImageIO.read(WithoutServer.class.getResource(path));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizeImageJpg = resizeImage(originalImage, type, width, height);
            resizedicon = new ImageIcon(resizeImageJpg);

        } catch (IOException ex) {
            Logger.getLogger(WithoutServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resizedicon;
    }
}
