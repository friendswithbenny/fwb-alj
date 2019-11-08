package org.fwb.alj.graph;

//import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.google.common.base.Preconditions;

public class Dag<T> {
	private final Map<T, Node> nodes = new HashMap<T, Node>();
//		roots = new HashMap<T, Node>();
	private final Function<T, Node> getNode =
		new Function<T, Node>() {
			@Override
			public Dag<T>.Node apply(T value) {
				return nodes.get(value);
			}
		};
	
	private final Set<Node> roots = new HashSet<Node>();
	
	public Set<T> getAncestors(T value) {
		Set<T> ancestors = new HashSet<T>();
		getAncestorsRecursive(nodes.get(value), ancestors);
		return ancestors;
	}
	private void getAncestorsRecursive(Node node, Set<T> ancestors) {
		if (ancestors.add(node.value))
			for (Node from : node.incoming.keySet())
				getAncestorsRecursive(from, ancestors);
	}
	
	private Node getNode(T value) {
		Node node = nodes.get(value);
		Preconditions.checkArgument(
			null != node,
			"node not found: %s",
			value);
		return node;
	}
	
	/**
	 * add a value not already in the Dag.
	 * @return a (root) node
	 * @throws IllegalStateException if the value is already in the Dag
	 */
	public Node addNode(T value) {
		Preconditions.checkArgument(
			! nodes.containsKey(value),
			"node already exists. cannot add: %s",
			value);
		
		Node node = new Node(value);
		nodes.put(value, node);
		
		roots.add(node);
		
		return node;
	}
	
	public void addEdge(T from, T to) {
		Node
			fromNode = getNode(from),
			toNode = getNode(to);
		
		Preconditions.checkArgument(
			fromNode.outgoing.containsKey(toNode),
			"duplicate edge: %s->%s",
			from,
			to);
		// no need to check reverse direction (redundant)
		
		Set<T> ancestors = getAncestors(from);
		Preconditions.checkArgument(
			! ancestors.contains(to),
			"loop detected. found child in ancestors: %s\n\t%s",
			to,
			ancestors);
		
		// mutations
		Edge edge = new Edge(fromNode, toNode);
		fromNode.outgoing.put(toNode, edge);
		toNode.incoming.put(fromNode, edge);
		
		roots.remove(to);
		
//		return edge;
	}
	
	public void removeEdge(T from, T to) {
		Node
			fromNode = getNode(from),
			toNode = getNode(to);
		
		Preconditions.checkArgument(
			null != fromNode.outgoing.remove(toNode),
			"cannot remove edge; not found: %s.>%s",
			from,
			to);
		toNode.incoming.remove(fromNode);
		
		if (toNode.incoming.isEmpty())
			roots.add(toNode);
	}
	
	public Node removeNode(T value) {
		Node node = nodes.remove(value);
		Preconditions.checkArgument(
			null != node,
			"cannot remove node; not found: %s",
			value);
		
		for (Node to: node.outgoing.keySet())
			removeEdge(value, to.value);
		for (Node from: node.incoming.keySet())
			removeEdge(from.value, value);
		
		roots.remove(value);
		
		return node;
	}
	
	public class Node {
		public final T value;
		
		private final Map<Node, Edge>
			outgoing = new HashMap<Node, Edge>(),
			incoming = new HashMap<Node, Edge>();
		
		public final Set<Node>
			parents = Collections.unmodifiableSet(outgoing.keySet()),
			children = Collections.unmodifiableSet(incoming.keySet());
		
		Node(T value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format(
				"Node(%s)",
				value);
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(value);
		}
		@Override
		public boolean equals(Object o) {
			return (o instanceof Dag.Node)
				&& Objects.equals(
					value,
					((Dag<?>.Node) o).value);
		}
	}
	
	/** this class exists solely to encapsulate edge metadata */
	private class Edge {
//		/** special reference to help with equality testing */
//		private final Dag<T> dag = Dag.this;
		
		private final Node
			from,
			to;
		private Edge(Node from, Node to) {
			this.from = from;
			this.to = to;
		}
		
		// add any metadata here
		
//		@Override
//		public String toString() {
//			return String.format(
//				"Edge(%s, %s)",
//				from.value,
//				to.value);
//		}
//		
//		@Override
//		public int hashCode() {
//			return Arrays.asList(from, to).hashCode();
//		}
//		
//		@Override
//		public boolean equals(Object o) {
//			if (o instanceof Dag.Edge) {
//				
//				@SuppressWarnings({ "rawtypes", "unchecked" })
//				Edge e = (Dag.Edge) o;
//				// this line crashes my whole eclipse compiler!
////				Dag<?>.Edge e = (Dag<?>.Edge) o;
//				
//				if (dag == e.dag)
//					return Arrays.asList(from, to).equals(Arrays.asList(e.from, e.to));
//				else
//					return false;
//			} else
//				return false;
//		}
	}
}
