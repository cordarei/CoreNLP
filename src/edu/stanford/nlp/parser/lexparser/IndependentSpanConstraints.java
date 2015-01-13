package edu.stanford.nlp.parser.lexparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CollectionUtils;

public class IndependentSpanConstraints {
	final int[] constraints;
	final int[] nextConstraints;
	final int length;
	
	public IndependentSpanConstraints(final int length, Tree goldTree) {
//		length = goldTree.getLeaves().size() + 1;
//		constraints = getConstraintsFromTree(goldTree);
//		nextConstraints = new int[length];
//		for (int i = 0, j = 0; i < length; i++) {
//			if (i == constraints[j]) {
//				j += 1;
//			}
//			nextConstraints[i] = constraints[j];
//		}
		this(length, getConstraintsFromTree(goldTree));
	}
	
	public IndependentSpanConstraints(final int length, int[] constraints) {
		this.length = length;
		this.constraints = constraints;
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
	
	private static int[] getConstraintsFromTree(Tree goldTree) {
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
	
	public static ArrayList<ArrayList<Integer>> getConstraintsFromFile(String filename) {
		ArrayList<ArrayList<Integer>> constraints = new ArrayList<ArrayList<Integer>>();

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename) , StandardCharsets.UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null ) {
				constraints.add(new ArrayList<Integer>());
				if (line.trim().length() > 0) {
					String[] fields = line.trim().split(" ");
					for (String s : fields) {
//						System.err.println(s);
						constraints.get(constraints.size() - 1).add(Integer.decode(s));
					}
				}
			}
		} catch (IOException e) {
			System.err.println(e);
			return null;
		}

//		int[][] ret = new int[constraints.size()][];
//		for (int i = 0; i < ret.length; i++) {
//			ret[i] = CollectionUtils.asIntArray(constraints.get(i));
//		}
//		return ret;
		return constraints;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(constraints);
	}
}
