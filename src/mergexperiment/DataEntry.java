package mergexperiment;

import javafx.beans.property.*;

import java.sql.Date;
import java.sql.Timestamp;

public class DataEntry {

    private SimpleObjectProperty<Timestamp> timestamp;
    private StringProperty username;
    private IntegerProperty id;
    private StringProperty sample;
    private StringProperty control;
    private StringProperty windowId;
    private StringProperty gene;
    private DoubleProperty avgCnvRatio;
    private DoubleProperty bbStd;
    private DoubleProperty cnvRatio;
    private DoubleProperty covStd;
    private DoubleProperty avgDupRatio;
    private DoubleProperty gcPerc;
    private DoubleProperty alleleFreq;
    private IntegerProperty readStats;
    private BooleanProperty isTraining;
    private BooleanProperty hetClassification;
    private DoubleProperty avgBowtieBwaRatio;
    private DoubleProperty cnvRatioStd;
    private DoubleProperty avgCov;

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
        this.cnvRatio = new SimpleDoubleProperty();
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
        this.cnvRatio = new SimpleDoubleProperty(dataEntry.getCnvRatio());
        this.covStd = new SimpleDoubleProperty(dataEntry.getCovStd());
        this.avgDupRatio = new SimpleDoubleProperty(dataEntry.getAvgDupRatio());
        this.gcPerc = new SimpleDoubleProperty(dataEntry.getGcPerc());
        this.alleleFreq = new SimpleDoubleProperty(dataEntry.getAlleleFreq());
        this.readStats = new SimpleIntegerProperty(dataEntry.getReadStats());
        this.isTraining = new SimpleBooleanProperty(dataEntry.getIsTraining());
        this.hetClassification = new SimpleBooleanProperty(dataEntry.getHetClassification());
        this.avgBowtieBwaRatio = new SimpleDoubleProperty(dataEntry.getAvgBowtieBwaRatio());
        this.cnvRatioStd = new SimpleDoubleProperty(dataEntry.getCnvRatioStd());
        this.avgCov = new SimpleDoubleProperty(dataEntry.getAvgCov());
    }

    public void setAvgCov(double cov) { avgCov.set(cov); }

    public double getAvgCov() { return avgCov.get(); }

    public void setCnvRatioStd(double cnv) { cnvRatioStd.set(cnv); }

    public double getCnvRatioStd()  { return cnvRatioStd.get(); }

    public void setHetClassification(boolean classification) { hetClassification.set(classification); }

    public boolean getIsTraining() {
        return isTraining.get();
    }

    public boolean getHetClassification() {
        return hetClassification.get();
    }

    public String getControl() {
        return control.get();
    }

    public String getWindowId() {
        return windowId.get();
    }

    public StringProperty windowIdProperty() {
        return windowId;
    }

    public String getGene() {
        return gene.get();
    }

    public StringProperty geneProperty() {
        return gene;
    }

    public double getAvgCnvRatio() {
        return avgCnvRatio.get();
    }

    public DoubleProperty avgCnvRatioProperty() {
        return avgCnvRatio;
    }

    public double getBbStd() {
        return bbStd.get();
    }

    public DoubleProperty bbStdProperty() {
        return bbStd;
    }

    public double getCnvRatio() {
        return cnvRatio.get();
    }

    public DoubleProperty cnvRatioProperty() {
        return cnvRatio;
    }

    public double getCovStd() {
        return covStd.get();
    }

    public DoubleProperty covStdProperty() {
        return covStd;
    }

    public double getAvgDupRatio() {
        return avgDupRatio.get();
    }

    public DoubleProperty avgDupRatioProperty() {
        return avgDupRatio;
    }

    public double getGcPerc() {
        return gcPerc.get();
    }

    public DoubleProperty gcPercProperty() {
        return gcPerc;
    }

    public double getAlleleFreq() {
        return alleleFreq.get();
    }

    public DoubleProperty alleleFreqProperty() {
        return alleleFreq;
    }

    public int getReadStats() {
        return readStats.get();
    }

    public void setReadStats(int readStatsInt) { readStats.set(readStatsInt); }

    public void setAvgBowtieBwaRatio(double bowtie) { avgBowtieBwaRatio.set(bowtie); }

    public double getAvgBowtieBwaRatio() { return avgBowtieBwaRatio.get(); }

    public IntegerProperty readStatsProperty() {
        return readStats;
    }

    public boolean isIsTraining() {
        return isTraining.get();
    }

    public BooleanProperty isTrainingProperty() {
        return isTraining;
    }

    public boolean isHetClassification() {
        return hetClassification.get();
    }

    public BooleanProperty hetClassificationProperty() {
        return hetClassification;
    }

    public StringProperty controlProperty() {
        return control;
    }

    public String getSample() {
        return sample.get();
    }

    public StringProperty sampleProperty() {
        return sample;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public Timestamp getTimestamp() {
        return timestamp.get();
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

    public void setId(int id) {
        this.id.set(id);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setAlleleFreq(double alleleFreq) {
        this.alleleFreq.set(alleleFreq);
    }

    public void setAvgCnvRatio(double avgCnvRatio) {
        this.avgCnvRatio.set(avgCnvRatio);
    }

    public void setControl(String control) {
        this.control.set(control);
    }

    public void setAvgDupRatio(double avgDupRatio) {
        this.avgDupRatio.set(avgDupRatio);
    }

    public void setBbStd(double bbStd) {
        this.bbStd.set(bbStd);
    }

    public SimpleObjectProperty<Timestamp> timestampProperty() {
        return timestamp;
    }

    public void setCnvRatio(double cnvRatio) {
        this.cnvRatio.set(cnvRatio);

    }

    public void setSample(String sample) {
        this.sample.set(sample);
    }

    public void setCovStd(double covStd) {
        this.covStd.set(covStd);
    }

    public void setWindowId(String windowId) {
        this.windowId.set(windowId);
    }

    public void setGcPerc(double gcPerc) {

        this.gcPerc.set(gcPerc);
    }

    public void setGene(String gene) {
        this.gene.set(gene);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DataEntry)) {
            return false;
        }
        DataEntry other = (DataEntry) obj;
        return getId() == ((DataEntry) obj).getId();
    }
}
