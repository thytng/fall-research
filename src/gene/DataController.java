package gene;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
import java.util.ArrayList;

public class DataController extends Application {
    private BorderPane borderPane = new BorderPane();

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

//    private FilteredList<DataEntry> unchangedEntries;
//    private FilteredList<DataEntry> changedEntries;
    private TableView<DataEntry> unchangedTable = createEmptyTable(false);
    private TableView<DataEntry> changedTable = createEmptyTable(false);
    private Stage compareStage = new Stage();
    private BorderPane comparePane = new BorderPane();
    private Scene compareScene = new Scene(comparePane);

    private ArrayList<DataEntry> updatedEntries;

    private PaginatingDataHandler mainHandler;
    private PaginatingDataHandler searchHandler;

    public void structureMainTableView() {
        try {
            mainHandler = new PaginatingDataHandler("genes");
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        mainTable = createEmptyTable(true);

        mainTable.setEditable(true);
        mainTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        Pagination pagination = new Pagination((mainHandler.getNumPages()), 0);
        pagination.setPageFactory(this::createPage);

        borderPane.setCenter(pagination);
    }

    private Node createPage(int pageIndex) {

        System.out.println("Button hit");
        System.out.println(pageIndex);

        ObservableList<DataEntry> data = mainHandler.loadPage(pageIndex);
        mainTable.setItems(data);
        return new BorderPane(mainTable);
    }

    private Node createSearchPage(int pageIndex) {

        System.out.println("Button hit");
        System.out.println(pageIndex);

        ObservableList<DataEntry> data = searchHandler.loadPage(pageIndex);
        mainTable.setItems(data);
        return new BorderPane(mainTable);
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

                // DATA POINTS THAT WERE CHANGED (ORIGINAL VERSION)
                ArrayList<DataEntry> listData = new ArrayList<>(mainHandler.getAllData().values());

                ObservableList<DataEntry> allData = FXCollections.observableArrayList(listData);

                FilteredList<DataEntry> changedEntries = allData.filtered(p -> p.isModified());
                changedTable.setItems(changedEntries);

                // DATA POINTS THAT WERE CHANGED (MODIFIED VERSION)
                ObservableList<DataEntry> unchangedEntries = FXCollections.observableArrayList();
                for (DataEntry entry : changedEntries) {
                    unchangedEntries.add(entry.getOg());
                }
                unchangedTable.setItems(unchangedEntries);

                comparePane.setLeft(unchangedTable);
                comparePane.setRight(changedTable);
                compareStage.setScene(compareScene);
                mainTable.setEditable(false);
                compareStage.showAndWait();
                mainTable.setEditable(true);
            }
        });

        sideBar.add(compareButton, 0, 1);
        currentData = FXCollections.observableArrayList(mainHandler.getAllData().values());
    }

    public void structureCommitView() {
        commitButton = new Button("Commit");
        commitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DBUtil.commitChanges();
                mainHandler.reinitialize();
//                originalData = getUpdatedData();
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
        TableColumn idCol = new TableColumn("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Integer>("id"));

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
                        if (!entry.isModified()) {
                            System.out.println("Not modified");
                            entry.setOriginalClass(entry.getOriginalClass());
                            entry.switchModified();
                            entry.addOgStats();
                        }

                        DAO.updateEntryStatus(entry.getId(), classified, mainHandler);

                        System.out.println(entry.toString());
                        System.out.println(entry.getOg().toString());
                    } catch (SQLException | ClassNotFoundException e) {
                        System.out.println("An error occurred when UPDATING CLASSIFIED: " + e);
                        e.printStackTrace();
                    }
                }
            });
        }
        table.getColumns().addAll(timeStampCol, geneCol, emailCol, sampleCol, controlCol, classifiedCol, idCol);
        return table;
    }

    private ObservableList<DataEntry> getUpdatedData() {
        return FXCollections.observableArrayList(mainHandler.getAllData().values());
    }

    public void start(Stage stage) {
//        try {
//            originalData = DAO.searchEntries();
//        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }

        updatedEntries = new ArrayList<>();
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
