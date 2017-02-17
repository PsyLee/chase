package Player;

import Engine.Glavna;

import com.bulletphysics.dynamics.RigidBody;

import javax.vecmath.Vector3f;

public class Player {

    float movevel = 0.05f;
    public boolean chase;
    public String nickname;
    public int id;
    public int score;

    //da napraam konstuktor so 2 vrednosti i tie da bidat od mrezata pocetnite poz
    public void update() {
        if (Glavna.c.W) {
            if (Glavna.c.camPitch != 90 && Glavna.c.camPitch != -90) // if we are facing
            // directly up or down,
            // we don't go forward,
            // it will be commented
            // out, when there will
            // be gravity
            {
                Glavna.c.moveCamera(movevel, 0.0f); // move forward 
            }
            Glavna.c.moveCameraUp(movevel, 0.0f); // move up/down
        }
        if (Glavna.c.S) {
            // same, just we use 180 degrees, so we move at the different
            // direction (move back)
            if (Glavna.c.camPitch != 90 && Glavna.c.camPitch != -90) {
                Glavna.c.moveCamera(movevel, 180.0f);
            }
            Glavna.c.moveCameraUp(movevel, 180.0f);
        }
        if (Glavna.c.D) {
            Glavna.c.moveCamera(movevel, 90.0f);
        }
        if (Glavna.c.A) {
            Glavna.c.moveCamera(movevel, 270.0f);
        }

    }

    public void MovePlayer(RigidBody igrac) {
        Vector3f sila = new Vector3f(0, 0, 0);
        float brzina = 3.5f;

        if(Glavna.c.JUMP==0){
            igrac.setLinearVelocity(new Vector3f(igrac.getLinearVelocity(new Vector3f()).x, 1, igrac.getLinearVelocity(new Vector3f()).z));
        }
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
//            System.out.println(sila.x + " " + sila.y + " " + sila.z);
            igrac.setLinearVelocity(sila);
        } else {
            igrac.setLinearVelocity(new Vector3f(0, igrac.getLinearVelocity(new Vector3f()).y, 0));
        }
    }
    
}
