package gene;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
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

import java.sql.SQLException;
import java.sql.Timestamp;

public class DataController extends Application {
    private BorderPane borderPane = new BorderPane();

    private ObservableList<DataEntry> originalData;
    private ObservableList<DataEntry> currentData;
    private ObservableList<DataEntry> searchData;
    private FilteredList<DataEntry> filteredData;

    private Scene mainScene = new Scene(borderPane);
    private TableView<DataEntry> mainTable = new TableView<>();

    private ChoiceBox<String> choiceBox = new ChoiceBox<>();
    private TextField searchField = new TextField();
    private GridPane sideBar = new GridPane();

    private Button compareButton;
    private Button commitButton;

    private FilteredList<DataEntry> unchangedEntries;
    private FilteredList<DataEntry> changedEntries;
    private TableView<DataEntry> unchangedTable = createEmptyTable(false);
    private TableView<DataEntry> changedTable = createEmptyTable(false);
    private Stage compareStage = new Stage();
    private BorderPane comparePane = new BorderPane();
    private Scene compareScene = new Scene(comparePane);

    public void structureMainTableView() {
        mainTable = createEmptyTable(true);
        currentData = getUpdatedData();
//            filteredData = new FilteredList<DataEntry>(currentData, p -> true);
        mainTable.setItems(currentData);
        mainTable.setEditable(true);
        mainTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        borderPane.setCenter(mainTable);
    }

    public void structureSearchView() {
        choiceBox.getItems().addAll("Email", "Gene");
        choiceBox.setValue("Email");

        searchField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String field = choiceBox.getValue().equals("Email") ? "email" : "gene";
                try {
                    searchData = DAO.searchEntry(field, searchField.getText().toLowerCase());
                    mainTable.setItems(searchData);
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

    public void structureSearchViewFL() {
        choiceBox.getItems().addAll("Email", "Gene");
        choiceBox.setValue("Email");

        searchField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (choiceBox.getValue()) {
                    case "Email":
                        filteredData.setPredicate(p -> p.getEmail().toLowerCase().contains(searchField.getText().toLowerCase().trim()));
                        break;
                    case "Gene":
                        filteredData.setPredicate(p -> p.getGene().toLowerCase().contains(searchField.getText().toLowerCase().trim()));
                        break;
                }
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
        {
            if (newVal != null) {
                searchField.clear();
                filteredData.setPredicate(p -> true);
            }
        });

        sideBar.add(choiceBox, 0, 0);
        sideBar.add(searchField, 1, 0);
        sideBar.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setLeft(sideBar);
    }

    public void structureCompareView() {
        compareButton = new Button("See Changes");
        compareButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentData = getUpdatedData();

                // DATA POINTS THAT WERE CHANGED (ORIGINAL VERSION)
                unchangedEntries = originalData.filtered(p -> !currentData.contains(p));
                unchangedTable.setItems(unchangedEntries);

                // DATA POINTS THAT WERE CHANGED (MODIFIED VERSION)
                changedEntries = currentData.filtered(p -> !originalData.contains(p));
                changedTable.setItems(changedEntries);

                comparePane.setLeft(unchangedTable);
                comparePane.setRight(changedTable);
                compareStage.setScene(compareScene);
                mainTable.setEditable(false);
                compareStage.showAndWait();
                mainTable.setEditable(true);
            }
        });

        sideBar.add(compareButton, 0, 1);
        currentData = getUpdatedData();
    }

    public void structureCommitView() {
        commitButton = new Button("Commit");
        commitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DBUtil.commitChanges();
                originalData = getUpdatedData();
            }
        });
        sideBar.add(commitButton, 0, 2);
    }

    private TableView<DataEntry> createEmptyTable(boolean modifyStatus) {
        TableView<DataEntry> table = new TableView<>();
        TableColumn timeStampCol = new TableColumn("Timestamp");
        timeStampCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Timestamp>("timestamp"));
        TableColumn geneCol = new TableColumn("Gene");
        geneCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("gene"));
        TableColumn emailCol = new TableColumn("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("email"));
        TableColumn sampleCol = new TableColumn("Sample");
        sampleCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("sample"));
        TableColumn controlCol = new TableColumn("Control");
        controlCol.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("control"));

        TableColumn classifiedCol = new TableColumn("Classified");
        classifiedCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Boolean>("classified"));
        if (modifyStatus) {
            classifiedCol.setCellFactory(ComboBoxTableCell.forTableColumn(true, false));
            classifiedCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                @Override
                public void handle(TableColumn.CellEditEvent event) {
                    DataEntry entry = (DataEntry) event.getRowValue();
                    Boolean classified = (Boolean) event.getNewValue();
                    try {
                        DAO.updateEntryStatus(entry.getGene(), entry.getEmail(), classified);
                    } catch (SQLException | ClassNotFoundException e) {
                        System.out.println("An error occurred when UPDATING CLASSIFIED: " + e);
                        e.printStackTrace();
                    }
                }
            });
        }
        table.getColumns().addAll(timeStampCol, geneCol, emailCol, sampleCol, controlCol, classifiedCol);
        return table;
    }

    private ObservableList<DataEntry> getUpdatedData() {
        try {
            return DAO.searchEntries();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while updating mainTable currentData: " + e);
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
        structureCommitView();

        borderPane.setPadding(new Insets(10, 10, 10, 10));

        stage.setScene(mainScene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
