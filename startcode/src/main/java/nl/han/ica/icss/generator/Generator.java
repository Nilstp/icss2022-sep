package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator {

	public String generate(AST ast) {
		StringBuilder sb = new StringBuilder();
		generateStylesheet(ast.root, sb, 0);
		return sb.toString();
	}

	private void generateStylesheet(Stylesheet stylesheet, StringBuilder sb, int indent) {
		for (ASTNode node : stylesheet.getChildren()) {
			if (node instanceof Stylerule) {
				generateStylerule((Stylerule) node, sb, indent);
			}
		}
	}

	private void generateStylerule(Stylerule rule, StringBuilder sb, int indent) {
		indent(sb, indent);
		sb.append(rule.selectors.toString()).append(" {\n");

		for (ASTNode node : rule.body) {
			if (node instanceof Declaration) {
				generateDeclaration((Declaration) node, sb, indent + 1);
			}
		}

		indent(sb, indent);
		sb.append("}\n");
	}

	private void generateDeclaration(Declaration decl, StringBuilder sb, int indent) {
		indent(sb, indent);
		sb.append(decl.property.name)
				.append(": ")
				.append(literalToString((Literal) decl.expression))
				.append(";\n");
	}

	private String literalToString(Literal literal) {
		if (literal instanceof PixelLiteral) {
			return ((PixelLiteral) literal).value + "px";
		} else if (literal instanceof PercentageLiteral) {
			return ((PercentageLiteral) literal).value + "%";
		} else if (literal instanceof ScalarLiteral) {
			return Integer.toString(((ScalarLiteral) literal).value);
		} else if (literal instanceof ColorLiteral) {
			return ((ColorLiteral) literal).value;
		} else if (literal instanceof BoolLiteral) {
			return Boolean.toString(((BoolLiteral) literal).value);
		}

		return "";
	}

	private void indent(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++) {
			sb.append("  ");
		}
	}
}
