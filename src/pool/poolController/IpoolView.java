package pool.poolController;

import pool.poolModel.Ball;

import java.util.ArrayList;

public interface IpoolView {
    void serverDrawGame(ArrayList<Ball> balls);

    void serverDrawQueue(ArrayList<Ball> balls, float angle, float queueX, float queueY);

    void serverDrawBall(ArrayList<Ball> balls);
}
