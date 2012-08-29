import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;


public class Demo extends Application {
    private Random         rnd;
    private long           lastTimerCall;
    private AnimationTimer timer;
    private DashBoardGauge control;

    @Override public void init() {
        rnd           = new Random();
        lastTimerCall = 0l;
        timer         = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now  > lastTimerCall + 3000000000l) {
                    control.setValue(rnd.nextDouble() * 100.0);
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        control = DashBoardGaugeBuilder.create()
                                       .title("Units in Stock")
                                       .label("Plasma HD TV")
                                       .sections(new Section[] {
                                           new Section(0, 30, Color.rgb(209, 46, 0), "Low"),
                                           new Section(30, 60, Color.rgb(249, 200, 19), "Medium"),
                                           new Section(60, 100, Color.rgb(131, 167, 36), "High")
                                       })
                                       //.pointerType(DashBoardGauge.PointerType.TYPE2)
                                       //.pointerColor(Color.RED)
                                       //.valueAnimationEnabled(true)
                                       .build();

        StackPane pane = new StackPane();
        pane.getChildren().add(control);

        Scene scene = new Scene(pane, Color.DARKGRAY);

        stage.setTitle("JavaFX Custom Control");
        stage.setScene(scene);
        stage.show();
        timer.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}


