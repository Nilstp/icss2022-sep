package nl.han.ica.icss.parser;

import java.util.Objects;
import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.push(new Stylesheet());
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet root = (Stylesheet) currentContainer.pop();
		ast.setRoot(root);
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		currentContainer.push(new Stylerule());
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	@Override
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
		currentContainer.push(new TagSelector(ctx.getText()));
	}

	@Override
	public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
		TagSelector selector = (TagSelector) currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
		currentContainer.push(new ClassSelector(ctx.getText()));
	}

	@Override
	public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
		ClassSelector selector = (ClassSelector) currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
		currentContainer.push(new IdSelector(ctx.getText()));
	}

	@Override
	public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
		IdSelector selector = (IdSelector) currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		String property = ctx.property().getText();
		currentContainer.push(new Declaration(property));
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		ASTNode expression = currentContainer.pop(); // <-- get expression first
		Declaration declaration = (Declaration) currentContainer.pop();

		declaration.addChild(expression);
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void exitLiteral(ICSSParser.LiteralContext ctx) {
		String text = ctx.getText();

		ASTNode node;

		if (text.matches("#[0-9a-fA-F]{6}")) {
			node = new ColorLiteral(text);
		} else if (text.endsWith("px")) {
			int value = Integer.parseInt(text.replace("px", ""));
			node = new PixelLiteral(value);
		} else if (text.endsWith("%")) {
			int value = Integer.parseInt(text.replace("%", ""));
			node = new PercentageLiteral(value);
		} else if (text.matches("[0-9]+")) {
			int value = Integer.parseInt(text);
			node = new ScalarLiteral(value);
		} else {
			throw new RuntimeException("Unknown literal: " + text);
		}

		currentContainer.push(node);
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		currentContainer.push(new VariableAssignment());
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		ASTNode expression = currentContainer.pop();
		VariableReference varRef = (VariableReference) currentContainer.pop();

		VariableAssignment var = (VariableAssignment) currentContainer.pop();
		var.addChild(varRef);
		var.addChild(expression);

		currentContainer.peek().addChild(var);
	}

	@Override
	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
		VariableReference ref = new VariableReference(ctx.getText());
		currentContainer.push(ref);
	}

	@Override
	public void exitBooleanLiteral(ICSSParser.BooleanLiteralContext ctx) {
		boolean value = ctx.getText().equals("TRUE");
		currentContainer.push(new BoolLiteral(value));
	}

	@Override
	public void exitAddExpr(ICSSParser.AddExprContext ctx) {
		if (ctx.getChildCount() == 3) {
			ASTNode right = currentContainer.pop();
			ASTNode left = currentContainer.pop();

			Operation op;
			if (ctx.PLUS() != null) {
				op = new AddOperation();
			} else {
				op = new SubtractOperation();
			}

			op.addChild(left);
			op.addChild(right);

			currentContainer.push(op);
		}
	}

	@Override
	public void exitMulExpr(ICSSParser.MulExprContext ctx) {
		if (ctx.getChildCount() == 3) {
			ASTNode right = currentContainer.pop();
			ASTNode left = currentContainer.pop();

			MultiplyOperation op = new MultiplyOperation();
			op.addChild(left);
			op.addChild(right);

			currentContainer.push(op);
		}
	}

	@Override
	public void exitAtom(ICSSParser.AtomContext ctx) {
		if (ctx.literal() != null) {
			// Already handled below
		} else if (ctx.variableReference() != null) {
			// Already handled below
		} else if (ctx.booleanLiteral() != null) {
			// Already handled below
		}
	}

	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		currentContainer.push(new IfClause());
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause ifClause = (IfClause) currentContainer.pop();
		currentContainer.peek().addChild(ifClause);
	}

	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		currentContainer.push(new ElseClause());
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		ElseClause elseClause = (ElseClause) currentContainer.pop();
		currentContainer.peek().addChild(elseClause); // IfClause.addChild handles ElseClause
	}
}