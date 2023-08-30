package pool.tests.model;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import pool.poolModel.poolGame;
import pool.poolModel.Ball;
import pool.poolModel.Player;
import pool.poolModel.Vector;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class poolGameTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }
    @Test
    void initializeShouldAdd16Balls(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        ArrayList<Ball> balls = game.getBalls();
        assertEquals(16, balls.size());
    }

    @Test
    void addPlayersShouldAddTwoPlayers(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        game.addPlayers();
        ArrayList<Player> players = game.getPlayers();
        assertEquals(2, players.size());
    }

    @Test
    void whenPowerToHighWhiteShouldNotBeShot() {
        var game = new poolGame();
        game.initialize(1920, 1080);
        ArrayList<Ball> balls = game.getBalls();
        game.shoot(50);
        for (Ball ball: balls){
            if(ball.getBallId() == 0){
                assertEquals(0, ball.getBallVel().getVectorX());
                assertEquals(0, ball.getBallVel().getVectorY());
            }
        }
    }

    @Test
    void aFoulShouldCauseABallInHand(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        ArrayList<Ball> balls = game.getBalls();
        game.shoot(1);
        for(Ball ball: balls){
            if (ball.getBallId() == 0){
                ball.setBallFoul(true);
                ball.setVelocity(new Vector(0,0));            }
        }
        game.ballInHand(200,200);
        assertTrue(game.getBallInHand());
    }

    @Test
    void pottingTheWhiteBallShouldCauseABallInHand(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        ArrayList<Ball> balls = game.getBalls();
        for(Ball ball: balls){
            if (ball.getBallId() == 0){
                ball.setPotted(true);
            }
        }
        game.ballInHand(200,200);
        assertTrue(game.getBallInHand());
    }

    @Test
    void settingTheWhiteBallShouldSetBallInHandToFalse(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        ArrayList<Ball> balls = game.getBalls();
        for(Ball ball: balls){
            if (ball.getBallId() == 0){
                ball.setPotted(true);
            }
        }
        game.setWhiteBall(720, 750);
        assertFalse(game.getBallInHand());
    }

    @Test
    void shotShouldBeTrueIfABallIsRolling(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        ArrayList<Ball> balls = game.getBalls();
        for(Ball ball: balls){
            if(ball.getBallId() == 13){
                ball.setVelocity(new Vector(1,2));
            }
        }
        assertTrue(game.checkShot());
    }

    @Test
    void shotShouldBeFalseWhenNoBallIsRolling(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        ArrayList<Ball> balls = game.getBalls();
        game.rotateQueue(1000,3000,500,700);
        game.shoot(10);
        for(Ball ball: balls){
            if(ball.getBallId() == 0){
                ball.setVelocity(new Vector(0, 0));
            }
        }
        assertFalse(game.checkShot());
    }

    @Test
    void checkOverWhiteBallShouldReturnTrueWhenTheWhiteIsAboveAnotherBall(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        ArrayList<Ball> balls = game.getBalls();
        for(Ball ball: balls){
            if (ball.getBallId() == 0){
                ball.setPotted(true);
            }
            if(ball.getBallId() == 13){
                Vector location = new Vector(ball.getLocation().getVectorX(), ball.getLocation().getVectorY());
                assertTrue(game.checkWhiteOverBall(location.getVectorX(), location.getVectorY()));
            }
        }
    }

    @Test
    void switchPlayerShouldSwitchPlayerToPlayerTwoAtTheFirstTime(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        game.addPlayers();
        ArrayList<Player> players = game.getPlayers();
        ArrayList<Ball> balls = game.getBalls();
        game.rotateQueue(1000,1500,500,500);
        game.shoot(10);
        for(Ball ball: balls) {
            if (ball.getBallId() == 0) {
                ball.setVelocity(new Vector(0,0));
            }
        }
        game.switchPlayer();
        for(Player player: players){
            if(player.getPlayerId() == 2){
                assertTrue(player.getCurrentlyPlaying());
            }
        }
    }

    @Test
    void aPlayerShouldWinIfAllHisBallsPlusTheBlackArePotted(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        game.addPlayers();
        ArrayList<Player> players = game.getPlayers();
        ArrayList<Ball> balls = game.getBalls();
        for(Player player: players){
            if(player.getCurrentlyPlaying()){
                player.setTeam("solids");
            }
        }
        for(Ball ball: balls) {
            if(ball.getBallId() >= 1 && ball.getBallId() <= 8){
                ball.setPottedThisShot(true);
            }
        }
        game.pottedDuringShot();
        game.checkWin();
        for(Player player: players){
            if(player.getCurrentlyPlaying()){
                assertEquals(1, player.getPlayerWins());
            }
        }
    }

    @Test
    void aPlayerShouldGetTheTeamOfTheBallThatHeFirstPotted(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        game.addPlayers();
        ArrayList<Player> players = game.getPlayers();
        ArrayList<Ball> balls = game.getBalls();
        for(Ball ball: balls) {
            if(ball.getBallId() == 2){
                ball.setPottedThisShot(true);
                game.setPottedBallCount(1);
                game.setPottedBalls();
            }
        }
        game.pottedDuringShot();
        game.setTeam();
        for(Player player: players){
            if(player.getCurrentlyPlaying()){
                assertEquals("solids", player.getTeam());
            }
        }
    }

    @Test
    void theOtherPlayerShouldGetTheOppositeTeam(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        game.addPlayers();
        ArrayList<Player> players = game.getPlayers();
        ArrayList<Ball> balls = game.getBalls();
        for(Ball ball: balls) {
            if(ball.getBallId() == 2){
                ball.setPottedThisShot(true);
                game.setPottedBallCount(1);
                game.setPottedBalls();
            }
        }
        game.pottedDuringShot();
        game.setTeam();
        for(Player player: players){
            if(!player.getCurrentlyPlaying()){
                assertEquals("stripes", player.getTeam());
            }
        }
    }

    @Test
    void PhysicsShouldSlowDownBall() {
        var game = new poolGame();
        game.initialize(1920, 1080);
        ArrayList<Ball> balls = game.getBalls();
        game.rotateQueue(1000,3000,500,700);
        game.shoot(10);
        for (Ball ball : balls) {
            if (ball.getBallId() == 0) {
                Vector vel = new Vector(ball.getBallVel().getVectorX(), ball.getBallVel().getVectorY());
                game.physics();
                Vector vel2 = new Vector(ball.getBallVel().getVectorX(), ball.getBallVel().getVectorY());
                assert Math.sqrt(Math.pow(vel.getVectorX(),2)) > Math.sqrt(Math.pow(vel2.getVectorX(),2));
                assert Math.sqrt(Math.pow(vel.getVectorY(),2)) > Math.sqrt(Math.pow(vel2.getVectorY(),2));
            }
        }
    }

    @Test
    void multiplyShouldMultiplyAVectorWithAFloat(){
        Vector vector = new Vector(1.25f, 6.5f);
        Vector newVector = vector.multiply(7.7f);
        assertEquals(9.625f, newVector.getVectorX());
        assertEquals(50.05f, newVector.getVectorY());
    }

    @Test
    void addShouldAddAVectorWithAnotherVector(){
        Vector vector = new Vector(1.25f, 6.5f);
        Vector vector2 = new Vector(6,5);
        Vector newVector = vector.add(vector2);
        assertEquals(7.25f, newVector.getVectorX());
        assertEquals(11.5f, newVector.getVectorY());
    }

    @Test
    void notHittingABallShouldProduceFoul(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        ArrayList<Ball> balls = game.getBalls();
        game.addPlayers();
        game.rotateQueue(100,500, 500,100);
        game.shoot(1);
        for(Ball ball: balls){
            if(ball.getBallId() == 0){
                ball.setVelocity(new Vector(0, 0));
                assertTrue(game.checkFoul());
            }
        }
    }

    @Test
    void FoulShouldBeFalseAtGameStart(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        game.addPlayers();
        assertFalse(game.checkFoul());
    }

    @Test
    void checkWinShouldReturnFalseWhenNoOneWins(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        game.addPlayers();
        ArrayList<Ball> balls = game.getBalls();
        for(Ball ball: balls) {
            if(ball.getBallId() == 2){
                ball.setPottedThisShot(true);
                game.setPottedBallCount(1);
                game.setPottedBalls();
            }
        }
        game.checkWin();
        assertFalse(game.checkWin());
    }

    @Test
    void startingPlayerInFirstRoundIsPlayerOne(){
        poolGame game = new poolGame();
        game.addPlayers();
        ArrayList<Player> players = game.getPlayers();
        for(Player player: players){
            if(player.getCurrentlyPlaying()){
                assertEquals("Player 1", player.getPlayerName());
            }
        }
    }

    @Test
    void ballCollisionShouldRemoveOverlapBetweenBalls(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        game.addPlayers();
        ArrayList<Ball> balls = game.getBalls();
        for(Ball ball: balls) {
            if (ball.getBallId() == 1) {
                ball.setLocation(new Vector(500, 500));
            }
            if (ball.getBallId() == 4) {
                ball.setLocation(new Vector(510, 510));
                game.physics();
                assertNotEquals(510, ball.getLocation().getVectorX());
                assertNotEquals(510, ball.getLocation().getVectorY());
            }
        }
    }

    @Test
    void pottedBallShouldBeDeclaredPotted(){
        var game = new poolGame();
        game.initialize(1920, 1080);
        game.addPlayers();
        ArrayList<Ball> balls = game.getBalls();
        for(Ball ball: balls) {
            if (ball.getBallId() == 1) {
                ball.setLocation(new Vector(340, 120));
                game.physics();
                assertTrue(ball.getPotted());
            }
        }
    }

    @Test
    void ifWhiteCollidesWithAnotherBallThatShouldBeSavedInHitBalls() {
        var game = new poolGame();
        game.initialize(1920, 1080);
        game.addPlayers();
        ArrayList<Ball> balls = game.getBalls();
        for (Ball ball : balls) {
            if (ball.getBallId() == 4) {
                ball.setLocation(new Vector(500, 500));
            }
            if (ball.getBallId() == 0) {
                ball.setLocation(new Vector(510, 510));
            }
        }
        game.physics();
        for(Ball ball: balls){
            if(ball.getBallId() == 0){
                assertEquals(1, ball.getHitBalls().size());
            }
        }
    }
}
