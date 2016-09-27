/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pulse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author carlobiedenharn
 */
public class Pulse extends Application {

    private static final String[] MINUTES = new String[]{"00", "15", "30", "45"};

    // Basic formatting functions
    private static final DateFormat DS_FORMAT = new SimpleDateFormat("yyyy_MM_dd");
    private static final DateFormat DS_TS_FORMAT = new SimpleDateFormat("yyyy/MM/dd hh:mma");
    private static final DateFormat TS_FORMAT = new SimpleDateFormat("hh:mma");
    private static final DateFormat MM_FORMAT = new SimpleDateFormat("mm");

    // The start at having some auto completion
    private String lastMessage = "";
    private String lastCategory = "No";
    private int categoryStreak = 0;

    private GridPane buildDefaultGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        return grid;
    }

    private ChoiceBox buildCategoryList() {
        ChoiceBox cb = new ChoiceBox(
                FXCollections.observableArrayList(
                        "Coding",
                        "Distracted",
                        "Emails",
                        new Separator(),
                        "Meeting",
                        "Code Review",
                        "Reading Docs",
                        new Separator(),
                        "Eating"
                )
        );
        return cb;
    }

    private String buildLogFile(Date date) {
        return "/var/pulse/" + DS_FORMAT.format(date) + "_logs.txt";
    }

    private void logPulse(String activity, String category, Date date) {
        String logLine = String.format("%s\t%s\t%s\n",
                TS_FORMAT.format(date),
                activity,
                category
        );
        try {
            Files.write(Paths.get(buildLogFile(date)),
                    logLine.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException ex) {
            Logger.getLogger(Pulse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start(Stage primaryStage) {

        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.minutes(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Get the current time and Fifteen Minutes ago.
                Date date = new Date();
                Date oldDate = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(15));;

                // Only show this popup every fifteen minutes.
                String minuteCount = MM_FORMAT.format(date);
                if (!(Arrays.asList(MINUTES).contains(minuteCount))) {
                    return;
                }

                // Build the popup screen config
                GridPane popupGrid = buildDefaultGrid();

                Text scenetitle2 = new Text(String.format("%s - %s",
                        TS_FORMAT.format(oldDate),
                        TS_FORMAT.format(date)
                ));
                scenetitle2.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                popupGrid.add(scenetitle2, 1, 0, 2, 1);

                Label oldActionMessage = new Label("Last heartbeat: " + lastMessage);
                popupGrid.add(oldActionMessage, 0, 1, 2, 1);

                Label oldActionPrompt = new Label(lastCategory + " streak: " + categoryStreak);
                popupGrid.add(oldActionPrompt, 0, 2, 2, 1);

                Label promptMessage = new Label("What were you doing:");
                popupGrid.add(promptMessage, 0, 3);

                TextField previousActivity = new TextField(lastMessage);
                popupGrid.add(previousActivity, 1, 3);

                Label categoryLabel = new Label("Category:");
                popupGrid.add(categoryLabel, 0, 4);
                ChoiceBox cb = buildCategoryList();

                popupGrid.add(cb, 1, 4);

                Stage stage2 = new Stage();
                Button btn = new Button("Submit");
                btn.setOnAction((e) -> {
                    String cbString = (String) cb.getValue();
                    logPulse(previousActivity.getText(), cbString, oldDate);

                    // Store the values for later use
                    if (lastCategory.equals(cbString)) {
                        categoryStreak++;
                    } else {
                        categoryStreak = 1;
                    }
                    lastMessage = previousActivity.getText();
                    lastCategory = cbString;

                    stage2.close();
                });
                btn.defaultButtonProperty().bind(btn.focusedProperty());

                HBox hbBtn = new HBox(10);
                hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                hbBtn.getChildren().add(btn);
                popupGrid.add(hbBtn, 2, 5);

                stage2.setTitle("Pulse Alert");
                stage2.setScene(new Scene(popupGrid, 550, 200));
                stage2.show();
                stage2.setAlwaysOnTop(true);
                stage2.toFront();
            }
        }));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();

        // Build the main screen and add the text/title.
        GridPane pulseScreen = buildDefaultGrid();
        Text scenetitle = new Text("Pulse");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        pulseScreen.add(scenetitle, 0, 0, 2, 1);

        Scene scene = new Scene(pulseScreen, 300, 150);
        primaryStage.setTitle("Pulse");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
