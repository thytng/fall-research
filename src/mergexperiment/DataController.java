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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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

    private HBox paginationBox = new HBox();
    private Button goLeft = new Button("Prev 15");
    private Button goRight = new Button("Next 15");

    private HBox resizeBox = new HBox();
    private Button expandButton = new Button("Expand Table");
    private Button condenseButton = new Button("Condense Table");
    private final double condensedColSize = 25.0;
    private final double expandedColSize = 80.0;

    private ObservableList<DataEntry> unchangedEntries;
    private ObservableList<DataEntry> changedEntries;
    private TableView<DataEntry> unchangedTable = createEmptyTable(false);
    private TableView<DataEntry> changedTable = createEmptyTable(false);
    private Stage compareStage = new Stage();
    private GridPane comparePane = new GridPane();
    private BorderPane bottomBar = new BorderPane();
    private Scene compareScene = new Scene(comparePane);

    /**
     * Structure the display of the main window.
     */
    public void structureMainTableView() {
        mainTable = createEmptyTable(true);
        currentData = getUpdatedData();
        mainTable.setItems(currentData);
        mainTable.setEditable(true);

        resizeBox.getChildren().addAll(condenseButton, expandButton);
        resizeBox.setSpacing(10);

        condenseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<TableColumn<DataEntry, ?>> cols = mainTable.getColumns();
                for (int i = 0; i < cols.size(); i++) {
                    if (i > 7) {
                        cols.get(i).setPrefWidth(condensedColSize);
                    }
                }
            }
        });
        expandButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<TableColumn<DataEntry, ?>> cols = mainTable.getColumns();
                for (int i = 0; i < cols.size(); i++) {
                    if (i > 7) {
                        cols.get(i).setPrefWidth(expandedColSize);
                    }
                }
            }
        });

        paginationBox.getChildren().addAll(goLeft, goRight);
        paginationBox.setSpacing(10);

        borderPane.setCenter(mainTable);

        goLeft.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    ObservableList<DataEntry> items = DAO.loadLast();
                    if (items.size() > 0) {
                        mainTable.setItems(items);
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        goRight.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    ObservableList<DataEntry> items = DAO.loadNext();
                    if (items.size() > 0) {
                        mainTable.setItems(items);
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        bottomBar.setLeft(paginationBox);
        bottomBar.setRight(resizeBox);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        borderPane.setBottom(bottomBar);

    }

    /**
     * Structure the display and handling of events that enable the user to search for values in the table.
     */
    public void structureSearchView() {
        choiceBox.getItems().addAll("username", "id", "sample", "control", "gene");
        choiceBox.setValue("username");

        searchField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                try {
                    currentData = DAO.searchEntry(choiceBox.getValue(), searchField.getText());
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

    /**
     * Structure the compare window to allow the user to review any changes made to the table before reverting these
     * changes or committing them to the remote database.
     */
    public void structureCompareView() {
        comparePane.add(unchangedTable, 0, 0);
        comparePane.add(changedTable, 1, 0);

        comparePane.add(commitButton, 1, 1);
        GridPane.setHalignment(commitButton, HPos.CENTER);
        comparePane.add(revertButton, 0, 1);
        GridPane.setHalignment(revertButton, HPos.CENTER);

        comparePane.setVgap(10);
        comparePane.setHgap(5);
        comparePane.setPadding(new Insets(10, 0, 10, 0));

        compareButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                structureCompareTables();
                mainTable.setEditable(true);
                mainTable.setItems(currentData);
                mainTable.refresh();
            }
        });

        sideBar.add(compareButton, 0, 1);
    }

    /**
     * Populate two tables reflecting the changes made to the database, showing only entries that were modified.
     * The data in one table will contain the entries' original state and the data in the other will reflect the
     * newly made changes.
     */
    private void structureCompareTables() {
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
                    compareStage.hide();
                }
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
                    compareStage.hide();
                }
            }
        });

        compareStage.showAndWait();
    }

    /**
     * Instantiate an empty TaleView with columns and their variable types and hide secondary columns to limit the
     * amount of information immediately displayed.
     * @param allowModification Boolean value to indicate whether the user can modify the `het_classification` column.
     *                          If true, the cells for this column will be represented as a ComboBox for the user to
     *                          change the classification status of entries.
     * @return
     */
    private TableView<DataEntry> createEmptyTable(boolean allowModification) {
        TableView<DataEntry> table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn timestampCol = new TableColumn("timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Timestamp>("timestamp"));
        TableColumn usernameCol = new TableColumn("username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("username"));
        TableColumn idCol = new TableColumn("id");
        idCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Integer>("id"));
        TableColumn sampleCol = new TableColumn("sample");
        sampleCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("sample"));
        TableColumn controlCol = new TableColumn("control");
        controlCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("control"));
        TableColumn windowIdCol = new TableColumn("window_id");
        windowIdCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("windowId"));
        TableColumn geneCol = new TableColumn("gene");
        geneCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("gene"));
        TableColumn avgCnvRatioCol = new TableColumn("avg_cnv_ratio");
        avgCnvRatioCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Double>("avgCnvRatio"));
        TableColumn avgBowtieBwaRatioCol = new TableColumn("avg_bowtie_bwa_ratio");
        avgBowtieBwaRatioCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Double>("avgBowtieBwaRatio"));
        TableColumn bbStdCol = new TableColumn("bb_std");
        bbStdCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Double>("bbStd"));
        TableColumn cnvRatioStdCol = new TableColumn("cnv_ratio_std");
        cnvRatioStdCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Double>("cnvRatioStd"));
        TableColumn covStdCol = new TableColumn("cov_std_col");
        covStdCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Double>("covStd"));
        TableColumn avgCovCol = new TableColumn("avg_cov");
        avgCovCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Double>("avgCov"));
        TableColumn avgDupRatioCol = new TableColumn("avg_dup_ratio");
        avgDupRatioCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Double>("avgDupRatio"));
        TableColumn gcPercCol = new TableColumn("gc_perc");
        gcPercCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Double>("gcPerc"));
        TableColumn alleleFreqCol = new TableColumn("allele_freq");
        alleleFreqCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Double>("alleleFreq"));
        TableColumn readStatsCol = new TableColumn("read_stats");
        readStatsCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Integer>("readStats"));
        TableColumn isTrainingCol = new TableColumn("is_training");
        isTrainingCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Boolean>("isTraining"));
        TableColumn hetClassificationCol = new TableColumn("het_classification");
        hetClassificationCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Boolean>("hetClassification"));
        if (allowModification) {
            hetClassificationCol.setCellFactory(ComboBoxTableCell.forTableColumn(true, false));
            hetClassificationCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                @Override
                public void handle(TableColumn.CellEditEvent event) {
                    DataEntry entry = (DataEntry) event.getRowValue();
                    boolean status = (boolean) event.getNewValue();
                    if (changedData.contains(entry)) {
                        changedData.remove(entry);
                    }
                    entry.setHetClassification(status);
                    changedData.add(entry);

                    try {
                        DAO.updateHetClassification(entry.getId(), status);
                    } catch (SQLException | ClassNotFoundException e) {
                        System.out.println("An error occurred when UPDATING classification: " + e);
                        e.printStackTrace();
                    }
                }
            });
        }

        avgCnvRatioCol.setPrefWidth(condensedColSize);
        avgBowtieBwaRatioCol.setPrefWidth(condensedColSize);
        bbStdCol.setPrefWidth(condensedColSize);
        cnvRatioStdCol.setPrefWidth(condensedColSize);
        covStdCol.setPrefWidth(condensedColSize);
        avgCovCol.setPrefWidth(condensedColSize);
        avgDupRatioCol.setPrefWidth(condensedColSize);
        gcPercCol.setPrefWidth(condensedColSize);
        alleleFreqCol.setPrefWidth(condensedColSize);
        readStatsCol.setPrefWidth(condensedColSize);
        isTrainingCol.setPrefWidth(condensedColSize);

        table.getColumns().addAll(timestampCol, usernameCol, idCol, sampleCol, controlCol, windowIdCol, geneCol,
                hetClassificationCol, avgCnvRatioCol, avgBowtieBwaRatioCol, bbStdCol, cnvRatioStdCol, covStdCol,
                avgCovCol, avgDupRatioCol, gcPercCol, alleleFreqCol, readStatsCol, isTrainingCol);

        return table;
    }

    /**
     * Create an alert window to get the user's confirmation about reverting or committing.
     * @param commit
     * @return
     */
    private Optional<ButtonType> alertCommit(boolean commit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        String message = commit ? "Commit" : "Revert";
        alert.setTitle(message + " Changes");
        alert.setHeaderText("Are you sure you want to " + message.toLowerCase() + "?");
        return alert.showAndWait();
    }

    /**
     * Compare the changes made in the table with the data as originally loaded.
     * Create a new list of entries that were changed but the entries themselves will not reflect these changes
     * to display.
     */
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

    /**
     * Retrieve the data from the database with any temporary changes.
     * @return
     */
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
                    structureCompareTables();
                }
                try {
                    DBUtil.disconnect();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        });
    }

//    public static void main(String[] args) {
//        launch(args);
//    }
}
