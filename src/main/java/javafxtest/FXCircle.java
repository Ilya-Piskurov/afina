package javafxtest;

import javafx.scene.shape.Circle;

public class FXCircle {
    private double prev_x = 0.f, prev_y = 0.f;

    Circle circle;

    FXCircle(float x_pos, float y_pos, float radius){
        circle = new Circle(x_pos, y_pos, radius);
    }

    public double getX() {
        return circle.getCenterX();
    }

    public void setX(double x) {
        circle.setCenterX(x);
    }

    public double getY() {
        return circle.getCenterY();
    }

    public void setY(double y) {
        circle.setCenterY(y);
    }

    public double getRadius() {
        return circle.getRadius();
    }

    public void setRadius(double radius) {
        circle.setRadius(radius);
    }

    public double getPrev_x() {
        return prev_x;
    }

    public void setPrev_x(double prev_x) {
        this.prev_x = prev_x;
    }

    public double getPrev_y() {
        return prev_y;
    }

    public void setPrev_y(double prev_y) {
        this.prev_y = prev_y;
    }
}
