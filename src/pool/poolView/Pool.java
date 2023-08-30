package pool.poolView;

import pool.poolController.IpoolController;
import pool.poolController.IpoolView;
import pool.poolController.poolController;
import pool.poolModel.Ball;
import pool.poolModel.Player;
import pool.poolModel.Vector;
import processing.core.PApplet;
import processing.core.PImage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is a processing view.
 */
public class Pool extends PApplet implements IpoolView {
    /**
     * Main method to start billard (not used).
     *
     * @param args
     */
    public static void main(String[] args) {
        PApplet.main(Pool.class);
    }

    private PImage poolTable;
    private PImage poolQueue;
    private PImage poolBallYellow;
    private PImage poolBallBlueHalf;
    private PImage poolBallYellowHalf;
    private PImage poolBallRed;
    private PImage poolBallWhite;
    private PImage poolBallBlack;
    private PImage poolBallBlue;
    private PImage poolBallPurple;
    private PImage poolBallOrange;
    private PImage poolBallGreen;
    private PImage poolBallBrown;
    private PImage poolBallRedHalf;
    private PImage poolBallPurpleHalf;
    private PImage poolBallOrangeHalf;
    private PImage poolBallGreenHalf;
    private PImage poolBallBrownHalf;
    private PImage queueMini;
    private PImage floor;
    private IpoolController controller;
    private static boolean server = false;
    private static boolean client = false;
    private float queueX;
    private float queueY;
    private Float angle;
    private ArrayList<Ball> balls = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private boolean keyPressed = false;
    private int mousePressedClient;
    private float mouseXClient;
    private float mouseYClient;
    private boolean shot = false;
    private boolean ballInHand = false;

    private ClientServerThread thread;

    /**
     * This method sets the size of the window the game is displayed in.
     */
    public void settings() {
        size(1920, 1080);
    }

    /**
     * This method is called once and sets up the application.
     */
    public void setup() {
        frameRate(60);
        this.controller = new poolController(this);
        poolTable = loadImage("res/poolTable.png");
        poolQueue = loadImage("res/queue.png");
        poolBallYellow = loadImage("res/poolBallYellow.png");
        poolBallBlueHalf = loadImage("res/poolBallBlueHalf.png");
        poolBallYellowHalf = loadImage("res/poolBallYellowHalf.png");
        poolBallRed = loadImage("res/poolBallRed.png");
        poolBallWhite = loadImage("res/poolBallWhite.png");
        poolBallBlack = loadImage("res/poolBallBlack.png");
        poolBallBlue = loadImage("res/poolBallBlue.png");
        poolBallBrown = loadImage("res/poolBallBrown.png");
        poolBallBrownHalf = loadImage("res/poolBallBrownHalf.png");
        poolBallGreen = loadImage("res/poolBallGreen.png");
        poolBallGreenHalf = loadImage("res/poolBallGreenHalf.png");
        poolBallOrange = loadImage("res/poolBallOrange.png");
        poolBallOrangeHalf = loadImage("res/poolBallOrangeHalf.png");
        poolBallPurple = loadImage("res/poolBallPurple.png");
        poolBallPurpleHalf = loadImage("res/poolBallPurpleHalf.png");
        poolBallRedHalf = loadImage("res/poolBallRedHalf.png");
        queueMini = loadImage("res/queueMini.png");
        floor = loadImage("res/floor.png");
        controller.initialize(super.width, super.height);
        controller.addPlayers();
    }

    /**
     * This method tries to start a new Server in the ClientServerThread
     *
     * @param ip   the ip address of the host
     * @param port the port the server should run on
     * @param p    an instance of the poolView
     * @return the poolView
     * @throws IOException if it can not start a server
     */
    public static Pool newServer(String ip, int port, Pool p) throws IOException {
        try {
            p.thread = ClientServerThread.newServer(ip, port, p);
            p.thread.start();
            server = true;
        } catch (IOException e) {
            System.out.println(e);
        }
        return p;
    }

