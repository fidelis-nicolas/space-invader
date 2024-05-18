package com.game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bullet extends Circle {
    private boolean passedCenter;

    public Bullet(double x, double y, boolean isEnemyBullet) {
        super(x, y, 5, isEnemyBullet ? Color.RED : Color.GREEN);
        this.passedCenter = false;
    }

    public void moveDown() {
        setCenterY(getCenterY() + 5);
    }

    public void moveUp() {
        setCenterY(getCenterY() - 5);
    }

    public boolean isPassedCenter() {
        return passedCenter;
    }

    public void setPassedCenter(boolean passedCenter) {
        this.passedCenter = passedCenter;
    }
}
