package gene;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Timestamp;

public class DataController extends Application {
    private ObservableList<DataEntry> data;
    private TableView<DataEntry> table = new TableView<>();
    private BorderPane borderPane = new BorderPane();
    private VBox vBox = new VBox(table);

    public void structureTableView() {
        initTable();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));

        borderPane.setCenter(vBox);
        borderPane.setPadding(new Insets(0, 10, 10, 0));
    }

    public void initTable() {
        try {
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
            data = DAO.searchEntries();
            table.setItems(data);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
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

        Scene scene = new Scene(borderPane);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
