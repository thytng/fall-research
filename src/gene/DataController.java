package gene;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Timestamp;

public class DataController extends Application {
    private BorderPane borderPane = new BorderPane();

    private ObservableList<DataEntry> data;
    private ObservableList<DataEntry> searchData;
    private TableView<DataEntry> table = new TableView<>();
    private VBox vBox = new VBox(table);

    private FilteredList<DataEntry> filteredData;
    ChoiceBox<String> choiceBox = new ChoiceBox<>();
    TextField textField = new TextField();
    GridPane gridPane = new GridPane();


    public void structureTableView() {
        initTable();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));

        borderPane.setCenter(vBox);
    }

    public void structureSearchViewSQL() {
        choiceBox.getItems().addAll("Email", "Gene");
        choiceBox.setValue("Email");

        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String field = choiceBox.getValue() == "Email" ? "email" : "gene";
                try {
                    searchData = DAO.searchEntry(field, textField.getText().toLowerCase());
                    table.setItems(searchData);
                } catch (SQLException | ClassNotFoundException e) {

                }
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
        {
            if (newVal != null) {
                updateData();
                table.setItems(data);
                textField.clear();
            }
        });

        gridPane.add(choiceBox, 0, 0);
        gridPane.add(textField, 1, 0);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setLeft(gridPane);
    }

    public void structureSearchView() {
        choiceBox.getItems().addAll("Email", "Gene");
        choiceBox.setValue("Email");

        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (choiceBox.getValue()) {
                    case "Email":
                        filteredData.setPredicate(p -> p.getEmail().toLowerCase().contains(textField.getText().toLowerCase().trim()));
                        break;
                    case "Gene":
                        filteredData.setPredicate(p -> p.getGene().toLowerCase().contains(textField.getText().toLowerCase().trim()));
                        break;
                }
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
        {
            if (newVal != null) {
                textField.clear();
                filteredData.setPredicate(p -> true);
            }
        });

        gridPane.add(choiceBox, 0, 0);
        gridPane.add(textField, 1, 0);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setLeft(gridPane);
    }

    public void initTable() {
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

        table.getColumns().addAll(timeStampCol, geneCol, emailCol, sampleCol, controlCol, classifiedCol);
        updateData();
//            filteredData = new FilteredList<DataEntry>(data, p -> true);
        table.setItems(data);
    }

    private void updateData() {
        try {
            data = DAO.searchEntries();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while updating table data: " + e);
        }
    }


    public ObservableList<DataEntry> getData() {
        return data;
    }

    public TableView<DataEntry> getTable() {
        return table;
    }

    public void start(Stage stage) {
        structureTableView();
        structureSearchViewSQL();

        borderPane.setPadding(new Insets(0, 10, 10, 0));

        Scene scene = new Scene(borderPane);

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
