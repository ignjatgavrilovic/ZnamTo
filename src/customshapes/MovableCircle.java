package customshapes;

import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * Created by Gavrilovic on 7/28/2016.
 */
public class MovableCircle extends Circle {

    private NamedPolygon parent;
    private double dragDeltaX;
    private double dragDeltaY;


    public MovableCircle(NamedPolygon parent) {
        this(0, 0, 0, Color.BLACK, parent);
    }

    public MovableCircle(double radius, NamedPolygon parent) {
        this(0, 0, radius, Color.BLACK, parent);
    }

    public MovableCircle(double radius, Paint fill, NamedPolygon parent) {
        this(0, 0, radius, fill, parent);
    }

    public MovableCircle(double centerX, double centerY, double radius, NamedPolygon parent) {
        this(centerX, centerY, radius, Color.BLACK, parent);
    }

    public MovableCircle(double centerX, double centerY, double radius, Paint fill, NamedPolygon parent) {
        super(centerX, centerY, radius, fill);
        this.parent = parent;
        setDragHandlers();    
    }

    public void setDragHandlers() {
        this.centerXProperty().addListener((number, oldValue, newValue) -> {
            for (int i = 0; i < parent.getVertices().size(); i++) {
                if (parent.getVertices().get(i).equals(this)) {
                    parent.getPoints().set(i*2, newValue.doubleValue());
                }
            }
        });

        this.centerYProperty().addListener((number, oldValue, newValue) -> {
            for (int i = 0; i < parent.getVertices().size(); i++) {
                if (parent.getVertices().get(i).equals(this)) {
                    parent.getPoints().set(i*2 + 1, newValue.doubleValue());
                }
            }
        });

        this.setOnMousePressed(mouseEvent -> {
            dragDeltaX = this.getCenterX() - mouseEvent.getSceneX();
            dragDeltaY = this.getCenterY() - mouseEvent.getSceneY();
        });

        this.setOnMouseDragged(mouseEvent -> {
            this.setCenterX( mouseEvent.getSceneX() + dragDeltaX );
            this.setCenterY( mouseEvent.getSceneY() + dragDeltaY );
            this.setCursor( Cursor.MOVE );
        });

        this.setOnMouseEntered(mouseEvent -> this.setCursor( Cursor.HAND ));

        this.setOnMouseReleased(mouseEvent -> this.setCursor( Cursor.HAND ));
    }
    
}
