package model.huffman;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import model.utility.NodeComparator;

public class Huffman {

	Map <Character, String> codes;
	
	public Huffman() {
		codes = new HashMap<Character, String>();
	}
	
	@SuppressWarnings("null")
	// build the Huffman tree according to the frequencies
	private void buildHuffmanTree (HashMap<Character, Integer> frequencies) {
		PriorityQueue<Node> queue = new PriorityQueue<Node>(frequencies.size(), new NodeComparator());
		
		// create the leafs
		for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
			queue.add(new Node (entry.getKey(), entry.getValue()));
		}
		
		// merge the leafs
		while (queue.size() > 1) {
			// Remove the 2 smallest nodes
			Node left = queue.poll();
			Node right = queue.poll();

			// Create an internal node
			Node internalNode = new Node(left.getFrequency() + right.getFrequency() , left, right);

			queue.add(internalNode);
		}
		
		assignCodes(queue.poll(), "");
		
		return;
	}
	
	private void assignCodes(Node root, String code) {
		
		if (root == null)
			return;
		
		if (root.isLeaf() && Character.isLetter(root.getCharacter()))
			codes.put(root.getCharacter(), code);
			
		assignCodes(root.getLeft(),  code + "0");
		assignCodes(root.getRight(), code + "1");
	}
	
	public void compress (HashMap<Character, Integer> frequencies) {
		buildHuffmanTree(frequencies);
	}

	public Map<Character, String> getCodes() {
		return codes;
	}
	
}






