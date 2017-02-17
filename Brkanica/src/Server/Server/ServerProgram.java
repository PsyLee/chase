package Server.Server;

import Server.Mapa.*;
import Server.Player.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerProgram extends Listener implements Runnable {

    static Server server;
    static final int port = 27960;
    static Map<Integer, Player> players = new ConcurrentHashMap<Integer, Player>();
    static Mapa mapa;

    @Override
    public void run() {
        server = new Server(5000, 5000);
        server.getKryo().register(PacketUpdateX.class);
        server.getKryo().register(PacketUpdateY.class);
        server.getKryo().register(PacketUpdateZ.class);
        server.getKryo().register(PacketUpdateCamPitch.class);
        server.getKryo().register(PacketUpdateCamYaw.class);
        server.getKryo().register(Chase.class);
        server.getKryo().register(Nickname.class);
        server.getKryo().register(Faten.class);
        server.getKryo().register(PlayerID.class);
        server.getKryo().register(PacketAddPlayer.class);
        server.getKryo().register(PacketRemovePlayer.class);
        server.getKryo().register(Koordinati.class);
        server.getKryo().register(Mapa.class);
        server.getKryo().register(mapaDATA.class);
        server.getKryo().register(java.util.ArrayList.class);
        server.getKryo().register(int[][].class);
        server.getKryo().register(int[].class);
        try {
            server.bind(port, port);
        } catch (IOException ex) {
            Logger.getLogger(ServerProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
        server.start();
        server.addListener(new ServerProgram());
        mapa = new Mapa(20, 20, 100);
        mapa.generiraj();
        mapa.pecati_matrica();
        System.out.println("The server is ready");

    }

    @Override
    public void connected(Connection c) {
        Player player = new Player();
        player.c = c;
        player.id = c.getID();

        PlayerID packetID = new PlayerID();
        packetID.id = c.getID();
       // System.out.println(packetID.id);

        server.sendToTCP(c.getID(), packetID);

        PacketAddPlayer packet = new PacketAddPlayer();
        packet.id = c.getID();
        server.sendToAllExceptTCP(c.getID(), packet);
        server.sendToTCP(c.getID(), mapa);

        for (Player p : players.values()) {
            PacketAddPlayer packet2 = new PacketAddPlayer();
            packet2.id = p.c.getID();
            c.sendTCP(packet2);
        }

        players.put(c.getID(), player);
        System.out.println("Connection received.");
    }

    @Override
    public void received(Connection c, Object o) {
        if (o instanceof PacketUpdateX) {
            PacketUpdateX packet = (PacketUpdateX) o;
            players.get(c.getID()).x = packet.x;

            packet.id = c.getID();
            server.sendToAllExceptUDP(c.getID(), packet);
            //System.out.println("received and sent an update X packet");

        } else if (o instanceof PacketUpdateY) {
            PacketUpdateY packet = (PacketUpdateY) o;
            players.get(c.getID()).y = packet.y;

            packet.id = c.getID();
            server.sendToAllExceptUDP(c.getID(), packet);

        } else if (o instanceof PacketUpdateZ) {
            PacketUpdateZ packet = (PacketUpdateZ) o;
            players.get(c.getID()).z = packet.z;

            packet.id = c.getID();
            server.sendToAllExceptUDP(c.getID(), packet);

        } else if (o instanceof PacketUpdateCamPitch) {
            PacketUpdateCamPitch packet = (PacketUpdateCamPitch) o;
            players.get(c.getID()).camPitch = packet.camPitch;

            packet.id = c.getID();
            server.sendToAllExceptUDP(c.getID(), packet);

        } else if (o instanceof PacketUpdateCamYaw) {
            PacketUpdateCamYaw packet = (PacketUpdateCamYaw) o;
            players.get(c.getID()).camYaw = packet.camYaw;

            packet.id = c.getID();
            server.sendToAllExceptUDP(c.getID(), packet);

        } else if (o instanceof Chase) {
            Chase packet = (Chase) o;
            players.get(c.getID()).chase = packet.chase;

            packet.id = c.getID();
            server.sendToAllExceptUDP(c.getID(), packet);

        } else if (o instanceof Nickname) {
            Nickname packet = (Nickname) o;
            players.get(c.getID()).nickname = packet.nickname;

            packet.id = c.getID();
            server.sendToAllExceptUDP(c.getID(), packet);
        } else if (o instanceof Faten) {
            Faten packet = (Faten) o;
            players.get(packet.id).score += packet.id_score;
            players.get(packet.id2).score += packet.id2_score;

            packet.id_score=players.get(packet.id).score;
            packet.id2_score=players.get(packet.id2).score;
            
            
            server.sendToAllTCP(packet);

        }
    }

    @Override
    public void disconnected(Connection c) {
        players.remove(c.getID());
        PacketRemovePlayer packet = new PacketRemovePlayer();
        packet.id = c.getID();
        server.sendToAllExceptTCP(c.getID(), packet);
        System.out.println("Connection dropped.");
    }

}
