package pool.poolController;

import pool.poolModel.Ball;
import pool.poolModel.Player;
import pool.poolModel.Vector;

import java.util.ArrayList;

public interface IpoolController {
    void nextFrame();

    void initialize(int width, int height);

    void update(float mouseX, float mouseY);

    void mousePressed();

    void mouseNotPressed();

    boolean getBallInHand();

    boolean checkShot();

    void setPottedBallCount(int puttedBallCount);

    ArrayList<Player> getPlayers();

    void addPlayers();

    void keyIsPressed();

    void keyIsReleased();

    boolean checkWin();

    Vector getQueue();

    ArrayList<Ball> getBalls();

    float getAngle();
}