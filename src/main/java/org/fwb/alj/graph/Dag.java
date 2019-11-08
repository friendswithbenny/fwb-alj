package org.fwb.alj.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;

public class Dag<T> {
	final Map<T, Node>
		nodes = new HashMap<T, Node>(),
		roots = new HashMap<T, Node>();
//		leaves = new HashMap<T, Node>();
	
	final Set<Edge> edges = new HashSet<Edge>();
//	final Map<T, Edge>
//		sources = new HashMap<T, Edge>(),
//		targets = new HashMap<T, Edge>();
	
	public Set<T> getAncestors(T value) {
		Set<T> ancestors = new HashSet<T>();
		getAncestorsRecursive(nodes.get(value), ancestors);
		return ancestors;
	}
	private void getAncestorsRecursive(Node node, Set<T> ancestors) {
		if (ancestors.add(node.value))
			for (Edge e : node.incoming)
				getAncestorsRecursive(e.from, ancestors);
	}
	
	/**
	 * add a value not already in the Dag.
	 * @return a (root) node
	 * @throws IllegalStateException if the value is already in the Dag
	 */
	public Node addNode(T value) {
		Preconditions.checkState(! nodes.containsKey(value));
		Node node = new Node(value);
		nodes.put(value, node);
		roots.put(value, node);
		return node;
	}
	
	public Edge addEdge(T from, T to) {
		Node
			fromNode = nodes.get(from),
			toNode = nodes.get(to);
		// TODO wrong exception, this really should be checkArgument or "NoSuchElementException"
		Preconditions.checkNotNull(fromNode);
		Preconditions.checkNotNull(toNode);
//		Preconditions.checkState(nodes.containsKey(from));
//		Preconditions.checkState(nodes.containsKey(to));
		
		// this implementation does not allow multi-edges
		Edge edge = new Edge(fromNode, toNode);
		Preconditions.checkState(! edges.contains(edge));
		
		// first validate
		Preconditions.checkState(! getAncestors(from).contains(to));
		
		// then mutate.
		edges.add(edge);
//		sources.put(from, edge);
//		targets.put(to, edge);
		fromNode.outgoing.add(edge);
		toNode.incoming.add( edge);
		
//		leaves.remove(from);
		roots.remove(to);
		
		return edge;
	}
	
	public Edge removeEdge(T from, T to) {
		Node
			fromNode = nodes.get(from),
			toNode = nodes.get(to);
		Preconditions.checkNotNull(fromNode);
		Preconditions.checkNotNull(toNode);
		
		Edge edge = new Edge(fromNode, toNode);
		Preconditions.checkNotNull(edges.remove(edge));
		
		fromNode.outgoing.remove(edge);
		toNode.incoming.remove(edge);
		
		if (toNode.incoming.isEmpty())
			roots.put(toNode.value, toNode);
		
		return edge;
	}
	
	public Node removeNode(T value) {
		Node node = nodes.remove(value);
		Preconditions.checkNotNull(node);
		
		roots.remove(value);
		
		for (Edge e: node.outgoing)
			removeEdge(value, e.to.value);
		for (Edge e: node.incoming)
			removeEdge(e.from.value, value);
		
		return node;
	}
	
	public class Node {
		final T value;
		
		final Set<Edge>
			outgoing = new HashSet<Edge>(),	
			incoming = new HashSet<Edge>();
		
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
				&& Objects.equals(value, ((Dag.Node) o).value);
		}
		
//		/** an outgoing edge */
//		public class Edge {
//			final Node from = Node.this;
//			final Node to;
//			Edge(Node to) {
//				this.to = to;
//			}
//		}
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
