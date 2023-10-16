abstract class VFCalc {//Вычисление факториала
    public static double faculty(double number) {
        if (Double.isInfinite(number) || Double.isNaN(number) || number < 0.0 || number % 1.0 != 0) {
            return Double.NaN;
        }
        double result = 1.0;
        for (int i = 0; i < number; i++) {
            result *= i + 1;
        }
        return result;
    }
}
