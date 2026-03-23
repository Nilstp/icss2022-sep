grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---

stylesheet: (variableAssignment | stylerule)* EOF;

stylerule: selector OPEN_BRACE (variableAssignment | declaration | ifClause)* CLOSE_BRACE;

selector: tagSelector
        | classSelector
        | idSelector;

tagSelector: LOWER_IDENT;
classSelector: CLASS_IDENT;
idSelector: ID_IDENT;

declaration: property COLON expression SEMICOLON;

property: LOWER_IDENT;

literal: COLOR
       | PIXELSIZE
       | PERCENTAGE
       | SCALAR;

variableAssignment: variableReference ASSIGNMENT_OPERATOR expression SEMICOLON;

expression
    : addExpr;

variableReference: CAPITAL_IDENT;

booleanLiteral: TRUE | FALSE;

addExpr: addExpr(PLUS | MIN) mulExpr
       | mulExpr;

mulExpr: mulExpr MUL atom
       | atom;

atom
    : literal
    | variableReference
    | booleanLiteral;

ifClause
    : IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE
      OPEN_BRACE (declaration | ifClause)* CLOSE_BRACE
      (ELSE OPEN_BRACE (declaration | ifClause)* CLOSE_BRACE)?
    ;