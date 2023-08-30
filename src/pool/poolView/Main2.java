package pool.poolView;

import processing.core.PApplet;

import java.io.IOException;

abstract class Main2 {
    public static void main(String[] args) throws IOException {
        var pool = new Pool();
        var p = Pool.newClient("localhost", 8080, pool);
        PApplet.runSketch(new String[]{"Pong"}, p);
    }
}