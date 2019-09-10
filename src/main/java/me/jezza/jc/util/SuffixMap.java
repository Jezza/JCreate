package me.jezza.jc.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A spanning tree based off the array. Each value of an array defines an edge.
 * Any branched generated from a part of an array will not have a value, unless the option is chosen during construction.
 *
 * @author Jezza
 */
public class SuffixMap<K, V> {
	private final Node root;

	/**
	 * Constructs a basic BranchedMap.
	 */
	public SuffixMap() {
		this(null);
	}

	/**
	 * @param rootValue - The default value to return on an empty or null array key.
	 */
	public SuffixMap(V rootValue) {
		root = new Node();
		root.value = rootValue;
	}

	/**
	 * Places the value along the given path.
	 * Any edges or nodes that need to be generated will be generated, with the options passed in during construction.
	 *
	 * @param keys  - The path that the value should be placed at.
	 * @param value - The value that should be stored.
	 * @return - Any old value that was previously stored at the path, null otherwise.
	 */
	public V put(K[] keys, V value) {
		if (keys == null || keys.length == 0)
			return null;
		Node base = node(keys, true);
		if (base == null)
			return null;
		V oldValue = base.value;
		base.value = value;
		return oldValue;
	}

	/**
	 * Retrieve the value that is defined by the given path.
	 *
	 * @param keys - The path to the value.
	 * @return - The value, if found.
	 */
	public V get(K[] keys) {
		if (keys == null || keys.length == 0)
			return root.value;
		Node base = node(keys, true);
		if (base == null) // Will never happen.
			return null;
		if (base.value != null)
			return base.value;
		Node parent = base;
		while (parent.value == null) {
			parent = parent.parent;
			if (parent == null)
				return null;
		}
		base.value = parent.value;
		return base.value;
	}

	public Pack<V> closest(K[] keys) {
		if (keys == null || keys.length == 0)
			return new Pack<>(0, root.value);
		int depth = 0;
		Node base = root;
		for (K link : keys) {
			Node next = nextNode(base, link, false);
			if (next == null)
				return new Pack<>(depth, base.value);
			depth++;
			base = next;
		}
		if (base == null) // "Should" never happen.
			return new Pack<>(depth, null);
		if (base.value != null)
			return new Pack<>(depth, base.value);
		Node parent = base;
		while (parent.value == null) {
			parent = parent.parent;
			if (parent == null)
				return new Pack<>(depth, null);
		}
		base.value = parent.value;
		return new Pack<>(depth, base.value);
	}

	public static class Pack<T> {
		public final int depth;
		public final T value;

		Pack(int depth, T value) {
			this.depth = depth;
			this.value = value;
		}

		@Override
		public String toString() {
			return "Pack{depth:" + depth + ", value:" + value + '}';
		}
	}

	public static void main(String[] args) {
		SuffixMap<String, String> map = new SuffixMap<>();
		map.put(new String[]{"first"}, "asdasd");
		map.put(new String[]{"first", "second"}, "hello");
		String[] params = new String[]{"first", "fourth", "third"};
		Pack<String> pack = map.closest(params);
		System.out.println(pack);
		System.out.println(Arrays.toString(Arrays.copyOfRange(params, pack.depth, params.length)));
	}

	/**
	 * Removes the node that is at the given path, this also means all of its children.
	 *
	 * @param keys - The path to the value.
	 * @return - The value of the node that was removed.
	 */
	public V remove(K[] keys) {
		if (keys == null || keys.length == 0) {
			root.value = null;
			return null;
		}
		Node base = node(keys, false);
		if (base == null)
			return null;
		K link = keys[keys.length - 1];
		base.parent.children.remove(link);
		V value = base.value;
		destroyBranch(base);
		return value;
	}

	/**
	 * Deletes all nodes, while retaining the options and root value.
	 */
	public void clear() {
		clear(root);
	}

	/**
	 * Internal method, used to grab the node at the path, potentially generating nodes as it goes, if the boolean is true.
	 *
	 * @param keys   - The path of the node to get.
	 * @param create - If the path should be generated. (This means that the result is never null)
	 * @return - The node at the path.
	 */
	private Node node(K[] keys, boolean create) {
		Node base = root;
		for (K link : keys) {
			base = nextNode(base, link, create);
			if (base == null)
				return null;
		}
		return base;
	}

	/**
	 * Internal method, used to get or created the next node in the chain.
	 *
	 * @param check  - The node that should be searched for the next link.
	 * @param link   - The link/edge. The value that defines the connection.
	 * @param create - If true, the necessary nodes will be generated.
	 * @return - The node that should be the next one in line. Can be null.
	 */
	private Node nextNode(Node check, K link, boolean create) {
		if (link == null)
			throw new NullPointerException("A link in the chain cannot be null. Parent: " + check.value);
		Node node = check.children.get(link);
		if (node != null)
			return node;
		return create ? createChild(check, link) : null;
	}

	/**
	 * Does the dirty work of actual creation and linking of the child node to the parent.
	 *
	 * @param parent - The parent of the node to be created.
	 * @param link   - The connection between the two nodes.
	 * @return - The created child node.
	 */
	private Node createChild(Node parent, K link) {
		Node child = new Node();
		child.parent = parent;
		parent.children.put(link, child);
		child.value = parent.value;
		return child;
	}

	private void clear(Node node) {
		Map<K, Node> children = node.children;
		if (!children.isEmpty()) {
			Iterator<Entry<K, Node>> it = children.entrySet().iterator();
			while (it.hasNext()) {
				Entry<K, Node> next = it.next();
				destroyBranch(next.getValue());
				it.remove();
			}
		}
	}

	/**
	 * Obliterates any nodes that are children of the node, and then destroys the node itself.
	 * Note: Doesn't remove the node from the parent of the node in question, so do that before calling this.
	 *
	 * @param node - The node to destroy.
	 */
	private void destroyBranch(Node node) {
		clear(node);
		node.children = null;
		node.value = null;
		node.parent = null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		toString(root, builder, 0, true);
		return builder.toString();
	}

	/**
	 * Builds a recursive representation of the map.
	 * Typically, you should just call toString();
	 *
	 * @param node    - The starting node.
	 * @param builder - The StringBuilder to add the information to.
	 * @param depth   - How deep the traversal has gone so far.
	 * @param skip    - If the given node shouldn't be printed.
	 */
	private void toString(Node node, StringBuilder builder, int depth, boolean skip) {
		Map<K, Node> children = node.children;
		if (!skip) {
			builder.append('"').append(node.value).append('"');
			if (!children.isEmpty())
				builder.append(", ").append(children.size());
			builder.append('\n');
		}
		if (!children.isEmpty()) {
			for (Entry<K, Node> entry : children.entrySet()) {
				for (int i = 0; i < depth; i++)
					builder.append('-');
				builder.append('|').append('"').append(entry.getKey()).append('"').append("=");
				toString(entry.getValue(), builder, depth + 1, false);
			}
		}
	}

	/**
	 * Internal representation of a node.
	 */
	private class Node {
		private Node parent;
		private V value;

		private Map<K, Node> children = new HashMap<>();
	}
}