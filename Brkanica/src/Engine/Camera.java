package Engine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.awt.GLCanvas;

public class Camera implements MouseMotionListener, KeyListener {

    GLCanvas canvas;
    public float camYaw = 0.0f; // agol rotacija levo desno
    public float camPitch = 0.0f; // agol rotacija gore dole
    float camX;
    float camY;
    float camZ;
    public boolean W = false;
    public boolean A = false;
    public boolean S = false;
    public boolean D = false;
    public boolean ESC = false;
    public boolean Q = false;

    public float player_move_x = 0.0f;
    public float player_move_z = 0.0f;

    public int UP = 2; //0-mrda, 1-prestaniue, 2-nisto
    public int DOWN = 2;
    public int LEFT = 2;
    public int RIGHT = 2;
    public int JUMP = 2;

    public void initCam(float x, float y, float z) {
        camX = x;
        camY = y;
        camZ = z;
    }

    public Camera(GLCanvas canvas) {
        this.canvas = canvas;
        canvas.requestFocusInWindow();
        BufferedImage cursorImg = new BufferedImage(16, 16,
                BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        canvas.setCursor(blankCursor);
    }

    void lockCamera() {
        // set campitch between -90 and 90 and set camyaw between 0 and 360
        // degrees
        if (camPitch > 90) {
            camPitch = 90;
        }
        if (camPitch < -90) {
            camPitch = -90;
        }
        if (camYaw < 0.0) {
            camYaw += 360.0;
        }
        if (camYaw > 360.0) {
            camYaw -= 360;
        }
    }

    public void PlayerMove(int direction) {
        float rad = (float) Math.toRadians(camYaw + direction);
        player_move_x = (float) ((-Math.sin(rad)));
        player_move_z = (float) ((+Math.cos(rad)));
    }

    public void moveCamera(float dist, float dir) {
        float rad = (float) Math.toRadians(camYaw + dir);
        camX -= Math.sin(rad) * dist; // calculate the new coorinate, if you
        // don't understand, draw a right
        // triangle with the datas, you have
        camZ += Math.cos(rad) * dist; // and try to calculate the new coorinate
        // with trigonometric functions, that
        // should help
    }

    public void moveCameraUp(float dist, float dir) {
        // the the same, only this time we calculate the y coorinate
        float rad = (float) Math.toRadians(camPitch + dir);
        camY -= Math.sin(rad) * dist;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        float delta_x = 0;
        float delta_y = 0;
        try {
            int momentalna_y = canvas.getHeight() / 2;
            int momentalna_x = canvas.getWidth() / 2;
            int dvizi_x = canvas.getMousePosition().x;
            int dvizi_y = canvas.getMousePosition().y;

            delta_x = (dvizi_x - momentalna_x);
            delta_y = (dvizi_y - momentalna_y);
        } catch (Exception ex) {
        }
        float brzina = 0.05f;
        camYaw += brzina * delta_x;
        camPitch += brzina * delta_y;
        lockCamera();

        try {
            Robot r = new Robot();
            Point p = canvas.getLocationOnScreen();
            r.mouseMove(p.x + canvas.getWidth() / 2, p.y + canvas.getHeight()
                    / 2);
        } catch (AWTException ex) {
            Logger.getLogger(Camera.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        if (delta_x != 0 || delta_y != 0) {
            // // System.out.println("delta_x =" + delta_x + "  delta_y=" +
            // delta_y);
            // System.out.println("CamYaw =" + camYaw + "  CamPitch=" +
            // camPitch);
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_W) {
            W = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            S = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            A = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            D = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            ESC = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            Q = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            UP = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            DOWN = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            LEFT = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            RIGHT = 0;
        }
       /* if (e.getKeyCode() == KeyEvent.VK_NUMPAD0) {
            JUMP = 0;
        }*/

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            W = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            S = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            A = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            ESC = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            Q = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            D = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            UP = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            DOWN = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            LEFT = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            RIGHT = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_NUMPAD0) {
            JUMP = 1;
        }
    }
}
