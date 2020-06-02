/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

//
//Copyright (C) 2005 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.
//
//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.
//
//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.symbc.numeric;

import java.util.Map;

/**
 * @author Sarfraz Khurshid (khurshid@lcs.mit.edu)
 *
 */
public class BinaryNonLinearIntegerExpression extends NonLinearIntegerExpression {
	public IntegerExpression left;

	public Operator op;

	public IntegerExpression right;

	public BinaryNonLinearIntegerExpression(IntegerExpression l, Operator o, IntegerExpression r) {
		left = l;
		op = o;
		right = r;
	}

	public long solution() {
		long l = left.solution();
		long r = right.solution();
		switch(op){
		  case PLUS:       return l + r;
		  case MINUS:      return l - r;
		  case MUL: return l * r;
		  case DIV: return l / r;
		  case AND: return l & r;
		  case OR: return l | r;
		  case XOR: return l ^ r;
		  case SHIFTL: return l << r;
		  case SHIFTR: return l >> r;
		  case SHIFTUR: return l >>> r;
		  default: throw new RuntimeException("## Error: BinaryNonLinearSolution solution: l " + l + " op " + op + " r " + r);
		}
	}

    public void getVarsVals(Map<String,Object> varsVals) {
    	left.getVarsVals(varsVals);
    	right.getVarsVals(varsVals);
    }

	public String stringPC() {
		return "(" + left.stringPC() + op.toString() + right.stringPC() + ")";
	}

	public String toString() {
		return "(" + left.toString() + op.toString() + right.toString() + ")";
	}

	public String prefix_notation ()
	{
		return "(" + op.prefix_notation() + " "+ left.prefix_notation()  + " "+right.prefix_notation() + ")";
	}
	
	// JacoGeldenhuys
	@Override
	public void accept(ConstraintExpressionVisitor visitor) {
		visitor.preVisit(this);
		left.accept(visitor);
		right.accept(visitor);
		visitor.postVisit(this);
	}

	@Override
	public int compareTo(Expression expr) {
		if (expr instanceof BinaryNonLinearIntegerExpression) {
			BinaryNonLinearIntegerExpression e = (BinaryNonLinearIntegerExpression) expr;
			int r = op.compareTo(e.op);
			if (r == 0) {
				r = left.compareTo(e.left);
			}
			if (r == 0) {
				r = right.compareTo(e.right);
			}
			return r;
		} else {
			return getClass().getCanonicalName().compareTo(expr.getClass().getCanonicalName());
		}
	}

	public Operator getOp() { return op;
	}
}
