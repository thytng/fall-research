package mergexperiment;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class DataController extends Application {
    private BorderPane borderPane = new BorderPane();

    private ObservableList<DataEntry> originalData;
    private ObservableList<DataEntry> currentData;
    private ObservableList<DataEntry> changedData = FXCollections.observableArrayList();

    private Scene mainScene = new Scene(borderPane);
    private TableView<DataEntry> mainTable = new TableView<>();

    private ChoiceBox<String> choiceBox = new ChoiceBox<>();
    private TextField searchField = new TextField();
    private GridPane sideBar = new GridPane();

    private Button compareButton = new Button("Compare Changes");
    private Button commitButton = new Button("Commit Changes");
    private Button revertButton = new Button("Revert Changes");

    private Button goLeft = new Button("Last 15");
    private Button goRight = new Button("Next 15");

    private ObservableList<DataEntry> unchangedEntries;
    private ObservableList<DataEntry> changedEntries;
    private TableView<DataEntry> unchangedTable = createEmptyTable(false);
    private TableView<DataEntry> changedTable = createEmptyTable(false);
    private Stage compareStage = new Stage();
    private GridPane comparePane = new GridPane();
    private GridPane buttonPane = new GridPane();
    private Scene compareScene = new Scene(comparePane);

    public void structureMainTableView() {
        mainTable = createEmptyTable(true);
        currentData = getUpdatedData();
        mainTable.setItems(currentData);
        mainTable.setEditable(true);
        mainTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        borderPane.setCenter(mainTable);

        buttonPane.add(goLeft, 0, 0);
        buttonPane.add(goRight, 1, 0);
        GridPane.setHalignment(goLeft, HPos.LEFT);
        GridPane.setHalignment(goRight, HPos.RIGHT);
        goLeft.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    mainTable.setItems(DAO.loadLast());
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        goRight.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    mainTable.setItems(DAO.loadNext());
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        borderPane.setBottom(buttonPane);
    }

    public void structureSearchView() {
        choiceBox.getItems().addAll("Employee Number", "Gender");
        choiceBox.setValue("Employee Number");

        searchField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String field = choiceBox.getValue().equals("Employee Number") ? "emp_no" : "gender";
                try {
                    currentData = DAO.searchEntry(field, searchField.getText().toLowerCase());
                    mainTable.setItems(currentData);
                } catch (SQLException | ClassNotFoundException e) {

                }
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
        {
            if (newVal != null) {
                currentData = getUpdatedData();
                mainTable.setItems(currentData);
                searchField.clear();
            }
        });

        sideBar.add(choiceBox, 0, 0);
        sideBar.add(searchField, 1, 0);
        sideBar.setHgap(10);
        sideBar.setVgap(10);
        sideBar.setPadding(new Insets(0, 10, 0, 10));
        borderPane.setLeft(sideBar);
    }

    public void structureCompareView() {
        comparePane.add(unchangedTable, 0, 0);
        comparePane.add(changedTable, 1, 0);

        comparePane.add(commitButton, 0, 1);
        GridPane.setHalignment(commitButton, HPos.CENTER);
        comparePane.add(revertButton, 1, 1);
        GridPane.setHalignment(revertButton, HPos.CENTER);

        comparePane.setVgap(10);
        comparePane.setHgap(5);
        comparePane.setPadding(new Insets(10, 0, 10, 0));

        compareButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                structureCompareWindow();
                mainTable.setEditable(true);
                mainTable.setItems(currentData);
                mainTable.refresh();
            }
        });

        sideBar.add(compareButton, 0, 1);
    }

    private void structureCompareWindow() {
        getDataChanges();

        unchangedTable.setItems(unchangedEntries);
        changedTable.setItems(changedEntries);

        compareStage.setScene(compareScene);
        mainTable.setEditable(false);

        // COMMIT
        commitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DBUtil.commitChanges();
                if (alertCommit(true).get() == ButtonType.OK) {
                    changedData = FXCollections.observableArrayList();
                    originalData = getUpdatedData();
                }
                compareStage.hide();
            }
        });


        // REVERT
        revertButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DBUtil.revertChanges();
                if (alertCommit(false).get() == ButtonType.OK) {
                    changedData = FXCollections.observableArrayList();
                    currentData = getUpdatedData();
                }
                compareStage.hide();
            }
        });

        compareStage.showAndWait();
    }

    private TableView<DataEntry> createEmptyTable(boolean modifyStatus) {
        TableView<DataEntry> table = new TableView<>();

        TableColumn timestampCol = new TableColumn("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Timestamp>("timestamp"));
        TableColumn usernameCol = new TableColumn("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("username"));
        TableColumn sampleCol = new TableColumn("Sample");
        sampleCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("sample"));

//        private SimpleObjectProperty<Timestamp> timestamp;
//        private StringProperty username;
//        private IntegerProperty id;
//        private StringProperty sample;
//        private StringProperty control;
//        private StringProperty windowId;
//        private StringProperty gene;
//        private DoubleProperty avgCnvRatio;
//        private DoubleProperty bbStd;
//        private DoubleProperty cnvRatio;
//        private DoubleProperty covStd;
//        private DoubleProperty avgDupRatio;
//        private DoubleProperty gcPerc;
//        private DoubleProperty alleleFreq;
//        private IntegerProperty readStats;
//        private BooleanProperty isTraining;
//        private BooleanProperty hetClassification;
//        private DoubleProperty avgBowtieBwaRatio;
//        private DoubleProperty cnvRatioStd;
//        private DoubleProperty avgCov;


        TableColumn classifiedCol = new TableColumn("Het_Classification");
        classifiedCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Character>("het_classification"));
        if (modifyStatus) {
            classifiedCol.setCellFactory(ComboBoxTableCell.forTableColumn(true, false));
            classifiedCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                @Override
                public void handle(TableColumn.CellEditEvent event) {
                    DataEntry entry = (DataEntry) event.getRowValue();
                    boolean classified = (boolean) event.getNewValue();
                    entry.setHetClassification(classified);
                    changedData.add(entry);
                    try {
                        DAO.updateEntryStatus(classified, entry.getId());
                    } catch (SQLException | ClassNotFoundException e) {
                        System.out.println("An error occurred when UPDATING gender: " + e);
                        e.printStackTrace();
                    }
                }
            });
        }
        table.getColumns().addAll(timeStampCol, geneCol, emailCol, sampleCol, controlCol, classifiedCol);
        return table;
    }

    private Optional<ButtonType> alertCommit(boolean commit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        String message = commit ? "Commit" : "Revert";
        alert.setTitle(message + " Changes");
        alert.setHeaderText("Are you sure you want to " + message.toLowerCase() + "?");
        return alert.showAndWait();
    }

    private void getDataChanges() {
        currentData = getUpdatedData();
        changedEntries = changedData.filtered(p -> !originalData.contains(p));
        unchangedEntries = FXCollections.observableArrayList();
        for (DataEntry entry : changedEntries) {
            DataEntry newEntry = new DataEntry(entry);
            newEntry.setHetClassification(!entry.getHetClassification());
            unchangedEntries.add(newEntry);
        }
    }

    private ObservableList<DataEntry> getUpdatedData() {
        try {
            return DAO.searchEntries();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while fetching the current data: " + e);
        }
        return null;
    }

    public void start(Stage stage) {
        try {
            originalData = DAO.searchEntries();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        structureMainTableView();
        structureSearchView();
        structureCompareView();

        borderPane.setPadding(new Insets(10, 10, 10, 10));

        stage.setScene(mainScene);
        stage.setMaximized(true);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                getDataChanges();
                if (changedEntries.size() != 0) {
                    structureCompareWindow();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