    /**
     * This method tries to start a new Client in the ClientServerThread.
     *
     * @param ip   the ip address
     * @param port the port
     * @param p    an instance of the poolView
     * @return the poolView
     * @throws IOException if it can not start a client, it automatically tries to be a server
     */
    public static Pool newClient(String ip, int port, Pool p) throws IOException {
        try {
            p.thread = ClientServerThread.newClient(ip, port, p);
            p.thread.start();
            client = true;
            return p;
        } catch (IOException e) {
            newServer(ip, port, p);
            client = false;
        }
        return p;
    }

    /**
     * This method sends the location of the mouse and if it is pressed or not to the {@link poolController}. It also calls the
     * nextFrame method in the {@link poolController}. And it handles the Server Client Communication.
     */
    public void draw() {
        if (thread.isConnected()) {
            if (server) {
                ArrayList<Player> plys = new ArrayList<>(controller.getPlayers());
                for (Player player : plys) {
                    if (player.getPlayerId() == 1 && player.getCurrentlyPlaying()) {
                        controller.update(mouseX, mouseY);
                    }
                }
            }
            if (mousePressed) {
                if (server) {
                    ArrayList<Player> plys = new ArrayList<>(controller.getPlayers());
                    for (Player player : plys) {
                        if (player.getPlayerId() == 1 && player.getCurrentlyPlaying()) {
                            controller.mousePressed();
                        }
                    }
                }
                if (client) {
                    sendMousePressedToServer(1);
                }
            } else {
                if (server) {
                    ArrayList<Player> plys = new ArrayList<>(controller.getPlayers());
                    for (Player player : plys) {
                        if (player.getPlayerId() == 1 && player.getCurrentlyPlaying()) {
                            controller.mouseNotPressed();
                        }
                    }
                }
                if (client) {
                    sendMousePressedToServer(0);
                }
            }
            if (server) {
                controller.nextFrame();
            }
        }
        if (server) {
            sendPlayersToClient();
            sendBallsToClient();
            ArrayList<Ball> balls = new ArrayList<>(controller.getBalls());
            for (Ball ball : balls) {
                if (ball.getBallId() == 0) {
                    if (!controller.checkShot() && !controller.getBallInHand() && !ball.getFoul() && !ball.getPotted()) {
                        sendPosToClient(controller.getQueue());
                    }
                    if (controller.checkShot() || controller.getBallInHand() || ball.getFoul() || ball.getPotted()) {
                        sendPosToClient(new Vector(-10000, -10000));
                    }
                }
            }
            sendAngleToClient(controller.getAngle());
            sendShotToClient(controller.checkShot());
            sendBallInHandToClient(controller.getBallInHand());
        }
        if (client) {
            clientDrawGame();
            clientDrawBalls();
            clientDrawQueue();
            sendMouseXYToServer(new Point2D.Float(mouseX, mouseY));
        }
    }

    /**
     * This method is called if a key is pressed. If it is the space key a method in the {@link poolController} is called.
     */
    public void keyPressed() {
        if (server) {
            ArrayList<Player> playersServer = new ArrayList<>(controller.getPlayers());
            for (Player player : playersServer) {
                if ((player.getPlayerId() == 1 && player.getCurrentlyPlaying())) {
                    if (keyCode == ' ') {
                        controller.keyIsPressed();
                    }
                }
            }
        }
        if (client) {
            if (keyCode == ' ') {
                sendSpaceToServer(true);
            }

        }
    }

    /**
     * This method is called if a key is released. If it is the space key a method in the {@link poolController} is called.
     */
    public void keyReleased() {
        if (server) {
            ArrayList<Player> playersServer = new ArrayList<>(controller.getPlayers());
            for (Player player : playersServer) {
                if ((player.getPlayerId() == 1 && player.getCurrentlyPlaying())) {
                    if (keyCode == ' ') {
                        controller.keyIsReleased();
                    }
                }
            }
        }
        if (client) {
            if (keyCode == ' ') {
                sendSpaceToServer(false);
            }
        }
    }

