package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private HashMap<String, IHANLinkedList<ExpressionType>> propertyTypes = new HashMap<>() {{
        put("width", new HANLinkedList<>() {{
            addFirst(ExpressionType.PIXEL);
            addFirst(ExpressionType.PERCENTAGE);
            addFirst(ExpressionType.SCALAR);
        }});
        put("height", new HANLinkedList<>() {{
            addFirst(ExpressionType.PIXEL);
            addFirst(ExpressionType.PERCENTAGE);
            addFirst(ExpressionType.SCALAR);
        }});
        put("color", new HANLinkedList<>() {{
            addFirst(ExpressionType.COLOR);
        }});
        put("background-color", new HANLinkedList<>() {{
            addFirst(ExpressionType.COLOR);
        }});
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
        ExpressionType type = resolveExpressionType(assignment.expression);

        variableTypes.getFirst().put(assignment.name.name, type);
    }

    private void checkDeclaration(Declaration declaration){
        ExpressionType actualType = resolveExpressionType(declaration.expression);
        IHANLinkedList<ExpressionType> expectedTypes = propertyTypes.get(declaration.property.name);

        if (expectedTypes == null) {
            declaration.setError("Unknown property: " + declaration.property.name);
        } else {
            boolean match = false;

            for (int i = 0; i < expectedTypes.getSize(); i++) {
                if (expectedTypes.get(i) == actualType) {
                    match = true;
                    break;
                }
            }

            if (!match) {
                declaration.setError("Type error: got " + actualType);
            }
        }
    }

    private ExpressionType resolveOperation(Operation op) {
        ExpressionType left = resolveExpressionType(op.lhs);
        ExpressionType right = resolveExpressionType(op.rhs);

        if (left == ExpressionType.COLOR || right == ExpressionType.COLOR) {
            op.setError("Color not allowed in operations");
            return ExpressionType.UNDEFINED;
        }

        if (op instanceof AddOperation || op instanceof SubtractOperation) {
            if (left != right) {
                op.setError("Operands must match");
                return ExpressionType.UNDEFINED;
            }
            return left;
        }

        if (op instanceof MultiplyOperation) {
            if (left == ExpressionType.SCALAR) return right;
            if (right == ExpressionType.SCALAR) return left;

            op.setError("One operand must be scalar");
            return ExpressionType.UNDEFINED;
        }

        return ExpressionType.UNDEFINED;
    }

    private void checkIfClause(IfClause ifClause){
        if(resolveExpressionType(ifClause.getConditionalExpression()) != ExpressionType.BOOL){
            ifClause.setError("Condition must be of type bool");
        }

        if(ifClause.getChildren() != null){
            variableTypes.addFirst(new HashMap<>());

            for(ASTNode node : ifClause.getChildren()){
                checkBodyNode(node);
            }

            variableTypes.removeFirst();
        }

        if(ifClause.getElseClause() != null){
            variableTypes.addFirst(new HashMap<>());

            for(ASTNode node : ifClause.getElseClause().body){
                checkBodyNode(node);
            }

            variableTypes.removeFirst();
        }
    }

    private ExpressionType resolveExpressionType(Expression expression) {
        if (expression instanceof BoolLiteral)       return ExpressionType.BOOL;
        if (expression instanceof ColorLiteral)      return ExpressionType.COLOR;
        if (expression instanceof PixelLiteral)      return ExpressionType.PIXEL;
        if (expression instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expression instanceof ScalarLiteral)     return ExpressionType.SCALAR;
        if (expression instanceof Operation) {
            return resolveOperation((Operation) expression);
        }
        if (expression instanceof VariableReference) {
            String name = ((VariableReference) expression).name;

            for (int i = 0; i < variableTypes.getSize(); i++) {
                HashMap<String, ExpressionType> scope = variableTypes.get(i);

                if (scope.containsKey(name)) {
                    return scope.get(name);
                }
            }

            expression.setError("Undefined variable: " + name);
            return ExpressionType.UNDEFINED;
        }
        return ExpressionType.UNDEFINED;
    }
}
