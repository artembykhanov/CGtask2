public class FormulaNumberElement extends FormulaElement {//Элемент формулы с числовым значением(PI, можно и другие задать)
    private double value;

    public FormulaNumberElement(double value) {
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }
}