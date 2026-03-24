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

OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---

stylesheet: (variable | ruleset)* EOF;

variable: CAPITAL_IDENT ASSIGNMENT_OPERATOR expression SEMICOLON;

ruleset: selector OPEN_BRACE body CLOSE_BRACE;

body: (declaration | ifClause)*;

selector
    : LOWER_IDENT
    | ID_IDENT
    | CLASS_IDENT;

declaration: LOWER_IDENT COLON expression SEMICOLON;

expression
    : expression MUL expression
    |expression PLUS expression
    | expression MIN expression
    | CAPITAL_IDENT
    | COLOR
    | PIXELSIZE
    | PERCENTAGE
    | SCALAR
    | TRUE
    | FALSE
    ;

ifClause
    : IF BOX_BRACKET_OPEN CAPITAL_IDENT BOX_BRACKET_CLOSE
      OPEN_BRACE body CLOSE_BRACE
      elseClause?
    ;

elseClause: ELSE OPEN_BRACE body CLOSE_BRACE;