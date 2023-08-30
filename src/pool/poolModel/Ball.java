package pool.poolModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a single ball in the game of billard. Balls can roll over the table, collide with the edges of
 * the table or with another ball or be potted.
 */
public class Ball implements Serializable {
    private int id, mass, widthBound, heightBound, pottedBalls = 0;
    private final String team;
    private float x, y, xVel, yVel, ballSize = 35f;
    private final ArrayList<Ball> hitBalls = new ArrayList<Ball>();
    private boolean potted, pottedThisShot, foul = false;

    public Ball(int id, int widthBound, int heightBound, String team, int mass, float x, float y) {
        this.id = id;
        this.widthBound = widthBound;
        this.heightBound = heightBound;
        this.team = team;
        this.mass = mass;
        this.x = x;
        this.y = y;
    }

    /**
     * This method applies friction to the balls causing them to slow down when rolling.
     */
    public void friction() {
        this.setLocation(new Vector(this.x += this.xVel, this.y += this.yVel));
        if (this.xVel > 0.05 || this.yVel > 0.05 || this.xVel < -0.05 || this.yVel < -0.05)
            this.setVelocity(new Vector(this.xVel *= 0.990, this.yVel *= 0.990));
        else
            this.setVelocity(new Vector(0, 0));
    }

    /**
     * This method check if a ball collides with one of the edges of the table, except the places where a hole is.
     * It then turns the relevant velocity around. If a ball rolls in a hole, its velocity is set to 0 and the location
     * is changed to the left side of the table where the potted balls are displayed.
     */
    public void checkTableCollision() {
        //linke und rechte bande abzgl.Loch
        if ((this.x + this.xVel > 320 + 1280 - 85 - ballSize / 2 || this.x + this.xVel < 320 + 85 + ballSize / 2) && (this.y + this.yVel > 177 + 85 + 46 && this.y + this.yVel < 177 + 727 - 46 - 85))
            this.xVel = -this.xVel;
            //obere und untere Bande abzgl. Löcher
        else if ((this.y + this.yVel > 177 + 727 - 85 - ballSize / 2 || this.y + this.yVel < 177 + 85 + ballSize / 2) && (this.x + this.xVel > 320 + 85 + 46 && this.x + this.xVel < widthBound / 2f - 23 || this.x + this.xVel > widthBound / 2f + 23 && this.x + this.xVel < 320 + 1280 - 85 - 46))
            yVel = -yVel;
        else if ((this.x + this.xVel > widthBound / 2f + 565 - ballSize / 2 || this.x + this.xVel < widthBound / 2f - 565 + ballSize / 2) || (this.y + this.yVel > heightBound / 2f + 285 - ballSize / 2 || this.y + this.yVel < heightBound / 2f - 285 + ballSize / 2)) {
            this.setVelocity(new Vector(0, 0));
            this.setLocation(new Vector(150, heightBound / 3f + pottedBalls * ballSize));
            this.potted = true;
            this.pottedThisShot = true;
        }
    }

    /**
     * This method checks if a ball is colliding with another ball. If so it check if they are overlapping. If this is
     * the case it removes the overlap so that they don't get stuck together. Then it calculates the new direction and
     * velocity of the colliding balls.
     *
     * @param other the ball that another ball is colliding with
     */
    public void checkBallCollision(Ball other) {
        if (other != this) {
            Vector delta = new Vector(this.x - other.x, this.y - other.y);
            if (delta.getVectorX() * delta.getVectorX() + delta.getVectorY() * delta.getVectorY() <= (ballSize * ballSize)) {
                //remove overlap
                float dist = (float) Math.sqrt(Math.pow(delta.getVectorX(), 2) + Math.pow(delta.getVectorY(), 2));
                if (dist < ballSize) {
                    this.setLocation(new Vector(new Vector(this.x, this.y).add(new Vector(delta.getVectorX(), delta.getVectorY()).divide(dist).multiply((ballSize - dist) / 2)).getVectorX(), new Vector(this.x, this.y).add(new Vector(delta.getVectorX(), delta.getVectorY()).divide(dist).multiply((ballSize - dist) / 2)).getVectorY()));
                    other.setLocation(new Vector(new Vector(other.x, other.y).add(new Vector(delta.getVectorX(), delta.getVectorY()).divide(dist).multiply((ballSize - dist) / 2).multiply(-1)).getVectorX(), new Vector(other.x, other.y).add(new Vector(delta.getVectorX(), delta.getVectorY()).divide(dist).multiply((ballSize - dist) / 2).multiply(-1)).getVectorY()));
                }
                //rotate velocity
                Vector vel1 = new Vector((float) (this.xVel * Math.cos(Math.atan2(delta.getVectorY(), delta.getVectorX())) + this.yVel * Math.sin(Math.atan2(delta.getVectorY(), delta.getVectorX()))), (float) (this.xVel * (-Math.sin(Math.atan2(delta.getVectorY(), delta.getVectorX()))) + this.yVel * Math.cos(Math.atan2(delta.getVectorY(), delta.getVectorX()))));
                Vector vel2 = new Vector((float) (other.xVel * Math.cos(Math.atan2(delta.getVectorY(), delta.getVectorX())) + other.yVel * Math.sin(Math.atan2(delta.getVectorY(), delta.getVectorX()))), (float) (other.xVel * (-Math.sin(Math.atan2(delta.getVectorY(), delta.getVectorX()))) + other.yVel * Math.cos(Math.atan2(delta.getVectorY(), delta.getVectorX()))));
                float vx1final = (this.mass - other.mass) * vel1.getVectorX() + 2 * other.mass * vel2.getVectorX() / (this.mass + other.mass);
                float vx2final = (other.mass - this.mass) * vel2.getVectorX() + 2 * this.mass * vel1.getVectorX() / (this.mass + other.mass);
                //rotate vel back
                this.setVelocity(new Vector((float) (vx1final * Math.cos(Math.atan2(delta.getVectorY(), delta.getVectorX())) + vel1.getVectorY() * (-Math.sin(Math.atan2(delta.getVectorY(), delta.getVectorX())))), (float) (vx1final * Math.sin(Math.atan2(delta.getVectorY(), delta.getVectorX())) + vel1.getVectorY() * Math.cos(Math.atan2(delta.getVectorY(), delta.getVectorX())))));
                other.setVelocity(new Vector((float) (vx2final * Math.cos(Math.atan2(delta.getVectorY(), delta.getVectorX())) + vel2.getVectorY() * (-Math.sin(Math.atan2(delta.getVectorY(), delta.getVectorX())))), (float) (vx2final * Math.sin(Math.atan2(delta.getVectorY(), delta.getVectorX())) + vel2.getVectorY() * Math.cos(Math.atan2(delta.getVectorY(), delta.getVectorX())))));
                if (this.id == 0)
                    hitBalls.add(other);
                else if (other.id == 0)
                    other.hitBalls.add(this);
            }
        }
    }

