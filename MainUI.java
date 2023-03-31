import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableFloatValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.control.*;
import javafx.scene.image.*;
import java.io.*;

import javafx.scene.layout.*;
import javafx.event.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class Collision implements Runnable {
    Thread t;
    Circle player;
    Circle c2;
    boolean collided;
    Label l;
    static String s;
    Stage stage;

    Collision(Circle player, Circle c2, Label l, Stage stage) {
        this.player = player;
        this.c2 = c2;
        this.l = l;
        this.stage = stage;
        collided = false;
        s = "";
        t = new Thread(this);
        t.start();
    }

    public void run() {
        while (!collided) {
            double dx = player.getTranslateX() - c2.getTranslateX() + player.getCenterX() - c2.getCenterX();
            double dy = player.getTranslateY() - c2.getTranslateY() + player.getCenterY() - c2.getCenterY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= player.getRadius() + c2.getRadius()) {
                player.setFill(Color.RED);
                c2.setFill(Color.RED);
                collided = true;
                Platform.runLater(() -> {
                    stage.show();
                });
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

public class MainUI extends Application {

    static boolean collided = false;

    public static void main(String[] args) {
        launch(args);
    }

    void collision(Circle player, Circle c2, Stage stage2, Label l) {
        new Collision(player, c2, l, stage2);
    }

    void attack(double height, double width, Group root, Circle player, Stage stage2, Label l) {
        Circle[] balls = new Circle[10];
        int count = 0;
        while (count < 10) {
            int randStartX = (int) (Math.random() * width);
            int randStartY = (int) -(Math.random() * 10000);
            System.out.println(randStartX);
            balls[count] = new Circle(randStartX, randStartY, 30);
            final Circle x = balls[count % 10];
            TranslateTransition transition = new TranslateTransition();
            transition.setDuration(Duration.millis(5000));
            transition.setToX(0);
            transition.setToY(height - 110 - randStartY);
            transition.setCycleCount(1);
            transition.setAutoReverse(false);
            transition.setNode(x);
            transition.play();
            transition.setOnFinished(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    root.getChildren().remove(x);
                    int points = Integer.parseInt(l.getText().split(": ")[1]);
                    l.setText("Points: " + ++points);
                }
            });
            root.getChildren().add(balls[count%10]);
            collision(x, player, stage2, l);
            count++;
        }

    }

    public void start(Stage stage1) throws Exception {
        double width = 1366;
        double height = 768;

        Stage stage2 = new Stage();
        stage2.setTitle("GAME OVER");
        stage2.initModality(Modality.APPLICATION_MODAL);
        stage2.initOwner(stage1);
        VBox g2 = new VBox();
        Label l2 = new Label("Game Over \n Better Luck next time!");
        l2.setTextFill(Color.web("#FE0000"));
        Font font = Font.font("Berlin Sans FB", FontWeight.BOLD, FontPosture.ITALIC, 20);
        l2.setFont(font);
        Button b1 = new Button("Restart");
        g2.getChildren().addAll(l2, b1);

        b1.addEventHandler(ActionEvent.ANY, new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                stage2.close();
                stage1.close();
                stage1.show();
            }
        });
        FileInputStream fis1 = new FileInputStream("star.png");
        Image im1 = new Image(fis1, 20, 20, false, false);
        ImageView iv = new ImageView(im1);

        HBox h1 = new HBox();
        Scene scene2 = new Scene(g2, 300, 300);
        stage2.setScene(scene2);
        Group root = new Group();
        Rectangle r1 = new Rectangle(0, height - 200, width, 200);
        r1.setFill(Color.web("#37449e"));

        int score = 0;
        Label l = new Label(("Points: " + score), iv);
        h1.getChildren().addAll(l);
        l.setFont(font);
        Circle player = new Circle(150, height - 200 - 100, 100);
        FileInputStream fis = new FileInputStream("sprite.jpeg");
        Image im = new Image(fis);
        // ImageView iv = new ImageView(im);
        player.setFill(new ImagePattern(im));
        attack(height, width, root, player, stage2, l);
        root.getChildren().addAll(r1, player, h1);
        Scene scene = new Scene(root, width, height, Color.web("#BBEFFF"));

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.UP) {
                    player.setTranslateY(   player.getTranslateY() - 10);
                } else if (event.getCode() == KeyCode.DOWN) {
                    double currentY = player.getTranslateY();
                    if (currentY < 0) {
                        player.setTranslateY(currentY + 10);
                        System.out.println(currentY);
                    }
                } else if (event.getCode() == KeyCode.LEFT) {
                    player.setTranslateX(player.getTranslateX() - 10);
                } else if (event.getCode() == KeyCode.RIGHT) {
                    player.setTranslateX(player.getTranslateX() + 10);
                }
            }
        });

        stage1.setScene(scene);
        stage1.show();
    }

}

