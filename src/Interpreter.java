import java.util.Arrays;

public abstract class Interpreter {

    private static String variableNames = "xabcdfgyz";
    private static double[] variableValues = new double[9];
    private static String currentOperator;
    private static int currentPosition, bracketCount, level, index;
    private static double result;
    private static boolean hasError = false;

    private static boolean isVariable(String operator, int[] ind) {//Проверка является ли переменной из variableNames
        int i = variableNames.indexOf(operator);
        if (i != -1) {
            ind[0] = i;
            return true;
        } else {
            ind[0] = -1;
            return false;
        }
    }

    private static int findClosingBracketPosition(String operator, int start) {
        int bracketCount = 0;
        for (int i = start; i < operator.length(); i++) {
            char c = operator.charAt(i);
            if (c == '(') {
                bracketCount++;
            } else if (c == ')') {
                bracketCount--;
            }
            if (bracketCount == 0) {
                return i;
            }
        }
        return -1; // Если не найдена закрывающая скобка
    }

    private static FormulaElement parseExpression(String operator) {
        if (operator.length() == 0) {//Проверка на пустую строку
            handleError();
        }
        //Проверяем начинается ли с "(" и заканчивается на ")", если так, то отбрасываем скобки
        if (operator.startsWith("(") && findClosingBracketPosition(operator, 0) == operator.length() - 1) {
            operator = operator.substring(1, operator.length() - 1);
        }
        try {
            result = Double.parseDouble(operator);
            return new FormulaNumberElement(result);//Проверка на число
        } catch (NumberFormatException ignored) {
        }

        if (operator.equals("PI")) {
            return new FormulaNumberElement(Math.PI);
        } else if (operator.equals("E")) {          //Проверка на константы
            return new FormulaNumberElement(Math.E);
        }

        int[] ind = new int[1]; //Массив для хранения индекса переменной

        if (operator.length() == 1 && isVariable(operator, ind)) { //Если переменная, то создаётся объект этой переменной
            return new FormulaVariableElement(ind[0]);
        }

        if (operator.length() > 4 && operator.charAt(3) == '(') {//Проверка на унарные операции
            currentPosition = findClosingBracketPosition(operator, 3);
            if (currentPosition != operator.length() - 1) {
                gotoNext();
            }
            currentOperator = operator.substring(4, currentPosition);
            switch (operator.substring(0, 3)) { //Унарные операции
                case "sqr": return new FormulaUnaryElement(parseExpression(currentOperator), Math::sqrt);
                case "cbr": return new FormulaUnaryElement(parseExpression(currentOperator), Math::cbrt);
                case "sin": return new FormulaUnaryElement(parseExpression(currentOperator), Math::sin);
                case "cos": return new FormulaUnaryElement(parseExpression(currentOperator), Math::cos);
                case "tan": return new FormulaUnaryElement(parseExpression(currentOperator), Math::tan);
                case "log": return new FormulaUnaryElement(parseExpression(currentOperator), Math::log10);
                case "abs": return new FormulaUnaryElement(parseExpression(currentOperator), Math::abs);
                case "fac": return new FormulaUnaryElement(parseExpression(currentOperator), VFCalc::faculty);
            }
        }

        gotoNext();

        for (int i = operator.length() - 1; i > -1; i--) {//Проверка на бинарные операции
            switch (operator.substring(i, i + 1)) {
                case "(": bracketCount++; break;
                case ")": bracketCount--; break;
                case "+":
                    if (bracketCount == 0 && level > 0) {
                        currentPosition = i;
                        level = 0;//Приоритет операций
                    }
                    break;
                case "-":
                    if (bracketCount == 0 && level > 1) {
                        currentPosition = i;
                        level = 1;
                    }
                    break;
                case "*":
                    if (bracketCount == 0 && level > 2) {
                        currentPosition = i;
                        level = 2;
                    }
                    break;
                case "%":
                    if (bracketCount == 0 && level > 3) {
                        currentPosition = i;
                        level = 3;
                    }
                    break;
                case "/":
                    if (bracketCount == 0 && level > 4) {
                        currentPosition = i;
                        level = 4;
                    }
                    break;
                case "^":
                    if (bracketCount == 0 && level > 5) {
                        currentPosition = i;
                        level = 5;
                    }
                    break;
            }
        }
        //В currentPosition находится позиция операции с нужным уровнем приоритета и без скобок вокруг нее
        if (currentPosition == 0 || currentPosition == operator.length() - 1) {
            handleError();
        }

        currentOperator = operator.substring(currentPosition, currentPosition + 1);
        String operand1 = operator.substring(0, currentPosition);
        String operand2 = operator.substring(currentPosition + 1);

        switch (currentOperator) { //Бинарные операции
            case "+": return new FormulaBinaryElement(parseExpression(operand1), parseExpression(operand2), (v1, v2) -> v1 + v2);
            case "-": return new FormulaBinaryElement(parseExpression(operand1), parseExpression(operand2), (v1, v2) -> v1 - v2);
            case "*": return new FormulaBinaryElement(parseExpression(operand1), parseExpression(operand2), (v1, v2) -> v1 * v2);
            case "/": return new FormulaBinaryElement(parseExpression(operand1), parseExpression(operand2), (v1, v2) -> v1 / v2);
            case "%": return new FormulaBinaryElement(parseExpression(operand1), parseExpression(operand2), Math::IEEEremainder);
            case "^": return new FormulaBinaryElement(parseExpression(operand1), parseExpression(operand2), Math::pow);
            default: handleError();
        }

        return new FormulaNumberElement(0.0);
    }

    public static FormulaElement evaluateFormula(String formula) {//Принимает формулу и парсит её
        if (formula.isEmpty()) {
            hasError = true;
            return new FormulaNumberElement(0.0);
        }
        hasError = false;
        return parseExpression(formula);
    }

    public static boolean hasError() {
        return hasError;
    }

    public static double getVariable(int index) {
        return (index > -1 && index < 10) ? variableValues[index] : 0.0;
    }

    public static void setVariable(int index, double value) {
        if (index > -1 && index < 10) {
            variableValues[index] = value;
        }
    }

    private static void handleError() {
        hasError = true;
    }//Наличие ошибки, тогда true

    private static void gotoNext() {//Сброс текущей позиции и уровня операций
        currentPosition = 0;
        level = 6;
        bracketCount = 0;
    }
}

//Позволяют создавать лямбда-выражения, которые определяют, как выполнять двоичную или унарную операцию над значениями
@FunctionalInterface
interface BinaryOperation {
    double apply(double value1, double value2);
}

@FunctionalInterface
interface UnaryOperation {
    double apply(double value);
}

