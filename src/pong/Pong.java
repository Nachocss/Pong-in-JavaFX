/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pong;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Nacho
 */
public class Pong extends Application {
    
    int ballXatStart = 10;
    int ballYatStart = 10;
    boolean goingRight = true;
    boolean goingDown = true;
    int borderWidth = 275;
    int borderHeight = 233;
    boolean playing = true;
    Rectangle ball = new Rectangle(10, 10);       
    Rectangle leftPlayer = new Rectangle(10, 50);
    Rectangle rightPlayer = new Rectangle(10, 50);
    Group root = new Group();
    Scene scene = new Scene(root, 300, 250);
    private int leftScore = 0;
    private int rightScore = 0;
    Text score = new Text();
    Thread rightPlayerMovement;
    Thread leftPlayerThread;
    int rightPlayerDirection = 0;  //0: stop, 1: down, -1: up
    int leftPlayerDirection = 0;  
    int PLAYER_BORDER_LIMIT = 200;
    int BALL_MOVEMENT_DELAY = 100;
    int PLAYER_MOVEMENT_DELAY = 50;
    
    @Override
    public void start(Stage primaryStage) {
       
        ball.setFill(Paint.valueOf("White"));
        leftPlayer.setFill(Paint.valueOf("White"));
        rightPlayer.setFill(Paint.valueOf("White"));
        
        score.setFill(Color.WHITE);
        score.setFont(Font.font ("Verdana", 24));
        score.setText(leftScore +" | "+ rightScore);
        score.setTranslateX(130);
        score.setTranslateY(30);
        
        root.getChildren().add(ball);
        root.getChildren().add(leftPlayer);
        root.getChildren().add(rightPlayer);
        root.getChildren().add(score);
       
        scene.setFill(Paint.valueOf("Black"));        
        
        primaryStage.setTitle("Pong!");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
        
        startGame();
        
        primaryStage.setOnCloseRequest((WindowEvent t) -> {
            System.exit(0);
        });
    }
    
    int ballOrientation = 5;
    
    private void setBallDirection() {
        if (ball.getTranslateX()+10 == rightPlayer.getTranslateX() 
                && ball.getTranslateY() >= rightPlayer.getTranslateY()
                && ball.getTranslateY() <= rightPlayer.getTranslateY()+50) {
            goingRight = false;
        }
        else if (ball.getTranslateX() == leftPlayer.getTranslateX()+10 
                && ball.getTranslateY() >= leftPlayer.getTranslateY()
                && ball.getTranslateY() <= leftPlayer.getTranslateY()+50) {
            ballOrientation = new Random().nextInt(11)+5;
            goingRight = true;
        }
        if (ball.getTranslateX() > borderWidth || ball.getTranslateX() < 13)
            setScorePoint();
        
        if (ball.getTranslateY() < 10)
            goingDown = true;
        else if (ball.getTranslateY() > borderHeight)
            goingDown = false;
    }
    
    private void setScorePoint() {
        if (ball.getTranslateX() > borderWidth)
            leftScore++;
        else
            rightScore++;
        
        score.setText(leftScore +" | "+ rightScore);
        setStartPositions();
    }
    
    
    private void setStartPositions() {
        ball.setTranslateX(140);
        ball.setTranslateY(120);
        
        leftPlayer.setTranslateX(25);
        leftPlayer.setTranslateY(100);
        
        rightPlayer.setTranslateX(borderWidth);
        rightPlayer.setTranslateY(100);
    }
    
    private void startGame() {
        setStartPositions();
        setKeyEvents();
        ballMovement.start();
    }
    
    Thread ballMovement = new Thread(){
        public void run() {
            while (playing) {
                setBallDirection();

                if (goingRight)
                    ball.setTranslateX(ball.getTranslateX()+5);
                else
                    ball.setTranslateX(ball.getTranslateX() - 5);

                if (goingDown)
                    ball.setTranslateY(ball.getTranslateY()+ballOrientation);
                else 
                    ball.setTranslateY(ball.getTranslateY()-ballOrientation);

                try {
                    Thread.sleep(BALL_MOVEMENT_DELAY);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
    
    private void rightPlayerMovement() {
        rightPlayerMovement = new Thread(){
            public void run() {
                while (true) {
                    switch(rightPlayerDirection) {
                        case 0: break;
                        case 1: if (rightPlayer.getTranslateY() < PLAYER_BORDER_LIMIT)
                                rightPlayer.setTranslateY(rightPlayer.getTranslateY() + 5);
                            break;
                        case -1: if (rightPlayer.getTranslateY() > 10)
                                rightPlayer.setTranslateY(rightPlayer.getTranslateY() - 5);
                            break;
                    }
                    try {
                        Thread.sleep(PLAYER_MOVEMENT_DELAY);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        rightPlayerMovement.start();
    }
    
      
    private void leftPlayerMovement() {
        leftPlayerThread = new Thread(){
            public void run() {
                while (true) {
                    switch(leftPlayerDirection) {
                        case 0: break;
                        case 1: if (leftPlayer.getTranslateY() < PLAYER_BORDER_LIMIT)
                                leftPlayer.setTranslateY(leftPlayer.getTranslateY() + 5);
                            break;
                        case -1: if (leftPlayer.getTranslateY() > 10)
                                leftPlayer.setTranslateY(leftPlayer.getTranslateY() - 5);
                            break;
                    }
                    try {
                        Thread.sleep(PLAYER_MOVEMENT_DELAY);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        leftPlayerThread.start();
    }
    
    private void setKeyEvents() {
        if (scene.getOnKeyPressed() == null) {
            rightPlayerMovement();
            leftPlayerMovement();
            scene.setOnKeyPressed((KeyEvent ke) -> {
                switch(ke.getCode()){
                    case UP:
                        rightPlayerDirection = -1;
                        break;
                    case DOWN:
                        rightPlayerDirection = 1;
                        break;
                    case W:
                        leftPlayerDirection = -1;
                        break;
                    case S:
                        leftPlayerDirection = 1;
                        break;
                }
            });
            scene.setOnKeyReleased((KeyEvent ke) -> {
                switch(ke.getCode()){
                    case UP:
                    case DOWN:
                        rightPlayerDirection = 0;
                        break;
                    case W:
                    case S:
                        leftPlayerDirection = 0;
                        break;
                }
            });
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}