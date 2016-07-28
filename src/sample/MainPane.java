package sample;

import customshapes.NamedPolygon;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
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
    Button enter = new Button("Enter");
    Button del   = new Button("Delete");
    Button esc   = new Button("Escape");
    Button save  = new Button("Save");
    Button edit  = new Button("Edit");

    public MainPane() {
        // MENU

        menuBar = createMenuBar();

        enter.setOnAction(event -> drawingPane.enter());
        del.setOnAction(event -> drawingPane.del());
        esc.setOnAction(event -> drawingPane.esc());
        save.setOnAction(event -> saveLesson("asdf.znamto", drawingPane.getImageView().getImage(), listOfPolygons));
        edit.setOnAction(event -> editPolygons());

        this.setTop(new VBox(menuBar, new HBox(enter, del, esc, save, edit)));

        // LIST OF POLYGONS

        listOfPolygons.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listOfPolygons.setOnMouseClicked(event -> onSelectedPolygon());
        listOfPolygons.setOnKeyPressed(event -> listViewKeyPressed(event));
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

        // find polygon by name
        String polygonName = listOfPolygons.getSelectionModel().getSelectedItem();
        NamedPolygon polygon = drawingPane.getPolygons().stream()
                .filter(poly -> poly.getName().equals(polygonName))
                .findFirst()
                .get();

        if (polygon.isBeingEdited()) {
            polygon.getVertices().forEach(c -> {
                c.setFill(Color.BLACK);
                c.setRadius(c.getRadius() / 2);
            });
            polygon.setBeingEdited(false);
            edit.setText("Edit");
        } else {
            polygon.getVertices().forEach(c -> {
                c.setFill(Color.RED);
                c.setRadius(c.getRadius() * 2);
            });
            polygon.setFill(null);
            polygon.setBeingEdited(true);
            edit.setText("Stop Editing");
        }
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

    private void listViewKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            System.out.println("a");

            // remove polygons and their circles from drawingPane
            drawingPane.getPolygons()
                .stream()
                .filter(polygon -> listOfPolygons.getSelectionModel().getSelectedItems().contains(polygon.getName()))
                .collect(Collectors.toList())
                .forEach(polygon -> drawingPane.delete(polygon));

            // remove polygon names from listView
            listOfPolygons.getItems().removeAll(
                listOfPolygons.getItems()
                        .stream()
                        .filter(item -> listOfPolygons.getSelectionModel().getSelectedItems().contains(item))
                        .collect(Collectors.toList())
            );
        } else if (event.getCode() == KeyCode.R) { // rename
            // TODO dialog for rename
        } else if (event.getCode() == KeyCode.C) { // copy
            // get selected polygons
            List<NamedPolygon> selected = drawingPane.getPolygons()
                .stream()
                .filter(polygon -> listOfPolygons.getSelectionModel().getSelectedItems().contains(polygon.getName()))
                .collect(Collectors.toList());

            List<NamedPolygon> copies = new ArrayList<>();
            selected.forEach(poly -> copies.add(drawingPane.getCopy(poly)));
            copies.forEach(poly -> {
                listOfPolygons.getItems().add(poly.getName());
                drawingPane.getChildren().add(poly);
                drawingPane.getPolygons().add(poly);
                poly.getVertices().forEach(c -> drawingPane.getChildren().add(c));
            });
            // TODO add rename for copied item(s)
        }
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
