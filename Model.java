import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;


/**
 * Created by
 * User: hansolo
 * Date: 22.08.12
 * Time: 16:06
 */
public class Model {
    private static final double ANGLE_RANGE = 220;
    private DoubleProperty              value;
    private DoubleProperty              minValue;
    private DoubleProperty              maxValue;
    private DoubleProperty              angleStep;
    private ObjectProperty<LinearScale> scale;
    private ObservableList<Section>     sections;
    private StringProperty              title;
    private StringProperty              label;
    private ChangeListener<Number>      handler;


    // ******************** Constructors **************************************
    public Model() {
        this(0, 100, 0, "", "");
    }

    public Model(final double MIN_VALUE, final double MAX_VALUE, final double VALUE, final String TITLE, final String LABEL) {
        minValue  = new SimpleDoubleProperty(MIN_VALUE);
        maxValue  = new SimpleDoubleProperty(MAX_VALUE);
        value     = new SimpleDoubleProperty(VALUE);
        angleStep = new SimpleDoubleProperty(ANGLE_RANGE / (getMaxValue() - getMinValue()));
        scale     = new SimpleObjectProperty<LinearScale>(new LinearScale(MIN_VALUE, MAX_VALUE));
        sections  = FXCollections.observableArrayList();
        title     = new SimpleStringProperty(TITLE);
        label     = new SimpleStringProperty(LABEL);
        handler   = new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                if (getScale().isTightScale()) {
                    getScale().calculateTight();
                } else {
                    getScale().calculateLoose();
                }
            }
        };
        init();
    }

    private void init() {
        minValue.addListener(handler);
        maxValue.addListener(handler);
    }


    // ******************** Methods *******************************************
    public double getValue() {
        return value.get();
    }

    public void setValue(double newValue) {
        newValue = newValue < minValue.get() ? minValue.get() : (newValue > maxValue.get() ? maxValue.get() : newValue);
        value.set(newValue);
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public double getMinValue() {
        return minValue.get();
    }

    public void setMinValue(double newMinValue) {
        newMinValue = newMinValue > maxValue.get() ? maxValue.get() - 1 : newMinValue;
        minValue.set(newMinValue);
        angleStep.set(ANGLE_RANGE / (getMaxValue() - newMinValue));
    }

    public ReadOnlyDoubleProperty minValueProperty() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue.get();
    }

    public void setMaxValue(double newMaxValue) {
        newMaxValue = newMaxValue < minValue.get() ? minValue.get() + 1 : newMaxValue;
        maxValue.set(newMaxValue);
        angleStep.set(ANGLE_RANGE / (newMaxValue - getMinValue()));
    }

    public ReadOnlyDoubleProperty maxValueProperty() {
        return maxValue;
    }

    public double getAngleStep() {
        return angleStep.get();
    }

    public double getAngleRange() {
        return ANGLE_RANGE;
    }

    public LinearScale getScale() {
        return scale.get();
    }

    public void setScale(final LinearScale LINEAR_SCALE) {
        scale.set(LINEAR_SCALE);
    }

    public ObjectProperty<LinearScale> scaleProperty() {
        return scale;
    }

    public boolean isNiceScaling() {
        return getScale().isNiceScaling();
    }

    public void setNiceScaling(final boolean NICE_SCALING) {
        getScale().setNiceScaling(NICE_SCALING);
    }

    public BooleanProperty niceScalingProperty() {
        return getScale().niceScalingProperty();
    }

    public boolean isTightScale() {
        return getScale().isTightScale();
    }

    public void setTightScale(final boolean TIGHT_SCALE) {
        getScale().setTightScale(TIGHT_SCALE);
    }

    public BooleanProperty tightScaleProperty() {
        return getScale().tightScaleProperty();
    }

    public ObservableList<Section> getSections() {
        return sections;
    }

    public void setSections(final Section... SECTION_ARRAY) {
        sections.clear();
        for (final Section SECTION : SECTION_ARRAY) {
            sections.add(new Section(SECTION.getStart(), SECTION.getStop(), SECTION.getColor(), SECTION.getText()));
        }
    }

    public void setSections(final List<Section> SECTIONS) {
        sections.clear();
        for (final Section SECTION : SECTIONS) {
            sections.add(new Section(SECTION.getStart(), SECTION.getStop(), SECTION.getColor(), SECTION.getText()));
        }
    }

    public void addSection(final Section SECTION) {
        sections.add(new Section(SECTION.getStart(), SECTION.getStop(), SECTION.getColor(), SECTION.getText()));
    }

    public void removeSection(final Section SECTION) {
        for (Section section : sections) {
            if (section.equals(SECTION)) {
                sections.remove(section);
                break;
            }
        }
    }

    public void resetSections() {
        sections.clear();
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(final String TITLE) {
        title.set(TITLE);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getLabel() {
        return label.get();
    }

    public void setLabel(final String LABEL) {
        label.set(LABEL);
    }

    public StringProperty labelProperty() {
        return label;
    }
}
