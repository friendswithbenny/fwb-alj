package org.fwb.alj.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.google.common.base.Preconditions;

//import org.fwb.alj.col.SetUtil.SetView;
//import com.google.common.collect.Collections2;

public class Dag<T> {
	private final Map<T, Node> nodes = new HashMap<T, Node>();
	
	private final Set<Node> roots = new HashSet<Node>();
	
	public Node getNode(T value) {
		Node node = nodes.get(value);
		Preconditions.checkArgument(
			null != node,
			"node not found: %s",
			value);
		return node;
	}
	
	/** depth-first */
	public Set<Node> getAncestors(T value) {
		Set<Node> ancestors = new HashSet<Node>();
		getAncestorsRecursive(
			getNode(value),
			ancestors);
		return ancestors;
	}
	/** depth first */
	public Set<Node> getDescendants(T value) {
		Set<Node> descendants = new HashSet<Node>();
		getDescendantsRecursive(
			getNode(value),
			descendants);
		return descendants;
	}
//	public Set<T> getAncestorValues(T value) {
//		/* TODO this doesn't work for some reason :( */
//		return new SetView<T>(Collections2.<Node, T>transform(getDescendants(value), getNodeValue));
//	}
//	public Set<T> getDescendantValues(T value) {
//		
//	}
	
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
		
		Set<Node> ancestors = getAncestors(from);
		Preconditions.checkArgument(
			! ancestors.contains(toNode),
			"loop detected. found child in ancestors: %s\n\t%s",
			to,
			ancestors);
		
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
	
	public void removeNode(T value) {
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
		
//		return node;
	}
	
	private void getAncestorsRecursive(Node node, Set<Node> ancestors) {
		if (ancestors.add(node))
			for (Node from : node.incoming.keySet())
				getAncestorsRecursive(from, ancestors);
	}
	private void getDescendantsRecursive(Node node, Set<Node> descendants) {
		if (descendants.add(node))
			for (Node from : node.outgoing.keySet())
				getDescendantsRecursive(from, descendants);
	}
	
	final Function<Node, T> getNodeValue = new Function<Node, T>() {
		@Override
		public T apply(Dag<T>.Node node) {
			return node.value;
		}
	};
	
	/** XXX TODO implement Comparable! */
	public class Node {
		private final Dag<T> dag = Dag.this;
		
		
		public final T value;
		
		private final Map<Node, Edge>
			outgoing = new HashMap<Node, Edge>(),
			incoming = new HashMap<Node, Edge>();
		
		public final Set<Node>
			parents = Collections.unmodifiableSet(outgoing.keySet()),
			children = Collections.unmodifiableSet(incoming.keySet());
		
		private Node(T value) {
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
			if (o instanceof Dag.Node) {
				@SuppressWarnings("unchecked")
				Node node = (Node) o;
				return dag == node.dag
					&& Objects.equals(
						value,
						((Dag<?>.Node) o).value);
			} return
				false;
		}
	}
	
	/** this class exists solely to encapsulate edge metadata */
	private class Edge {
		@SuppressWarnings("unused")
		final Node
			from,
			to;
		private Edge(Node from, Node to) {
			this.from = from;
			this.to = to;
		}
		
		/* add any metadata here */
		
	}
}
