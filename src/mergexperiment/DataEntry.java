package mergexperiment;

import javafx.beans.property.*;

import java.sql.Timestamp;

/**
 * Class representation of an entry in the database. Contains Property variables
 * for all database columns.
 */
public class DataEntry {

    private SimpleObjectProperty<Timestamp> timestamp;
    private StringProperty username;
    private IntegerProperty id;
    private StringProperty sample;
    private StringProperty control;
    private StringProperty windowId;
    private StringProperty gene;
    private DoubleProperty avgCnvRatio;
    private DoubleProperty avgBowtieBwaRatio;
    private DoubleProperty bbStd;
    private DoubleProperty cnvRatioStd;
    private DoubleProperty covStd;
    private DoubleProperty avgCov;
    private DoubleProperty avgDupRatio;
    private DoubleProperty gcPerc;
    private DoubleProperty alleleFreq;
    private IntegerProperty readStats;
    private BooleanProperty isTraining;
    private BooleanProperty hetClassification;

    public DataEntry() {
        this.timestamp = new SimpleObjectProperty<>();
        this.username = new SimpleStringProperty();
        this.id = new SimpleIntegerProperty();
        this.sample = new SimpleStringProperty();
        this.control = new SimpleStringProperty();
        this.windowId = new SimpleStringProperty();
        this.gene = new SimpleStringProperty();
        this.avgCnvRatio = new SimpleDoubleProperty();
        this.bbStd = new SimpleDoubleProperty();
        this.covStd = new SimpleDoubleProperty();
        this.avgDupRatio = new SimpleDoubleProperty();
        this.gcPerc = new SimpleDoubleProperty();
        this.alleleFreq = new SimpleDoubleProperty();
        this.readStats = new SimpleIntegerProperty();
        this.isTraining = new SimpleBooleanProperty();
        this.hetClassification = new SimpleBooleanProperty();
        this.avgBowtieBwaRatio = new SimpleDoubleProperty();
        this.cnvRatioStd = new SimpleDoubleProperty();
        this.avgCov = new SimpleDoubleProperty();
    }

    public DataEntry(DataEntry dataEntry) {
        this.timestamp = new SimpleObjectProperty<>(dataEntry.getTimestamp());
        this.username = new SimpleStringProperty(dataEntry.getUsername());
        this.id = new SimpleIntegerProperty(dataEntry.getId());
        this.sample = new SimpleStringProperty(dataEntry.getSample());
        this.control = new SimpleStringProperty(dataEntry.getControl());
        this.windowId = new SimpleStringProperty(dataEntry.getWindowId());
        this.gene = new SimpleStringProperty(dataEntry.getGene());
        this.avgCnvRatio = new SimpleDoubleProperty(dataEntry.getAvgCnvRatio());
        this.bbStd = new SimpleDoubleProperty(dataEntry.getBbStd());
        this.covStd = new SimpleDoubleProperty(dataEntry.getCovStd());
        this.avgDupRatio = new SimpleDoubleProperty(dataEntry.getAvgDupRatio());
        this.gcPerc = new SimpleDoubleProperty(dataEntry.getGcPerc());
        this.alleleFreq = new SimpleDoubleProperty(dataEntry.getAlleleFreq());
        this.readStats = new SimpleIntegerProperty(dataEntry.getReadStats());
        this.isTraining = new SimpleBooleanProperty(dataEntry.isTraining());
        this.hetClassification = new SimpleBooleanProperty(dataEntry.getHetClassification());
        this.avgBowtieBwaRatio = new SimpleDoubleProperty(dataEntry.getAvgBowtieBwaRatio());
        this.cnvRatioStd = new SimpleDoubleProperty(dataEntry.getCnvRatioStd());
        this.avgCov = new SimpleDoubleProperty(dataEntry.getAvgCov());
    }

    public Timestamp getTimestamp() {
        return timestamp.get();
    }

    public SimpleObjectProperty<Timestamp> timestampProperty() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp.set(timestamp);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getSample() {
        return sample.get();
    }

