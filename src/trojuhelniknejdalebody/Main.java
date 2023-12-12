package trojuhelniknejdalebody;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main extends Application {
    private List<Circle> points = new ArrayList<>();
    private Polygon triangle = new Polygon();
    private Label countLabel = new Label("Počet bodů: 0");
    private Color selectedColor = Color.RED;

    @Override
    public void start(Stage primaryStage) {
        Pane drawingPane = new Pane();
        drawingPane.setBackground(new Background(new BackgroundFill(createGradient(), null, null)));

        ColorPicker colorPicker = new ColorPicker(selectedColor);
        colorPicker.setOnAction(event -> {
            selectedColor = colorPicker.getValue();
            points.forEach(circle -> circle.setFill(selectedColor));
        });

        Button endButton = new Button("Konec");
        endButton.setOnAction(event -> Platform.exit());

        Button deleteButton = new Button("Vymazat");
        deleteButton.setOnAction(event -> {
            points.clear();
            drawingPane.getChildren().clear();
            countLabel.setText("Počet bodů: 0");
            updateTriangle(drawingPane);
        });

        HBox controlPane = new HBox(10, endButton, deleteButton, new Label("Barva"), colorPicker, countLabel);
        controlPane.setAlignment(Pos.CENTER);
        controlPane.setPadding(new Insets(10));
        controlPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(drawingPane);
        mainPane.setBottom(controlPane);

        Scene scene = new Scene(mainPane, 600, 400);

        drawingPane.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                Circle newPoint = new Circle(event.getX(), event.getY(), 5, selectedColor);
                newPoint.setStroke(Color.BLACK);
                points.add(newPoint);
                drawingPane.getChildren().add(newPoint);
                countLabel.setText("Počet bodů: " + points.size());
                updateTriangle(drawingPane);
            } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                Circle pointToRemove = null;
                for (Circle point : points) {
                    if (point.contains(event.getX(), event.getY())) {
                        pointToRemove = point;
                        break;
                    }
                }
                if (pointToRemove != null) {
                    points.remove(pointToRemove);
                    drawingPane.getChildren().remove(pointToRemove);
                    countLabel.setText("Počet bodů: " + points.size());
                    updateTriangle(drawingPane);
                }
            }
        });

        countLabel.setFont(new Font("Arial", 12));
        triangle.setStroke(Color.rgb(0, 0, 204));
        triangle.setStrokeWidth(3);
        triangle.getStrokeDashArray().addAll(10.0, 10.0);
        triangle.setStrokeLineCap(StrokeLineCap.ROUND);
        triangle.setFill(Color.rgb(0, 102, 255, 0.5));
        drawingPane.getChildren().add(triangle);

        primaryStage.setTitle("Triangle App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private LinearGradient createGradient() {
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.REFLECT,
                new Stop(0, Color.WHITE),
                new Stop(0.25, Color.YELLOW),
                new Stop(0.5, Color.WHITE),
                new Stop(0.75, Color.YELLOW),
                new Stop(1, Color.WHITE));
    }

    private void updateTriangle(Pane drawingPane) {
        drawingPane.getChildren().remove(triangle);
        if (points.size() < 3) {
            return;
        }
        double centerX = drawingPane.getWidth() / 2;
        double centerY = drawingPane.getHeight() / 2;
        points.sort(Comparator.comparingDouble(point -> -Math.hypot(point.getCenterX() - centerX, point.getCenterY() - centerY)));
        triangle.getPoints().setAll(
                points.get(0).getCenterX(), points.get(0).getCenterY(),
                points.get(1).getCenterX(), points.get(1).getCenterY(),
                points.get(2).getCenterX(), points.get(2).getCenterY());
        drawingPane.getChildren().add(triangle);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
