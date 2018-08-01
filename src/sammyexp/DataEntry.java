package sammyexp;

import javafx.beans.property.*;

import java.sql.Timestamp;

public class DataEntry {
    private SimpleObjectProperty<Timestamp> timestamp;
    private StringProperty gene;
    private StringProperty email;
    private StringProperty sample;
    private StringProperty control;
    private BooleanProperty classified;

    public DataEntry() {
        this.timestamp = new SimpleObjectProperty<>();
        this.gene = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.sample = new SimpleStringProperty();
        this.control = new SimpleStringProperty();
        this.classified = new SimpleBooleanProperty();
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

    public String getGene() {
        return gene.get();
    }

    public StringProperty geneProperty() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene.set(gene);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
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

    public boolean isClassified() {
        return classified.get();
    }

    public BooleanProperty classifiedProperty() {
        return classified;
    }

    public void setClassified(boolean classified) {
        this.classified.set(classified);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || ! (obj instanceof DataEntry)) {
            return false;
        }
        DataEntry other = (DataEntry) obj;
        return getTimestamp().equals(other.getTimestamp()) && getGene().equals(other.getGene()) &&
                getEmail().equals(other.getEmail()) && getControl().equals(other.getControl()) &&
                getSample().equals(other.getSample()) && (isClassified() == other.isClassified());
    }
}
