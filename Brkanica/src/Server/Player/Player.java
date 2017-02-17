package Server.Player;

import com.esotericsoftware.kryonet.Connection;


public class Player {
        public int id;
	public float x, y,z;
        public float camPitch, camYaw;
	public Connection c;
        public boolean chase;
        public String nickname;
        public int score;
}
