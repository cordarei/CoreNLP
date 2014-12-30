package edu.stanford.nlp.parser.lexparser;

import java.util.ArrayList;
import java.util.Arrays;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CollectionUtils;

public class IndependentSpanConstraints {
	final int[] constraints;
	final int[] nextConstraints;
	final int[] prevConstraints;
//	final boolean[] violations;
	final int length;
	
	public IndependentSpanConstraints(Tree goldTree) {
		length = goldTree.yield().size() + 1;
		constraints = getConstraints(goldTree);
		nextConstraints = new int[length];
		for (int i = 0, j = 0; i < length; i++) {
			if (i == constraints[j]) {
				j += 1;
			}
			nextConstraints[i] = constraints[j];
		}
		prevConstraints = new int[length];
		for (int i = 0, j = 0, prev=0; i < length; i++) {
			if (i == constraints[j]) {
				prev = constraints[j];
				j += 1;
			}
			prevConstraints[i] = prev;
		}
//		violations = new boolean[length*length];
		
//		int start = 0;
//		for (int i : constraints) {
//			while (start < i) {
//				for (int end = start + 1; end <= i; end++) {
//					violations[start*length + end] = false;
//				}
//				for (int end = i + 1; end <= length; end++) {
//					violations[start*length + end] = true;
//				}
//				start++;
//			}
//		}
//		while (start < length) {
//			for (int end = start + 2; end <=length; end++) {
//				violations[start*length + end] = false;
//			}
//			start++;
//		}
//		System.err.println("Constraints: " + Arrays.toString(constraints));
//		System.err.println("NextConstraints: " + Arrays.toString(nextConstraints));
//		System.err.println("PrevConstraints: " + Arrays.toString(prevConstraints));
//		for (int i = 0; i < length; i++) {
//			for (int j = i + 2; j <= length; j++) {
//				System.err.println("Span: (" + i + "," + j + ") is violation?: " + violations[i*length + j]);
//			}
//		}
	}
	
	public boolean violatesConstraints(final int start, final int end) {
		if (start == 0 && end + 1 >= length) {//what is the correct boundary here?
			return false;
		}
		if (start + 1 == end) {//length-one spans never violate
			return false;
		}
//		for(int i : constraints) {
//			if (i > start && i < end) {
////				System.err.println("Edge from " + start + " to " + end + " with state " + stateIndex.get(edge.state) + " violates constraint " + i);
//				return true;
//			}
//		}
		return end > nextConstraints[start];
//		return violations[start*length + end];
	}
	public int getNextConstraint(final int start) {
		return nextConstraints[start];
	}
	public int getPrevConstraint(final int start) {
		return prevConstraints[start];
	}
	
	private int[] getConstraints(Tree goldTree) {
		Tree t = goldTree.skipRoot();
		ArrayList<Integer> constraints = new ArrayList<Integer>();
		int total = 0;
		for (Tree child : t.children()) {
			total += child.yield().size();
			constraints.add(total);
		}
		constraints.add(total + 1);
		//constraints.remove(constraints.size()-1);
		return CollectionUtils.asIntArray(constraints);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Arrays.toString(constraints);
	}
}
