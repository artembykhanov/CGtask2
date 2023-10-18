import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LinearFunction extends JFrame {
    private static String formula;
    private static final int width = 900;
    private static final int height = 900;
    private static final double deltaX = 0.001;

    private static double minX = -10;
    private static double maxX = 10;
    private static double minY = -10;
    private static double maxY = 10;
    private List<Point> dataPoints;

    public LinearFunction(String formula) {
        this.formula = formula;
        this.dataPoints = new ArrayList<>();
        setTitle("График функции");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.BLACK);
                int x0 = (int) ((-minX / (maxX - minX)) * getWidth());
                int y0 = (int) ((maxY / (maxY - minY)) * getHeight());

                g2.drawLine(0, y0, getWidth(), y0);
                g2.drawLine(x0, 0, x0, getHeight());

                FormulaElement result = Interpreter.evaluateFormula(formula);
                for (double x = minX; x <= maxX; x += deltaX) {
                    Interpreter.setVariable(0, x);

                    if (Interpreter.hasError()) {
                        System.out.println("Ошибка в формуле!");
                        break;
                    } else {
                        double y = result.getValue();
                        System.out.println("При x = " + x + " ; Значение y = " + y);

                        int xScreen = (int) ((x - minX) / (maxX - minX) * getWidth());
                        int yScreen = (int) ((maxY - y) / (maxY - minY) * getHeight());
                        dataPoints.add(new Point(xScreen, yScreen));
                    }
                }

                g2.setColor(Color.BLUE);
                for (Point point : dataPoints) {
                    g2.fillRect(point.x, point.y, 2, 2);
                }
            }
        };

        add(chartPanel);
    }


}
