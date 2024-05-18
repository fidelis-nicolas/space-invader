package com.game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Pane gamepane;
    @FXML
    Circle mainCharacter;
    @FXML
    Button right;
    @FXML
    Button left;
    @FXML
    Button btnshoot;

    private ArrayList<Rectangle> enemies = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();

    private double position = 0.0;

    @FXML
    public void moveMainCharacter(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        switch (keyCode) {
            case A:
                position -= 15;
                mainCharacter.setTranslateX(position);
                break;
            case S:
                position += 15;
                mainCharacter.setTranslateX(position);
                break;
            case SPACE:
                shoot();
                break;
            default:
                break;
        }
    }

    public void moveLeft() {
        position -= 10;
        mainCharacter.setTranslateX(position);
    }

    public void moveRight() {
        position += 10;
        mainCharacter.setTranslateX(position);
    }




    @FXML
    public void shoot() {
        double mainCharacterX = mainCharacter.getTranslateX();
        double bulletX = mainCharacterX + mainCharacter.getLayoutX();

        Bullet bullet = new Bullet(bulletX, 339, false);
        gamepane.getChildren().add(bullet);
        bullets.add(bullet);

        new Thread(() -> {
            try {
                while (bullet.getCenterY() > 0) {
                    Thread.sleep(50);
                    bullet.moveUp();

                    synchronized (enemies) {
                        Iterator<Rectangle> iterator = enemies.iterator();
                        while (iterator.hasNext()) {
                            Rectangle enemy = iterator.next();
                            if (bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                                Platform.runLater(() -> {
                                    gamepane.getChildren().remove(enemy);
                                    iterator.remove();
                                    gamepane.getChildren().remove(bullet);
                                    if (enemies.isEmpty()) {
                                        System.out.println("You won!!");
                                    }
                                });
                                return;
                            }
                        }
                    }
                }
                bullets.remove(bullet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void enemyShoot() {
        if (enemies.isEmpty()) return;

        Random random = new Random();
        int index = random.nextInt(enemies.size());
        Rectangle enemy = enemies.get(index);

        double bulletStartX = enemy.getX() + enemy.getWidth() / 2;
        double bulletStartY = enemy.getY() + enemy.getHeight();

        Bullet bullet = new Bullet(bulletStartX, bulletStartY, true);
        Platform.runLater(() -> {
            gamepane.getChildren().add(bullet);
            System.out.println("Enemy shooting from position: " + enemy.getX() + ", " + enemy.getY());
        });
        bullets.add(bullet);

        new Thread(() -> {
            try {
                while (bullet.getCenterY() < gamepane.getHeight()) {
                    Thread.sleep(50);
                    Platform.runLater(bullet::moveDown);
                    double screenHeight = gamepane.getHeight();
                    double screenCenterY = screenHeight / 2;

                    if (bullet.getCenterY() >= screenCenterY && !bullet.isPassedCenter()) {
                        bullet.setPassedCenter(true);
                        System.out.println("Bullet passed center, shooting again.");
                        Platform.runLater(this::enemyShoot); // Call enemyShoot once the bullet passes the center
                    }

                    if (bullet.getBoundsInParent().intersects(mainCharacter.getBoundsInParent())) {
                        Platform.runLater(() -> {
                            System.out.println("You lost!!");
                            // Handle player losing the game
                        });
                        break;
                    }
                }
                Platform.runLater(() -> gamepane.getChildren().remove(bullet));
                bullets.remove(bullet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int i = 0; i < 10; i++) {
            Rectangle enemy = new Rectangle(50 + i * 50, 30, 20, 20);
            enemy.setFill(Color.RED);
            enemies.add(enemy);
            gamepane.getChildren().add(enemy);
        }
        enemyShoot(); // Start shooting with the first enemy
    }

}
