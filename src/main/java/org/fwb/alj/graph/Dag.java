package org.fwb.alj.dag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

public class Dag<T> {
	final Map<T, Node>
		nodes = new HashMap<T, Node>(),
		roots = new HashMap<T, Node>();
	
	final Set<Edge> edges = new HashSet<Edge>();
//	final Map<T, Edge>
//		sources = new HashMap<T, Edge>(),
//		targets = new HashMap<T, Edge>();
	
	public Set<T> getAncestors(T value) {
		Set<T> ancestors = new HashSet<T>();
		Node node = nodes.get(value);
		
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
		// this implementation does not allow multi-edges
		Edge edge = new Edge(from, to);
		Preconditions.checkState(! edges.contains(edge));
		
		Node
			fromNode = nodes.get(from),
			toNode = nodes.get(to);
		// TODO wrong exception, this really should be checkArgument or "NoSuchElementException"
		Preconditions.checkNotNull(fromNode);
		Preconditions.checkNotNull(toNode);
//		Preconditions.checkState(nodes.containsKey(from));
//		Preconditions.checkState(nodes.containsKey(to));
		
		// first validate
		
		
		// then mutate.
		edges.add(edge);
//		sources.put(from, edge);
//		targets.put(to, edge);
		fromNode.outgoing.put(to, edge);
		toNode.incoming.put(from,  edge);
		
		roots.remove(to);
		
		return edge;
	}
	
	public class Node {
		final T value;
		
		final Map<T, Edge>
			incoming = new HashMap<T, Edge>(),
			outgoing = new HashMap<T, Edge>();
		
		Node(T value) {
			this.value = value;
		}
		
		Set<Node> getParents() {
			
		}
		Set<Node> getChildren() {
			
		}
	}
	public class Edge {
		/** special reference to help with equality testing */
		final Dag<T> dag = Dag.this;
		
		final T
			from,
			to;
		Edge(T from, T to) {
			this.from = from;
			this.to = to;
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
		
		@Override
		public int hashCode() {
			return Arrays.asList(from, to).hashCode();
		}
	}
	public class Path {
		
	}
}
