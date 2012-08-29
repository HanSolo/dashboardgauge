import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ControlBuilder;
import javafx.scene.paint.Color;
import javafx.util.Builder;

import java.util.HashMap;
import java.util.List;


public class DashBoardGaugeBuilder<B extends DashBoardGaugeBuilder<B>> extends ControlBuilder<B> implements Builder<DashBoardGauge> {
    private HashMap<String, Property> properties = new HashMap<String, Property>();


    // ******************** Constructors **************************************
    protected DashBoardGaugeBuilder() {}


    // ******************** Methods *******************************************
    public static final DashBoardGaugeBuilder create() {
        return new DashBoardGaugeBuilder();
    }

    public final DashBoardGaugeBuilder model(final Model MODEL) {
        properties.put("MODEL", new SimpleObjectProperty<Model>(MODEL));
        return this;
    }

    public final DashBoardGaugeBuilder minValue(final double MIN_VALUE) {
        properties.put("MIN_VALUE", new SimpleDoubleProperty(MIN_VALUE));
        return this;
    }

    public final DashBoardGaugeBuilder maxValue(final double MAX_VALUE) {
        properties.put("MAX_VALUE", new SimpleDoubleProperty(MAX_VALUE));
        return this;
    }

    public final DashBoardGaugeBuilder value(final double VALUE) {
        properties.put("VALUE", new SimpleDoubleProperty(VALUE));
        return this;
    }

    public final DashBoardGaugeBuilder title(final String TITLE) {
        properties.put("TITLE", new SimpleStringProperty(TITLE));
        return this;
    }

    public final DashBoardGaugeBuilder titleVisible(final boolean TITLE_VISIBLE) {
        properties.put("TITLE_VISIBLE", new SimpleBooleanProperty(TITLE_VISIBLE));
        return this;
    }

    public final DashBoardGaugeBuilder label(final String LABEL) {
        properties.put("LABEL", new SimpleStringProperty(LABEL));
        return this;
    }

    public final DashBoardGaugeBuilder labelVisible(final boolean LABEL_VISIBLE) {
        properties.put("LABEL_VISIBLE", new SimpleBooleanProperty(LABEL_VISIBLE));
        return this;
    }

    public final DashBoardGaugeBuilder sections(final Section[] SECTIONS) {
        properties.put("SECTIONS_ARRAY", new SimpleObjectProperty<Section[]>(SECTIONS));
        return this;
    }

    public final DashBoardGaugeBuilder sections(final List<Section> SECTIONS) {
        properties.put("SECTIONS_LIST", new SimpleObjectProperty<List<Section>>(SECTIONS));
        return this;
    }

    public final DashBoardGaugeBuilder valueAnimationEnabled(final boolean VALUE_ANIMATION_ENABLED) {
        properties.put("VALUE_ANIMATION_ENABLED", new SimpleBooleanProperty(VALUE_ANIMATION_ENABLED));
        return this;
    }

    public final DashBoardGaugeBuilder pointerColor(final Color POINTER_COLOR) {
        properties.put("POINTER_COLOR", new SimpleObjectProperty<Color>(POINTER_COLOR));
        return this;
    }

    public final DashBoardGaugeBuilder pointerType(final DashBoardGauge.PointerType POINTER_TYPE) {
        properties.put("POINTER_TYPE", new SimpleObjectProperty<DashBoardGauge.PointerType>(POINTER_TYPE));
        return this;
    }

    public final DashBoardGaugeBuilder tooltipVisible(final boolean TOOLTIP_VISIBLE) {
        properties.put("TOOLTIP_VISIBLE", new SimpleBooleanProperty(TOOLTIP_VISIBLE));
        return this;
    }

    @Override public final B prefWidth(final double PREF_WIDTH) {
        properties.put("PREF_WIDTH", new SimpleDoubleProperty(PREF_WIDTH));
        return (B)this;
    }

    @Override public final B prefHeight(final double PREF_HEIGHT) {
        properties.put("PREF_HEIGHT", new SimpleDoubleProperty(PREF_HEIGHT));
        return (B)this;
    }

    @Override public final B layoutX(final double LAYOUT_X) {
        properties.put("LAYOUT_X", new SimpleDoubleProperty(LAYOUT_X));
        return (B)this;
    }

    @Override public final B layoutY(final double LAYOUT_Y) {
        properties.put("LAYOUT_Y", new SimpleDoubleProperty(LAYOUT_Y));
        return (B)this;
    }

    @Override public final DashBoardGauge build() {
        final DashBoardGauge CONTROL = new DashBoardGauge();

        if (properties.containsKey("MODEL")) {
            CONTROL.setModel(((ObjectProperty<Model>) properties.get("MODEL")).get());
        }

        if (properties.containsKey("PREF_WIDTH") && properties.containsKey("PREF_HEIGHT")) {
            CONTROL.setPrefSize(((DoubleProperty) properties.get("PREF_WIDTH")).get(), ((DoubleProperty) properties.get("PREF_HEIGHT")).get());
        }

        for (String key : properties.keySet()) {
            if ("MIN_VALUE".equals(key)) {
                CONTROL.setMinValue(((DoubleProperty) properties.get(key)).get());
            } else if("MAX_VALUE".equals(key)) {
                CONTROL.setMaxValue(((DoubleProperty) properties.get(key)).get());
            } else if ("VALUE".equals(key)) {
                CONTROL.setValue(((DoubleProperty) properties.get(key)).get());
            } else if ("TITLE".equals(key)) {
                CONTROL.setTitle(((StringProperty) properties.get(key)).get());
            } else if ("TITLE_VISIBLE".equals(key)) {
                CONTROL.setTitleVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("LABEL".equals(key)) {
                CONTROL.setLabel(((StringProperty) properties.get(key)).get());
            } else if ("LABEL_VISIBLE".equals(key)) {
                CONTROL.setLabelVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("SECTIONS_ARRAY".equals(key)) {
                CONTROL.setSections(((ObjectProperty<Section[]>) properties.get(key)).get());
            } else if ("SECTIONS_LIST".equals(key)) {
                CONTROL.setSections(((ObjectProperty<List<Section>>) properties.get(key)).get());
            } else if ("LAYOUT_X".equals(key)) {
                CONTROL.setLayoutX(((DoubleProperty) properties.get(key)).get());
            } else if ("LAYOUT_Y".equals(key)) {
                CONTROL.setLayoutY(((DoubleProperty) properties.get(key)).get());
            } else if ("VALUE_ANIMATION_ENABLED".equals(key)) {
                CONTROL.setValueAnimationEnabled(((BooleanProperty) properties.get(key)).get());
            } else if ("POINTER_COLOR".equals(key)) {
                CONTROL.setPointerColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("POINTER_TYPE".equals(key)) {
                CONTROL.setPointerType(((ObjectProperty<DashBoardGauge.PointerType>) properties.get(key)).get());
            } else if ("TOOLTIP_VISIBLE".equals(key)) {
                CONTROL.setTooltipVisible(((BooleanProperty) properties.get(key)).get());
            }
        }

        return CONTROL;
    }
}

