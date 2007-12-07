grammar SimPEL;

options {
    output=AST; 
    language=Java;
    ASTLabelType=LinkedListTree;
}
tokens {
    PROCESS; PICK; SEQUENCE; FLOW; IF; ELSEIF; ELSE; WHILE; UNTIL; FOREACH; FORALL; INVOKE;
    RECEIVE; REPLY; ASSIGN; THROW; WAIT; EXIT; TIMEOUT; TRY; CATCH; CATCH_ALL; SCOPE; EVENT;
    ALARM; COMPENSATION; COMPENSATE; CORRELATION; CORR_MAP;
    EXPR; EXT_EXPR; XML_LITERAL;
}
@parser::header {
package org.apache.ode.simpel.antlr;

import uk.co.badgersinfoil.e4x.antlr.LinkedListTokenStream;
import uk.co.badgersinfoil.e4x.antlr.LinkedListTree;
import uk.co.badgersinfoil.e4x.E4XHelper;
import org.apache.ode.simpel.ErrorListener;
import org.apache.ode.simpel.util.JSHelper;
}
@lexer::header {
package org.apache.ode.simpel.antlr;
import org.apache.ode.simpel.ErrorListener;
}

@lexer::members {
    private ErrorListener el;

    public void setErrorListener(ErrorListener el) {
    	this.el = el;
    }
    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    	el.reportRecognitionError(tokenNames, e.line, getErrorMessage(e, tokenNames), e);
    }
}

@parser::members {
    public static final int CHANNEL_PLACEHOLDER = 999;

    private SimPELLexer lexer;
    private CharStream cs;
    private ErrorListener el;
    
    public void setInput(SimPELLexer lexer, CharStream cs) {
        this.lexer = lexer;
        this.cs = cs;
    }
    public void setErrorListener(ErrorListener el) {
    	this.el = el;
    }

    /** Handle 'island grammar' for embeded XML-literal elements. */
    private LinkedListTree parseXMLLiteral() throws RecognitionException {
        return E4XHelper.parseXMLLiteral(lexer, cs, (LinkedListTokenStream)input);
    }
    /** Handle 'island grammar' for embeded JavaScript-literal elements. */
    private LinkedListTree parseJSLiteral() throws RecognitionException {
        return JSHelper.parseJSLiteral(lexer, cs, (LinkedListTokenStream)input);
    }
    
    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    	el.reportRecognitionError(tokenNames, e.line, getErrorMessage(e, tokenNames), e);
    }
}

// MAIN BPEL SYNTAX

program	:	declaration+;
declaration
	:	funct | process;

// Process
process	:	'process' ns_id block -> ^(PROCESS ns_id block);

block	:	'{' process_stmt '}' -> ^(SEQUENCE process_stmt);

process_stmt
	:	(pick | flow | if_ex | while_ex | until_ex | foreach | forall | try_ex | scope_ex
		| invoke | receive | reply | assign | throw_ex | wait_ex |  exit)+;
		
// Structured activities
pick	:	'pick' '{' receive* timeout* '}' -> ^(PICK receive* timeout*);
timeout	:	'timeout' '(' expr ')' block -> ^(TIMEOUT expr block); 

// TODO links
flow	:	'parrallel' '{' exprs+=process_stmt '}' ('and' '{' exprs+=process_stmt '}')* 
		-> ^(FLOW $exprs);

if_ex	:	'if' '(' expr ')' block
		('else if' '(' expr ')' block)?
		('else' block)? -> ^(IF expr block ^(ELSEIF expr block)? ^(ELSE expr block)?);

while_ex:	'while' '(' expr ')' block -> ^(WHILE expr block);

until_ex:	'do' block 'until' '(' expr ')' -> ^(UNTIL expr block);

foreach	:	'for' '(' ID '=' init=expr ';' cond=expr ';' assign ')' block -> ^(FOREACH ID $init $cond assign block);
forall	:	'forall' '(' ID '=' from=expr 'to' to=expr ')' block -> ^(FORALL ID $from $to block);

try_ex	:	'try' tb=block catch_ex* ('catch' '(' ID ')' cb=block)? -> ^(TRY $tb catch_ex* ^(CATCH_ALL ID $cb)?);
		
catch_ex:	'catch' '(' ns_id ID ')' block -> ^(CATCH ns_id ID block);

scope_ex:	'scope' ('(' ID ')')? block scope_stmt* -> ^(SCOPE ID? block scope_stmt*);
scope_stmt
	:	event | alarm | compensation;

event	:	'event' '(' p=ID ',' o=ID ',' m=ID ')' block -> ^(EVENT $p $o $m block);
alarm	:	'alarm' '(' expr ')' block -> ^(ALARM expr block);
compensation
	:	'compensation' block -> ^(COMPENSATION block);

// Simple activities
invoke	:	'invoke' '(' p=ID ',' o=ID (',' in=ID)? ')' -> ^(INVOKE $p $o $in?);

receive	:	'receive' '(' p=ID ',' o=ID (',' m=ID)? (',' correlation)? ')' block? -> ^(RECEIVE $p $o $m? correlation? block?);

reply	:	'reply' '(' ID ')' -> ^(REPLY ID);

assign	:	ID '=' rvalue -> ^(ASSIGN ID rvalue);
rvalue
	:	 receive | invoke | expr | xml_literal;
	
throw_ex:	'throw' '('ID')' -> ^(THROW ID);

wait_ex	:	'wait' '('expr')' -> ^(WAIT expr);

compensate
	:	'compensate' ('(' ID ')')? -> ^(COMPENSATE ID?);

exit	:	'exit' -> ^(EXIT);


// Others
// TODO This will not work for any function whose code contains braces
correlation
	:	'{' corr_mapping (',' corr_mapping)* '}' -> ^(CORRELATION corr_mapping*);
corr_mapping
	:	f1=ID ':' f2=ID '(' v=ID ')' -> ^(CORR_MAP $f1 $f2 $v);

funct	:	'function'^ f=ID '(' ID? (','! ID)* ')' js_block;

// Expressions
expr	:	s_expr | EXT_EXPR;

s_expr	:	condExpr;
condExpr:	aexpr ( ('==' ^|'!=' ^|'<' ^|'>' ^|'<=' ^|'>=' ^) aexpr )?;
aexpr	:	mexpr (('+'|'-') ^ mexpr)*;
mexpr	:	atom (('*'|'/') ^ atom)* | STRING;
atom	:	ID | INT | '(' s_expr ')' -> s_expr;

ns_id	:	(ID '::')? ID;

// In-line XML

xml_literal
@init { LinkedListTree xml = null; }
	:	// We have to have the LT in the outer grammar for lookahead
		// in AS3Parser to be able to predict that the xmlLiteral rule
		// should be used.
		'<' { xml=parseXMLLiteral(); } -> { xml };

js_block
@init { LinkedListTree js = null; }
	:	'{' { js=parseJSLiteral(); } -> { js };

EXT_EXPR
	:	'[' (options {greedy=false;} : .)* ']';

// Basic tokens
ID	:	(LETTER | '_' ) (LETTER | DIGIT | '_' )*;
INT	:	(DIGIT )+ ;
STRING	:	'"' ( ESCAPE_SEQ | ~('\\'|'"') )* '"';
ESCAPE_SEQ
	:	'\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\');

SL_COMMENTS
	:	('#'|'//') .* CR { $channel = HIDDEN; };
CR	:	('\r' | '\n' )+ { $channel = HIDDEN; };
WS	:	( ' ' | '\t' )+ { skip(); };
fragment DIGIT
    :    '0'..'9';
fragment LETTER
    : 'a'..'z' | 'A'..'Z';
