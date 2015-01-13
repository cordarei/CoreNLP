package edu.stanford.nlp.parser.lexparser;

import java.util.ArrayList;
import java.util.Arrays;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CollectionUtils;

public class IndependentSpanConstraints {
	final int[] constraints;
	final int[] nextConstraints;
	final int length;
	
	public IndependentSpanConstraints(Tree goldTree) {
		length = goldTree.getLeaves().size() + 1;
		constraints = getConstraintsFromTree(goldTree);
		nextConstraints = new int[length];
		for (int i = 0, j = 0; i < length; i++) {
			if (i == constraints[j]) {
				j += 1;
			}
			nextConstraints[i] = constraints[j];
		}
	}
	
	public boolean violatesConstraints(final int start, final int end) {
		if (start == 0 && end + 1 >= length) {//what is the correct boundary here?
			return false;
		}
		if (start + 1 == end) {//length-one spans never violate
			return false;
		}

		return end > nextConstraints[start];
	}

	public int getNextConstraint(final int start) {
		return nextConstraints[start];
	}

	public int[] getConstraints() {
		return constraints;
	}
	
	private int[] getConstraintsFromTree(Tree goldTree) {
		Tree t = goldTree.skipRoot();
		ArrayList<Integer> constraints = new ArrayList<Integer>();
		int total = 0;
		for (Tree child : t.children()) {
			total += child.getLeaves().size();
			constraints.add(total);
		}
		constraints.add(total + 1);
		//constraints.remove(constraints.size()-1);
		return CollectionUtils.asIntArray(constraints);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(constraints);
	}
}
