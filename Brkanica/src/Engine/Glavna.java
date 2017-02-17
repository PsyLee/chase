package Engine;

import Player.Chase;
import Player.MPPlayer;
import Player.Network;
import Player.Nickname;
import Player.PacketUpdateCamPitch;
import Player.PacketUpdateCamYaw;
import Player.PacketUpdateX;
import Player.PacketUpdateY;
import Player.PacketUpdateZ;
import Player.Player;
import com.bulletphysics.linearmath.Transform;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.*;
import javax.vecmath.Vector3f;

public class Glavna implements GLEventListener, Runnable {

	static JFrame frame;
	private static GraphicsEnvironment graphic_enviroment;
	private static boolean isFullScreen = false;
	private static Dimension xgraphic;
	private static Point point = new Point(0, 0);
	private GLU glu = new GLU();
	public static Camera c;

	// texturi
	private int skybox_texture[];
	private int spaceship_floor_texture;
	private int kocka_texture;
	private int dzid_texture;

	// static Client client = new Client();
	public static Mapa mapa;
	public static Fizika fizika;
	TextRenderer tr = new TextRenderer(new Font("SansSerif", Font.BOLD, 14));

	public static Network network = new Network();
	public static Map<Integer, MPPlayer> players = new HashMap<Integer, MPPlayer>();
	LinkedList<MPPlayer> igraci_sinhronizirani = new LinkedList<MPPlayer>();
	public static Player player = new Player();

	// podesavanje za main loop
	long lastTime = System.nanoTime();
	final double amountOfTicks = 30.0; // TICKS * FRAMERATE
	final float MAXFRAMERATE = 3; // 20*3 = 60 FPS
	int fps_counter = 0;

	double ns = 1000000000 / amountOfTicks;
	double delta = 0;
	double updates = 0;
	int frames = 0;
	long timer = System.currentTimeMillis();
	long previousTime = 0;
	long unprocessedSecconds = 0;
	boolean ticked = false;

	// za screen
	static public String window_size = "800x500";
	static public boolean fullscreen = true;

	// za render na rez
	int xpoz = 100;

	private void nacrtaj_mapa(GLAutoDrawable glad) {
		final GL2 gl = glad.getGL().getGL2();

		nacrtaj_pod(glad, mapa.data.golemina_x, mapa.data.golemina_y);
		nacrtaj_granica(glad, 0, 0, mapa.data.golemina_x, mapa.data.golemina_y);

		for (int i = 0; i < fizika.objekti_mapa.size(); i++) {
			gl.glPushMatrix();
			Transform transform = new Transform();
			fizika.objekti_mapa.get(i).getWorldTransform(transform);
			float[] matt = new float[16];
			transform.getOpenGLMatrix(matt);
			gl.glMultMatrixf(matt, 0);
			nacrtaj_objekt_mapa(glad, 1);
			gl.glPopMatrix();
		}
	}

	private void nacrtaj_igraci(GLAutoDrawable glad) {

		for (int i = 0; i < igraci_sinhronizirani.size(); i++) {
			double x = igraci_sinhronizirani.get(i).x;
			double pred_x = igraci_sinhronizirani.get(i).predX;
			double y = igraci_sinhronizirani.get(i).y;
			double pred_y = igraci_sinhronizirani.get(i).predY;
			double z = igraci_sinhronizirani.get(i).z;
			double pred_z = igraci_sinhronizirani.get(i).predZ;

			double pitch = igraci_sinhronizirani.get(i).camPitch;
			double pred_pitch = igraci_sinhronizirani.get(i).predCamPitch;
			double yaw = igraci_sinhronizirani.get(i).camYaw;
			double pred_yaw = igraci_sinhronizirani.get(i).predCamYaw;

			float interpolirana_x = (float) (pred_x + ((x - pred_x) * delta));
			float interpolirana_y = (float) (pred_y + ((y - pred_y) * delta));
			float interpolirana_z = (float) (pred_z + ((z - pred_z) * delta));

			float razlika_agli = PresmetajVistinskaRazlika((float) pred_yaw,
					(float) yaw);

			float interpolirana_pitch = (float) (pred_pitch + ((pitch - pred_pitch) * delta));
			float interpolirana_yaw = (float) (pred_yaw + (razlika_agli * delta));

			nacrtaj_igrac(glad, interpolirana_x, interpolirana_y,
					interpolirana_z, interpolirana_pitch, interpolirana_yaw,
					igraci_sinhronizirani.get(i).chase);
		}
	}

