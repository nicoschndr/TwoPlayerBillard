package pool.poolModel;

import pool.poolController.IpoolGame;

import java.util.ArrayList;

/**
 * The game implementation of billard. Its part of the model of the game and can be used with a combination of view and
 * controller or directly through the JShell.
 */
public class poolGame implements IpoolGame {
    private final ArrayList<Ball> balls = new ArrayList<Ball>(), pottedThisShot = new ArrayList<Ball>();
    private Vector distUnit = new Vector(1, 0);
    private int pottedBalls = 0, solidsPotted = 0, stripesPotted = 0, heightBound;
    private boolean ballInHand, firstShot, playerSwitched = false, set = true, end = true, win = false;
    private final ArrayList<Player> players = new ArrayList<Player>();
    String team = "";

    /**
     * This method sets up a new Round of the Game. It clears all Balls from the previous Round and then adds new Balls
     * which are forming a triangle, except oft the white ball which is located onm the other side of the table. After
     * that the boolean "firstShot" is set to true to make sure game does not detect a foul. The method does this only
     * when the boolean "end" is set to true. That is the case at the first start of the game and when a Frame
     * (designation of a won Round) is finished.
     * You call it with two integers which define the width and height of the window in which the game is displayed.
     *
     * @param widthBound  horizontal width of the window where the game is displayed
     * @param heightBound vertical height of the window where the game is displayed
     */
    public void initialize(int widthBound, int heightBound) {
        balls.clear();
        for (int i = 0; i <= 8; i++) {
            if (i == 0)
                balls.add(new Ball(i, widthBound, heightBound, "white", 20, widthBound / 3f, heightBound / 2f));
            if (i == 7 || i == 6)
                balls.add(new Ball(i, widthBound, heightBound, "solids", 20, widthBound / 3f * 2 + (i - 5) * 35f, heightBound / 2f - (i - 5) * 17.5f));
            if (i == 8)
                balls.add(new Ball(i, widthBound, heightBound, "black", 20, widthBound / 3f * 2, heightBound / 2f));
            else if (i != 0 && i != 7 && i != 6)
                balls.add(new Ball(i, widthBound, heightBound, "solids", 20, widthBound / 3f * 2 - 70 + (i - 1) * 35f, heightBound / 2f + (i - 1) * 17.5f));
        }
        for (int i = 9; i <= 15; i++) {
            if (i == 13 || i == 14)
                balls.add(new Ball(i, widthBound, heightBound, "stripes", 20, widthBound / 3f * 2 + (i - 12) * 35f, heightBound / 2f + (i - 12) * 17.5f));
            if (i == 15)
                balls.add(new Ball(i, widthBound, heightBound, "stripes", 20, widthBound / 3f * 2 + (i - 13) * 35f, heightBound / 2f + (i - 15) * 17.5f));
            else if (i != 13 && i != 14)
                balls.add(new Ball(i, widthBound, heightBound, "stripes", 20, widthBound / 3f * 2 - 70 + (i - 8) * 35f, heightBound / 2f - (i - 8) * 17.5f));
        }
        firstShot = true;
        end = false;
        set = true;
        playerSwitched = false;
        ballInHand = false;
        pottedBalls = 0;
        pottedThisShot.clear();
        this.heightBound = heightBound;
    }

    /**
     * This method adds the Players to the game. It pushes new Player Objects in the ArrayList where players ar saved.
     */
    public void addPlayers() {
        players.add(new Player(1, "Player 1", "", 0, true));
        players.add(new Player(2, "Player 2", "", 0, false));
    }

    /**
     * This method is called every Frame and calls the ball collision detection of a ball with another ball or with the
     * table edge. This is done for every ball of the game.
     */
    public void physics() {
        for (Ball ball : balls) {
            ball.setFriction();
            for (Ball ball1 : balls)
                ball.checkBallCollision(ball1);
            ball.checkTableCollision();
        }
    }

    /**
     * This method is rotating the queue so that is always faces the white ball. Therefore, an angle is needed that
     * indicates how far the queue needs to be rotated in the view. This is done by creating a Vector this indicates
     * the position of the Mouse relative to the Ball. Then another Vector is created which only indicates the
     * relative x-Axis position of the mouse to the Ball. After that the angle between those two Vectors is calculated.
     * This is done with a formula where you dived the product of the two Vector through the product of the lengths of
     * the Vectors. Then you need the arcos cosinus and the Math.toDegrees method to get the angle. Depending on the
     * quarter where the mouse is, you have to add or substract something to or from the angle because of the sign
     * of the Vector values.
     * You need to call it with the x and y location of the mouse as well as the x and y location of the white ball as
     * a float, and it returns the angle as a float.
     *
     * @param mouseX location of the mouse on the x-Axis
     * @param mouseY location of the mouse on the y-Axis
     * @param ballX  location of the white ball on the x-Axis
     * @param ballY  location of the white ball on th y-Axis
     * @return the angle that is needed to rotate the queue as a float
     */
    public float rotateQueue(float mouseX, float mouseY, float ballX, float ballY) {
        distUnit = new Vector(mouseX - ballX, mouseY - ballY).divide(new Vector(mouseX - ballX, mouseY - ballY).length());
        float angle = (float) Math.toDegrees(Math.acos(new Vector(mouseX - ballX, mouseY - ballY).divide(new Vector(mouseX - ballX, mouseY - ballY).length()).skalar(new Vector(mouseX - ballX, 0).divide(new Vector(mouseX - ballX, 0).length())) / new Vector(mouseX - ballX, mouseY - ballY).divide(new Vector(mouseX - ballX, mouseY - ballY).length()).length() * new Vector(mouseX - ballX, 0).divide(new Vector(mouseX - ballX, 0).length()).length()));
        if (mouseX <= ballX && mouseY >= ballY) return (float) (180 - angle);
        else if (mouseY <= ballY && mouseX <= ballX) return (float) (180 + angle);
        else if (mouseY <= ballY) return (float) (360 - angle);
        return (float) angle;
    }

