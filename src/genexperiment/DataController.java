package genexperiment;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.SQLException;
import java.sql.Timestamp;

public class DataController extends Application {
    private BorderPane borderPane = new BorderPane();

    private PaginatingDataHandler handler;
    private Pagination pagination;

    private final int ENTRIES_PER_PAGE = 15;

    private TableView<DataEntry> table = initTable();

    @Override
    public void start(final Stage stage) throws Exception {

        handler = new PaginatingDataHandler("genes"); //TODO:

        Pagination pagination = new Pagination((handler.getNumPages()), 0);
        pagination.setPageFactory(this::createPage);

        borderPane.setCenter(pagination);
        borderPane.setPadding(new Insets(0, 10, 10, 0));

        Scene scene = new Scene(borderPane);

        stage.setScene(scene);
        stage.show();
    }

    public TableView<DataEntry> initTable() {

        TableView<DataEntry> table = new TableView<>();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        VBox vBox = new VBox(table);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));

        TableColumn idCol = new TableColumn("id");
        idCol.setCellValueFactory(new PropertyValueFactory<DataEntry, Integer>("id"));
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
                    DAO.updateEntryStatus(entry.getGene(), entry.getEmail(), classified, entry.getId());
                } catch (SQLException | ClassNotFoundException e) {
                    System.out.println("An error occurred when UPDATING CLASSIFIED: " + e);
                    e.printStackTrace();
                }
            }
        });

        table.getColumns().addAll(timeStampCol, geneCol, emailCol, sampleCol, controlCol, classifiedCol, idCol);

        return table;
    }

    private Node createPage(int pageIndex) {

        System.out.println("Button hit");
        System.out.println(pageIndex);

        ObservableList<DataEntry> data = handler.loadPage(pageIndex);
        table.setItems(data);
        return new BorderPane(table);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