    /**
     * This method draws text, table and background of the server and calls methods of the game that are needed every frame.
     *
     * @param balls ArrayList of where all balls are saved in
     */
    public void serverDrawGame(ArrayList<Ball> balls) {
        background(floor);
        textSize(10);
        text(frameRate, 20, 20);
        if (server) {
            ArrayList<Player> players = controller.getPlayers();
            textSize(40);
            for (Player player : players) {
                int id = player.getPlayerId();
                String name = player.getPlayerName();
                int wins = player.getPlayerWins();
                if (id == 1) {
                    if (player.getCurrentlyPlaying()) {
                        fill(255, 0, 0);
                        ellipse(575, 70, 20, 20);
                        image(queueMini, 575, 90);
                        if (!controller.checkShot() && !controller.getBallInHand()) {
                            fill(0, 0, 255);
                            textSize(50);
                            text("Rotate the Queue by clicking your mouse and hold space to shoot!", super.width / 2f - 670, height - 100);
                        }
                        if (controller.getBallInHand()) {
                            fill(0, 0, 255);
                            textSize(50);
                            text("Ball in Hand!", super.width / 2f - 120, height - 120);
                            fill(255);
                            textSize(30);
                            text("You can put the Ball on the table by clicking!", super.width / 2f - 270, height - 90);
                        }
                    }
                    fill(255);
                    textSize(40);
                    text("You", 600, 80);
                    if(player.getTeam().contains("stripes")){
                        image(poolBallBlueHalf, 670,53,30,30);
                    }
                    if(player.getTeam().contains("solids")){
                        image(poolBallBlue, 670,53,30,30);
                    }
                    textSize(20);
                    text("Frames:", 600, 120);
                    text(wins, 750, 120);
                }
                if (id == 2) {
                    if (player.getCurrentlyPlaying()) {
                        fill(255, 0, 0);
                        ellipse(1225, 70, 20, 20);
                        image(queueMini, 1225, 90);
                        if (!controller.checkShot() && !controller.getBallInHand()) {
                            fill(0, 0, 255);
                            textSize(50);
                            text("Waiting for Opponent...", super.width / 2f - 230, height - 100);
                        }
                        if (controller.getBallInHand()) {
                            fill(0, 0, 255);
                            textSize(50);
                            text("Ball in Hand!", super.width / 2f - 130, height - 120);
                            fill(255);
                            textSize(30);
                            text("Your Opponent can put the Ball on the table!", super.width / 2f - 278, height - 90);
                        }
                    }
                    fill(255);
                    textSize(40);
                    text("Opponent", 1250, 80);
                    if(player.getTeam().contains("stripes")){
                        image(poolBallBlueHalf, 1430,53,30,30);
                    }
                    if(player.getTeam().contains("solids")){
                        image(poolBallBlue, 1430,53,30,30);
                    }
                    textSize(20);
                    text("Frames:", 1250, 120);
                    text(wins, 1400, 120);
                }
            }
            textSize(40);
            text("vs", 940, 80);
            text("Potted:", 100, 300);
            int poolTableWidth = 1280;
            int poolTableHeight = 727;
            image(poolTable, super.width / 2f - poolTableWidth / 2f, super.height / 2f - poolTableHeight / 2f);
            if (controller.checkWin()) {
                controller.initialize(super.width, super.height);
            }
        }
        if (server) {
            ArrayList<Player> players1 = new ArrayList<>(controller.getPlayers());
            for (Player player : players1) {
                if (this.keyPressed && player.getPlayerId() == 2 && player.getCurrentlyPlaying()) {
                    controller.keyIsPressed();
                }
                if (!this.keyPressed && player.getPlayerId() == 2 && player.getCurrentlyPlaying()) {
                    controller.keyIsReleased();
                }
                if (this.mousePressedClient == 1 && player.getPlayerId() == 2 && player.getCurrentlyPlaying()) {
                    controller.mousePressed();
                }
                if (this.mousePressedClient == 1 && player.getPlayerId() == 2 && player.getCurrentlyPlaying() && controller.getBallInHand()) {
                    controller.mousePressed();
                }
                if (this.mousePressedClient == 0 && player.getPlayerId() == 2 && player.getCurrentlyPlaying()) {
                    controller.mouseNotPressed();
                }
                if (player.getPlayerId() == 2 && player.getCurrentlyPlaying()) {
                    controller.update(this.mouseXClient, this.mouseYClient);
                }
            }
        }
    }

