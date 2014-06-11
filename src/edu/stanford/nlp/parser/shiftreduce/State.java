package edu.stanford.nlp.parser.shiftreduce;

import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.Scored;
import edu.stanford.nlp.util.TreeShapedStack;

/**
 * A class which encodes the current state of the parsing.  This can
 * be used either for direct search or for beam search.
 * <br>
 * Important information which needs to be encoded:
 * <ul>
 *
 * <li>A stack.  This needs to be updatable in O(1) time to keep the
 * parser's run time linear.  This is done by using a linked-list type
 * stack in which new states are created by the <code>push</code>
 * operation. 
 *
 * <li>A queue.  This also needs to be updatable in O(1) time.  This
 * is accomplished by having all the states share the same list of
 * queued items, with different states only changing an index into the
 * queue.
 *
 * <li>The score of the current state.  This is useful in beam searches.
 *
 * <li>Whether or not the current state is "finalized".  If so, the
 * only thing that can be done from now on is to idle.
 *
 * </ul>
 */
public class State implements Scored {
  /**
   * Expects a list of preterminals.  The preterminals should be built
   * with CoreLabels and have HeadWord and HeadTag annotations set.
   */
  public State(List<Tree> sentence) {
    this(new TreeShapedStack<Tree>(), new TreeShapedStack<Transition>(), new TreeShapedStack<HeadPosition>(), sentence, 0, 0.0, false);
  }

  State(TreeShapedStack<Tree> stack, TreeShapedStack<Transition> transitions, TreeShapedStack<HeadPosition> separators,
        List<Tree> sentence, int tokenPosition, double score, boolean finished) {
    this.stack = stack;
    this.transitions = transitions;
    this.separators = separators;
    this.sentence = sentence;
    this.tokenPosition = tokenPosition;
    this.score = score;
    this.finished = finished;
  }

  /**
   * The stack of Tree pieces we have already assembled.
   */
  final TreeShapedStack<Tree> stack;

  /**
   * The transition sequence used to get to the current position
   */
  final TreeShapedStack<Transition> transitions;

  /**
   * Used to describe the relative location of separators to the head of a subtree
   */
  public enum HeadPosition { NONE, LEFT, RIGHT, BOTH, HEAD };

  /**
   * A description of where the separators such as ,;:- are in a
   * subtree, relative to the head of the subtree
   */
  final TreeShapedStack<HeadPosition> separators;

  /**
   * The words we are parsing.  They need to be tagged before we can
   * parse.  The words are stored as preterminal Trees whose only
   * nodes are the tag node and the word node.
   */
  final List<Tree> sentence;

  /**
   * Essentially, the position in the queue part of the state.  
   * 0 represents that we are at the start of the queue and nothing
   * has been shifted yet.
   */
  final int tokenPosition;

  /**
   * The score of the current state based on the transitions that were
   * used to create it.
   */
  final double score;

  @Override
  public double score() { return score; }

  /**
   * Whether or not processing has finished.  Once that is true, only
   * idle transitions are allowed.
   */ 
  final boolean finished;

  public boolean isFinished() { return finished; }

  public boolean endOfQueue() {
    return tokenPosition == sentence.size();
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("State summary\n");
    result.append("  Tokens: " + sentence + "\n");
    result.append("  Token position: " + tokenPosition + "\n");
    result.append("  Current stack contents: " + stack + "\n");
    result.append("  Score: " + score + "\n");
    result.append("  " + ((finished) ? "" : "not ") + "finished\n");
    return result.toString();
  }
}
