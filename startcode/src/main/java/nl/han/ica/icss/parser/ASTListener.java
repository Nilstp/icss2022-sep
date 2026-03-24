package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.selectors.*;

import java.util.ArrayList;

public class ASTListener extends ICSSBaseListener {

	private AST ast;
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

	public AST getAST() {
		return ast;
	}
	
// ─── STYLESHEET ───────────────────────────────────────────────────────────

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.push(new Stylesheet());
		currentContainer.push(new Sentinel("stylesheet"));
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		ArrayList<ASTNode> children = new ArrayList<>();
		while (!(currentContainer.peek() instanceof Sentinel)) {
			children.add(0, currentContainer.pop());
		}
		currentContainer.pop(); // pop sentinel
		Stylesheet root = (Stylesheet) currentContainer.pop();
		for (ASTNode child : children) root.addChild(child);
		ast.setRoot(root);
	}

// ─── VARIABLE ASSIGNMENT ──────────────────────────────────────────────────

	@Override
	public void enterVariable(ICSSParser.VariableContext ctx) {
		currentContainer.push(new VariableAssignment());
		currentContainer.push(new VariableReference(ctx.CAPITAL_IDENT().getText()));
	}

	@Override
	public void exitVariable(ICSSParser.VariableContext ctx) {
		ASTNode expression = currentContainer.pop();
		VariableReference varRef = (VariableReference) currentContainer.pop();
		VariableAssignment var = (VariableAssignment) currentContainer.pop();
		var.addChild(varRef);
		var.addChild(expression);
		currentContainer.push(var);
	}

// ─── RULESET ──────────────────────────────────────────────────────────────

	@Override
	public void enterRuleset(ICSSParser.RulesetContext ctx) {
		Stylerule stylerule = new Stylerule();
		String selectorText = ctx.selector().getText();
		if (selectorText.startsWith("#")) {
			stylerule.addChild(new IdSelector(selectorText));
		} else if (selectorText.startsWith(".")) {
			stylerule.addChild(new ClassSelector(selectorText));
		} else {
			stylerule.addChild(new TagSelector(selectorText));
		}
		currentContainer.push(stylerule);
		currentContainer.push(new Sentinel("ruleset"));
	}

	@Override
	public void exitRuleset(ICSSParser.RulesetContext ctx) {
		ArrayList<ASTNode> children = new ArrayList<>();
		while (!(currentContainer.peek() instanceof Sentinel)) {
			children.add(0, currentContainer.pop());
		}
		currentContainer.pop(); // pop sentinel
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		for (ASTNode child : children) stylerule.addChild(child);
		currentContainer.push(stylerule);
	}

// ─── DECLARATION ─────────────────────────────────────────────────────────

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.push(new Declaration(ctx.LOWER_IDENT().getText()));
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		ASTNode expression = currentContainer.pop();
		Declaration declaration = (Declaration) currentContainer.pop();
		declaration.addChild(expression);
		currentContainer.push(declaration);
	}

// ─── IF CLAUSE ────────────────────────────────────────────────────────────

	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause ifClause = new IfClause();
		ifClause.addChild(new VariableReference(ctx.CAPITAL_IDENT().getText()));
		currentContainer.push(ifClause);
		currentContainer.push(new Sentinel("ifClause"));
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		ArrayList<ASTNode> children = new ArrayList<>();
		while (!(currentContainer.peek() instanceof Sentinel)) {
			children.add(0, currentContainer.pop());
		}
		currentContainer.pop(); // pop sentinel
		IfClause ifClause = (IfClause) currentContainer.pop();
		for (ASTNode child : children) ifClause.addChild(child);
		currentContainer.push(ifClause);
	}

// ─── ELSE CLAUSE ─────────────────────────────────────────────────────────

	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		currentContainer.push(new ElseClause());
		currentContainer.push(new Sentinel("elseClause"));
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		ArrayList<ASTNode> children = new ArrayList<>();
		while (!(currentContainer.peek() instanceof Sentinel)) {
			children.add(0, currentContainer.pop());
		}
		currentContainer.pop(); // pop sentinel
		ElseClause elseClause = (ElseClause) currentContainer.pop();
		for (ASTNode child : children) elseClause.addChild(child);
		currentContainer.push(elseClause);
	}

// ─── EXPRESSION ──────────────────────────────────────────────────────────

	@Override
	public void exitExpression(ICSSParser.ExpressionContext ctx) {
		if (ctx.getChildCount() == 3 && ctx.expression().size() == 2) {
			ASTNode right = currentContainer.pop();
			ASTNode left = currentContainer.pop();
			Operation op;
			if (ctx.PLUS() != null) {
				op = new AddOperation();
			} else if (ctx.MIN() != null) {
				op = new SubtractOperation();
			} else {
				op = new MultiplyOperation();
			}
			op.addChild(left);
			op.addChild(right);
			currentContainer.push(op);
			return;
		}

		String text = ctx.getText();
		if (ctx.COLOR() != null) {
			currentContainer.push(new ColorLiteral(text));
		} else if (ctx.PIXELSIZE() != null) {
			currentContainer.push(new PixelLiteral(text));
		} else if (ctx.PERCENTAGE() != null) {
			currentContainer.push(new PercentageLiteral(text));
		} else if (ctx.SCALAR() != null) {
			currentContainer.push(new ScalarLiteral(Integer.parseInt(text)));
		} else if (ctx.TRUE() != null) {
			currentContainer.push(new BoolLiteral(true));
		} else if (ctx.FALSE() != null) {
			currentContainer.push(new BoolLiteral(false));
		} else if (ctx.CAPITAL_IDENT() != null) {
			currentContainer.push(new VariableReference(text));
		}
	}

// ─── SENTINEL MARKER ─────────────────────────────────────────────────────

	private static class Sentinel extends ASTNode {
		private final String name;

		Sentinel(String name) {
			this.name = name;
		}

		@Override
		public String getNodeLabel() {
			return "SENTINEL:" + name;
		}
	}
}