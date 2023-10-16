public class FormulaBinaryElement extends FormulaElement {//Элемент формулы с двоичной операцией(сложение, вычитание, умножение, деление)
    private FormulaElement operand1, operand2;
    private BinaryOperation deleg;//Определяет какая операция выполняется

    public FormulaBinaryElement(FormulaElement operand1, FormulaElement operand2, BinaryOperation deleg) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.deleg = deleg;
    }

    @Override
    public double getValue() {
        return deleg.apply(operand1.getValue(), operand2.getValue());
    }
}