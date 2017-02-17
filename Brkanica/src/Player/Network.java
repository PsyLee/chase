package Player;

import Engine.Glavna;
import Engine.Koordinati;
import Engine.Mapa;
import Engine.mapaDATA;

import com.bulletphysics.linearmath.Transform;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.Random;

public class Network extends Listener {

    public Client client;
    //String ip = "192.168.0.103";
    String ip;
    int port = 27960;
    public boolean moze = false;
    
//      Network(String IP){
//      ip = IP;
//      }
    
    public void setIP(String IP) {
        ip = IP;
    }
    
     public String getIP() {
        return this.ip;
    }
    

    public void connect() {
        client = new Client(5000, 5000);
        client.getKryo().register(PacketUpdateX.class);
        client.getKryo().register(PacketUpdateY.class);
        client.getKryo().register(PacketUpdateZ.class);
        client.getKryo().register(PacketUpdateCamPitch.class);
        client.getKryo().register(PacketUpdateCamYaw.class);
        client.getKryo().register(Chase.class);
        client.getKryo().register(Nickname.class);
        client.getKryo().register(Faten.class);
        client.getKryo().register(PlayerID.class);
        client.getKryo().register(PacketAddPlayer.class);
        client.getKryo().register(PacketRemovePlayer.class);
        client.getKryo().register(Koordinati.class);
        client.getKryo().register(Mapa.class);
        client.getKryo().register(mapaDATA.class);
        client.getKryo().register(java.util.ArrayList.class);
        client.getKryo().register(int[][].class);
        client.getKryo().register(int[].class);
        client.addListener(this);

        client.start();
        try {
            client.connect(5000, ip, port, port);
            moze=true;
            
        } catch (IOException e) {
          //  e.printStackTrace();
        }
    }

    public void received(Connection c, Object o) {
        if (o instanceof PacketAddPlayer) {
            PacketAddPlayer packet = (PacketAddPlayer) o;
            MPPlayer newPlayer = new MPPlayer();
            newPlayer.id = packet.id;
            Glavna.players.put(packet.id, newPlayer);

        } else if (o instanceof PacketRemovePlayer) {
            PacketRemovePlayer packet = (PacketRemovePlayer) o;
            Glavna.players.remove(packet.id);

        } else if (o instanceof PacketUpdateX) {
            PacketUpdateX packet = (PacketUpdateX) o;
            Glavna.players.get(packet.id).predX = Glavna.players.get(packet.id).x;
            Glavna.players.get(packet.id).x = packet.x;

        } else if (o instanceof PacketUpdateY) {
            PacketUpdateY packet = (PacketUpdateY) o;
            Glavna.players.get(packet.id).predY = Glavna.players.get(packet.id).y;
            Glavna.players.get(packet.id).y = packet.y;

        } else if (o instanceof PacketUpdateZ) {
            PacketUpdateZ packet = (PacketUpdateZ) o;
            Glavna.players.get(packet.id).predZ = Glavna.players.get(packet.id).z;
            Glavna.players.get(packet.id).z = packet.z;

        } else if (o instanceof Mapa) {
            Glavna.mapa = (Mapa) o;
          //  Glavna.mapa.pecati_matrica();
           /* for (Iterator<Koordinati> it = Glavna.mapa.igraci_pozicii.iterator(); it.hasNext();) {
                Koordinati i = it.next();
                System.out.print("[" + i.x + "," + i.z + "]    ");
            }*/

        } else if (o instanceof PacketUpdateCamPitch) {
            PacketUpdateCamPitch packet = (PacketUpdateCamPitch) o;
            Glavna.players.get(packet.id).predCamPitch = Glavna.players.get(packet.id).camPitch;
            Glavna.players.get(packet.id).camPitch = packet.camPitch;

        } else if (o instanceof PacketUpdateCamYaw) {
            PacketUpdateCamYaw packet = (PacketUpdateCamYaw) o;
            Glavna.players.get(packet.id).predCamYaw = Glavna.players.get(packet.id).camYaw;
            Glavna.players.get(packet.id).camYaw = packet.camYaw;

        } else if (o instanceof Chase) {
            Chase packet = (Chase) o;
            Glavna.players.get(packet.id).chase = packet.chase;

        } else if (o instanceof Nickname) {
            Nickname packet = (Nickname) o;
            Glavna.players.get(packet.id).nickname = packet.nickname;

        } else if (o instanceof PlayerID) {
            PlayerID packet = (PlayerID) o;
           // System.out.println("Primeno " + packet.id);
            Glavna.player.id = packet.id;

        } else if (o instanceof Faten) {
            Faten packet = (Faten) o;
            //ako moeto id e prvoto id
            if (Glavna.player.id == packet.id) {
                
                Glavna.player.chase = !Glavna.player.chase;
                Glavna.player.score = packet.id_score;
                Glavna.players.get(packet.id2).score = packet.id2_score;
                
                //proverka za slobodna random pozicija
                Koordinati kor;
                while (true) {
                    Random rnd = new Random();
                    int a = rnd.nextInt(Glavna.mapa.igraci_pozicii.size());
                    if (Glavna.slobodenRandom(a)) {
                        kor = Glavna.mapa.igraci_pozicii.get(a);
                        break;
                    }
                }
                // premestuvanej na nekoja random pozicija
                Transform update = new Transform();
                Glavna.fizika.igrac.getWorldTransform(update);
              //  System.out.println("Momentalni " + update.origin.x + " " + update.origin.z);
                update.origin.x = kor.x;
                update.origin.z = kor.z;
               // System.out.println("Novi " + update.origin.x + " " + update.origin.z);
                Glavna.fizika.igrac.setWorldTransform(update);

                //  Glavna.fizika.initIgrac(new Vector3f(kor.x, 1, kor.z), new Vector3f(0.25f, 0.25f, 0.25f));
                // Glavna.c.initCam(kor.x + 0.5f, 0, kor.z - 0.5f);
                
                // dokolku mojata id e ednakov so id2
            } else if (Glavna.player.id == packet.id2) {
                Glavna.player.chase = !Glavna.player.chase;
                Glavna.player.score = packet.id2_score;
                Glavna.players.get(packet.id).score = packet.id_score;
                
                //proverka za slobodna random pozicija
                Koordinati kor;
                while (true) {
                    Random rnd = new Random();
                    int a = rnd.nextInt(Glavna.mapa.igraci_pozicii.size());
                    if (Glavna.slobodenRandom(a)) {
                        kor = Glavna.mapa.igraci_pozicii.get(a);
                        break;
                    }
                }

                //translacina na random pozicija
                Transform update = new Transform();
                Glavna.fizika.igrac.getWorldTransform(update);
              //  System.out.println("Momentalni " + update.origin.x + " " + update.origin.z);
                update.origin.x = kor.x;
                update.origin.z = kor.z;
              //  System.out.println("Novi " + update.origin.x + " " + update.origin.z);
                Glavna.fizika.igrac.setWorldTransform(update);
                // Glavna.fizika.initIgrac(new Vector3f(kor.x, 1, kor.z), new Vector3f(0.25f, 0.25f, 0.25f));
                // Glavna.c.initCam(kor.x + 0.5f, 0, kor.z - 0.5f);

               // dokolku nikoe od id ne e moe (se doprele dva razlicni igraci)
                //se menja samo score zs oni smi si menjaat pozicija
            }else {
                Glavna.players.get(packet.id).score = packet.id_score;
                Glavna.players.get(packet.id2).score = packet.id2_score;
            }
           // Glavna.player.id = packet.id;
            
        }
    }
}
