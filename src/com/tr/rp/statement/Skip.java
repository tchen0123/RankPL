package com.tr.rp.statement;

import com.tr.rp.core.DStatement;
import com.tr.rp.core.VarStore;
import com.tr.rp.core.rankediterators.RankedIterator;

public class Skip implements DStatement {

	@Override
	public RankedIterator<VarStore> getIterator(RankedIterator<VarStore> parent) {
		return parent;
	}

}