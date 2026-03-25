package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private HashMap<String, ExpressionType> propertyTypes = new HashMap<>() {{
        put("width", ExpressionType.PIXEL);
        put("height", ExpressionType.PIXEL);
        put("color", ExpressionType.COLOR);
        put("background-color", ExpressionType.COLOR);
    }};

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        variableTypes.addFirst(new HashMap<>());

        for(ASTNode node : ast.root.getChildren()) {
            if(node instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) node);
            } else if(node instanceof Stylerule){
                checkStyleRule((Stylerule) node);
            }
        }

        variableTypes.removeFirst();
    }

    private void checkStyleRule(Stylerule stylerule) {
        variableTypes.addFirst(new HashMap<>());

        for(ASTNode node : stylerule.body) {
            checkBodyNode(node);
        }

        variableTypes.removeFirst();
    }

    private void checkBodyNode(ASTNode node){
        if(node instanceof VariableAssignment){
            checkVariableAssignment((VariableAssignment) node);
        } else if(node instanceof Declaration){
            checkDeclaration((Declaration) node);
        } else if (node instanceof IfClause) {
            checkIfClause((IfClause) node);
        }
    }

    private void checkVariableAssignment(VariableAssignment assignment){
        System.out.println("Checking variable assignment: " + assignment.name);
    }

    private void checkDeclaration(Declaration declaration){
        ExpressionType actualType = resolveExpressionType(declaration.expression);
        ExpressionType expectedType = propertyTypes.get(declaration.property.name);

        if (expectedType == null) {
            throw new RuntimeException("Unknown property: " + declaration.property.name);
        } else if (actualType != expectedType) {
            throw new RuntimeException("Type error: expected " + expectedType + " but got " + actualType);
        }
    }

    private void checkIfClause(IfClause ifClause){
        System.out.println("Checking if clause");
    }

    private ExpressionType resolveExpressionType(Expression expression) {
        if (expression instanceof BoolLiteral)       return ExpressionType.BOOL;
        if (expression instanceof ColorLiteral)      return ExpressionType.COLOR;
        if (expression instanceof PixelLiteral)      return ExpressionType.PIXEL;
        if (expression instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expression instanceof ScalarLiteral)     return ExpressionType.SCALAR;
        return ExpressionType.UNDEFINED;
    }
}
