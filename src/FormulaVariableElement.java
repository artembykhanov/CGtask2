public class FormulaVariableElement extends FormulaElement {//Элемент формулы с переменной
    private int index;//Указывает на переменную из массива variableNames

    public FormulaVariableElement(int index) {
        this.index = index;
    }

    @Override
    public double getValue() {
        return Interpreter.getVariable(index);
    }
}