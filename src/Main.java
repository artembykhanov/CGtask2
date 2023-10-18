import static javafx.application.Application.launch;

public class Main {
    public static void main(String[] args) {
        String formula = "sin(E^x)";

       LinearFunction plotter1 = new LinearFunction();
       plotter1.setFormula(formula);
       plotter1.launchApp();
       // Запускаем приложение

    }

}