    /**
     * This method shoots the white ball (id == 0) with a given power in the opposite direction of where the queue is
     * pointing to. Therefore, the distUnit Vector which defines the direction of where the queue is pointing to is used
     * but with a minus sign before it.
     * You need to call it with an integer which defines the power and must be between 0 and 30.
     *
     * @param power defines the hardness of a shot (max. 30)
     */
    public void shoot(int power) {
        if (power > 30 || power < 1)
            System.err.println("Power muss zwischen 1 und 30 liegen!");
        else {
            firstShot = false;
            playerSwitched = false;
            pottedThisShot.clear();
            for (Ball ball : balls) {
                ball.setPottedThisShot(false);
                int id = ball.getBallId();
                if (id == 0) {
                    ball.clearHitBalls();
                    ball.setVelocity(new Vector(-distUnit.getVectorX() * power / 1.5f, -distUnit.getVectorY() * power / 1.5f));
                    power = 0;
                    set = false;
                }
            }
        }
    }


    /**
     * This method sets the white Ball to the location of the cursor until the Ball is laid on the table through a
     * click. That is only done when the white Ball was potted or a Player made a foul.
     * You need to call it with the x and y location of the mouse as a float.
     *
     * @param mouseX location of the mouse on the x-Axis
     * @param mouseY location of the mouse on the y-Axis
     */
    public void ballInHand(float mouseX, float mouseY) {
        for (Ball ball : balls) {
            boolean potted = ball.getPotted();
            boolean foul = ball.getFoul();
            int id = ball.getBallId();
            if (potted || foul && !firstShot) {
                if (id == 0) {
                    ballInHand = true;
                    ball.setLocation(new Vector(mouseX, mouseY));
                }
            }
        }
    }

    /**
     * This method puts the whits ball back on the table to the location of the cursor after a "ball in hand".
     * You need to call it with the x and y location of the mouse as a float.
     *
     * @param mouseX location of the mouse on the x-Axis
     * @param mouseY location of the mouse on the y-Axis
     */
    public void setWhiteBall(float mouseX, float mouseY) {
        for (Ball ball : balls) {
            int id = ball.getBallId();
            if (id == 0) {
                ball.setLocation(new Vector(mouseX, mouseY));
                set = true;
                ball.setPotted(false);
                ball.setBallFoul(false);
                ballInHand = false;
            }
        }
    }

    /**
     * This method calls a method in the ball class that applies friction to the balls so that they slow down when
     * rolling. In addition to that the number of currently potted Balls is set in every ball.
     */
    public void setPottedBalls() {
        for (Ball ball : balls)
            ball.setPottedBalls(pottedBalls);
    }

    /**
     * This method checks if a player has played foul. This is the case when a player did not hit a ball or the first
     * ball he hit was one of the enemies or the black ball. It is also foul when you pot the black ball before you
     * potted all of your balls. In this case the other player wins.
     */
    public boolean checkFoul() {
        if (!checkShot() && !ballInHand && !set && !playerSwitched) {
            for (Player player : players) {
                for (Ball potted : pottedThisShot) {
                    if (potted.getTeam().contains("black") && player.getTeam().contains("stripes") && stripesPotted < 7)
                        solidsPotted = 8;
                    if (potted.getTeam().contains("black") && player.getTeam().contains("solids") && solidsPotted < 7)
                        stripesPotted = 8;
                }
                if (player.getCurrentlyPlaying()) {
                    for (Ball ball : balls) {
                        int id = ball.getBallId();
                        if (id == 0) {
                            ArrayList<Ball> hitBalls = ball.getHitBalls();
                            if (((hitBalls.isEmpty()) || (hitBalls.get(0).getTeam().contains("stripes") && player.getTeam().contains("solids")) || (hitBalls.get(0).getTeam().contains("solids") && player.getTeam().contains("stripes")) || (hitBalls.get(0).getTeam().contains("black"))))
                                ball.setBallFoul(true);
                            win = false;
                            return true;
                        }
                    }
                }
            }
        }
        win = false;
        return false;
    }