    /**
     * This method draws text, table and background of the Client.
     */
    private void clientDrawGame() {
        background(floor);
        textSize(10);
        text(frameRate, 20, 20);
        textSize(40);
        for (Player player : this.players) {
            int id = player.getPlayerId();
            String name = player.getPlayerName();
            int wins = player.getPlayerWins();
            if (id == 1) {
                if (player.getCurrentlyPlaying()) {
                    fill(255, 0, 0);
                    ellipse(575, 70, 20, 20);
                    image(queueMini, 575, 90);
                    if (!this.shot && !this.ballInHand) {
                        fill(0, 0, 255);
                        textSize(50);
                        text("Waiting for Opponent...", super.width / 2f - 230, height - 100);
                    }
                    if (this.ballInHand) {
                        fill(0, 0, 255);
                        textSize(50);
                        text("Ball in Hand!", super.width / 2f - 130, height - 120);
                        fill(255);
                        textSize(30);
                        text("Your Opponent can put the Ball on the table!", super.width / 2f - 278, height - 90);
                    }
                }
                fill(255);
                textSize(40);
                text("Opponent", 600, 80);
                if(player.getTeam().contains("stripes")){
                    image(poolBallBlueHalf, 780,53,30,30);
                }
                if(player.getTeam().contains("solids")){
                    image(poolBallBlue, 780,53,30,30);
                }
                textSize(20);
                text("Frames:", 600, 120);
                text(wins, 750, 120);
            }
            if (id == 2) {
                if (player.getCurrentlyPlaying()) {
                    fill(255, 0, 0);
                    ellipse(1225, 70, 20, 20);
                    image(queueMini, 1225, 90);
                    if (!this.shot && !this.ballInHand) {
                        fill(0, 0, 255);
                        textSize(50);
                        text("Rotate the Queue by clicking your mouse and hold space to shoot!", super.width / 2f - 670, height - 100);
                    }
                    if (this.ballInHand) {
                        fill(0, 0, 255);
                        textSize(50);
                        text("Ball in Hand!", super.width / 2f - 120, height - 120);
                        fill(255);
                        textSize(30);
                        text("You can put the Ball on the table by clicking!", super.width / 2f - 270, height - 90);
                    }
                }
                fill(255);
                textSize(40);
                text("You", 1250, 80);
                if(player.getTeam().contains("stripes")){
                    image(poolBallBlueHalf, 1320,53,30,30);
                }
                if(player.getTeam().contains("solids")){
                    image(poolBallBlue, 1320,53,30,30);
                }
                textSize(20);
                text("Frames:", 1250, 120);
                text(wins, 1400, 120);
            }
        }
        textSize(40);
        text("vs", 940, 80);
        text("Potted:", 100, 300);
        int poolTableWidth = 1280;
        int poolTableHeight = 727;
        image(poolTable, super.width / 2f - poolTableWidth / 2f, super.height / 2f - poolTableHeight / 2f);
    }


