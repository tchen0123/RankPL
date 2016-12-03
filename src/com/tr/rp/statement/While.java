package com.tr.rp.statement;

import com.tr.rp.core.DStatement;
import com.tr.rp.core.VarStore;
import com.tr.rp.core.rankediterators.AbsurdIterator;
import com.tr.rp.core.rankediterators.BufferingIterator;
import com.tr.rp.core.rankediterators.RankedIterator;
import com.tr.rp.expressions.bool.BoolExpression;

public class While implements DStatement {

	private BoolExpression exp;
	private DStatement s;
	private int maxIterations = Integer.MAX_VALUE;
	
	public While(BoolExpression exp, DStatement s) {
		this.exp = exp;
		this.s = s;
	}
		
	public While setMaxDepth(int maxIterations) {
		this.maxIterations = maxIterations;
		return this;
	}

	@Override
	public RankedIterator<VarStore> getIterator(RankedIterator<VarStore> in) {
		int iterations = 0;
		
		while (iterations++ < maxIterations) {
			// Do one iteration
			in = generateIteration(in);
			
			// Check if exp is still satisfied
			BufferingIterator bi = new BufferingIterator(in);
			boolean expSatisfied = false;
			boolean hasNext = bi.next();
			if (!hasNext) { // Undefined
				return new AbsurdIterator(); 
			}
			while (hasNext) {
				if (exp.isTrue(bi.getItem())) {
					System.out.println(bi.getItem());
					expSatisfied = true;
					break;
				}
				hasNext = bi.next();
			}
			
			bi.reset();
			bi.stopBuffering();

			// If exp is not satisfied, we are done
			if (!expSatisfied) {
				return bi;
			}
			
			// Try another iteration
			in = bi;
		}
		
		return null;

	}
	
	private RankedIterator<VarStore> generateIteration(RankedIterator<VarStore> in) {
		return (new IfElse(exp, s, new Skip())).getIterator(in);
	}
}