	public void nacrtaj_kursor(GLAutoDrawable glad) {
		final GL2 gl = glad.getGL().getGL2();

		IntBuffer screen = IntBuffer.allocate(4);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, screen);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0, screen.get(2), 0, screen.get(3), -1, 1);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glPushAttrib(GL2.GL_DEPTH_TEST);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glDepthMask(false);

		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glColor3f(0.5f, 0.5f, 0.8f);

		gl.glTranslatef(screen.get(2) / 2, screen.get(3) / 2, 0);

		gl.glEnable(GL2.GL_LINE_SMOOTH);

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2f(15, 15);
		gl.glVertex2f(5, 5);

		gl.glVertex2f(-15, 15);
		gl.glVertex2f(-5, 5);

		gl.glVertex2f(-5, -5);
		gl.glVertex2f(-15, -15);

		gl.glVertex2f(5, -5);
		gl.glVertex2f(15, -15);
		gl.glEnd();

		gl.glDisable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POINT_SMOOTH);

		gl.glColor3f(0.45f, 0.5f, 0.6f);
		gl.glPointSize(2.0f);
		// gl.glBegin(GL.GL_POINTS);
		//
		//
		// gl.glVertex2i(0, 0);
		// gl.glEnd();
		gl.glDisable(GL2.GL_POINT_SMOOTH);

		gl.glEnable(GL2.GL_TEXTURE);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glPopMatrix();

		gl.glPopAttrib();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();

	}

	public void nacrtaj_skybox(GLAutoDrawable glad, int golemina) {
		final GL2 gl = glad.getGL().getGL2();

		gl.glDisable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_TEXTURE_2D);

		gl.glColor3f(0.8f, 0.8f, 0.8f);

		gl.glPushMatrix();
		gl.glTranslatef(-golemina / 2, -golemina / 2, -golemina / 2);

		gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[2]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(0, 0, 0); // predna
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(0, golemina, 0);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(golemina, golemina, 0);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(golemina, 0, 0);
		gl.glEnd();

		gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[0]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(0, 0, golemina); // zadna
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(0, golemina, golemina);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(golemina, golemina, golemina);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(golemina, 0, golemina);
		gl.glEnd();

		gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[3]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(0, 0, 0); // levo
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(0, 0, golemina);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(0, golemina, golemina);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(0, golemina, 0);
		gl.glEnd();

		gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[4]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(golemina, 0, 0); // desno
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(golemina, 0, golemina);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(golemina, golemina, golemina);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(golemina, golemina, 0);
		gl.glEnd();

		gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[1]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(0, 0, 0);// dole
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(golemina, 0, 0);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(golemina, 0, golemina);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(0, 0, golemina);
		gl.glEnd();

		gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[5]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(0, golemina, 0);// gore
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(golemina, golemina, 0);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(golemina, golemina, golemina);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(0, golemina, golemina);
		gl.glEnd();

		gl.glPopMatrix();
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	public void nacrtaj_objekt_mapa(GLAutoDrawable glad, int golemina) {
		final GL2 gl = glad.getGL().getGL2();

		// gl.glDisable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_TEXTURE_2D);

		gl.glColor3f(0.7f, 0.7f, 0.7f);

		gl.glPushMatrix();
		gl.glTranslatef(-golemina / 2, -golemina / 2, -golemina / 2);

		gl.glBindTexture(GL2.GL_TEXTURE_2D, kocka_texture);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(0, 0, 0); // predna
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(0, golemina, 0);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(golemina, golemina, 0);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(golemina, 0, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(0, 0, golemina); // zadna
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(0, golemina, golemina);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(golemina, golemina, golemina);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(golemina, 0, golemina);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(0, 0, 0); // levo
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(0, 0, golemina);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(0, golemina, golemina);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(0, golemina, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(golemina, 0, 0); // desno
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(golemina, 0, golemina);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(golemina, golemina, golemina);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(golemina, golemina, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(0, 0, 0);// dole
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(golemina, 0, 0);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(golemina, 0, golemina);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(0, 0, golemina);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(0, golemina, 0);// gore
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(golemina, golemina, 0);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(golemina, golemina, golemina);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(0, golemina, golemina);
		gl.glEnd();

		gl.glPopMatrix();
		// gl.glEnable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	public void nacrtaj_pod(GLAutoDrawable glad, int size_x, int size_y) {
		final GL2 gl = glad.getGL().getGL2();

		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, spaceship_floor_texture);
		gl.glBegin(GL2.GL_QUADS);
		gl.glNormal3f(0, 1, 0);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		for (int i = 0; i < size_x; i++) {
			for (int j = 0; j < size_y; j++) {
				gl.glTexCoord2f(0, 0);
				gl.glVertex3f(i, -0.5f, j);
				gl.glTexCoord2f(1, 0);
				gl.glVertex3f(i + 1, -0.5f, j);
				gl.glTexCoord2f(1, 1);
				gl.glVertex3f(i + 1, -0.5f, j + 1);
				gl.glTexCoord2f(0, 1);
				gl.glVertex3f(i, -0.5f, j + 1);
			}
		}
		gl.glEnd();
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	private void nacrtaj_granica(GLAutoDrawable glad, int od_x, int od_y,
			int do_x, int do_y) {
		final GL2 gl = glad.getGL().getGL2();
		gl.glColor3f(0.3f, 0.3f, 0.6f);

		int visina = 2;

		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, dzid_texture);

		gl.glColor3f(0.5f, 0.5f, 0.5f);

		gl.glPushMatrix();

		for (int i = od_x; i < do_x; i++) {
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(i, -1, od_y); // predna
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(i, visina - 1, od_y);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(i + 1, visina - 1, od_y);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(i + 1, -1, od_y);
			gl.glEnd();
		}

		for (int i = od_x; i < do_x; i++) {
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(i, -1, do_y); // zadna
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(i, visina - 1, do_y);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(i + 1, visina - 1, do_y);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(i + 1, -1, do_y);
			gl.glEnd();
		}

		for (int i = od_y; i < do_y; i++) {
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(od_x, -1, i); // levo
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(od_x, visina - 1, i);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(od_x, visina - 1, i + 1);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(od_x, -1, i + 1);
			gl.glEnd();
		}

		for (int i = od_y; i < do_y; i++) {
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(do_x, -1, i); // desno
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(do_x, visina - 1, i);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(do_x, visina - 1, i + 1);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(do_x, -1, i + 1);
			gl.glEnd();
		}
		gl.glPopMatrix();

		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	public void nacrtaj_Koord_Sistem(GLAutoDrawable glad) {
		final GL2 gl = glad.getGL().getGL2();

		gl.glColor3f(1.0f, 0.0f, 0.0f);

		gl.glBegin(GL2.GL_LINES);

		gl.glVertex3f(0, 0.0f, 0);// x
		gl.glVertex3f(3, 0.0f, 0);

		gl.glColor3f(0.0f, 1.0f, 0.0f);
		gl.glVertex3f(0, 0.0f, 0);// y
		gl.glVertex3f(0, 3.0f, 0);

		gl.glColor3f(0.0f, 0.0f, 1.0f);
		gl.glVertex3f(0, 0.0f, 0);// z
		gl.glVertex3f(0, 0.0f, 3);

		gl.glEnd();

	}

	@Override
	public void init(GLAutoDrawable glad) {
		final GL2 gl = glad.getGL().getGL2();

		// gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		fizika = new Fizika();

		// fizika
		this.fizika.fizika_pod(new Vector3f(0.0f, -1.0f, 0.0f));
		this.fizika.fizika_granica(this.mapa.data.golemina_x,
				this.mapa.data.golemina_y);

		for (int i = 0; i < mapa.lista_objekti.size(); i++) {
			float x = mapa.lista_objekti.get(i).getX();
			float z = mapa.lista_objekti.get(i).getZ();
			this.fizika.fizika_objekt(new Vector3f(x, 0.0f, z));
		}
		Koordinati kor;

		while (true) {
			Random rnd = new Random();
			int a = rnd.nextInt(mapa.igraci_pozicii.size());
			if (slobodenRandom(a)) {
				kor = mapa.igraci_pozicii.get(a);
				break;
			}
		}

		// this.fizika.probna_kocka(new Vector3f(10.0f, 10.0f, 10.0f));
		this.fizika.initIgrac(new Vector3f(kor.x, 0, kor.z), new Vector3f(
				0.31f, 0.5f, 0.31f));
		//
		c.initCam(kor.x + 0.5f, 0, kor.z - 0.5f);

		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

		// svetlo
		float[] lightAmbient = { 0.5f, 0.5f, 0.5f, 1.0f };
		float[] lightDiffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] lightPosition = { 4.0f, 10.0f, 4.0f, 1.0f };

		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmbient, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPosition, 0);

		gl.glEnable(GL2.GL_LIGHT1);
		gl.glEnable(GL2.GL_LIGHTING);

		// tekstura
		gl.glEnable(GL2.GL_TEXTURE_2D);
		try {
			skybox_texture = new int[6];
			// File file = new
			// File(Glavna.class.getResource("/images/drakeq_bk.tga"));
			// Texture t = TextureIO.newTexture(file, true);
			// File file = new File("resources/images/drakeq_bk.tga");
			Texture t = TextureIO.newTexture(
					Glavna.class.getResource("/images/drakeq_bk.tga"), true,
					null);

			skybox_texture[0] = t.getTextureObject(gl);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[0]);

			// file = new File("resources/images/drakeq_dn.tga");
			t = TextureIO.newTexture(
					Glavna.class.getResource("/images/drakeq_dn.tga"), true,
					null);
			skybox_texture[1] = t.getTextureObject(gl);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[1]);

			// file = new File("resources/images/drakeq_ft.tga");
			t = TextureIO.newTexture(
					Glavna.class.getResource("/images/drakeq_ft.tga"), true,
					null);
			skybox_texture[2] = t.getTextureObject(gl);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[2]);

			// file = new File("resources/images/drakeq_lf.tga");
			t = TextureIO.newTexture(
					Glavna.class.getResource("/images/drakeq_lf.tga"), true,
					null);
			skybox_texture[3] = t.getTextureObject(gl);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[3]);

			// file = new File("resources/images/drakeq_rt.tga");
			t = TextureIO.newTexture(
					Glavna.class.getResource("/images/drakeq_rt.tga"), true,
					null);
			skybox_texture[4] = t.getTextureObject(gl);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[4]);

			// file = new File("resources/images/drakeq_up.tga");
			t = TextureIO.newTexture(
					Glavna.class.getResource("/images/drakeq_up.tga"), true,
					null);
			skybox_texture[5] = t.getTextureObject(gl);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, skybox_texture[5]);

			// file = new File("resources/images/spaceship_floor.jpg");
			t = TextureIO.newTexture(
					Glavna.class.getResource("/images/spaceship_floor.jpg"),
					true, null);
			spaceship_floor_texture = t.getTextureObject(gl);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, spaceship_floor_texture);

			// file = new File("resources/images/kocka.jpg");
			t = TextureIO.newTexture(
					Glavna.class.getResource("/images/kocka.jpg"), true, null);
			kocka_texture = t.getTextureObject(gl);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, kocka_texture);

			// file = new File("resources/images/wall.jpg");
			t = TextureIO.newTexture(
					Glavna.class.getResource("/images/wall.jpg"), true, null);
			dzid_texture = t.getTextureObject(gl);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, dzid_texture);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose(GLAutoDrawable glad) {
		//final GL2 gl = glad.getGL().getGL2();
	}

	public void nacrtaj_igrac(GLAutoDrawable glad, float x, float y, float z,
			float camPitch, float camYaw, boolean chase) {
		final GL2 gl = glad.getGL().getGL2();

		gl.glPushMatrix();
		gl.glTranslated(x + 0.5f, y, z + 0.5f);
		// gl.glRotatef(camPitch, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(-camYaw, 0.0f, 1.0f, 0.0f);
		// gl.glColor3f(0.8f, 0.8f, 0.8f);
		igrac_(glad, chase);
		gl.glPopMatrix();

		// glava
		gl.glPushMatrix();
		gl.glTranslated(x + 0.5f, y + 0.8f, z + 0.5f);
		gl.glRotatef(-camYaw, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(camPitch, 1.0f, 0.0f, 0.0f);
		// gl.glColor3f(0.54f, 0.31f, 0.6f);
		glava(glad, chase);
		gl.glPopMatrix();

	}

	public void igrac_(GLAutoDrawable glad, boolean chase) {
		final GL2 gl = glad.getGL().getGL2();
		// gl.glDisable(GL2.GL_LIGHTING);

		float telo[] = { 0f, 0f, 0f };
		float raka[] = { 0.329412f, 0.329412f, 0.329412f };

		if (chase == true) {
			telo = new float[] { 0.729f, 0.098f, 0.098f };
		} else {
			telo = new float[] { 0.137255f, 0.137255f, 0.556863f };
		}

		GLUquadric body = glu.gluNewQuadric();

		// telo
		gl.glPushMatrix();
		gl.glRotatef(-90, 1.0f, 0.0f, 0.0f);
		gl.glColor3f(telo[0], telo[1], telo[2]);
		glu.gluCylinder(body, 0, 0.25, 0.6, 15, 15);
		gl.glPopMatrix();

		// telo kapak
		gl.glPushMatrix();
		gl.glTranslatef(0f, 0.6f, 0f);
		gl.glColor3f(telo[0], telo[1], telo[2]);
		kapak(glad, 0.25f);
		gl.glPopMatrix();

		// leva raka
		gl.glPushMatrix();
		gl.glColor3f(raka[0], raka[1], raka[2]);
		gl.glTranslatef(-0.24f, 0.1f, 0f);
		gl.glRotatef(-90, 1.0f, 0f, 0.0f);
		glu.gluCylinder(body, 0, 0.1, 0.5, 15, 15);
		gl.glPopMatrix();

		// leva raka kapak
		gl.glPushMatrix();
		gl.glColor3f(raka[0], raka[1], raka[2]);
		gl.glTranslatef(-0.24f, 0.599f, 0f);
		kapak(glad, 0.1f);
		gl.glPopMatrix();

		// desna raka
		gl.glPushMatrix();
		gl.glColor3f(raka[0], raka[1], raka[2]);
		gl.glTranslatef(0.24f, 0.1f, 0f);
		gl.glRotatef(-90, 1.0f, 0.0f, 0.0f);
		glu.gluCylinder(body, 0, 0.1, 0.5, 15, 15);
		gl.glPopMatrix();

		// desna raka kapak
		gl.glPushMatrix();
		gl.glColor3f(raka[0], raka[1], raka[2]);
		gl.glTranslatef(0.24f, 0.599f, 0f);
		kapak(glad, 0.1f);
		gl.glPopMatrix();

		// gl.glEnable(GL2.GL_LIGHTING);

	}

	void kapak(GLAutoDrawable glad, float radius) {
		final GL2 gl = glad.getGL().getGL2();
		// gl.glDisable(GL2.GL_LIGHTING);

		gl.glPushMatrix();
		gl.glRotatef(90, 1.0f, 0.0f, 0.0f);

		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		for (float angle = 0f; angle < 2 * Math.PI; angle += 0.2) {
			gl.glVertex2f((float) Math.sin(angle) * radius,
					(float) Math.cos(angle) * radius);
		}
		gl.glEnd();
		gl.glPopMatrix();

		// gl.glEnable(GL2.GL_LIGHTING);
	}

	void glava(GLAutoDrawable glad, boolean chase) {
		final GL2 gl = glad.getGL().getGL2();
		// gl.glDisable(GL2.GL_LIGHTING);

		float usi[] = { 0f, 0f, 0f };
		float glava[] = { 0.329412f, 0.329412f, 0.329412f };
		if (chase == true) {
			usi = new float[] { 0.729f, 0.098f, 0.098f };
		} else {
			usi = new float[] { 0.137255f, 0.137255f, 0.556863f };
		}

		GLUquadric body = glu.gluNewQuadric();

		// levo oko
		gl.glPushMatrix();
		gl.glColor3f(0f, 0f, 0f);
		gl.glTranslatef(-0.12f, 0f, 0.20f);
		glu.gluSphere(body, 0.07, 15, 15);
		gl.glPopMatrix();

		// desno oko
		gl.glPushMatrix();
		gl.glColor3f(0f, 0f, 0f);
		gl.glTranslatef(0.12f, 0f, 0.20f);
		glu.gluSphere(body, 0.07, 15, 15);
		gl.glPopMatrix();

		// levo uvo
		gl.glPushMatrix();
		gl.glColor3f(usi[0], usi[1], usi[2]);
		gl.glTranslatef(0.22f, 0f, 0f);
		glu.gluSphere(body, 0.09, 10, 15);
		gl.glPopMatrix();

		// desno uvo
		gl.glPushMatrix();
		gl.glColor3f(usi[0], usi[1], usi[2]);
		gl.glTranslatef(-0.22f, 0f, 0f);
		glu.gluSphere(body, 0.09, 10, 15);
		gl.glPopMatrix();

		// sferesta glava
		gl.glPushMatrix();
		gl.glScalef(1.2f, 1, 1);
		gl.glColor3f(glava[0], glava[1], glava[2]);
		glu.gluSphere(body, 0.24, 15, 15);
		gl.glPopMatrix();

		// gl.glEnable(GL2.GL_LIGHTING);
	}

	public void render(GLAutoDrawable glad) {
		final GL2 gl = glad.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		if (c.ESC == true) {
			System.exit(0);
		}

		player.update();

		player.MovePlayer(fizika.igrac);

		Transform igrac_poz = new Transform();
		fizika.igrac.getWorldTransform(igrac_poz);

		glu.gluLookAt(0, 0, 0, 0, 0, 1, 0, 1, 0);

		// System.out.println("Koordinati na igrac X=" + -c.camX + " Y=" +
		// -c.camY + " Z=" + -c.camZ);
		gl.glRotatef(-c.camPitch, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(c.camYaw, 0.0f, 1.0f, 0.0f);
		nacrtaj_skybox(glad, 100);

		gl.glTranslatef(-igrac_poz.origin.x - 0.5f,
				-igrac_poz.origin.y - 0.75f, -igrac_poz.origin.z - 0.5f);// igrac

		nacrtaj_mapa(glad);
		nacrtaj_igraci(glad);
		nacrtaj_kursor(glad);

		fizika.getDynamicsWorld().stepSimulation(
				(float) (1.0f / (amountOfTicks * MAXFRAMERATE)));

		if (ticked == true) {
			// System.out.println("Proverka");
			fizika.proveriKolizii();
			ticked = false;
		}
		tr.beginRendering(glad.getWidth(), glad.getHeight());
		String nick = "Nickname: " + this.player.nickname;
		String score = "Score: " + this.player.score;
		String status = "Status: ";
		
		if(this.player.chase==true)
			status += "Chase";
		else
			status += "Run";

		tr.setColor(0.7f, 0.5f, 0.56f, 0.9f);
		tr.draw(nick, 6, glad.getHeight() - 20);
		tr.draw(score, 6, glad.getHeight() - 40);
		tr.draw(status, 6, glad.getHeight() - 60);
		tr.endRendering();

		if (c.Q == true) {
			xpoz = 100;
			tr.beginRendering(glad.getWidth(), glad.getHeight());

			tr.draw("Nickname", glad.getWidth() / 2 - 100, glad.getHeight()
					- (xpoz - 20));
			tr.draw("Score", glad.getWidth() / 2 + 50, glad.getHeight()
					- (xpoz - 20));
			tr.draw("__________________________", glad.getWidth() / 2 - 110,
					glad.getHeight() - (xpoz - 20));
			tr.draw(player.nickname, glad.getWidth() / 2 - 100,
					glad.getHeight() - (xpoz));
			tr.draw("" + player.score, glad.getWidth() / 2 + 50,
					glad.getHeight() - (xpoz));

			for (MPPlayer player : players.values()) {
				tr.setColor(0.7f, 0.5f, 0.56f, 0.9f);
				tr.draw(player.nickname, glad.getWidth() / 2 - 100,
						glad.getHeight() - (xpoz + 20));
				tr.draw("" + player.score, glad.getWidth() / 2 + 50,
						glad.getHeight() - (xpoz += 20));

			}
			tr.endRendering();

		}

		// System.out.println(gl.glGetString(GL2.GL_RENDERER));
		gl.glFlush();
	}

	@Override
	public void display(GLAutoDrawable glad) {

		long now = System.nanoTime();
		delta += (now - lastTime) / ns;
		lastTime = now;
		if (delta >= 1) { // fizika
			tick(glad);
			updates++;
			delta--;
			fps_counter = 0;
		}

		frames++;
		render(glad);
		if (System.currentTimeMillis() - timer > 1000) {
			timer += 1000;
			frame.setTitle("Ticks: " + updates + " Fps: " + frames);
			updates = 0;
			frames = 0;
		}
	}

	@Override
	public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {

		final GL2 gl = glad.getGL().getGL2();
		if (height <= 0) {
			height = 1;
		}
		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, h, 0.1, 100.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

	}

	private static void keyBindings(JPanel p, final JFrame frame,
			final Glavna jp) {
		ActionMap actionMap = p.getActionMap();
		InputMap inputMap = p.getInputMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "F1");
		actionMap.put("F1", new AbstractAction() {

			private static final long serialVersionUID = -6576101918414437189L;

			@Override
			public void actionPerformed(ActionEvent drawable) {
				
				fullScreen(frame);
			}
		});
	}

	protected static void fullScreen(JFrame f) {

		if (!isFullScreen) {
			f.dispose();
			f.setUndecorated(true);
			f.setVisible(true);
			f.setResizable(false);
			xgraphic = f.getSize();
			point = f.getLocation();
			f.setLocation(0, 0);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			f.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
			isFullScreen = true;
		} else {
			f.dispose();
			f.setUndecorated(false);
			f.setResizable(true);
			f.setLocation(point);
			f.setSize(xgraphic);
			f.setVisible(true);

			isFullScreen = false;
		}
	}

	@Override
	public void run() {

		String[] parts = window_size.split("x");
		int x = Integer.parseInt(parts[0]);
		int y = Integer.parseInt(parts[1]);

		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);

		final GLCanvas canvas = new GLCanvas(capabilities);
		Glavna jp = new Glavna();

		canvas.addGLEventListener(jp);

		// network.connect();
		final FPSAnimator animator = new FPSAnimator(canvas, 60);

		frame = new JFrame();
		frame.getContentPane().add(canvas);

		// canvas.setSize(1024, 768);
		if (fullscreen == true) {
			frame.setUndecorated(true);
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		} else {
			canvas.setSize(x, y);
		}

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (animator.isStarted()) {
					animator.stop();
				}
				System.exit(0);
			}
		});

		frame.setSize(frame.getContentPane().getPreferredSize());

		graphic_enviroment = GraphicsEnvironment.getLocalGraphicsEnvironment();

		GraphicsDevice[] devices = graphic_enviroment.getScreenDevices();

		/*for (GraphicsDevice graphicsDevice : devices) {
			// System.out.println(graphicsDevice.toString());
		}*/

		devices[0].getDisplayMode();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int windowX = Math.max(0, (screenSize.width - frame.getWidth()) / 2);
		int windowY = Math.max(0, (screenSize.height - frame.getHeight()) / 2);

		frame.setLocation(windowX, windowY);
		/**
         *
         */
		frame.setVisible(true);
		/*
		 * Time to add Button Control
		 */
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(0, 0));
		frame.add(p, BorderLayout.SOUTH);

		c = new Camera(canvas);
		canvas.addMouseMotionListener(c);
		canvas.addKeyListener(c);

		keyBindings(p, frame, jp);

		animator.start();
	}

	private void tick(GLAutoDrawable glad) {
		ticked = true;
		// prakjanje na koordinati
		update();
		igraci_sinhronizirani.clear();
		for (MPPlayer player : players.values()) {
			MPPlayer p = new MPPlayer();
			p.id = player.id;
			p.x = player.x;
			p.y = player.y;
			p.z = player.z;
			p.camPitch = player.camPitch;
			p.camYaw = player.camYaw;
			p.predX = player.predX;
			p.predY = player.predY;
			p.predZ = player.predZ;
			p.predCamPitch = player.predCamPitch;
			p.predCamYaw = player.predCamYaw;
			p.chase = player.chase;
			igraci_sinhronizirani.add(p);
			// System.out.println("Drugi " + player.id + " " + player.score);
		}
		// System.out.println("Jas " + player.id + " " + player.nickname);
		fizika.update_igraci(igraci_sinhronizirani);

		// for (int i = 0; i < fizika.igraci.size(); i++) {
		// System.out.println(fizika.igraci.get(i).isStaticObject());
		//
		// }
	}

	public void update() {

		// Update position
		// Send the player's X value
		Transform update = new Transform();
		fizika.igrac.getWorldTransform(update);

		PacketUpdateX packet = new PacketUpdateX();
		// packet.x = c.camX;
		packet.x = update.origin.x;
		network.client.sendUDP(packet);

		// Send the player's Y value
		PacketUpdateY packet1 = new PacketUpdateY();
		// packet1.y = c.camY;
		packet1.y = update.origin.y;
		network.client.sendUDP(packet1);

		PacketUpdateZ packet2 = new PacketUpdateZ();
		// packet2.z = c.camZ;
		packet2.z = update.origin.z;
		network.client.sendUDP(packet2);

		PacketUpdateCamPitch packet3 = new PacketUpdateCamPitch();
		packet3.camPitch = c.camPitch;
		network.client.sendUDP(packet3);

		PacketUpdateCamYaw packet4 = new PacketUpdateCamYaw();
		packet4.camYaw = c.camYaw;
		network.client.sendUDP(packet4);

		Chase packet5 = new Chase();
		packet5.chase = player.chase;
		network.client.sendUDP(packet5);

		Nickname packet6 = new Nickname();
		packet6.nickname = player.nickname;
		network.client.sendUDP(packet6);

	}

	private float PresmetajVistinskaRazlika(float a1, float a2) {
		double agol1 = Double.valueOf(a1); // koga se rabotese so float imase
											// greski
		double agol2 = Double.valueOf(a2); // pri presmetki zaradi zaokruzuvanje
											// na decimali
		double angle = (Math.abs(agol1 - agol2)) % 360;

		if (angle > 180) {
			angle = 360 - angle;
		}

		if ((agol1 + angle) % 360 == agol2) {
			return (float) angle;
		}
		return (float) -angle;
	}

	public static boolean slobodenRandom(int br) {
		boolean slobodno = true;
		for (MPPlayer player : players.values()) {
			if ((player.x < Glavna.mapa.igraci_pozicii.get(br).x + 1 && player.x > Glavna.mapa.igraci_pozicii
					.get(br).x - 1)
					&& (player.z < Glavna.mapa.igraci_pozicii.get(br).z + 1 && player.z > Glavna.mapa.igraci_pozicii
							.get(br).z - 1)) {
				slobodno = false;
				break;
			}
		}
		return slobodno;
	}

}
