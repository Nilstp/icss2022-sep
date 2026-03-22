package nl.han.ica.icss.parser;

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
		Declaration declaration = (Declaration) currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void enterLiteral(ICSSParser.LiteralContext ctx) {
		String text = ctx.getText();

		if (text.matches("#[0-9a-fA-F]{6}")) {
			currentContainer.push(new ColorLiteral(text));
		} else if (text.endsWith("px")) {
			int value = Integer.parseInt(text.replace("px", ""));
			currentContainer.push(new PixelLiteral(value));
		} else if (text.endsWith("%")) {
			int value = Integer.parseInt(text.replace("%", ""));
			currentContainer.push(new PercentageLiteral(value));
		}
	}

	@Override
	public void exitLiteral(ICSSParser.LiteralContext ctx) {
		ASTNode literal = currentContainer.pop();
		currentContainer.peek().addChild(literal);
	}
}