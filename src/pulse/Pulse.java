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
    
    private static final String[] MINUTES = new String[] {"00","15","30","45"};
    
    @Override
    public void start(Stage primaryStage) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd hh:mma");
        DateFormat timeFormat = new SimpleDateFormat("hh:mma");
        DateFormat minuteFormat = new SimpleDateFormat("mm");
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Text scenetitle = new Text("Pulse");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.minutes(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Date date = new Date();
                Date oldDate = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(15));;
                String minuteCount = minuteFormat.format(date);
                if (!(Arrays.asList(MINUTES).contains(minuteCount))) {
                    return;
                }
 
                GridPane grid2 = new GridPane();
                grid2.setAlignment(Pos.CENTER);
                grid2.setHgap(10);
                grid2.setVgap(10);
                grid2.setPadding(new Insets(25, 25, 25, 25));
        
                Text scenetitle2 = new Text(String.format(
                    "Update %s - %s",
                    dateTimeFormat.format(oldDate),
                    timeFormat.format(date)
                ));
                scenetitle2.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                grid2.add(scenetitle2, 0, 0, 2, 1);

                Label userName2 = new Label("What you were doing:");
                grid2.add(userName2, 0, 1);
                TextField userTextField2 = new TextField();
                grid2.add(userTextField2, 1, 1);

                Label twoName2 = new Label("Category:");
                grid2.add(twoName2, 0, 2);
                ChoiceBox cb = new ChoiceBox(
                    FXCollections.observableArrayList(
                        "Coding",
                        "Testing",
                        "Designing",
                        "Reading Documentation",
                        new Separator(),
                        "Emails",
                        "Meeting",
                        "Blocked",
                        new Separator(),
                        "Distracted",
                        "Breakfast",
                        "Lunch",
                        "Dinner"
                    )
                );
                grid2.add(cb, 1, 2);
            
                Stage stage2 = new Stage();
                Button btn = new Button("Submit");
                btn.setOnAction((e) -> {
                    String x = String.format(
                        "%s\t%s\t%s\n",
                        timeFormat.format(oldDate),
                        userTextField2.getText(),
                        (String)cb.getValue()
                    );
                    System.out.println("/var/pulse/" + dateFormat.format(oldDate) + "_logs.txt");
                    try {
                        Files.write(
                                Paths.get("/var/pulse/" + dateFormat.format(oldDate) + "_logs.txt"),
                                x.getBytes(),
                                StandardOpenOption.CREATE,
                                StandardOpenOption.APPEND
                        );
                    } catch (IOException ex) {
                        Logger.getLogger(Pulse.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    stage2.close();
                });
                btn.defaultButtonProperty().bind(btn.focusedProperty());

                HBox hbBtn = new HBox(10);
                hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                hbBtn.getChildren().add(btn);
                grid2.add(hbBtn, 1, 4);

                stage2.setTitle("Pulse Alert");
                stage2.setScene(new Scene(grid2, 550, 200));
                stage2.show();
                stage2.setAlwaysOnTop(true);
                stage2.toFront();
            }
        }));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();
        
        Scene scene = new Scene(grid, 300, 150);
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
