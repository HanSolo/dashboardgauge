import com.sun.javafx.scene.control.skin.SkinBase;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Popup;
import javafx.stage.PopupBuilder;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;


public class DashBoardGaugeSkin extends SkinBase<DashBoardGauge, DashBoardGaugeBehavior> {
    private DashBoardGauge           control;
    private boolean                  isDirty;
    private boolean                  initialized;
    private Text                     valueText;
    private Text                     labelText;
    private Text                     titleText;
    private String                   currentSectionText;
    private Group                    background;
    private Group                    sections;
    private Group                    scale;
    private Shape                    pointerShape;
    private Group                    pointerGroup;
    private Group                    pointer;
    private Circle                   knob;
    private Group                    foreground;
    private Rotate                   rotate;
    private NumberFormat             valueFormat;
    private Timeline                 timeline;
    private DoubleProperty           gaugeValue;
    private Rectangle                labelMain;
    private EventHandler<MouseEvent> mouseHandler;
    private Popup                    popup;
    private Label                    popupLabel;
    private Rectangle                popupBackground;
    private long                     popupShownAt;
    private AnimationTimer           popupClosingTimer;


    // ******************** Constructors **************************************
    public DashBoardGaugeSkin(final DashBoardGauge CONTROL) {
        super(CONTROL, new DashBoardGaugeBehavior(CONTROL));
        control        = CONTROL;
        initialized    = false;
        isDirty        = false;
        background     = new Group();
        sections       = new Group();
        scale          = new Group();
        pointerGroup   = new Group();
        pointer        = new Group(pointerGroup);
        foreground     = new Group();
        rotate         = new Rotate();
        valueFormat    = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        timeline       = new Timeline();
        gaugeValue     = new SimpleDoubleProperty(control.getValue());
        labelMain      = new Rectangle();
        popup          = PopupBuilder.create()
                                     .autoHide(true)
                                     .autoFix(true)
                                     .hideOnEscape(true)
                                     .build();
        popupLabel     = new Label();
        mouseHandler   = new EventHandler<MouseEvent>() {
            @Override public void handle(final MouseEvent EVENT) {
                final Object    SRC  = EVENT.getSource();
                final EventType TYPE = EVENT.getEventType();
                if (SRC.equals(foreground) && TYPE == MouseEvent.MOUSE_MOVED) {
                    popupLabel.setText(titleText.getText() + ": " + valueFormat.format(gaugeValue.get()) + currentSectionText);
                    if (popupLabel.getLayoutBounds().getWidth() != 0) {
                        popupBackground.setWidth(popupLabel.getLayoutBounds().getWidth() + 10);
                    }
                    popup.show(getScene().getRoot(), EVENT.getScreenX(), EVENT.getScreenY());
                }
            }
        };

        popupClosingTimer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > popupShownAt + 1500000000) {
                    popup.hide();
                }
            }
        };

        init();
        initPopup();
    }

    private void init() {
        if (control.getPrefWidth() < 0 | control.getPrefHeight() < 0) {
            control.setPrefSize(199, 200);
        }

        if(!control.getSections().isEmpty()) {
            updateSections();
        }

        // Register listeners
        registerChangeListener(control.prefWidthProperty(), "PREF_WIDTH");
        registerChangeListener(control.prefHeightProperty(), "PREF_HEIGHT");
        registerChangeListener(control.minValueProperty(), "SCALE");
        registerChangeListener(control.maxValueProperty(), "SCALE");
        registerChangeListener(control.niceScalingProperty(), "SCALE");
        registerChangeListener(control.tightScaleProperty(), "SCALE");
        registerChangeListener(control.scaleProperty(), "SCALE");
        registerChangeListener(control.valueProperty(), "VALUE");
        registerChangeListener(control.modelProperty(), "MODEL");
        registerChangeListener(control.titleProperty(), "BACKGROUND");
        registerChangeListener(control.titleVisibleProperty(), "BACKGROUND");
        registerChangeListener(control.labelProperty(), "BACKGROUND");
        registerChangeListener(control.labelVisibleProperty(), "BACKGROUND");
        registerChangeListener(control.pointerTypeProperty(), "POINTER");
        registerChangeListener(control.pointerColorProperty(), "POINTER");
        registerChangeListener(control.tooltipVisibleProperty(), "FOREGROUND");
        registerChangeListener(gaugeValue, "GAUGE_VALUE");
        control.getSections().addListener(new InvalidationListener() {
            @Override public void invalidated(Observable observable) {
                updateSections();
                paint();
            }
        });

        initialized = true;
        paint();

        updatePointerRotation();
    }

    private void initPopup() {
        popup.getContent().clear();
        StackPane pane = new StackPane();
        popupLabel.setText(titleText.getText() + ": " + valueFormat.format(gaugeValue.get()) + currentSectionText);
        popupBackground = new Rectangle(0, 0, 200, 24);
        popupBackground.setArcWidth(10);
        popupBackground.setArcHeight(10);
        popupBackground.setFill(Color.rgb(255, 255, 255, 0.9));
        popupBackground.setStroke(Color.rgb(132, 132, 140, 0.9));
        pane.getChildren().addAll(popupBackground, popupLabel);
        popup.getContent().addAll(pane);

        popup.setOnShowing(new EventHandler<WindowEvent>() {
            @Override public void handle(WindowEvent windowEvent) {
                popupShownAt = System.nanoTime();
                popupClosingTimer.start();
            }
        });
    }


    // ******************** Methods *******************************************
    @Override protected void handleControlPropertyChanged(final String PROPERTY) {
        super.handleControlPropertyChanged(PROPERTY);
        if ("PREF_WIDTH".equals(PROPERTY)) {
            updateSections();
            paint();
        } else if ("PREF_HEIGHT".equals(PROPERTY)) {
            updateSections();
            paint();
        } else if ("SCALE".equals(PROPERTY)) {
            drawScale();
        } else if ("MODEL".equals(PROPERTY)) {
            paint();
        } else if ("VALUE".equals(PROPERTY)) {
            if (control.isValueAnimationEnabled()) {
                final KeyValue KEY_VALUE = new KeyValue(gaugeValue, control.getValue(), Interpolator.SPLINE(0.5, 0.4, 0.4, 1.0));
                final KeyFrame KEY_FRAME = new KeyFrame(Duration.millis(1500), KEY_VALUE);
                timeline.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent actionEvent) {
                        gaugeValue.set(control.getValue());
                    }
                });
                timeline    = new Timeline();
                timeline.getKeyFrames().add(KEY_FRAME);
                timeline.play();
            } else {
                gaugeValue.set(control.getValue());
            }
        } else if ("BACKGROUND".equals(PROPERTY)) {
            drawBackground();
        } else if ("POINTER".equals(PROPERTY)) {
            drawPointer();
        } else if ("FOREGROUND".equals(PROPERTY)) {
            drawForeground();
        } else if ("GAUGE_VALUE".equals(PROPERTY)) {
            valueText.setText(valueFormat.format(gaugeValue.get()));
            if (control.getLabel().isEmpty()) {
                valueText.setX(labelMain.getLayoutBounds().getMinX() + (labelMain.getLayoutBounds().getWidth() - valueText.getLayoutBounds().getWidth()) / 2.0);
            } else {
                valueText.setX(labelMain.getLayoutBounds().getMaxX() - valueText.getLayoutBounds().getWidth() - 0.025 * control.getPrefWidth());
            }
            if (!control.getSections().isEmpty()) {
                currentSectionText = "";
                for (Section section : control.getSections()) {
                    if (gaugeValue.get() > section.getStart() && gaugeValue.get() < section.getStop()) {
                        currentSectionText = " (" + section.getText() + ")";
                        break;
                    }
                }
            }
            updatePointerRotation();
        }
    }

    public final void paint() {
        if (!initialized) {
            init();
        }
        drawBackground();
        drawSections();
        drawScale();
        drawPointer();
        drawForeground();

        addDropShadow(true, Color.color(0.0, 0.0, 0.0, 0.25), pointer);
        addDropShadow(false, Color.color(0.0, 0.0, 0.0, 0.25), knob);

        getChildren().clear();
        getChildren().addAll(background,
                             sections,
                             scale,
                             pointer,
                             foreground);
    }

    @Override public void layoutChildren() {
        if (isDirty) {
            paint();
            isDirty = false;
        }
        super.layoutChildren();
    }

    @Override public final DashBoardGauge getSkinnable() {
        return control;
    }

    @Override public final void dispose() {
        control = null;
    }

    @Override protected double computePrefWidth(final double PREF_WIDTH) {
        double prefWidth = 199;
        if (PREF_WIDTH != -1) {
            prefWidth = Math.max(0, PREF_WIDTH - getInsets().getLeft() - getInsets().getRight());
        }
        return super.computePrefWidth(prefWidth);
    }

    @Override protected double computePrefHeight(final double PREF_HEIGHT) {
        double prefHeight = 200;
        if (PREF_HEIGHT != -1) {
            prefHeight = Math.max(0, PREF_HEIGHT - getInsets().getTop() - getInsets().getBottom());
        }
        return super.computePrefHeight(prefHeight);
    }

	@Override protected double computeMinWidth(final double MIN_WIDTH) {
	    return super.computeMinWidth(Math.max(199, MIN_WIDTH - getInsets().getLeft() - getInsets().getRight()));
	}

	@Override protected double computeMinHeight(final double MIN_HEIGHT) {
	    return super.computeMinHeight(Math.max(200, MIN_HEIGHT - getInsets().getTop() - getInsets().getBottom()));
	}

	@Override protected double computeMaxWidth(final double MAX_WIDTH) {
	    return super.computeMaxWidth(Math.max(199, MAX_WIDTH - getInsets().getLeft() - getInsets().getRight()));
	}

	@Override protected double computeMaxHeight(final double MAX_HEIGHT) {
	    return super.computeMaxHeight(Math.max(200, MAX_HEIGHT - getInsets().getTop() - getInsets().getBottom()));
	}

    private void updateSections() {
        final double WIDTH   = control.getPrefWidth();
        final double HEIGHT  = control.getPrefHeight();
        final Point2D CENTER = new Point2D(0.5 * WIDTH, 0.6425 * HEIGHT);

        final double OUTER_RADIUS_X = 0.4271356784 * WIDTH;
        final double OUTER_RADIUS_Y = 0.425 * HEIGHT;
        final Shape INNER           = new Circle(CENTER.getX(), CENTER.getY(), 0.3718592965 * WIDTH);

        for (final Section section : control.getSections()) {
            final double SECTION_START = section.getStart() < control.getMinValue() ? control.getMinValue() : section.getStart();
            final double SECTION_STOP  = section.getStop() > control.getMaxValue() ? control.getMaxValue() : section.getStop();
            final double ANGLE_START   = 200 - (SECTION_START * control.getAngleStep()) + (control.getMinValue() * control.getAngleStep());
            final double ANGLE_EXTEND  = -(SECTION_STOP - SECTION_START) * control.getAngleStep();

            final Arc OUTER_ARC = new Arc();
            OUTER_ARC.setType(ArcType.ROUND);
            OUTER_ARC.setCenterX(CENTER.getX());
            OUTER_ARC.setCenterY(CENTER.getY());
            OUTER_ARC.setRadiusX(OUTER_RADIUS_X);
            OUTER_ARC.setRadiusY(OUTER_RADIUS_Y);
            OUTER_ARC.setStartAngle(ANGLE_START);
            OUTER_ARC.setLength(ANGLE_EXTEND);
            final Shape SECTION = Shape.subtract(OUTER_ARC, INNER);
            section.setSectionArea(SECTION);
        }
    }

    private void updatePointerRotation() {
        final Point2D CENTER = new Point2D(0.5 * control.getPrefWidth(), 0.6425 * control.getPrefHeight());
        rotate.setPivotX(CENTER.getX());
        rotate.setPivotY(CENTER.getY());
        rotate.setAngle(250 + gaugeValue.get() * control.getAngleStep());
        pointerShape.getTransforms().clear();
        pointerShape.getTransforms().add(rotate);
    }

    private void addDropShadow(final boolean WITH_LIGHTING, final Color COLOR, final Node... NODES) {
        if (NODES.length == 0) {
            return;
        }
        final double        SIZE     = control.getPrefWidth();
        final Lighting      LIGHTING = new Lighting();
        final Light.Distant LIGHT    = new Light.Distant();
        LIGHT.setAzimuth(270);
        LIGHT.setElevation(50);
        LIGHTING.setLight(LIGHT);

        final DropShadow DROP_SHADOW = new DropShadow();
        if (WITH_LIGHTING) {
            DROP_SHADOW.setInput(LIGHTING);
        }
        DROP_SHADOW.setOffsetX(-0.02 * SIZE);
        DROP_SHADOW.setOffsetY(0.02 * SIZE);
        DROP_SHADOW.setRadius(0.04 * SIZE);
        DROP_SHADOW.setBlurType(BlurType.GAUSSIAN);
        DROP_SHADOW.setColor(COLOR);

        for (Node node : NODES) {
            node.setEffect(DROP_SHADOW);
        }
    }


    // ******************** Drawing related ***********************************
    public final void drawBackground() {
        final double SIZE   = control.getPrefWidth() < control.getPrefHeight() ? control.getPrefWidth() : control.getPrefHeight();
        final double WIDTH  = control.getPrefWidth();
        final double HEIGHT = control.getPrefHeight();

        background.getChildren().clear();

        background.getStyleClass().clear();
        background.getStyleClass().setAll("dashboard");

        final Shape IBOUNDS = new Rectangle(0, 0, WIDTH, HEIGHT);
        IBOUNDS.setOpacity(0.0);
        background.getChildren().add(IBOUNDS);

        final Rectangle BACKGROUND = new Rectangle(0.0, 0.0,
                                                   WIDTH, HEIGHT);
        final Paint BACKGROUND_FILL = new LinearGradient(0, 0.0,
                                                         0, HEIGHT,
                                                         false, CycleMethod.NO_CYCLE,
                                                         new Stop(0.0, Color.color(0.90, 0.90, 0.90, 1)),
                                                         new Stop(0.1, Color.color(0.90, 0.90, 0.90, 1)),
                                                         new Stop(0.2, Color.WHITE),
                                                         new Stop(1.0, Color.WHITE));
        BACKGROUND.setFill(BACKGROUND_FILL);
        BACKGROUND.setStroke(null);

        final Rectangle LABEL_FRAME = new Rectangle(0.07035175879396985 * WIDTH, 0.83 * HEIGHT,
                                                   0.8542713567839196 * WIDTH, 0.12 * HEIGHT);
        LABEL_FRAME.setArcWidth(0.04020100502512563 * WIDTH);
        LABEL_FRAME.setArcHeight(0.04 * HEIGHT);
        final Paint LABEL_FRAME_FILL = new LinearGradient(0.5025125628140703 * WIDTH, 0.83 * HEIGHT,
                                                          0.5025125628140703 * WIDTH, 0.955 * HEIGHT,
                                                          false, CycleMethod.NO_CYCLE,
                                                          new Stop(0.0, Color.color(0.8392156863, 0.8392156863, 0.8745098039, 1)),
                                                          new Stop(1.0, Color.color(0.7411764706, 0.7411764706, 0.7411764706, 1)));
        LABEL_FRAME.setFill(LABEL_FRAME_FILL);
        LABEL_FRAME.setStroke(null);

        labelMain = new Rectangle(0.07537688442211055 * WIDTH, 0.835 * HEIGHT,
                                  0.8442211055276382 * WIDTH, 0.11 * HEIGHT);
        labelMain.setArcWidth(LABEL_FRAME.getArcWidth() - 1.5);
        labelMain.setArcHeight(LABEL_FRAME.getArcHeight() - 1.5);
        final Paint LABEL_MAIN_FILL = new LinearGradient(0.507537688442211 * WIDTH, 0.835 * HEIGHT,
                                                         0.507537688442211 * WIDTH, 0.95 * HEIGHT,
                                                         false, CycleMethod.NO_CYCLE,
                                                         new Stop(0.0, Color.color(0.9098039216, 0.9372549020, 0.9372549020, 1)),
                                                         new Stop(1.0, Color.color(0.7411764706, 0.7411764706, 0.7411764706, 1)));
        labelMain.setFill(LABEL_MAIN_FILL);
        labelMain.setStroke(null);

        labelText = new Text();
        labelText.setText(control.getLabel());
        labelText.setFont(Font.font("ArialMT", FontWeight.NORMAL, FontPosture.REGULAR, 0.07035175879396985 * WIDTH));
        labelText.setTextAlignment(TextAlignment.LEFT);
        labelText.setX(labelMain.getLayoutBounds().getMinX() + 0.025 * WIDTH);
        labelText.setY(0.935 * HEIGHT);
        labelText.setTextOrigin(VPos.BOTTOM);
        labelText.setFill(Color.color(0.2509803922, 0.2509803922, 0.3529411765, 1));

        valueText = new Text();
        valueText.setText(valueFormat.format(control.getValue()));
        valueText.setFont(Font.font("ArialMT", FontWeight.NORMAL, FontPosture.REGULAR, 0.08040201005025126 * WIDTH));
        valueText.setTextAlignment(TextAlignment.RIGHT);
        if (control.getLabel().isEmpty()) {
            valueText.setX(labelMain.getLayoutBounds().getMinX() + (labelMain.getLayoutBounds().getWidth() - valueText.getLayoutBounds().getWidth()) / 2.0);
        } else {
            valueText.setX(labelMain.getLayoutBounds().getMaxX() - valueText.getLayoutBounds().getWidth() - 0.025 * WIDTH);
        }
        valueText.setY(0.935 * HEIGHT);
        valueText.setTextOrigin(VPos.BOTTOM);
        valueText.setFill(Color.color(0.2470588235, 0.2470588235, 0.3490196078, 1));

        if (!control.isLabelVisible()) {
            LABEL_FRAME.setVisible(false);
            labelMain.setVisible(false);
            labelText.setVisible(false);
            valueText.setVisible(false);
            BACKGROUND.setHeight(HEIGHT - LABEL_FRAME.getHeight());
        }


        final Rectangle SEPARATOR = new Rectangle(0.05 * WIDTH, 0.105 * HEIGHT,
                                                  WIDTH - WIDTH * 0.1, 0.015 * HEIGHT);
        final Paint SEPARATOR_FILL = new LinearGradient(0, SEPARATOR.getLayoutBounds().getMinY(),
                                                        0, SEPARATOR.getLayoutBounds().getMaxY(),
                                                        false, CycleMethod.NO_CYCLE,
                                                        new Stop(0.0, Color.color(0.78, 0.78, 0.78, 1)),
                                                        new Stop(0.4, Color.color(0.84, 0.84, 0.84, 1)),
                                                        new Stop(1.0, Color.WHITE));
        SEPARATOR.setFill(SEPARATOR_FILL);
        SEPARATOR.setStroke(null);

        titleText = new Text();
        titleText.setText(control.getTitle());
        titleText.setFont(Font.font("ArialMT", FontWeight.NORMAL, FontPosture.REGULAR, 0.06030150753768844 * WIDTH));
        titleText.setX(0.04522613065326633 * WIDTH);
        titleText.setY(0.095 * HEIGHT);
        titleText.setTextOrigin(VPos.BOTTOM);
        final Paint TITLE_FILL = Color.color(0.2549019608, 0.2549019608, 0.3568627451, 1);
        titleText.setFill(TITLE_FILL);

        if (!control.isTitleVisible()) {
            SEPARATOR.setVisible(false);
            titleText.setVisible(false);
            double adjustedHeight = HEIGHT - SEPARATOR.getLayoutBounds().getHeight() - titleText.getLayoutBounds().getHeight();
            adjustedHeight = !control.isLabelVisible() ? adjustedHeight - LABEL_FRAME.getLayoutBounds().getHeight() : adjustedHeight;
            double adjustedY      = SEPARATOR.getLayoutBounds().getHeight() + titleText.getLayoutBounds().getHeight();
            BACKGROUND.setHeight(adjustedHeight);
            BACKGROUND.setLayoutY(adjustedY);
        }

        final Point2D CENTER = new Point2D(0.5 * WIDTH, 0.6425 * HEIGHT);
        Circle frameCircle   = new Circle(CENTER.getX(), CENTER.getY() , 0.455 * HEIGHT);
        Shape framePunch     = new Rectangle(0, 0.815 * HEIGHT, WIDTH, HEIGHT);
        final Shape FRAME    = Shape.subtract(frameCircle, framePunch);


        final Paint FRAME_FILL = new LinearGradient(0, FRAME.getLayoutBounds().getMinY(),
                                                    0, FRAME.getLayoutBounds().getMaxY(),
                                                    false, CycleMethod.NO_CYCLE,
                                                    new Stop(0.0, Color.color(0.71, 0.71, 0.71, 1)),
                                                    new Stop(1.0, Color.color(0.87, 0.87, 0.87, 1)));
        FRAME.setFill(FRAME_FILL);
        FRAME.setStroke(null);

        Shape mainCircle = new Circle(CENTER.getX(), CENTER.getY(), frameCircle.getRadius() - 1.5);
        Shape mainPunch  = new Rectangle(0, framePunch.getLayoutBounds().getMinY() - 1.5, WIDTH, HEIGHT);
        final Shape MAIN = Shape.subtract(mainCircle, mainPunch);

        final Paint MAIN_FILL = new LinearGradient(0, MAIN.getLayoutBounds().getMinY(),
                                                   0, MAIN.getLayoutBounds().getMaxY(),
                                                   false, CycleMethod.NO_CYCLE,
                                                   new Stop(0.0, Color.WHITE),
                                                   new Stop(0.3, Color.WHITE),
                                                   new Stop(1.0, Color.color(0.9372549020, 0.9372549020, 0.9372549020, 1)));
        MAIN.setFill(MAIN_FILL);
        MAIN.setStroke(null);

        final InnerShadow INNER_SHADOW = new InnerShadow();
        INNER_SHADOW.setOffsetX(-0.045 * SIZE);
        INNER_SHADOW.setOffsetY(0.055 * SIZE);
        INNER_SHADOW.setRadius(0.035 * MAIN.getLayoutBounds().getWidth());
        INNER_SHADOW.setColor(Color.color(0, 0, 0, 0.2));
        INNER_SHADOW.setBlurType(BlurType.GAUSSIAN);
        MAIN.setEffect(INNER_SHADOW);

        background.getChildren().addAll(BACKGROUND,
                                        LABEL_FRAME,
                                        labelMain,
                                        labelText,
                                        valueText,
                                        SEPARATOR,
                                        titleText,
                                        FRAME,
                                        MAIN);
        background.setCache(true);
    }

    public final void drawSections() {
        final double WIDTH   = control.getPrefWidth();
        final double HEIGHT  = control.getPrefHeight();

        sections.getChildren().clear();

        final Shape IBOUNDS = new Rectangle(0, 0, WIDTH, HEIGHT);
        IBOUNDS.setOpacity(0.0);
        sections.getChildren().add(IBOUNDS);
        if (WIDTH > 0 && HEIGHT > 0) {
            for (Section section : control.getSections()) {
                final Shape SECTION = section.getSectionArea();
                SECTION.setFill(section.getColor());
                sections.getChildren().add(SECTION);
            }
        }
        sections.setBlendMode(BlendMode.MULTIPLY);
        sections.setCache(true);
    }

    public final void drawPointer() {
        final double  WIDTH  = control.getPrefWidth();
        final double  HEIGHT = control.getPrefHeight();
        final Point2D CENTER = new Point2D(0.5 * WIDTH, 0.6425 * HEIGHT);

        pointerGroup.getChildren().clear();

        final Shape IBOUNDS = new Rectangle(0, 0, WIDTH, HEIGHT);
        IBOUNDS.setOpacity(0.0);
        switch(control.getPointerType()) {
            case TYPE2:
                pointerShape = new Path();
                ((Path) pointerShape).getElements().add(new MoveTo(CENTER.getX(), CENTER.getY()));
                ((Path) pointerShape).getElements().add(new LineTo(CENTER.getX() + 0.0150753769 * WIDTH, CENTER.getY()));
                ((Path) pointerShape).getElements().add(new LineTo(CENTER.getX() + 0.0150753769 * WIDTH, CENTER.getY() - 0.215 * HEIGHT));
                ((Path) pointerShape).getElements().add(new LineTo(CENTER.getX(), CENTER.getY() - 0.23 * HEIGHT));
                ((Path) pointerShape).getElements().add(new LineTo(CENTER.getX() - 0.0150753769 * WIDTH, CENTER.getY() - 0.215 * HEIGHT));
                ((Path) pointerShape).getElements().add(new LineTo(CENTER.getX() - 0.0150753769 * WIDTH, CENTER.getY()));
                ((Path) pointerShape).getElements().add(new ClosePath());
                break;
            case TYPE1:
            default:
                pointerShape = new Rectangle(0.4924623116 * WIDTH, CENTER.getY() - 0.23 * HEIGHT,
                                             0.01507537688442211 * WIDTH, 0.23 * HEIGHT);

                ((Rectangle) pointerShape).setArcWidth(0.015 * HEIGHT);
                ((Rectangle) pointerShape).setArcHeight(0.015 * HEIGHT);
                break;
        }
        pointerShape.setFill(control.getPointerColor());
        pointerShape.setStroke(null);
        pointerGroup.getChildren().addAll(IBOUNDS, pointerShape);
    }

    public final void drawForeground() {
        final double WIDTH  = control.getPrefWidth();
        final double HEIGHT = control.getPrefHeight();

        foreground.getChildren().clear();
        final Rectangle IBOUNDS = new Rectangle(0, 0, WIDTH, HEIGHT);
        IBOUNDS.setOpacity(0.0);
        foreground.getChildren().add(IBOUNDS);

        knob = new Circle(0.5 * WIDTH, 0.6425 * HEIGHT, 0.0829145728643216 * WIDTH);
        final Paint KNOB_FILL = Color.WHITE;
        knob.setFill(KNOB_FILL);
        knob.setStroke(null);
        foreground.getChildren().add(knob);
        foreground.setCache(true);
        if (control.isTooltipVisible()) {
            foreground.addEventFilter(MouseEvent.MOUSE_MOVED, mouseHandler);
        } else {
            foreground.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseHandler);
        }
    }

    public final void drawScale() {
        final double  WIDTH             = control.getPrefWidth();
        final double  HEIGHT            = control.getPrefWidth();
        final Point2D CENTER            = new Point2D(0.5 * WIDTH, 0.5 * HEIGHT);
        final double  TEXT_DISTANCE     = 0.07 * WIDTH;
        final double  RADIUS            = 0.36 * WIDTH;
        final double  ROTATION_OFFSET   = 250;

        final Transform TRANSFORM = Transform.rotate(ROTATION_OFFSET - 180, CENTER.getX(), CENTER.getY());
        scale.getTransforms().clear();
        scale.getTransforms().add(TRANSFORM);
        scale.setTranslateY(0.1425 * HEIGHT);

        scale.getChildren().clear();

        scale.getChildren().clear();
        final Shape IBOUNDS = new Rectangle(0, 0, WIDTH, HEIGHT);
        IBOUNDS.setOpacity(0.0);
        scale.getChildren().add(IBOUNDS);

        NumberFormat numberFormat = new DecimalFormat("0");

        // Definitions
        final Font STD_FONT;
        if (WIDTH < 250) {
            STD_FONT = Font.font("Verdana", FontWeight.NORMAL, 11);
        } else {
            STD_FONT = Font.font("Verdana", FontWeight.NORMAL, (0.05 * WIDTH));
        }

        final Shape INNER           = new Circle(CENTER.getX(), CENTER.getY(), 0.3718592965 * WIDTH);
        final double OUTER_RADIUS_X = 0.4271356784 * WIDTH;
        final double OUTER_RADIUS_Y = 0.425 * HEIGHT;

        final double ANGLE_STEP  = control.getAngleStep();
        double valueCounter      = control.isTightScale() ? control.getMinValue() + control.getScale().getTightScaleOffset() * control.getScale().getMinorTickSpacing() : control.getMinValue();
        int majorTickCounter     = control.isTightScale() ? control.getScale().getMaxNoOfMinorTicks() - 1 - (int) (control.getScale().getTightScaleOffset()) : control.getScale().getMaxNoOfMinorTicks() - 1; // Indicator when to draw the major tickmark
        final double LOWER_BOUND = control.getMinValue();
        final double UPPER_BOUND = control.getMaxValue();
        final double STEP_SIZE   = control.getScale().getMinorTickSpacing();
        double sinValue;
        double cosValue;

        for (double angle = 0, counter = LOWER_BOUND ; Double.compare(counter, UPPER_BOUND) <= 0 ; angle -= ANGLE_STEP, counter += STEP_SIZE) {
            sinValue = Math.sin(Math.toRadians(angle));
            cosValue = Math.cos(Math.toRadians(angle));

            majorTickCounter++;

            // Draw tickmark every major tickmark spacing
            if (majorTickCounter == control.getScale().getMaxNoOfMinorTicks()) {

                // This check could be replaced by a property and was only implemented to make it look like the ADF version
                if (Math.abs(valueCounter) % 20 == 0) {
                    Point2D textPoint  = new Point2D(CENTER.getX() + (RADIUS - TEXT_DISTANCE) * sinValue, CENTER.getY() + (RADIUS - TEXT_DISTANCE) * cosValue);

                    // Draw the standard tickmark labels
                    final Text tickLabel = new Text();
                    tickLabel.setText(numberFormat.format(valueCounter));
                    tickLabel.setFontSmoothingType(FontSmoothingType.LCD);
                    tickLabel.setTextOrigin(VPos.BOTTOM);
                    tickLabel.setTextAlignment(TextAlignment.CENTER);
                    tickLabel.setBoundsType(TextBoundsType.LOGICAL);
                    tickLabel.setFill(Color.color(0.2509803922, 0.2509803922, 0.3529411765, 1));
                    tickLabel.setStroke(null);
                    tickLabel.setFont(STD_FONT);
                    tickLabel.setX(textPoint.getX() - tickLabel.getLayoutBounds().getWidth() / 2.0);
                    tickLabel.setY(textPoint.getY() + tickLabel.getLayoutBounds().getHeight() / 2.0);
                    tickLabel.setRotate(180 - ROTATION_OFFSET);
                    scale.getChildren().add(tickLabel);
                    if (Double.compare(valueCounter, control.getMinValue()) != 0 && Double.compare(valueCounter, control.getMaxValue()) != 0) {
                        final double SECTION_START = valueCounter - 0.25;
                        final double SECTION_STOP  = valueCounter + 0.25;
                        final double ANGLE_START   = 270 - (SECTION_START * control.getAngleStep()) + (control.getMinValue() * control.getAngleStep());
                        final double ANGLE_EXTEND  = -(SECTION_STOP - SECTION_START) * control.getAngleStep();
                        final Arc OUTER_ARC = new Arc();
                        OUTER_ARC.setType(ArcType.ROUND);
                        OUTER_ARC.setCenterX(CENTER.getX());
                        OUTER_ARC.setCenterY(CENTER.getY());
                        OUTER_ARC.setRadiusX(OUTER_RADIUS_X);
                        OUTER_ARC.setRadiusY(OUTER_RADIUS_Y);
                        OUTER_ARC.setStartAngle(ANGLE_START);
                        OUTER_ARC.setLength(ANGLE_EXTEND);
                        final Shape SECTION = Shape.subtract(OUTER_ARC, INNER);
                        if (control.getSections().isEmpty()) {
                            SECTION.setFill(Color.color(0.2509803922, 0.2509803922, 0.3529411765, 1));
                        } else {
                            SECTION.setFill(Color.WHITE);
                        }
                        SECTION.setStroke(null);
                        scale.getChildren().add(SECTION);
                    }
                }
                valueCounter += control.getScale().getMajorTickSpacing();
                majorTickCounter = 0;
                continue;
            }
        }
        scale.setCache(true);
    }
}

