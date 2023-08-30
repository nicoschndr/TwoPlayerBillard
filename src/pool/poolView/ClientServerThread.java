package pool.poolView;

import pool.poolModel.Ball;
import pool.poolModel.Player;
import pool.poolModel.Vector;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

/**
 * This class is a Thread, that runs parallel to the main Thread.
 * Its main purpose is to handle the communication between a server and a client.
 */
public class ClientServerThread extends Thread{
    private ServerSocket serversocket;
    private Socket socket;
    private Pool pool;
    private ObjectOutputStream oos;

    /**
     * This method starts a new Server.
     * @param ip the ip of the host
     * @param port the port of the host
     * @param pool the view
     * @return a new instance of the thread
     * @throws IOException if server can not be started
     */
    public static ClientServerThread newServer(String ip, int port, Pool pool) throws IOException {
        var cst = new ClientServerThread(pool);
        try {
            cst.serversocket = new ServerSocket(port);
        } catch (IOException e) {
            throw e;
        }
        return cst;
    }

    private ClientServerThread(Pool p) {this.pool = p;}

    /**
     * This method starts a new Client.
     * @param ip the ip address
     * @param port the port
     * @param pool the view
     * @return a new instance of the thread
     * @throws IOException if client can not be started
     */
    public static ClientServerThread newClient(String ip, int port, Pool pool) throws IOException {
        var cst = new ClientServerThread(pool);
        try{
            cst.socket = new Socket(ip, port);
            cst.oos = new ObjectOutputStream(cst.socket.getOutputStream());
        }catch (IOException e) {
            throw e;
        }
        return cst;
    }

    /**
     * This method indicates if client and server are connected.
     * @return boolean that says if client and server are connected
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    /**
     * This method tries to send the location of the queue.
     * @param queue the location of the queue
     */
    public void offerQueue (Vector queue){
        try{
            if(oos != null)
                oos.writeObject(queue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method tries to send all balls of the game.
     * @param balls all balls of the game
     */
    public void offerBall (Ball[] balls){
        try{
            if(oos != null)
                oos.writeObject(balls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method tries to send all players of the game.
     * @param players all players of the game
     */
    public void offerPlayer(Player[] players) {
        try{
            if(oos != null)
                oos.writeObject(players);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method tries to send the angle the queue needs to be rotated.
     * @param angle thea angle the queue needs to be rotated
     */
    public void offerAngle(float angle){
        try{
            if(oos != null)
                oos.writeObject(angle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method tries to send the information if the space key is pressed
     * @param b the information if the space key is pressed
     */
    public void offer(Boolean b) {
        try{
            if(oos != null)
                oos.writeObject(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method tries to send the information if the mouse is pressed.
     * @param i the information if the mouse is pressed
     */
    public void offerMousePressed(Integer i) {
        try{
            if(oos != null)
                oos.writeObject(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method tries to send the location of the mouse
     * @param p the location of the mouse
     */
    public void offerMouseXY(Point2D p) {
        try{
            if(oos != null)
                oos.writeObject(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method tries to send the information if it is currently a shot.
     * @param point the information if it is currently a shot
     */
    public void offerShot(Point point) {
        try{
            if(oos != null)
                oos.writeObject(point);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method tries to send the information if it is currently ball in hand.
     * @param ballInHand the information if it is currently ball in hand
     */
    public void offerBallInHand(String ballInHand) {
        try{
            if(oos != null)
                oos.writeObject(ballInHand);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method handles the communication between server and client.
     */
    @Override
    public void run() {
        try {
            if(socket == null) {
                socket = serversocket.accept();
                oos = new ObjectOutputStream(socket.getOutputStream());
            }

            var ois = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Object obj = ois.readObject();
                if(obj instanceof Vector) {
                    pool.setNewPosClient((Vector)obj);
                }
                if(obj instanceof Ball[]) {
                    pool.setNewBallsPosClient((Ball[])obj);
                }
                if(obj instanceof Float) {
                    pool.setNewAngleClient((Float)obj);
                }
                if(obj instanceof Player[]) {
                    pool.setNewPlayersClient((Player[])obj);
                }
                if(obj instanceof Boolean) {
                    pool.setKeyPressedServer((Boolean)obj);
                }
                if(obj instanceof Integer) {
                    pool.setMousePressedServer((Integer)obj);
                }
                if(obj instanceof Point2D) {
                    pool.setMouseXYServer((Point2D)obj);
                }
                if(obj instanceof Point) {
                    pool.setShotClient((Point)obj);
                }
                if(obj instanceof String) {
                    pool.setBallInHandClient((String)obj);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
