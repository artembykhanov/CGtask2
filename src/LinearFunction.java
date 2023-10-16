import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LinearFunction extends Application {
    private static String formula;

    private final static int height = 900;
    private final static int width = 900;
    private static final double deltaX = 0.001; // Плотность точек

    // Области значений
    private static double minX = -10;
    private static double maxX = 10;
    private static double minY = -10;
    private static double maxY = 10;
    private double lastX;
    private double lastY;
    private boolean isPanning = false;
    private NumberAxis xAxis;
    private NumberAxis yAxis;

    public void setFormula(String formula) {
        LinearFunction.formula = formula;
    }

    public void launchApp() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("График функции");

        xAxis = new NumberAxis(minX, maxX, 1);
        yAxis = new NumberAxis(minY, maxY, 1);
        xAxis.setLabel("X");
        yAxis.setLabel("Y");

        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("График функции f(x) = " + formula);
        scatterChart.setPrefSize(width, height);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(formula);

        FormulaElement result = Interpreter.evaluateFormula(formula);
        List<XYChart.Data<Number, Number>> dataPoints = new ArrayList<>();

        //Вычисления точек графика
        for (double x = minX; x <= maxX; x += deltaX) {
            Interpreter.setVariable(0, x);

            if (Interpreter.hasError()) {
                System.out.println("Ошибка в формуле!");
                break;
            } else {
                double y = result.getValue();
                System.out.println("При x = " + x + " ; Значение y = " + y);

                // Создаём точку, чтобы установить радиус
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, y);
                Circle circle = new Circle(1.0); // Задайте радиус точки
                dataPoint.setNode(circle);
                dataPoints.add(dataPoint);
            }
        }

        // Добавление точек в график
        series.getData().addAll(dataPoints);
        scatterChart.getData().add(series);

        // Обработка событий масштабирования и перемещения
        scatterChart.setOnScroll(this::handleScroll);
        scatterChart.setOnMousePressed(this::handleMousePressed);
        scatterChart.setOnMouseDragged(this::handleMouseDragged);
        scatterChart.setOnMouseReleased(this::handleMouseReleased);

        // Интерфейс сцены
        Pane chartPane = new Pane(scatterChart);
        Scene scene = new Scene(chartPane, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        double zoomFactor = 1.1;

        // Изменение масштаба графика при прокрутке
        if (delta < 0) {
            minX /= zoomFactor;
            maxX /= zoomFactor;
            minY /= zoomFactor;
            maxY /= zoomFactor;
        } else {
            minX *= zoomFactor;
            maxX *= zoomFactor;
            minY *= zoomFactor;
            maxY *= zoomFactor;
        }

        // Обновление границ осей
        xAxis.setLowerBound(minX);
        xAxis.setUpperBound(maxX);
        yAxis.setLowerBound(minY);
        yAxis.setUpperBound(maxY);

        event.consume();
    }

    private void handleMousePressed(MouseEvent event) {
        //Начальные координаты, для реализации перемещения
        lastX = event.getX();
        lastY = event.getY();
        isPanning = true;
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isPanning) {
            double deltaX = event.getX() - lastX;
            double deltaY = event.getY() - lastY;

            // Перемещение графика при перетаскивании
            minX += deltaX / 50.0;
            maxX += deltaX / 50.0;
            minY -= deltaY / 50.0;
            maxY -= deltaY / 50.0;

            // Обновление границ осей
            xAxis.setLowerBound(minX);
            xAxis.setUpperBound(maxX);
            yAxis.setLowerBound(minY);
            yAxis.setUpperBound(maxY);

            lastX = event.getX();
            lastY = event.getY();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        isPanning = false;
    }
}
