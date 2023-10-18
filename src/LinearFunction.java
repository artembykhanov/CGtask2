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
    private static final double zoomFactor = 1.1;

    private double minX = -10;
    private double maxX = 10;
    private double minY = -10;
    private double maxY = 10;
    private List<Point> dataPoints;
    private int lastMouseX;
    private int lastMouseY;
    private boolean isPanning;

    public LinearFunction(String formula) {
        this.formula = formula;
        this.dataPoints = new ArrayList();
        setTitle("График функции");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                //Оси
                g2.setColor(Color.BLACK);
                int x0 = (int) ((-minX / (maxX - minX)) * getWidth());
                int y0 = (int) ((maxY / (maxY - minY)) * getHeight());
                g2.drawLine(0, y0, getWidth(), y0);
                g2.drawLine(x0, 0, x0, getHeight());

                dataPoints.clear(); // Очищаем список точек

                FormulaElement result = Interpreter.evaluateFormula(formula);
                for (double x = minX; x <= maxX; x += deltaX) {
                    Interpreter.setVariable(0, x);

                    if (Interpreter.hasError()) {
                        System.out.println("Ошибка в формуле!");
                        break;
                    } else {
                        double y = result.getValue();
                        int xScreen = (int) ((x - minX) / (maxX - minX) * getWidth());
                        int yScreen = (int) ((maxY - y) / (maxY - minY) * getHeight());
                        dataPoints.add(new Point(xScreen, yScreen));
                    }
                }

                g2.setColor(new Color(255, 0, 0));
                for (Point point : dataPoints) {
                    g2.fillRect(point.x, point.y, 2, 2);
                }
            }
        };

        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                isPanning = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPanning = false;
            }
        });

        chartPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isPanning) {
                    int deltaX = e.getX() - lastMouseX;
                    int deltaY = e.getY() - lastMouseY;

                    double xDelta = deltaX * (maxX - minX) / getWidth();
                    double yDelta = deltaY * (maxY - minY) / getHeight();

                    minX -= xDelta;
                    maxX -= xDelta;
                    minY += yDelta;
                    maxY += yDelta;

                    lastMouseX = e.getX();
                    lastMouseY = e.getY();

                    chartPanel.repaint();
                }
            }
        });

        chartPanel.addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation();
            if (rotation > 0) {
                // Уменьшение масштаба
                minX /= zoomFactor;
                maxX /= zoomFactor;
                minY /= zoomFactor;
                maxY /= zoomFactor;
            } else {
                // Увеличение масштаба
                minX *= zoomFactor;
                maxX *= zoomFactor;
                minY *= zoomFactor;
                maxY *= zoomFactor;
            }
            chartPanel.repaint();
        });
        add(chartPanel);
        setVisible(true);
    }
}
