package Engine;

import Player.Faten;
import Player.MPPlayer;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import static Engine.Glavna.network;

public class Fizika {

    DynamicsWorld dynamicsWorld;
    BroadphaseInterface broadface;
    CollisionConfiguration configuration;
    CollisionDispatcher dispacher;
    SequentialImpulseConstraintSolver solver;

    RigidBody fizika_pod;

    LinkedList<RigidBody> objekti_mapa = new LinkedList<RigidBody>();
    LinkedList<RigidBody> igraci = new LinkedList<RigidBody>(); // drugi igraci

    public RigidBody igrac; //ti

    public Fizika() {
        broadface = new DbvtBroadphase();
        configuration = new DefaultCollisionConfiguration();
        dispacher = new CollisionDispatcher(configuration);
        solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispacher, broadface, solver, configuration);
        dynamicsWorld.setGravity(new Vector3f(0, -3, 0));
    }

    public void initIgrac(Vector3f pozicija, Vector3f golemina) {
        CollisionShape igrac = new CylinderShape(golemina);
//        CollisionShape igrac = new BoxShape(golemina);
        MotionState state = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), pozicija, 1.0f)));
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(1.0f, state, igrac, new Vector3f(0, 0, 0));
        this.igrac = new RigidBody(info);
        info.friction = 0.4f;
        this.igrac.setUserPointer("Tvoj Igrac");
        this.igrac.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        this.dynamicsWorld.addRigidBody(this.igrac);
    }

    public void fizika_granica(int mapa_x, int mapa_y) {
        CollisionShape levo = new BoxShape(new Vector3f(0.5f, 2.0f, (float) mapa_y / 2.0f));
        MotionState state_levo = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1.0f), new Vector3f(-1.02f, 0.0f, ((float) mapa_y / 2.0f) - 0.5f), 1.0f)));
        RigidBodyConstructionInfo info_levo = new RigidBodyConstructionInfo(0.0f, state_levo, levo, new Vector3f(0.0f, 0.0f, 0.0f));
        RigidBody levo_granica = new RigidBody(info_levo);
        this.dynamicsWorld.addRigidBody(levo_granica);

        CollisionShape desno = new BoxShape(new Vector3f(0.5f, 2.0f, (float) mapa_y / 2.0f));
        MotionState state_desno = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1.0f), new Vector3f(mapa_x, 0.0f, ((float) mapa_y / 2.0f) - 0.5f), 1.0f)));
        RigidBodyConstructionInfo info_desno = new RigidBodyConstructionInfo(0.0f, state_desno, desno, new Vector3f(0.0f, 0.0f, 0.0f));
        RigidBody desno_granica = new RigidBody(info_desno);
        this.dynamicsWorld.addRigidBody(desno_granica);

        CollisionShape napred = new BoxShape(new Vector3f((float) mapa_x / 2.0f, 2.0f, 0.5f));
        MotionState state_napred = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1.0f), new Vector3f(((float) mapa_y / 2.0f) - 0.5f, 0.0f, -1.02f), 1.0f)));
        RigidBodyConstructionInfo info_napred = new RigidBodyConstructionInfo(0.0f, state_napred, napred, new Vector3f(0.0f, 0.0f, 0.0f));
        RigidBody napred_granica = new RigidBody(info_napred);
        this.dynamicsWorld.addRigidBody(napred_granica);

        CollisionShape nazad = new BoxShape(new Vector3f((float) mapa_x / 2.0f, 2.0f, 0.5f));
        MotionState state_nazad = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1.0f), new Vector3f(((float) mapa_y / 2.0f) - 0.5f, 0.0f, mapa_y), 1.0f)));
        RigidBodyConstructionInfo info_nazad = new RigidBodyConstructionInfo(0.0f, state_nazad, nazad, new Vector3f(0.0f, 0.0f, 0.0f));
        RigidBody nazad_granica = new RigidBody(info_nazad);
        this.dynamicsWorld.addRigidBody(nazad_granica);
    }

    public void fizika_pod(Vector3f pozicija) {
        CollisionShape pod = new StaticPlaneShape(new Vector3f(0.0f, 1.0f, 0.0f), 0.0f);
        MotionState state = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1.0f), pozicija, 1.0f)));
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(0.0f, state, pod, new Vector3f(0.0f, 0.0f, 0.0f));
        this.fizika_pod = new RigidBody(info);
        fizika_pod.setUserPointer("Pod");
        this.dynamicsWorld.addRigidBody(fizika_pod);
    }

    public void fizika_objekt(Vector3f pozicija) {
        pozicija.sub(new Vector3f(0, 0.5f, 0));

        CollisionShape objekt = new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f));//golemina na kockata
        MotionState state = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), pozicija, 1.0f)));
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(0.0f, state, objekt, new Vector3f(0, 0, 0));
        RigidBody obj = new RigidBody(info);
        obj.setUserPointer("Objekt");
        obj.setCollisionFlags(obj.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
        this.objekti_mapa.add(obj);
        this.dynamicsWorld.addRigidBody(obj);
    }

    public void fizika_igraci(Vector3f pozicija, Vector3f golemina, int id) { // drugite igraci, golemina se misli na halfextends
        CollisionShape igrac_nekoj = new BoxShape(golemina);
        MotionState state = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), pozicija, 1.0f)));
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(0.0f, state, igrac_nekoj, new Vector3f(0, 0, 0));
        RigidBody igr = new RigidBody(info);
        igr.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        igr.setUserPointer(id);
        igr.setLinearVelocity(new Vector3f(-1, -1, -1));
        this.igraci.add(igr);
        this.dynamicsWorld.addRigidBody(igr);
    }

    private void izbrisi_igraci() {
        for (int i = 0; i < this.igraci.size(); i++) {
            dynamicsWorld.removeRigidBody(igraci.get(i));
        }
        this.igraci.clear();
    }

    public DynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }

    private void dodaj_igraci(LinkedList<MPPlayer> igraci_sinhronizirani) {
        for (int i = 0; i < igraci_sinhronizirani.size(); i++) {
            float x = igraci_sinhronizirani.get(i).x;
            float y = igraci_sinhronizirani.get(i).y;
            float z = igraci_sinhronizirani.get(i).z;
            this.fizika_igraci(new Vector3f(x, y, z), new Vector3f(0.5f, 0.5f, 0.5f), igraci_sinhronizirani.get(i).id);
        }
    }

    void update_igraci(LinkedList<MPPlayer> igraci_sinhronizirani) {

        this.izbrisi_igraci();
        this.dodaj_igraci(igraci_sinhronizirani);
    }

    public void MovePlayer() {
        Vector3f sila = new Vector3f(0, 0, 0);
        float brzina = 3.5f;

        if (Glavna.c.UP == 0) {
            Glavna.c.PlayerMove(0);
            sila.add(new Vector3f(Glavna.c.player_move_x * brzina, 0, Glavna.c.player_move_z * brzina));
        }
        if (Glavna.c.UP == 1) {

        }
        if (Glavna.c.UP == 2) {

        }
        if (Glavna.c.DOWN == 0) {
            Glavna.c.PlayerMove(180);
            sila.add(new Vector3f(Glavna.c.player_move_x * brzina, 0, Glavna.c.player_move_z * brzina));
        }
        if (Glavna.c.DOWN == 1) {

        }
        if (Glavna.c.DOWN == 2) {
            //ne pravi nisto
        }
        if (Glavna.c.LEFT == 0) {
            Glavna.c.PlayerMove(-90);
            sila.add(new Vector3f(Glavna.c.player_move_x * brzina, 0, Glavna.c.player_move_z * brzina));
        }
        if (Glavna.c.LEFT == 1) {

            Glavna.c.LEFT = 2;
        }
        if (Glavna.c.LEFT == 2) {
            //ne pravi nisto
        }
        if (Glavna.c.RIGHT == 0) {
            Glavna.c.PlayerMove(90);
            sila.add(new Vector3f(Glavna.c.player_move_x * brzina, 0, Glavna.c.player_move_z * brzina));
        }
        if (Glavna.c.RIGHT == 1) {

            Glavna.c.RIGHT = 2;
        }
        if (Glavna.c.RIGHT == 2) {
            //ne pravi nisto
        }

        if (Glavna.c.UP == 0 || Glavna.c.DOWN == 0 || Glavna.c.LEFT == 0 || Glavna.c.RIGHT == 0) {
            sila.y = igrac.getLinearVelocity(new Vector3f()).y;
            igrac.setLinearVelocity(sila);
        } else {
            igrac.setLinearVelocity(new Vector3f(0, igrac.getLinearVelocity(new Vector3f()).y, 0));
        }
    }

    public void proveriKolizii() {
        int br_kolizii = dynamicsWorld.getDispatcher().getNumManifolds();

//        System.out.println(br_kolizii);
        for (int i = 0; i < br_kolizii; i++) {
            PersistentManifold pm = dynamicsWorld.getDispatcher().getManifoldByIndexInternal(i);

            try {
                RigidBody body1 = (RigidBody) pm.getBody0();
                RigidBody body2 = (RigidBody) pm.getBody1();

                String b = body1.getUserPointer().toString();
                String b2 = body2.getUserPointer().toString();
                if ((b.startsWith("Tvoj Igrac") && isNumeric(b2))
                        || (b2.startsWith("Tvoj Igrac") && isNumeric(b))) {
                    if (isNumeric(b2) && isNumeric(b)) {

                    } else if (isNumeric(b)) {
                        if (Glavna.players.get(Integer.parseInt(b)).chase == Glavna.player.chase) {
                          //  System.out.println("soigraci");
                            //ako fati soigrac
                            //  Glavna.player.score -= 2;
                            izbrisi_igraci();

                            Faten packet6 = new Faten();
                            packet6.id = Integer.parseInt(b);
                            packet6.id2 = Glavna.player.id;
                            packet6.id_score -= 2;
                            packet6.id2_score -= 2;
                            network.client.sendUDP(packet6);

                        } else {
                           // System.out.println("Fateni");
                            Faten packet6 = new Faten();

                            if (Glavna.player.chase == true && Glavna.players.get(Integer.parseInt(b)).chase == false) {
                                packet6.id2_score += 2;
                                packet6.id_score -= 1;
                            } else if (Glavna.player.chase == false && Glavna.players.get(Integer.parseInt(b)).chase == true) {
                                packet6.id2_score -= 1;
                                packet6.id_score += 2;
                            }

                            packet6.id = Integer.parseInt(b);
                            packet6.id2 = Glavna.player.id;

                            network.client.sendUDP(packet6);
                            izbrisi_igraci();
                        }
                    } else if (isNumeric(b2)) {
                        if (Glavna.players.get(Integer.parseInt(b2)).chase == Glavna.player.chase) {
                           // System.out.println("soigraci");
                            //ako fati soigrac
                            //  Glavna.player.score -= 2;
                            izbrisi_igraci();

                            Faten packet6 = new Faten();
                            packet6.id = Integer.parseInt(b2);
                            packet6.id2 = Glavna.player.id;
                            packet6.id_score -= 2;
                            packet6.id2_score -= 2;
                            network.client.sendUDP(packet6);

                        } else {

                          //  System.out.println("Fateni");
                            Faten packet6 = new Faten();

                            if (Glavna.player.chase == true && Glavna.players.get(Integer.parseInt(b2)).chase == false) {
                                packet6.id2_score += 2;
                                packet6.id_score -= 1;
                            } else if (Glavna.player.chase == false && Glavna.players.get(Integer.parseInt(b2)).chase == true) {
                                packet6.id2_score -= 1;
                                packet6.id_score += 2;
                            }

                            packet6.id = Integer.parseInt(b2);
                            packet6.id2 = Glavna.player.id;

                            network.client.sendUDP(packet6);
                            izbrisi_igraci();

                        }
                    }

                 //   System.out.println("Se sudrija: " + ((RigidBody) pm.getBody0()).getUserPointer() + " i " + ((RigidBody) pm.getBody1()).getUserPointer());
                  //  System.out.println();
                }
            } catch (Exception ex) {
            }
        }
    }

    public static boolean isNumeric(String str) {
        try {
           Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
