package org.fwb.alj.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;

public class Dag<T> {
	private final Map<T, Node>
		nodes = new HashMap<T, Node>(),
		roots = new HashMap<T, Node>();
	
//	private final Set<Edge> edges = new HashSet<Edge>();
	
	public Set<T> getAncestors(T value) {
		Set<T> ancestors = new HashSet<T>();
		getAncestorsRecursive(nodes.get(value), ancestors);
		return ancestors;
	}
	private void getAncestorsRecursive(Node node, Set<T> ancestors) {
		if (ancestors.add(node.value))
			for (Edge e : node.incoming.values())
				getAncestorsRecursive(e.from, ancestors);
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
		roots.put(value, node);
		return node;
	}
	
	public Edge addEdge(T from, T to) {
		// redundant with main logic
//		Preconditions.checkArgument(
//			! Objects.equals(from,  to),
//			"self-loop: %s, %s", from, to);
		
		Node
			fromNode = getNode(from),
			toNode = getNode(to);
		
		// this implementation does not allow multi-edges
		Preconditions.checkArgument(
			fromNode.outgoing.containsKey(toNode),
			"duplicate edge: %s->%s",
			from,
			to);
		// no need to check reverse direction (redundant)
		
//		Preconditions.checkArgument(
//			! edges.contains(edge),
//			"duplicate edge: %s",
//			edge);
		
		// first validate
		Set<T> ancestors = getAncestors(from);
		Preconditions.checkArgument(
			! ancestors.contains(to),
			"loop detected. found child in ancestors: %s\n\t%s",
			to,
			ancestors);
		
		// then mutate.
		Edge edge = new Edge(fromNode, toNode);
//		edges.add(edge);
		fromNode.outgoing.put(toNode, edge);
		toNode.incoming.put(fromNode, edge);
		
		// target by-definition not a root
		roots.remove(to);
		
		return edge;
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
//		Edge edge = new Edge(fromNode, toNode);
//		Preconditions.checkArgument(
//			edges.remove(edge),
//			"cannot remove edge; not found: %s",
//			edge);
//		fromNode.outgoing.remove(toNode);
		toNode.incoming.remove(fromNode);
		
		if (toNode.incoming.isEmpty())
			roots.put(toNode.value, toNode);
		
//		return edge;
	}
	
	public Node removeNode(T value) {
		Node node = nodes.remove(value);
		Preconditions.checkArgument(
			null != node,
			"cannot remove node; not found: %s",
			value);
		
		roots.remove(value);
		
		for (Edge e: node.outgoing.values())
			removeEdge(value, e.to.value);
		for (Edge e: node.incoming.values())
			removeEdge(e.from.value, value);
		
		return node;
	}
	
	public class Node {
		final T value;
		
		final Map<Node, Edge>
			outgoing = new HashMap<Node, Edge>(),	
			incoming = new HashMap<Node, Edge>();
		
		Node(T value) {
			this.value = value;
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
	public class Edge {
		/** special reference to help with equality testing */
		final Dag<T> dag = Dag.this;
		
		final Node
			from,
			to;
		Edge(Node from, Node to) {
			this.from = from;
			this.to = to;
		}
		
		@Override
		public int hashCode() {
			return Arrays.asList(from, to).hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof Dag.Edge) {
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				Edge e = (Dag.Edge) o;
				// this line crashes my whole eclipse compiler!
//				Dag<?>.Edge e = (Dag<?>.Edge) o;
				
				if (dag == e.dag)
					return Arrays.asList(from, to).equals(Arrays.asList(e.from, e.to));
				else
					return false;
			} else
				return false;
		}
	}
}
