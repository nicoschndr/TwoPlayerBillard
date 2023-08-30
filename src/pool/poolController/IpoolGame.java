package pool.poolController;

import pool.poolModel.Ball;
import pool.poolModel.Player;

import java.util.ArrayList;

public interface IpoolGame {
    boolean checkWhiteOverBall(float mouseX, float mouseY);

    void setWhiteBall(float mouseX, float mouseY);

    ArrayList<Ball> getBalls();

    void initialize(int widthBound, int heightBound);

    float rotateQueue(float mouseX, float mouseY, float ballX, float ballY);

    void setPottedBalls();

    boolean checkFoul();

    boolean getBallInHand();

    void setPottedBallCount(int pottedBallsCount);

    boolean checkShot();

    void ballInHand(float mouseX, float mouseY);

    void shoot(int power);

    ArrayList<Player> getPlayers();

    void switchPlayer();

    void pottedDuringShot();

    boolean checkWin();

    void addPlayers();

    void physics();
    void setTeam();
}
