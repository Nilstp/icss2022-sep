// Generated from C:/Users/nilst/Documents/GitHub/Compilerbouw/startcode/src/main/antlr4/nl/han/ica/icss/parser/ICSS.g4 by ANTLR 4.13.2
package nl.han.ica.icss.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ICSSParser}.
 */
public interface ICSSListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ICSSParser#stylesheet}.
	 * @param ctx the parse tree
	 */
	void enterStylesheet(ICSSParser.StylesheetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#stylesheet}.
	 * @param ctx the parse tree
	 */
	void exitStylesheet(ICSSParser.StylesheetContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(ICSSParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(ICSSParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#stylerule}.
	 * @param ctx the parse tree
	 */
	void enterStylerule(ICSSParser.StyleruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#stylerule}.
	 * @param ctx the parse tree
	 */
	void exitStylerule(ICSSParser.StyleruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#body}.
	 * @param ctx the parse tree
	 */
	void enterBody(ICSSParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#body}.
	 * @param ctx the parse tree
	 */
	void exitBody(ICSSParser.BodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#selector}.
	 * @param ctx the parse tree
	 */
	void enterSelector(ICSSParser.SelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#selector}.
	 * @param ctx the parse tree
	 */
	void exitSelector(ICSSParser.SelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(ICSSParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(ICSSParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OperationExpression}
	 * labeled alternative in {@link ICSSParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterOperationExpression(ICSSParser.OperationExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OperationExpression}
	 * labeled alternative in {@link ICSSParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitOperationExpression(ICSSParser.OperationExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralExpression}
	 * labeled alternative in {@link ICSSParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpression(ICSSParser.LiteralExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralExpression}
	 * labeled alternative in {@link ICSSParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpression(ICSSParser.LiteralExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VariableExpression}
	 * labeled alternative in {@link ICSSParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterVariableExpression(ICSSParser.VariableExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VariableExpression}
	 * labeled alternative in {@link ICSSParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitVariableExpression(ICSSParser.VariableExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#ifClause}.
	 * @param ctx the parse tree
	 */
	void enterIfClause(ICSSParser.IfClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#ifClause}.
	 * @param ctx the parse tree
	 */
	void exitIfClause(ICSSParser.IfClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#elseClause}.
	 * @param ctx the parse tree
	 */
	void enterElseClause(ICSSParser.ElseClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#elseClause}.
	 * @param ctx the parse tree
	 */
	void exitElseClause(ICSSParser.ElseClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(ICSSParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(ICSSParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(ICSSParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(ICSSParser.LiteralContext ctx);
}