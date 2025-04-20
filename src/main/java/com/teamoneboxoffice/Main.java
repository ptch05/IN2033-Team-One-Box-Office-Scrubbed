package com.teamoneboxoffice;

import com.teamoneboxoffice.util.NavigationUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        NavigationUtil.setPrimaryStage(primaryStage);

        String fxmlFileName = "LoginForm.fxml"; // Start with Login form
        String fxmlResourcePath = "/scenes/" + fxmlFileName;

        try {
            URL fxmlUrl = getClass().getResource(fxmlResourcePath);
            Objects.requireNonNull(
                    fxmlUrl,
                    "Cannot find FXML file: " + fxmlResourcePath
            );

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);

            String cssPath = "/styles/styles.css";
            URL cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println(
                        "Could not find stylesheet resource via getResource: " +
                                cssPath
                );
            }

            primaryStage.setTitle("Box Office Login");
            primaryStage.setScene(scene);
            primaryStage.setWidth(1200);
            primaryStage.setHeight(850);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlResourcePath);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
