package com.tr.rp.expressions.bool;

import com.tr.rp.core.DExpression;
import com.tr.rp.core.VarStore;
import com.tr.rp.expressions.num.IntLiteral;
import com.tr.rp.expressions.num.Var;

public class LessThan extends BoolExp {

	public final DExpression e1, e2;

	public LessThan(DExpression e1, DExpression e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	
	public LessThan(DExpression e, String var) {
		this(e, new Var(var));
	}
	
	public LessThan(String var, DExpression e) {
		this(new Var(var), e);
	}
	
	public LessThan(DExpression e, int val) {
		this(e, new IntLiteral(val));
	}
	
	public LessThan(int val, DExpression e) {
		this(new IntLiteral(val), e);
	}
	
	public LessThan(String var1, String var2) {
		this(new Var(var1), new Var(var2));
	}
	
	public LessThan(String var, int val) {
		this(new Var(var), new IntLiteral(val));
	}
	
	public LessThan(int val, String var) {
		this(new IntLiteral(val), new Var(var));
	}

	@Override
	public boolean isTrue(VarStore e) {
		return e1.getVal(e) < e2.getVal(e);
	}

	@Override
	public BoolExp transformRankExpressions(VarStore v, int rank) {
		DExpression t1 = e1.transformRankExpressions(v, rank);
		DExpression t2 = e2.transformRankExpressions(v, rank);
		if (t1 != e1 || t2 != e2) {
			return new LessThan(t1, t2);
		} else {
			return this;
		}
	}

}