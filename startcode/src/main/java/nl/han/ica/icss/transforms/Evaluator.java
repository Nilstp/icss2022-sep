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
        return null;
    }
    
}
