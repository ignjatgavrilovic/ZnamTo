package sample;

import customshapes.MovableCircle;
import customshapes.NamedPolygon;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gavrilovic on 7/25/2016.
 */
public class DrawingPane extends Pane
{
    List<NamedPolygon> polygons = new ArrayList<>();
    ImageView imageView = new ImageView();
    MainPane parent;
    boolean nextPolygon = true;
    NamedPolygon currentPolygon;
    private double dragDeltaX;
    private double dragDeltaY;


    public DrawingPane(MainPane parent) {
        this(null, parent);
    }

    public DrawingPane(Image image, MainPane parent) {
        if (image != null)
            imageView.setImage(image);
        setImageViewEventHandlers2();
        getChildren().addAll(imageView);
        this.parent = parent;
        imageView.setFocusTraversable(true);
    }

    public void enter() {
        currentPolygon.getVertices().forEach(c -> {
            currentPolygon.getPoints().add(c.getCenterX());
            currentPolygon.getPoints().add(c.getCenterY());
        });

        currentPolygon.setFill(null);
        getChildren().add(currentPolygon);
        polygons.add(currentPolygon);
        nextPolygon = true;
        parent.addPolygonToList(currentPolygon);
    }

    public void esc() {
        currentPolygon.getVertices().forEach(c -> this.getChildren().remove(c));
        currentPolygon.getVertices().clear();
    }

    public void del() {
        MovableCircle c = currentPolygon.getVertices().get(currentPolygon.getVertices().size() - 1);
        this.getChildren().remove(c);
        currentPolygon.getVertices().remove(c);
    }

    private void setImageViewEventHandlers2() {
        imageView.setOnMousePressed(event -> {
            if (nextPolygon) {
                currentPolygon = new NamedPolygon();
                nextPolygon = false;
            }
            MovableCircle circle = new MovableCircle(event.getX(), event.getY(), 2, currentPolygon);
            currentPolygon.getVertices().add(circle);
            getChildren().add(circle);
        });

        // TODO sredi da ovo zapravo radi
        imageView.setOnKeyPressed(event -> {
            if (KeyCode.ENTER == event.getCode()) {
                enter();
            }
            // TODO sredi posebne slucajeve za ESC i DEL
            if (KeyCode.ESCAPE == event.getCode()) {
                esc();
            }
            if (KeyCode.DELETE == event.getCode()) {
                del();
            }
        });
    }

    public void delete(NamedPolygon polygon) {
        polygon.getVertices().forEach(c -> getChildren().remove(c));
        polygon.getVertices().clear();
        getChildren().remove(polygon);
    }

    public NamedPolygon getCopy(NamedPolygon original) {
        NamedPolygon copy = new NamedPolygon(original.getName() + "Copy");

        List<MovableCircle> list = new ArrayList<>();
        original.getVertices().forEach(circle -> {
            MovableCircle c = new MovableCircle(circle.getCenterX() + 15, circle.getCenterY() + 15, circle.getRadius(), copy);
            list.add(c);
            copy.getPoints().add(c.getCenterX());
            copy.getPoints().add(c.getCenterY());
        });

        copy.setVertices(list);
        return copy;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public List<NamedPolygon> getPolygons() {
        return polygons;
    }

    public void setPolygons(List<NamedPolygon> polygons) {
        this.polygons = polygons;
    }
}