    /**
     * This method checks if any of the balls are still rolling. If so it is still a shot, if not it is not a shot
     * anymore. It also checks if the white ball was potted. If so it's location is set to the side of the table
     * where the potted balls are shown.
     */
    public boolean checkShot() {
        for (Ball ball : balls) {
            Vector ballVel = ball.getBallVel();
            if (ballVel.getVectorX() != 0 || ballVel.getVectorY() != 0) return true;
        }
        return false;
    }

    /**
     * This method checks if the position of the white ball is currently above another ball. It is needed when a player
     * has "ball in hand" and wants to put the white back on the table. If the white ball is above another one, the
     * player can't place it. Therefore, it is checked if the distance between the cursor and another ball is greater
     * than one ball size.
     * You need to call it with the x and y location of the mouse as a float, and it returns true if the white ball is
     * above another ball and false if not.
     *
     * @param mouseX location of the mouse on the x-Axis
     * @param mouseY location of the mouse on the y-Axis
     * @return true if the white ball is above another ball, false if not
     */
    public boolean checkWhiteOverBall(float mouseX, float mouseY) {
        for (Ball ball : balls) {
            int id = ball.getBallId();
            if (id != 0) {
                Vector location = ball.getLocation();
                float ballSize = ball.getBallSize();
                if (Math.sqrt(Math.pow(location.getVectorX() - mouseX, 2)) + Math.sqrt(Math.pow(location.getVectorY() - mouseY, 2)) <= Math.sqrt(Math.pow(ballSize, 2)))
                    return true;
            }
        }
        return false;
    }

    /**
     * This method is used to switch between players. This is done by changing the "currentlyPlaying" variable in
     * the player class to the opposite of what it was before calling the method.
     */
    public void switchPlayer() {
        if ((pottedThisShot.size() == 0 || ballInHand) && !playerSwitched && !firstShot && !checkShot()) {
            for (Player player : players) {
                boolean currentlyPlaying = player.getCurrentlyPlaying();
                currentlyPlaying = !currentlyPlaying;
                player.setCurrentlyPlaying(currentlyPlaying);
            }
            playerSwitched = true;
        }
    }

    /**
     * This method checks for every ball if it was potted during the last shot. If so it pushes the ball into a
     * ArrayList where all the balls that where potted during the last shot ar saved.
     */
    public void pottedDuringShot() {
        for (Ball ball : balls) {
            if (ball.getPottedThisShot() && !ball.getTeam().contains("white"))
                pottedThisShot.add(ball);
            if (((ball.getTeam().contains("solids") || ball.getTeam().contains("black")) && ball.getPottedThisShot()))
                solidsPotted++;
            if ((ball.getTeam().contains("stripes") || ball.getTeam().contains("black")) && ball.getPottedThisShot())
                stripesPotted++;
            ball.setPottedThisShot(false);
        }
    }

    /**
     * This method sets the team of a players based on what is potted first.
     */
    public void setTeam() {
        for (Player player : players) {
            if (!player.getTeam().contains("solids") && !player.getTeam().contains("stripes") && !pottedThisShot.isEmpty() && !pottedThisShot.get(0).getTeam().contains("white") && !pottedThisShot.get(0).getTeam().contains("black")) {
                if (player.getCurrentlyPlaying()) {
                    player.setTeam(pottedThisShot.get(0).getTeam());
                    team = pottedThisShot.get(0).getTeam();
                }
                if (!player.getCurrentlyPlaying() && team.contains("stripes"))
                    player.setTeam("solids");
                if (!player.getCurrentlyPlaying() && team.contains("solids"))
                    player.setTeam("stripes");
            }
        }
    }

    /**
     * This method checks if a player has won a frame. That is the case when a player potted all of his balls plus the
     * black. That are seven balls.
     * The method also sets the team of the players depending on the first ball that was potted during a frame. The
     * player then gets the team of the ball.
     */
    public boolean checkWin() {
        if (!win) {
            for (Player player : players) {
                if ((stripesPotted == 8 && player.getTeam().contains("stripes")) || (solidsPotted == 8 && player.getTeam().contains("solids"))) {
                    player.setWins();
                    solidsPotted = 0;
                    stripesPotted = 0;
                    pottedThisShot.clear();
                    player.setTeam("");
                    end = true;
                }
            }
        }
        if (end) return true;
        win = true;
        switchPlayer();
        return false;
    }

    /**
     * This method return an ArrayList of all the balls of the game.
     *
     * @return ArrayList of all the balls of the game
     */
    public ArrayList<Ball> getBalls() {
        return balls;
    }

    /**
     * This method return an ArrayList of all the players of the game.
     *
     * @return ArrayList of all the players of the game
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * This method returns if a player has currently "ball in hand".
     *
     * @return a boolean that says if a player has currently "ball in hand"
     */
    public boolean getBallInHand() {
        return ballInHand;
    }

    /**
     * This method sets the amount of potted balls in a round.
     * You need to call it with the amount of balls potted in the round as integer.
     *
     * @param pottedBallsCount amount of potted balls in a round as integer
     */
    public void setPottedBallCount(int pottedBallsCount) {
        pottedBalls = pottedBallsCount;
    }
}