    /**
     * This method draws all balls of the game for the server-side.
     *
     * @param balls ArrayList of where all balls are saved in
     */
    public void serverDrawBall(ArrayList<Ball> balls) {
        if (server) {
            int pottedBallCount = 0;
            for (Ball ball : balls) {
                if (ball.getPotted() && ball.getBallId() != 0) {
                    pottedBallCount++;
                }
                switch (ball.getBallId()) {
                    case 0:
                        image(poolBallWhite, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 1:
                        image(poolBallYellow, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 2:
                        image(poolBallBlue, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 3:
                        image(poolBallRed, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 4:
                        image(poolBallPurple, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 5:
                        image(poolBallOrange, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 6:
                        image(poolBallGreen, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 7:
                        image(poolBallBrown, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 8:
                        image(poolBallBlack, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 9:
                        image(poolBallYellowHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 10:
                        image(poolBallBlueHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 11:
                        image(poolBallRedHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 12:
                        image(poolBallPurpleHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 13:
                        image(poolBallOrangeHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 14:
                        image(poolBallGreenHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                    case 15:
                        image(poolBallBrownHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                        break;
                }
            }
            controller.setPottedBallCount(pottedBallCount);
        }
    }

    /**
     * This method draws all balls of the game for the client-side.
     */
    private void clientDrawBalls() {
        for (Ball ball : this.balls) {
            switch (ball.getBallId()) {
                case 0:
                    image(poolBallWhite, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 1:
                    image(poolBallYellow, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 2:
                    image(poolBallBlue, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 3:
                    image(poolBallRed, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 4:
                    image(poolBallPurple, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 5:
                    image(poolBallOrange, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 6:
                    image(poolBallGreen, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 7:
                    image(poolBallBrown, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 8:
                    image(poolBallBlack, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 9:
                    image(poolBallYellowHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 10:
                    image(poolBallBlueHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 11:
                    image(poolBallRedHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 12:
                    image(poolBallPurpleHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 13:
                    image(poolBallOrangeHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 14:
                    image(poolBallGreenHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
                case 15:
                    image(poolBallBrownHalf, (ball.getLocation().getVectorX() - ball.getBallSize() / 2), (ball.getLocation().getVectorY() - ball.getBallSize() / 2), ball.getBallSize(), ball.getBallSize());
                    break;
            }
        }
    }

    /**
     * This method draws the queue for the server-side.
     *
     * @param balls  ArrayList of where all balls are saved in
     * @param angle  the angle that indicated how far the queue needs to be rotated as float
     * @param queueX the current location of the queue on the x-Axis
     * @param queueY the current location of the queue on the y-Axis
     */
    public void serverDrawQueue(ArrayList<Ball> balls, float angle, float queueX, float queueY) {
        if (server) {
            for (Ball ball : balls) {
                if (!controller.checkShot() && !controller.getBallInHand() && !ball.getFoul() && !ball.getPotted()) {
                    int id = ball.getBallId();
                    float ballX = ball.getLocation().getVectorX();
                    float ballY = ball.getLocation().getVectorY();
                    if (id == 0) {
                        pushMatrix();
                        translate(ballX, ballY);
                        rotate(radians(angle));
                        image(poolQueue, queueX, queueY);
                        popMatrix();
                    }
                }
            }
        }
    }

    /**
     * This method draws the queue for the client-side.
     */
    private void clientDrawQueue() {
        for (Ball ball : this.balls) {
            if (!controller.checkShot() && !controller.getBallInHand() && !ball.getFoul() && !ball.getPotted()) {
                int id = ball.getBallId();
                float ballX = ball.getLocation().getVectorX();
                float ballY = ball.getLocation().getVectorY();
                if (id == 0) {
                    pushMatrix();
                    translate(ballX, ballY);
                    rotate(radians(angle));
                    image(poolQueue, queueX, queueY);
                    popMatrix();
                }
            }
        }
    }

    /**
     * This method sends all Balls from the server to the client-side.
     */
    private void sendBallsToClient() {
        if (server) {
            ArrayList<Ball> balls = new ArrayList<Ball>(controller.getBalls());
            Ball[] balls1 = new Ball[16];
            for (int i = 0; i < balls1.length; i++) {
                Ball balli = new Ball(balls.get(i).getBallId(), super.width, super.height, balls.get(i).getTeam(), 20, balls.get(i).getLocation().getVectorX(), balls.get(i).getLocation().getVectorY());
                balls1[i] = balli;
            }
            thread.offerBall(balls1);
        }
    }

    /**
     * This method saves the updated balls for the client-side.
     *
     * @param balls2 the balls coming from the server
     */
    public void setNewBallsPosClient(Ball[] balls2) {
        if (client) {
            this.balls = new ArrayList<>(Arrays.asList(balls2));
        }
    }

    /**
     * This method sends all Players from the server to the client-side.
     */
    private void sendPlayersToClient() {
        if (server) {
            ArrayList<Player> players = new ArrayList<Player>(controller.getPlayers());
            Player[] players1 = new Player[2];
            for (int i = 0; i < players1.length; i++) {
                Player playeri = new Player(players.get(i).getPlayerId(), players.get(i).getPlayerName(), players.get(i).getTeam(), players.get(i).getPlayerWins(), players.get(i).getCurrentlyPlaying());
                players1[i] = playeri;
            }
            thread.offerPlayer(players1);
        }
    }

    /**
     * This method save the players for the client-side.
     *
     * @param players2 the players coming from the server.
     */
    public void setNewPlayersClient(Player[] players2) {
        if (client) {
            this.players = new ArrayList<>(Arrays.asList(players2));
        }
    }

    /**
     * This method sends the position of the queue from server to client.
     *
     * @param queue the location of the queue on the x- and y-Axis as vector
     */
    public void sendPosToClient(Vector queue) {
        if (server) {
            thread.offerQueue(queue);
        }
    }

    /**
     * This method save the location of the queue on the clint-side.
     *
     * @param queue the location of the queue coming from the server
     */
    public void setNewPosClient(Vector queue) {
        if (client) {
            queueX = queue.getVectorX();
            queueY = queue.getVectorY();
        }
    }

    /**
     * This method sends the angle the queue needs to be rotated from server to client.
     *
     * @param angle the angle the queue needs to be rotated
     */
    private void sendAngleToClient(Float angle) {
        thread.offerAngle(angle);
    }

    /**
     * This method saves the angle the queue needs to be rotated for the client-side.
     *
     * @param angle the angle the queue needs to be rotated from the server
     */
    public void setNewAngleClient(Float angle) {
        if (client) {
            this.angle = angle;
        }
    }

    /**
     * This method send the information if it is currently a shot for server to client.
     *
     * @param checkShot the information if it is currently a shot
     */
    private void sendShotToClient(boolean checkShot) {
        if (server) {
            if (checkShot) {
                thread.offerShot(new Point(1, 0));
            }
            if (!checkShot) {
                thread.offerShot(new Point(0, 1));
            }
        }
    }

    /**
     * This method saves the information if it is currently a shot for the client-side.
     *
     * @param point the information if it is currently a shot from the server
     */
    public void setShotClient(Point point) {
        if (client) {
            if (point.getX() == 1) {
                this.shot = true;
            }
            if (point.getX() == 0) {
                this.shot = false;
            }
        }
    }

    /**
     * This method sends the information if it is currently ball in hand from server to client.
     *
     * @param ballInHand he information if it is currently ball in hand
     */
    private void sendBallInHandToClient(boolean ballInHand) {
        if (server) {
            if (ballInHand) {
                thread.offerBallInHand("ballInHand");
            }
            if (!ballInHand) {
                thread.offerBallInHand("notBallInHand");
            }
        }
    }

    /**
     * This method saves the information if it is currently ball in hand for the client-side.
     *
     * @param ballInHand the information if it is currently ball in hand from the server
     */
    public void setBallInHandClient(String ballInHand) {
        if (client) {
            if (ballInHand.contains("ballInHand")) {
                this.ballInHand = true;
            }
            if (ballInHand.contains("notBallInHand")) {
                this.ballInHand = false;
            }
        }
    }

    /**
     * This method sends the information if the space key is pressed from client to server.
     *
     * @param b the information if the space key is pressed
     */
    private void sendSpaceToServer(Boolean b) {
        if (client) {
            thread.offer(b);
        }
    }

    /**
     * This method saves the information if the space key is pressed for the server-side.
     *
     * @param b the information if the space key is pressed from the client
     */
    public void setKeyPressedServer(Boolean b) {
        this.keyPressed = b;
    }

    /**
     * This method sends the information if the mouse is pressed from client to server.
     *
     * @param i the information if the mouse is pressed
     */
    private void sendMousePressedToServer(Integer i) {
        if (client) {
            thread.offerMousePressed(i);
        }
    }

    /**
     * This method saves the information if the mouse is pressed for the server-side.
     *
     * @param i the information if the mouse is pressed from the client
     */
    public void setMousePressedServer(Integer i) {
        this.mousePressedClient = i;
    }

    /**
     * This method send the current mouse location from client to server.
     *
     * @param p the current mouse location
     */
    private void sendMouseXYToServer(Point2D p) {
        if (client) {
            thread.offerMouseXY(p);
        }
    }

    /**
     * This method saves the current mouse location for the server-side.
     *
     * @param p the current mouse location from the client
     */
    public void setMouseXYServer(Point2D p) {
        mouseXClient = (float) p.getX();
        mouseYClient = (float) p.getY();
    }
}