package sample;

import customshapes.MovableCircle;
import customshapes.NamedPolygon;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gavrilovic on 7/25/2016.
 */
public class DrawingPane extends Pane
{
    List<NamedPolygon> polygons = new ArrayList<>();
    NamedPolygon currentPolygon;
    ImageView imageView = new ImageView();
    MainPane parent;
    boolean nextPolygon = true;

    public DrawingPane(MainPane parent) {
        this(null, parent);
    }

    public DrawingPane(Image image, MainPane parent) {
        if (image != null)
            imageView.setImage(image);
        setImageViewEventHandlers();
        getChildren().addAll(imageView);
        this.parent = parent;
        imageView.setFocusTraversable(true);
    }

    // add the polygon to the picture
    // TODO also add the name to listView
    public void enter() {
        currentPolygon.getVertices().forEach(c -> {
            currentPolygon.getPolygon().getPoints().add(c.getCenterX());
            currentPolygon.getPolygon().getPoints().add(c.getCenterY());
        });

        currentPolygon.getPolygon().setFill(null);
        getChildren().add(currentPolygon.getPolygon());
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

    private void setImageViewEventHandlers() {
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
            // TODO sredi posebne slucajeve za ESC i DEL (jos nema dodatih krugova itd)
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
        getChildren().remove(polygon.getPolygon());
    }

    public NamedPolygon getCopy(NamedPolygon original) {
        NamedPolygon copy = new NamedPolygon(original.getName() + "Copy");

        List<MovableCircle> list = new ArrayList<>();
        original.getVertices().forEach(circle -> {
            MovableCircle c = new MovableCircle(circle.getCenterX() + 15, circle.getCenterY() + 15, circle.getRadius(), copy);
            list.add(c);
            copy.getPolygon().getPoints().add(c.getCenterX());
            copy.getPolygon().getPoints().add(c.getCenterY());
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