    /**
     * This method returns the id of a ball.
     *
     * @return the id of a ball as integer
     */
    public int getBallId() {
        return this.id;
    }

    /**
     * This method returns the velocity of a ball.
     *
     * @return the velocity of a ball (x and y) as a Vector
     */
    public Vector getBallVel() {
        return new Vector(this.xVel, this.yVel);
    }

    /**
     * This method returns a boolean that indicates if a player played foul.
     *
     * @return a boolean that indicates if a player played foul
     */
    public boolean getFoul() {
        return this.foul;
    }

    /**
     * This method returns a boolean that indicates if a ball is potted.
     *
     * @return a boolean that indicates if a ball is potted
     */
    public boolean getPotted() {
        return this.potted;
    }

    /**
     * This method return the location of a ball.
     *
     * @return the location of a ball as a Vector.
     */
    public Vector getLocation() {
        return new Vector(this.x, this.y);
    }

    /**
     * This method sets a new location to a ball.
     *
     * @param location the new location (x and y value) as a Vector.
     */
    public void setLocation(Vector location) {
        this.x = location.getVectorX();
        this.y = location.getVectorY();
    }

    /**
     * This method returns the size of a ball.
     *
     * @return the size of a ball as float
     */
    public float getBallSize() {
        return this.ballSize;
    }

    /**
     * This method returns a boolean that indicates if a ball was pot during the last shot.
     *
     * @return a boolean that indicates if a ball was pot during the last sho
     */
    public boolean getPottedThisShot() {
        return this.pottedThisShot;
    }

    /**
     * This method sets the boolean of a ball that indicates if a player played foul to a given value.
     *
     * @param foul a boolean that indicates if a player played foul (true or false)
     */
    public void setBallFoul(boolean foul) {
        this.foul = foul;
    }

    /**
     * This method returns all balls the white has hit during a shot.
     *
     * @return an ArrayList with all balls the white has hit during a shot
     */
    public ArrayList<Ball> getHitBalls() {
        return this.hitBalls;
    }

    /**
     * This method sets the value of a boolean that indicates if a ball was pot during the last shot.
     *
     * @param pottedThiShot the value of a boolean that indicates if a ball was pot during the last shot (true or false)
     */
    public void setPottedThisShot(boolean pottedThiShot) {
        this.pottedThisShot = pottedThiShot;
    }

    /**
     * This method sets the boolean that indicates if a ball is potted or not.
     *
     * @param potted boolean that indicates if a ball is potted or not (true or false)
     */
    public void setPotted(boolean potted) {
        this.potted = potted;
    }

    /**
     * This method sets the amount of balls that are currently potted to a given value.
     *
     * @param pottedBalls the amount of balls that are currently potted as integer
     */
    public void setPottedBalls(int pottedBalls) {
        this.pottedBalls = pottedBalls;
    }

    /**
     * This method calls the friction method in the ball class.
     */
    public void setFriction() {
        this.friction();
    }

    /**
     * This method sets the velocity of a ball.
     *
     * @param vel the velocity of a ball (x and y) as a vector
     */
    public void setVelocity(Vector vel) {
        this.xVel = vel.getVectorX();
        this.yVel = vel.getVectorY();
    }

    /**
     * This method clears the Arraylist where all balls are listed the white has collided with during the last shot.
     */
    public void clearHitBalls() {
        this.hitBalls.clear();
    }

    /**
     * This method returns the team a ball belongs to
     *
     * @return the team a ball belongs to ("solids", "stripes", "black" or "white") as a String
     */
    public String getTeam() {
        return this.team;
    }

}