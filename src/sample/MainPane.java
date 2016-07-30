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

    private ListView<NamedPolygon> listOfPolygons = new ListView<>();
    private DrawingPane drawingPane = new DrawingPane(this);
    private MenuBar menuBar;
    private Button enter = new Button("Enter");
    private Button del   = new Button("Delete");
    private Button esc   = new Button("Escape");
    private Button edit  = new Button("Edit");

    public MainPane() {
        // MENU

        menuBar = createMenuBar();

        enter.setOnAction(event -> drawingPane.enter());
        del.setOnAction(event -> drawingPane.del());
        esc.setOnAction(event -> drawingPane.esc());
        edit.setOnAction(event -> editPolygons());

        this.setTop(new VBox(menuBar, new HBox(enter, del, esc, edit)));

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

        NamedPolygon polygon = listOfPolygons.getSelectionModel().getSelectedItem();

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
            polygon.getPolygon().setFill(null);
            polygon.setBeingEdited(true);
            edit.setText("Stop Editing");
        }
    }

    private void onSelectedPolygon() {
        List<NamedPolygon> selected = listOfPolygons.getSelectionModel().getSelectedItems();

        listOfPolygons.getItems().forEach(namedPolygon -> {
            if (selected.contains(namedPolygon)) {
                namedPolygon.getPolygon().setFill(Color.web("ANTIQUEWHITE", 0.8));
            } else {
                namedPolygon.getPolygon().setFill(null);
            }
        });
    }

    private void saveLesson(Image image, ListView<NamedPolygon> listOfPolygons) {
        try {
            String filename = ""; // TODO dialog za unos
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

        listOfPolygons.getItems().add(currentPolygon);
    }

    private void listViewKeyPressed(KeyEvent event) {
        // get selected polygons
        List<NamedPolygon> selected = listOfPolygons.getSelectionModel().getSelectedItems();

        if (event.getCode() == KeyCode.DELETE) {
            // remove polygons and their circles from drawingPane
            selected.forEach(polygon -> drawingPane.delete(polygon));

            // remove polygon names from listView
            listOfPolygons.getItems().removeAll(selected);
        } else if (event.getCode() == KeyCode.R) { // rename
            if (selected.size() == 1) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setHeaderText("Novi naziv poligona: ");
                Optional<String> text = dialog.showAndWait();
                selected.get(0).setName(text.get());
                listOfPolygons.refresh();
            }
            else {
                // TODO alert dialog da sme samo jedan izabrani da se rename-uje
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Greska");
                //alert.setHeaderText("Information Alert");
                alert.setContentText("Mozete preimenovati samo jedan po jedan poligon!");
                alert.show();
            }
        } else if (event.getCode() == KeyCode.C) { // copy
            List<NamedPolygon> copies = new ArrayList<>();
            selected.forEach(poly -> copies.add(drawingPane.getCopy(poly)));
            copies.forEach(poly -> {
                listOfPolygons.getItems().add(poly);
                drawingPane.getChildren().add(poly.getPolygon());
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
        MenuItem save = new MenuItem("Save");
        save.setOnAction(event -> saveLesson(drawingPane.getImageView().getImage(), listOfPolygons));
        file.getItems().addAll(open, save);

        Menu help = new Menu("Help");
        MenuItem about = new MenuItem("About");
        help.getItems().addAll(about);

        menuBar.getMenus().addAll(file, help);

        return menuBar;
    }

    // getters and setters

    public ListView<NamedPolygon> getListOfPolygons() {
        return listOfPolygons;
    }

    public void setListOfPolygons(ListView<NamedPolygon> listOfPolygons) {
        this.listOfPolygons = listOfPolygons;
    }
}
