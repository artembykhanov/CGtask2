import static javafx.application.Application.launch;

public class Main {
    public static void main(String[] args) {
        String formula = "cbr(x)";

       LinearFunction plotter1 = new LinearFunction();
       plotter1.setFormula(formula);
       // Запускаем приложение
       plotter1.launchApp();

    }

}