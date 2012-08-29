import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;


/**
 * Created by
 * User: hansolo
 * Date: 27.07.12
 * Time: 15:44
 */
public class Scale {
    private DoubleProperty  minValue;
    private DoubleProperty  maxValue;
    private DoubleProperty  range;
    private DoubleProperty  uncorrectedMinValue;
    private DoubleProperty  uncorrectedMaxValue;
    private BooleanProperty lastLabelVisible;


    // ******************** Constructors **************************************
    public Scale() {
        this(0, 100);
    }

    public Scale(final double MIN_VALUE, final double MAX_VALUE) {
        minValue            = new SimpleDoubleProperty(MIN_VALUE);
        maxValue            = new SimpleDoubleProperty(MAX_VALUE);
        range               = new SimpleDoubleProperty();
        range.bind(maxValue.subtract(minValue));
        uncorrectedMinValue = new SimpleDoubleProperty(MIN_VALUE);
        uncorrectedMaxValue = new SimpleDoubleProperty(MAX_VALUE);
        lastLabelVisible    = new SimpleBooleanProperty(true);
    }


    // ******************** Methods *******************************************
    public final double getMinValue() {
        return minValue.get();
    }

    public final void setMinValue(final double MIN_VALUE) {
        minValue.set(MIN_VALUE);
    }

    public final ReadOnlyDoubleProperty minValueProperty() {
        return minValue;
    }

    public final double getMaxValue() {
        return maxValue.get();
    }

    public final void setMaxValue(final double MAX_VALUE) {
        maxValue.set(MAX_VALUE);
    }

    public final ReadOnlyDoubleProperty maxValueProperty() {
        return maxValue;
    }

    public final double getRange() {
        return range.get();
    }

    public final ReadOnlyDoubleProperty rangeProperty() {
        return range;
    }

    public final double getUncorrectedMinValue() {
        return uncorrectedMinValue.get();
    }

    public final void setUncorrectedMinValue(final double UNCORRECTED_MIN_VALUE) {
        minValue.set(UNCORRECTED_MIN_VALUE);
        uncorrectedMinValue.set(UNCORRECTED_MIN_VALUE);
    }

    public final ReadOnlyDoubleProperty uncorrectedMinValueProperty() {
        return uncorrectedMinValue;
    }

    public final double getUncorrectedMaxValue() {
        return uncorrectedMaxValue.get();
    }

    public final void setUncorrectedMaxValue(final double UNCORRECTED_MAX_VALUE) {
        maxValue.set(UNCORRECTED_MAX_VALUE);
        uncorrectedMaxValue.set(UNCORRECTED_MAX_VALUE);
    }

    public final ReadOnlyDoubleProperty uncorrectedMaxValueProperty() {
        return uncorrectedMaxValue;
    }

    public final boolean isLastLabelVisible() {
        return lastLabelVisible.get();
    }

    public final void setLastLabelVisible(final boolean LAST_LABEL_VISIBLE) {
        lastLabelVisible.set(LAST_LABEL_VISIBLE);
    }

    public final BooleanProperty lastLabelVisibleProperty() {
        return lastLabelVisible;
    }
}
