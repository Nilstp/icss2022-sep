package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();

        evaluateStyleSheet(ast.root);
    }

    public void evaluateStyleSheet (Stylesheet stylesheet){
        variableValues.addFirst(new HashMap<>());

        for(ASTNode node: stylesheet.getChildren()){
            if(node instanceof VariableAssignment){
                VariableAssignment var = (VariableAssignment) node;
                Literal value = evaluate(var.expression);
                variableValues.getFirst().put(var.name.name, value);
            } else if(node instanceof Stylerule){

            }
        }

        variableValues.removeFirst();
    }

    private Literal evaluate(Expression expression){
        if(expression instanceof Literal){
            return (Literal) expression;
        }

        if(expression instanceof VariableReference){
            String name = ((VariableReference) expression).name;

            for(int i = 0; i < variableValues.getSize(); i++) {
                HashMap<String, Literal> scope = variableValues.get(i);

                if (scope.containsKey(name)) {
                    return scope.get(name);
                }
            }
        }

        if (expression instanceof Operation) {
            return evaluateOperation((Operation) expression);
        }
        
        return null;
    }

    private Literal evaluateOperation(Operation op) {
        Literal left = evaluate(op.lhs);
        Literal right = evaluate(op.rhs);

        if (op instanceof AddOperation) {
            return add(left, right);
        }

        if (op instanceof SubtractOperation) {
            return subtract(left, right);
        }

        if (op instanceof MultiplyOperation) {
            return multiply(left, right);
        }

        return null;
    }

    private Literal add(Literal l, Literal r) {
        if (l instanceof PixelLiteral && r instanceof PixelLiteral) {
            return new PixelLiteral(
                    ((PixelLiteral) l).value + ((PixelLiteral) r).value
            );
        }

        if (l instanceof PercentageLiteral && r instanceof PercentageLiteral) {
            return new PercentageLiteral(
                    ((PercentageLiteral) l).value + ((PercentageLiteral) r).value
            );
        }

        if (l instanceof ScalarLiteral && r instanceof ScalarLiteral) {
            return new ScalarLiteral(
                    ((ScalarLiteral) l).value + ((ScalarLiteral) r).value
            );
        }

        return null;
    }

    private Literal subtract(Literal l, Literal r) {
        if (l instanceof PixelLiteral && r instanceof PixelLiteral) {
            return new PixelLiteral(
                    ((PixelLiteral) l).value - ((PixelLiteral) r).value
            );
        }

        if (l instanceof PercentageLiteral && r instanceof PercentageLiteral) {
            return new PercentageLiteral(
                    ((PercentageLiteral) l).value - ((PercentageLiteral) r).value
            );
        }

        if (l instanceof ScalarLiteral && r instanceof ScalarLiteral) {
            return new ScalarLiteral(
                    ((ScalarLiteral) l).value - ((ScalarLiteral) r).value
            );
        }

        return null;
    }

    private Literal multiply(Literal l, Literal r) {

        if (l instanceof ScalarLiteral && r instanceof PixelLiteral) {
            return new PixelLiteral(
                    ((ScalarLiteral) l).value * ((PixelLiteral) r).value
            );
        }

        if (l instanceof PixelLiteral && r instanceof ScalarLiteral) {
            return new PixelLiteral(
                    ((PixelLiteral) l).value * ((ScalarLiteral) r).value
            );
        }

        if (l instanceof ScalarLiteral && r instanceof PercentageLiteral) {
            return new PercentageLiteral(
                    ((ScalarLiteral) l).value * ((PercentageLiteral) r).value
            );
        }

        if (l instanceof PercentageLiteral && r instanceof ScalarLiteral) {
            return new PercentageLiteral(
                    ((PercentageLiteral) l).value * ((ScalarLiteral) r).value
            );
        }

        if (l instanceof ScalarLiteral && r instanceof ScalarLiteral) {
            return new ScalarLiteral(
                    ((ScalarLiteral) l).value * ((ScalarLiteral) r).value
            );
        }

        return null;
    }
    
}
