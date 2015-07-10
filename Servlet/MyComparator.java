package edu.ucla.cs.cs144;

import java.util.Comparator;
import org.w3c.dom.*;

public class MyComparator implements Comparator<Node> {
	@Override
	public int compare(Node x, Node y) {
		String xTime = ((Element) x).getElementsByTagName("Time").item(0).getTextContent();	
		String yTime = ((Element) y).getElementsByTagName("Time").item(0).getTextContent();	
		if (xTime.compareTo(yTime) < 0)
			return 1;
		else if (xTime.compareTo(yTime) > 0) 
			return -1;
		return 0;
	}
}
