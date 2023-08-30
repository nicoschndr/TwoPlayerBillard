package pool.poolController;

import pool.poolModel.Ball;
import pool.poolModel.Player;
import pool.poolModel.Vector;
import pool.poolModel.poolGame;

import java.util.ArrayList;

/**
 * This class creates and uses the {@link poolGame}. It processes mouse and key inputs and send all necessary data to
 * the view so that the application can be drawn.
 */
public class poolController implements IpoolController {
    private IpoolView view;
    private IpoolGame game;
    private gameState state;
    private boolean mousePressed = false;
    private float mouseX, mouseY;
    private int power;
    private float queueX = 35f, queueY = -8.75f, angle;

    public poolController(IpoolView view) {
        this.view = view;
        this.game = new poolGame();
        this.state = gameState.TITLE_SCREEN;
    }

    /**
     * This method changes the values of the variables that stand for the current location of the mouse.
     *
     * @param mX the current location of the mouse on the x-Axis
     * @param mY the current location of the mouse on the y-Axis
     */
    @Override
    public void update(float mX, float mY) {
        mouseX = mX;
        mouseY = mY;
    }

    /**
     * This method is called every frame. It calls the draw methods of the view and calls methods in the game that are
     * needed every frame.
     */
    public void nextFrame() {
        game.checkShot();
        if (!game.getBallInHand())
            game.physics();
        game.setPottedBalls();
        game.pottedDuringShot();
        game.checkFoul();
        if (!game.checkShot()) {
            game.ballInHand(mouseX, mouseY);
        }
        game.setTeam();

        if(game.getBallInHand() && mousePressed) {
            game.setWhiteBall(mouseX, mouseY);
        }
        game.checkWin();
        ArrayList<Ball> balls = game.getBalls();
        view.serverDrawGame(balls);
        for (Ball ball : balls) {
            view.serverDrawBall(balls);
            if (!game.checkShot() && !ball.getPotted() && !ball.getFoul() && !game.getBallInHand()) {
                if (mousePressed) {
                    if (ball.getBallId() == 0) {
                        angle = game.rotateQueue(mouseX, mouseY, ball.getLocation().getVectorX(), ball.getLocation().getVectorY());
                    }
                }
            }
        }
        view.serverDrawQueue(balls, angle, queueX, queueY);
    }

    /**
     * This method is called when the space key is pressed. It sets up the power of shot and the new queue position
     * depending on the power value.
     */
    public void keyIsPressed() {
        ArrayList<Ball> balls = game.getBalls();
        for (Ball ball : balls) {
            int id = ball.getBallId();
            if (id == 0) {
                if (!game.checkShot()) {
                    if (power < 30) {
                        queueX += 3;
                        queueY += 0;
                        power++;
                    }
                }
            }
        }
    }

    /**
     * This method is called when the space key is released. it then calls the shoot() method in {@link poolGame}
     */
    public void keyIsReleased() {
        if (!game.checkShot()) {
            game.shoot(power);
            queueX = 35f;
            queueY = (float) -8.75;
            power = 0;
        }
    }

    /**
     * This method initializes the game (adds balls).
     *
     * @param widthBound  width of the window the game is displayed in
     * @param heightBound height of the window the game is displayed in
     */
    public void initialize(int widthBound, int heightBound) {
        game.initialize(widthBound, heightBound);
    }

    /**
     * This method is called if the mouse is pressed and sets a boolean that indicated that to true.
     */
    public void mousePressed() {
        mousePressed = true;
    }

    /**
     * This method is called if the mouse is pressed and sets a boolean that indicated that to true.
     */
    public void mouseNotPressed() {
        mousePressed = false;
    }

    /**
     * This method return an ArrayList where all players are saved in.
     *
     * @return an ArrayList where all players are saved in
     */
    public ArrayList<Player> getPlayers() {
        return game.getPlayers();
    }

    /**
     * This method calls the checkShot() method in {@link poolGame}.
     */
    public boolean checkShot() {
        return game.checkShot();
    }

    /**
     * This method calls the setPottedBalls() method in {@link poolGame}.
     *
     * @param pottedBallsCount amount of potted balls as integer
     */
    public void setPottedBallCount(int pottedBallsCount) {
        game.setPottedBallCount(pottedBallsCount);
    }

    /**
     * This method returns the value of the getBallInHand() method in {@link poolGame}
     *
     * @return the value of the getBallInHand method (ture or false)
     */
    public boolean getBallInHand() {
        return game.getBallInHand();
    }

    /**
     * This method calls the addPlayers() method in {@link poolGame}
     */
    public void addPlayers() {
        game.addPlayers();
    }

    /**
     * This method calls the checkWin() method in {@link poolGame}
     * @return result of checkWin()
     */
    public boolean checkWin(){
        return game.checkWin();
    }

    /**
     * This method returns the location of the queue.
     * @return the location of the queue.
     */
    public Vector getQueue(){
        return new Vector(queueX, queueY);
    }

    /**
     * This method call the getBall() method in {@link poolGame}
     * @return all balls in an ArrayList
     */
    public ArrayList<Ball> getBalls(){
        return game.getBalls();
    }

    /**
     * This method returns the angle the queue needs to be rotated
     * @return the angle the queue needs to be rotated
     */
    public float getAngle(){
        return angle;
    }
}
