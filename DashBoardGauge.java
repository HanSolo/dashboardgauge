import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;

import java.util.List;


public class DashBoardGauge extends Control {
    private static final String         DEFAULT_STYLE_CLASS = "dashboard-gauge";
    public static enum                  PointerType {
        TYPE1,
        TYPE2
    }
    private ObjectProperty<Model>       model;
    private BooleanProperty             valueAnimationEnabled;
    private BooleanProperty             titleVisible;
    private BooleanProperty             labelVisible;
    private BooleanProperty             tooltipVisible;
    private ObjectProperty<Color>       pointerColor;
    private ObjectProperty<PointerType> pointerType;


    // ******************** Constructors **************************************
    public DashBoardGauge() {
        this(new Model());
    }

    public DashBoardGauge(final Model MODEL) {
        model                 = new SimpleObjectProperty<Model>(MODEL);
        valueAnimationEnabled = new SimpleBooleanProperty(false);
        titleVisible          = new SimpleBooleanProperty(true);
        labelVisible          = new SimpleBooleanProperty(true);
        tooltipVisible        = new SimpleBooleanProperty(true);
        pointerColor          = new SimpleObjectProperty<Color>(Color.color(0.1921568627, 0.2235294118, 0.2627450980, 1));
        pointerType           = new SimpleObjectProperty<PointerType>(PointerType.TYPE1);
        init();
    }

    private void init() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }


    // ******************** Methods *******************************************
    public Model getModel() {
        return model.get();
    }

    public void setModel(final Model MODEL) {
        model.set(MODEL);
    }

    public ReadOnlyObjectProperty<Model> modelProperty() {
        return model;
    }

    public double getValue() {
        return getModel().getValue();
    }

    public void setValue(double value) {
        getModel().setValue(value);
    }

    public DoubleProperty valueProperty() {
        return getModel().valueProperty();
    }

    public double getMinValue() {
        return getModel().getMinValue();
    }

    public void setMinValue(double minValue) {
        getModel().setMinValue(minValue);
    }

    public ReadOnlyDoubleProperty minValueProperty() {
        return getModel().minValueProperty();
    }

    public double getMaxValue() {
        return getModel().getMaxValue();
    }

    public void setMaxValue(double maxValue) {
        getModel().setMaxValue(maxValue);
    }

    public ReadOnlyDoubleProperty maxValueProperty() {
        return getModel().maxValueProperty();
    }

    public double getAngleStep() {
        return getModel().getAngleStep();
    }

    public double getAngleRange() {
        return getModel().getAngleRange();
    }

    public LinearScale getScale() {
        return getModel().getScale();
    }

    public void setScale(final LinearScale LINEAR_SCALE) {
        getModel().setScale(LINEAR_SCALE);
    }

    public ObjectProperty<LinearScale> scaleProperty() {
        return getModel().scaleProperty();
    }

    public boolean isNiceScaling() {
            return getModel().isNiceScaling();
        }

    public void setNiceScaling(final boolean NICE_SCALING) {
        getModel().setNiceScaling(NICE_SCALING);
    }

    public BooleanProperty niceScalingProperty() {
        return getModel().niceScalingProperty();
    }

    public boolean isTightScale() {
        return getModel().isTightScale();
    }

    public void setTightScale(final boolean TIGHT_SCALE) {
        getModel().setTightScale(TIGHT_SCALE);
    }

    public BooleanProperty tightScaleProperty() {
        return getModel().tightScaleProperty();
    }

    public final ObservableList<Section> getSections() {
        return getModel().getSections();
    }

    public final void setSections(final Section... SECTION_ARRAY) {
        getModel().setSections(SECTION_ARRAY);
    }

    public final void setSections(final List<Section> SECTIONS) {
        getModel().setSections(SECTIONS);
    }

    public final void addSection(final Section SECTION) {
        getModel().addSection(SECTION);
    }

    public final void removeSection(final Section SECTION) {
        getModel().removeSection(SECTION);
    }

    public final void resetSections() {
        getModel().resetSections();
    }

    public final String getTitle() {
        return getModel().getTitle();
    }

    public final void setTitle(final String TITLE) {
        getModel().setTitle(TITLE);
    }

    public final StringProperty titleProperty() {
        return getModel().titleProperty();
    }

    public final boolean isTitleVisible() {
        return titleVisible.get();
    }

    public final void setTitleVisible(final boolean TITLE_VISIBLE) {
        titleVisible.set(TITLE_VISIBLE);
    }

    public final BooleanProperty titleVisibleProperty() {
        return titleVisible;
    }

    public final String getLabel() {
        return getModel().getLabel();
    }

    public final void setLabel(final String LABEL) {
        getModel().setLabel(LABEL);
    }

    public final StringProperty labelProperty() {
        return getModel().labelProperty();
    }

    public final boolean isLabelVisible() {
        return labelVisible.get();
    }

    public final void setLabelVisible(final boolean LABEL_VISIBLE) {
        labelVisible.set(LABEL_VISIBLE);
    }

    public final BooleanProperty labelVisibleProperty() {
        return labelVisible;
    }

    public final boolean isValueAnimationEnabled() {
        return valueAnimationEnabled.get();
    }

    public final void setValueAnimationEnabled(final boolean VALUE_ANIMATION_ENABLED) {
        valueAnimationEnabled.set(VALUE_ANIMATION_ENABLED);
    }

    public final ReadOnlyBooleanProperty valueAnimationEnabledProperty() {
        return valueAnimationEnabled;
    }

    public final Color getPointerColor() {
        return pointerColor.get();
    }

    public final void setPointerColor(final Color POINTER_COLOR) {
        pointerColor.set(POINTER_COLOR);
    }

    public final ObjectProperty<Color> pointerColorProperty() {
        return pointerColor;
    }

    public final PointerType getPointerType() {
        return pointerType.get();
    }

    public final void setPointerType(final PointerType POINTER_TYPE) {
        pointerType.set(POINTER_TYPE);
    }

    public final ObjectProperty<PointerType> pointerTypeProperty() {
        return pointerType;
    }

    public final boolean isTooltipVisible() {
        return tooltipVisible.get();
    }

    public final void setTooltipVisible(final boolean TOOLTIP_VISIBLE) {
        tooltipVisible.set(TOOLTIP_VISIBLE);
    }

    public final BooleanProperty tooltipVisibleProperty() {
        return tooltipVisible;
    }

    @Override public void setPrefSize(final double WIDTH, final double HEIGHT) {
        double prefHeight = WIDTH < (HEIGHT * 0.995) ? (WIDTH * 1.0050251256281406) : HEIGHT;
        double prefWidth = prefHeight * 0.995;
        super.setPrefSize(prefWidth, prefHeight);
    }


    // ******************** Style related *************************************
    @Override protected String getUserAgentStylesheet() {
        return getClass().getResource(getClass().getSimpleName().toLowerCase() + ".css").toExternalForm();
    }
}

