public class FormulaUnaryElement extends FormulaElement {//Элемент формулы с унарной операцией(sin,cos,tan,log10 и тд)
    private FormulaElement element;
    private UnaryOperation deleg;//Определяет какая операция выполняется

    public FormulaUnaryElement(FormulaElement element, UnaryOperation deleg) {
        this.element = element;
        this.deleg = deleg;
    }

    @Override
    public double getValue() {
        return deleg.apply(element.getValue());
    }
}