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
public class NamedPolygon {

    private String name;
    private List<MovableCircle> vertices = new ArrayList<>();
    private Polygon polygon = new Polygon();
    private boolean isBeingEdited = false;

    public NamedPolygon() {
        init();
    }

    public NamedPolygon(String name) {
        this.name = name;
        init();
    }

    public NamedPolygon(String name, double... points) {
        polygon = new Polygon(points);
        this.name = name;
        init();
    }

    public void addVertices(List<MovableCircle> newVertices) {

    }

    public void removeVertices(List<MovableCircle> oldVertices) {

    }

    private void init() {
        polygon.setFill(null);
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(2);

        final ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>();

        polygon.setOnMousePressed(event -> mousePosition.set(new Point2D(event.getSceneX(), event.getSceneY())));

        polygon.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mousePosition.get().getX();
            double deltaY = event.getSceneY() - mousePosition.get().getY();
            polygon.setLayoutX(polygon.getLayoutX()+deltaX);
            polygon.setLayoutY(polygon.getLayoutY()+deltaY);
            vertices.forEach(c -> {
                c.setLayoutX(c.getLayoutX() + deltaX);
                c.setLayoutY(c.getLayoutY() + deltaY);
            });
            mousePosition.set(new Point2D(event.getSceneX(), event.getSceneY()));
        });
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

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public boolean isBeingEdited() {
        return isBeingEdited;
    }

    public void setBeingEdited(boolean beingEdited) {
        isBeingEdited = beingEdited;
    }

    // representation of NamedPolygon in ListView
    @Override
    public String toString() {
        return name;
    }
}