    public StringProperty sampleProperty() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample.set(sample);
    }

    public String getControl() {
        return control.get();
    }

    public StringProperty controlProperty() {
        return control;
    }

    public void setControl(String control) {
        this.control.set(control);
    }

    public String getWindowId() {
        return windowId.get();
    }

    public StringProperty windowIdProperty() {
        return windowId;
    }

    public void setWindowId(String windowId) {
        this.windowId.set(windowId);
    }

    public String getGene() {
        return gene.get();
    }

    public StringProperty geneProperty() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene.set(gene);
    }

    public double getAvgCnvRatio() {
        return avgCnvRatio.get();
    }

    public DoubleProperty avgCnvRatioProperty() {
        return avgCnvRatio;
    }

    public void setAvgCnvRatio(double avgCnvRatio) {
        this.avgCnvRatio.set(avgCnvRatio);
    }

    public double getAvgBowtieBwaRatio() {
        return avgBowtieBwaRatio.get();
    }

    public DoubleProperty avgBowtieBwaRatioProperty() {
        return avgBowtieBwaRatio;
    }

    public void setAvgBowtieBwaRatio(double avgBowtieBwaRatio) {
        this.avgBowtieBwaRatio.set(avgBowtieBwaRatio);
    }

    public double getBbStd() {
        return bbStd.get();
    }

    public DoubleProperty bbStdProperty() {
        return bbStd;
    }

    public void setBbStd(double bbStd) {
        this.bbStd.set(bbStd);
    }

    public double getCnvRatioStd() {
        return cnvRatioStd.get();
    }

    public DoubleProperty cnvRatioStdProperty() {
        return cnvRatioStd;
    }

    public void setCnvRatioStd(double cnvRatioStd) {
        this.cnvRatioStd.set(cnvRatioStd);
    }

    public double getCovStd() {
        return covStd.get();
    }

    public DoubleProperty covStdProperty() {
        return covStd;
    }

    public void setCovStd(double covStd) {
        this.covStd.set(covStd);
    }

    public double getAvgCov() {
        return avgCov.get();
    }

    public DoubleProperty avgCovProperty() {
        return avgCov;
    }

    public void setAvgCov(double avgCov) {
        this.avgCov.set(avgCov);
    }

    public double getAvgDupRatio() {
        return avgDupRatio.get();
    }

    public DoubleProperty avgDupRatioProperty() {
        return avgDupRatio;
    }

    public void setAvgDupRatio(double avgDupRatio) {
        this.avgDupRatio.set(avgDupRatio);
    }

    public double getGcPerc() {
        return gcPerc.get();
    }

    public DoubleProperty gcPercProperty() {
        return gcPerc;
    }

    public void setGcPerc(double gcPerc) {
        this.gcPerc.set(gcPerc);
    }

    public double getAlleleFreq() {
        return alleleFreq.get();
    }

    public DoubleProperty alleleFreqProperty() {
        return alleleFreq;
    }

    public void setAlleleFreq(double alleleFreq) {
        this.alleleFreq.set(alleleFreq);
    }

    public int getReadStats() {
        return readStats.get();
    }

    public IntegerProperty readStatsProperty() {
        return readStats;
    }

    public void setReadStats(int readStats) {
        this.readStats.set(readStats);
    }

    public boolean isTraining() {
        return isTraining.get();
    }

    public BooleanProperty isTrainingProperty() {
        return isTraining;
    }

    public void setIsTraining(boolean isTraining) {
        this.isTraining.set(isTraining);
    }

    public boolean getHetClassification() {
        return hetClassification.get();
    }

    public BooleanProperty hetClassificationProperty() {
        return hetClassification;
    }

    public void setHetClassification(boolean hetClassification) {
        this.hetClassification.set(hetClassification);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DataEntry)) {
            return false;
        }
        DataEntry other = (DataEntry) obj;
        return getId() == other.getId() && getHetClassification() == other.getHetClassification();
    }
}