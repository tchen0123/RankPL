package com.tr.rp.core;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import com.tr.rp.expressions.bool.And;
import com.tr.rp.expressions.bool.BoolExpression;
import com.tr.rp.expressions.bool.Equals;
import com.tr.rp.expressions.bool.False;
import com.tr.rp.expressions.bool.LessOrEq;
import com.tr.rp.expressions.bool.LessThan;
import com.tr.rp.expressions.bool.Not;
import com.tr.rp.expressions.bool.Or;
import com.tr.rp.expressions.bool.True;
import com.tr.rp.expressions.bool.Xor;
import com.tr.rp.expressions.num.Divide;
import com.tr.rp.expressions.num.IntLiteral;
import com.tr.rp.expressions.num.Minus;
import com.tr.rp.expressions.num.NumExpression;
import com.tr.rp.expressions.num.Plus;
import com.tr.rp.expressions.num.Times;
import com.tr.rp.parser.DefProgLexer;
import com.tr.rp.parser.DefProgParser;
import com.tr.rp.parser.DefProgParser.BoolexprContext;
import com.tr.rp.parser.DefProgParser.NumexprContext;
import com.tr.rp.statement.Assign;
import com.tr.rp.statement.Choose;
import com.tr.rp.statement.Composition;
import com.tr.rp.statement.IfElse;
import com.tr.rp.statement.Observe;
import com.tr.rp.statement.RPLBaseTest;

public class ParseTest extends RPLBaseTest {

	public void testParseVariable() {
		String program = "x := 0;";
		assert(parseStatement(program).equals(new Assign("x",0)));
		program = "x1 := 0;";
		DStatement s = (DStatement) parseStatement(program);
		assert(s.equals(new Assign("x1",0)));
	}
	
	public void testParseAssign() {
		String program = "x := 20;";
		assert(parseStatement(program).equals(new Assign("x",20)));
	}

	public void testParseComposition() {
		String program = "x := 20; x := 30;";
		assert(parseStatement(program).equals(new Composition(
				new Assign("x",20),
				new Assign("x",30))));
	}

	public void testParseIfElse() {
		String program = "IF (x == 10) THEN x := 20 ELSE x := 30;";
		assert(parseStatement(program).equals(new IfElse(
				new Equals("x",10),
				new Assign("x",20),
				new Assign("x",30))));
	}

	public void testParseAssignChoose() {
		String program = "x := 0 << 5 >> 20;";
		assert(parseStatement(program).equals(new Choose(
				new Assign("x",0), new Assign("x",20), 5)));
	}

	public void testParseChoose() {
		String program = "{ x := 0 } << 5 >> { x := 20 };";
		assert(parseStatement(program).equals(new Choose(
				new Assign("x",0), new Assign("x",20), 5)));
	}

	public void testParseObserve() {
		String program = "OBSERVE (x == 10);";
		assert(parseStatement(program).equals(new Observe(new Equals("x", 10))));
	}
	
	public void testParseBoolExpr() {
		assert(parseBoolExpr("((a == 0) & (b == 1))")).equals(new And(new Equals("a", 0), new Equals("b", 1)));
		assert(parseBoolExpr("(a == 0) | b == 1")).equals(new Or(new Equals("a", 0), new Equals("b", 1)));
		assert(parseBoolExpr("a == 0 ^ (b == 1)")).equals(new Xor(new Equals("a", 0), new Equals("b", 1)));
		assert(parseBoolExpr("a == 0").equals(new Equals("a", 0)));
		assert(parseBoolExpr("a != 0").equals(new Not(new Equals("a", 0))));
		assert(parseBoolExpr("a <= 0").equals(new LessOrEq("a", 0)));
		assert(parseBoolExpr("a < 0").equals(new LessThan("a", 0)));
		assert(parseBoolExpr("true").equals(new True()));
		assert(parseBoolExpr("false").equals(new False()));
		assert(parseBoolExpr("! false").equals(parseBoolExpr("! false")));
		// Correct precedence!
		assert(parseBoolExpr("true & false | true ^ false").equals(
				new Xor(new Or(new And(new True(), new False()), new True()), new False())));
		System.out.println(parseBoolExpr("true ^ false | true & false"));
		assert(parseBoolExpr("true ^ false | true & false").equals(
				new Xor(new True(),new Or(new False(), new And(new True(), new False())))));
		assert(parseBoolExpr("true & false & true").equals(
				new And(new And(new True(), new False()), new True())));
		
		// TODO: precedence of compare operators
	}

	public void testParseNumExpr() {
		assert(parseNumExpr("(a + 1)")).equals(new Plus("a", 1));
		assert(parseNumExpr("a - (1)")).equals(new Minus("a", 1));
		assert(parseNumExpr("1 * b")).equals(new Times(1, "b"));
		assert(parseNumExpr("((100) / 0)")).equals(new Divide(new IntLiteral(100), new IntLiteral(0)));
		System.out.println(parseNumExpr("a + b * c"));
		assert(parseNumExpr("a + b * c")).equals(new Plus("a", new Times("b", "c")));
		assert(parseNumExpr("a / b + c")).equals(new Plus(new Divide("a", "b"), "c"));
	}

	private LanguageElement parseStatement(String code) {
        CharStream charStream = new ANTLRInputStream(code);
        DefProgLexer lexer = new DefProgLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        DefProgParser parser = new DefProgParser(tokens);

        ConcreteParser classVisitor = new ConcreteParser();
        return classVisitor.visit(parser.program());
	}
	
	private BoolExpression parseBoolExpr(String code) {
        CharStream charStream = new ANTLRInputStream(code);
        DefProgLexer lexer = new DefProgLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        DefProgParser parser = new DefProgParser(tokens);

        ConcreteParser classVisitor = new ConcreteParser();
        BoolexprContext ctx = parser.boolexpr();
        BoolExpression res = (BoolExpression)classVisitor.visit(ctx);
        return res;
	}

	private NumExpression parseNumExpr(String code) {
        CharStream charStream = new ANTLRInputStream(code);
        DefProgLexer lexer = new DefProgLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        DefProgParser parser = new DefProgParser(tokens);

        ConcreteParser classVisitor = new ConcreteParser();
        NumexprContext ctx = parser.numexpr();
        NumExpression res = (NumExpression)classVisitor.visit(ctx);
        return res;
	}
}
