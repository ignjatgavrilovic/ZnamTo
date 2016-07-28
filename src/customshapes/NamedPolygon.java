package customshapes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gavrilovic on 7/25/2016.
 */
public class NamedPolygon extends Polygon {

    private String name;
    private List<MovableCircle> vertices = new ArrayList<>();
    private boolean isBeingEdited = false;

    public NamedPolygon() {
        this.setFill(null);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(2);

        final ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>();

        this.setOnMousePressed(event -> mousePosition.set(new Point2D(event.getSceneX(), event.getSceneY())));

        this.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mousePosition.get().getX();
            double deltaY = event.getSceneY() - mousePosition.get().getY();
            setLayoutX(getLayoutX()+deltaX);
            setLayoutY(getLayoutY()+deltaY);
            vertices.forEach(c -> {
                c.setLayoutX(c.getLayoutX() + deltaX);
                c.setLayoutY(c.getLayoutY() + deltaY);
            });
            mousePosition.set(new Point2D(event.getSceneX(), event.getSceneY()));
        });
    }

    public NamedPolygon(String name) {
        this.name = name;
    }

    public NamedPolygon(String name, double... points) {
        super(points);
        this.name = name;
    }

    public void addVertices(List<MovableCircle> newVertices) {

    }

    public void removeVertices(List<MovableCircle> oldVertices) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MovableCircle> getVertices() {
        return vertices;
    }

    public void setVertices(List<MovableCircle> vertices) {
        this.vertices = vertices;
    }

    public boolean isBeingEdited() {
        return isBeingEdited;
    }

    public void setBeingEdited(boolean beingEdited) {
        isBeingEdited = beingEdited;
    }
}
