package sample;

import customshapes.NamedPolygon;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Gavrilovic on 7/25/2016.
 */
public class MainPane extends BorderPane {

    private ListView<String> listOfPolygons = new ListView<>();
    private DrawingPane drawingPane = new DrawingPane(this);
    private MenuBar menuBar;

    public MainPane() {
        // MENU

        menuBar = createMenuBar();
        Button enter = new Button("Enter");
        Button del   = new Button("Delete");
        Button esc   = new Button("Escape");
        Button save  = new Button("Save");
        Button edit  = new Button("Edit");
        enter.setOnAction(event -> drawingPane.enter());
        del.setOnAction(event -> drawingPane.del());
        esc.setOnAction(event -> drawingPane.esc());
        save.setOnAction(event -> saveLesson("asdf.znamto", drawingPane.getImageView().getImage(), listOfPolygons));
        edit.setOnAction(event -> editPolygons());

        this.setTop(new VBox(menuBar, new HBox(enter, del, esc, save, edit)));

        // LIST OF POLYGONS

        listOfPolygons.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listOfPolygons.setOnMouseClicked(event -> onSelectedPolygon());
        this.setLeft(listOfPolygons);

        // DRAWING PANE

        this.setCenter(drawingPane);

        this.setPrefWidth(800);
        this.setPrefHeight(600);
    }

    private void editPolygons() {
        if (listOfPolygons.getSelectionModel().getSelectedItems().size() != 1) {
            // TODO iskace alert
            return;
        }

        String polygonName = listOfPolygons.getSelectionModel().getSelectedItem();
        NamedPolygon polygon = drawingPane.getPolygons().stream()
                .filter(poly -> poly.getName().equals(polygonName))
                .findFirst()
                .get();

        polygon.getVertices().forEach(c -> {
            c.setFill(Color.RED);
            c.setRadius(c.getRadius() * 2);
        });
        polygon.setFill(null);
    }

    private void onSelectedPolygon() {
        List<NamedPolygon> list = drawingPane.getPolygons();

        // don't fill polygons that are not selected
        list.stream()
            .filter(namedPolygon -> !listOfPolygons.getSelectionModel().getSelectedItems().contains(namedPolygon.getName()))
            .collect(Collectors.toList())
            .forEach(namedPolygon1 -> namedPolygon1.setFill(null));

        // fill selected polygons
        list.stream()
            .filter(namedPolygon -> listOfPolygons.getSelectionModel().getSelectedItems().contains(namedPolygon.getName()))
            .collect(Collectors.toList())
            .forEach(p -> p.setFill(Color.web("0x008800", 0.8)));
    }

    private void saveLesson(String filename, Image image, ListView<String> listOfPolygons) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    // methods for listeners

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.setTitle("Izaberite sliku");
        File imageFile = fileChooser.showOpenDialog(Main.getWindow());
        Image image = new Image(imageFile.toURI().toString());
        drawingPane.getImageView().fitWidthProperty().bind(drawingPane.widthProperty());
        drawingPane.getImageView().fitHeightProperty().bind(drawingPane.heightProperty());
        System.out.println("Size: " + image.getWidth() + "x" + image.getHeight());
        System.out.println("Pane size: " + drawingPane.getWidth() + "x" + drawingPane.getHeight());
        drawingPane.getImageView().setImage(image);
    }

    public void addPolygonToList(NamedPolygon currentPolygon) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Unesite naziv poligona: ");
        Optional<String> text = dialog.showAndWait();
        currentPolygon.setName(text.get());

        listOfPolygons.getItems().add(currentPolygon.getName());
    }

    // gui utility methods

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");
        MenuItem open = new MenuItem("Open");
        open.setOnAction(event -> openFile());
        file.getItems().addAll(open);

        Menu help = new Menu("Help");
        MenuItem about = new MenuItem("About");
        help.getItems().addAll(about);

        menuBar.getMenus().addAll(file, help);

        return menuBar;
    }

    // getters and setters

    public ListView<String> getListOfPolygons() {
        return listOfPolygons;
    }

    public void setListOfPolygons(ListView<String> listOfPolygons) {
        this.listOfPolygons = listOfPolygons;
    }
}
