/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;

 	static PrintWriter writer1;
	static PrintWriter writer2;
	static PrintWriter writer3;
	static PrintWriter writer4;
	static PrintWriter writer5;
	static PrintWriter writer6;
	static PrintWriter writer7;
	static PrintWriter writer8;
	static PrintWriter writer9;
	static PrintWriter writer10;
	static PrintWriter writer11;

    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        /* Fill in code here (you will probably need to write auxiliary
            methods). */

		try {
		 	// Retrieve data for itemName table	
       		Element e = doc.getDocumentElement();
			Element[] result = getElementsByTagNameNR(e, "Item");
			for (int i = 0; i < result.length; i++) {
        		writer1.print(result[i].getAttribute("ItemID"));
				writer1.print('\t');
				String name = getElementTextByTagNameNR(result[i], "Name");
				writer1.println(name);
			}

			// Retrieve data for itemCategory table
			for (int i = 0; i < result.length; i++) {
				Element[] resultCategory = getElementsByTagNameNR(result[i], "Category");
				for (int j = 0; j < resultCategory.length; j++) {
				writer2.print(result[i].getAttribute("ItemID"));
				writer2.print('\t');
				String category = getElementText(resultCategory[j]);
				writer2.println(category);
				}
			}

			// Retrieve data for current bidding information
			for (int i = 0; i < result.length; i++) {
				Element resultCurrently = getElementByTagNameNR(result[i], "Currently");
				Element resultFirstBid = getElementByTagNameNR(result[i], "First_Bid");
				Element resultNumOfBids = getElementByTagNameNR(result[i], "Number_of_Bids");
				writer3.print(result[i].getAttribute("ItemID"));
				writer3.print('\t');
				writer3.print(strip(getElementText(resultCurrently)));
				writer3.print('\t');
				writer3.print(strip(getElementText(resultFirstBid)));
				writer3.print('\t');
				writer3.println(getElementText(resultNumOfBids));
			}

			// Retrieve data for user bidder rating information
			for (int i = 0; i < result.length; i++) {
				Element bids = getElementByTagNameNR(result[i], "Bids");
				Element[] bid = getElementsByTagNameNR(bids, "Bid");
				for (int j = 0; j < bid.length; j++) {
					Element resultBidder = getElementByTagNameNR(bid[j], "Bidder");
					writer4.print(resultBidder.getAttribute("UserID"));
					writer4.print('\t');
					writer4.println(resultBidder.getAttribute("Rating"));
					if (getElementTextByTagNameNR(resultBidder, "Location") != "") {
						writer5.print(resultBidder.getAttribute("UserID"));
						writer5.print('\t');
						writer5.print(getElementTextByTagNameNR(resultBidder, "Location"));
						writer5.print('\t');
						writer5.println(getElementTextByTagNameNR(resultBidder, "Country"));
					}
				}
			}

			// Retrieve data for user seller rating information
			for (int i = 0; i < result.length; i++) {
				Element seller = getElementByTagNameNR(result[i], "Seller");
				writer6.print(seller.getAttribute("UserID"));
				writer6.print('\t');
				writer6.println(seller.getAttribute("Rating"));
			}

			// Retrieve item location information and description
			for (int i = 0; i < result.length; i++) {
				writer7.print(result[i].getAttribute("ItemID"));
				writer7.print('\t');
				writer7.print(getElementTextByTagNameNR(result[i], "Location"));
				writer7.print('\t');
				writer7.print(getElementTextByTagNameNR(result[i], "Country"));
				writer7.print('\t');
				writer7.println(getElementTextByTagNameNR(result[i], "Description"));
			}

			// Retrieve item started and ends time
			SimpleDateFormat inputFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < result.length; i++) {
				writer8.print(result[i].getAttribute("ItemID"));
				writer8.print('\t');
				Date date = inputFormat.parse(getElementTextByTagNameNR(result[i], "Started"));
				String timestamp = outputFormat.format(date);
				writer8.print(timestamp);
				writer8.print('\t');
				date = inputFormat.parse(getElementTextByTagNameNR(result[i], "Ends"));
				timestamp = outputFormat.format(date);
				writer8.println(timestamp);
			}

			// Retrieve bid amount
			for (int i = 0; i < result.length; i++) {
				Element bids = getElementByTagNameNR(result[i], "Bids");
				Element[] bid = getElementsByTagNameNR(bids, "Bid");
				for (int j = 0; j < bid.length; j++) {
					Element resultBidder = getElementByTagNameNR(bid[j], "Bidder");
					writer9.print(result[i].getAttribute("ItemID"));
					writer9.print('\t');
					writer9.print(resultBidder.getAttribute("UserID"));
					writer9.print('\t');
					Date date = inputFormat.parse(getElementTextByTagNameNR(bid[j], "Time"));
					String timestamp = outputFormat.format(date);
					writer9.print(timestamp);
					writer9.print('\t');
					writer9.println(strip(getElementTextByTagNameNR(bid[j], "Amount")));
				}
			}

			// Retrieve item latitude and longitude
			for (int i = 0; i < result.length; i++) {
				Element location = getElementByTagNameNR(result[i], "Location");
				if (location.getAttribute("Latitude") != "") {
					writer10.print(result[i].getAttribute("ItemID"));
					writer10.print('\t');
					writer10.print(location.getAttribute("Latitude"));
					writer10.print('\t');
					writer10.println(location.getAttribute("Longitude"));
				}
			}

			// Retrive buy price 
			for (int i = 0; i < result.length; i++) {
				if (getElementTextByTagNameNR(result[i], "Buy_Price") != "") {
					writer11.print(result[i].getAttribute("ItemID"));
					writer11.print('\t');
					writer11.println(strip(getElementTextByTagNameNR(result[i], "Buy_Price")));
				}
			}
		}
		catch (Exception err) {
			System.out.println(err.getMessage());
		}
        
        /**************************************************************/
    }
    
    public static void main (String[] args) throws FileNotFoundException {
		
		try {
 			writer1 = new PrintWriter("itemName.txt", "UTF-8");
			writer2 = new PrintWriter("itemCategory.txt", "UTF-8");
			writer3 = new PrintWriter("bidInformation.txt", "UTF-8");
			writer4 = new PrintWriter("bidderRating.txt", "UTF-8");
			writer5 = new PrintWriter("bidderLocation.txt", "UTF-8");
			writer6 = new PrintWriter("sellerRating.txt", "UTF-8");
			writer7 = new PrintWriter("itemInformation.txt", "UTF-8");
			writer8 = new PrintWriter("itemTime.txt", "UTF-8");
			writer9 = new PrintWriter("itemBidAmount.txt", "UTF-8");
			writer10 = new PrintWriter("itemLatitude.txt", "UTF-8");
			writer11 = new PrintWriter("itemBuyPrice.txt", "UTF-8");
		}
		catch (Exception err) {
			System.out.println(err.getMessage());
		}
		
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }

		writer1.close();
		writer2.close();
		writer3.close();
		writer4.close();
		writer5.close();
		writer6.close();
		writer7.close();
		writer8.close();
		writer9.close();
		writer10.close();
		writer11.close();
    }
}
