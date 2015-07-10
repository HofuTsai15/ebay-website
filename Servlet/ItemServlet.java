package edu.ucla.cs.cs144;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.Arrays;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.xml.sax.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import javax.lang.model.element;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		HttpSession session = request.getSession(true);
		String creditCard = (String)request.getParameter("creditCard");
		if (creditCard == null || creditCard.isEmpty()) {
			AuctionSearchClient client = new AuctionSearchClient();
			String itemId = request.getParameter("id");
			String xml = client.getXMLDataForItemId(itemId);

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			Document doc = builder.parse(is);
		
			// extract item node
			NodeList nodes = doc.getElementsByTagName("Item");
			request.setAttribute("id", itemId);
			// Store the information into http session
			session.setAttribute("id", itemId);

			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element)nodes.item(i);
				// For item name
				NodeList names = element.getElementsByTagName("Name");
				request.setAttribute("name", names.item(0).getTextContent());
				// Store the information into http session
				session.setAttribute("name", names.item(0).getTextContent());


				// For item Category
				NodeList category = element.getElementsByTagName("Category");
				String[] categoryStr = new String[category.getLength()];
				for (int j = 0; j < category.getLength(); j++) {
					categoryStr[j] = category.item(j).getTextContent();
				}
				request.setAttribute("category", categoryStr);

				// For currently
				NodeList currently = element.getElementsByTagName("Currently");
				request.setAttribute("currently", currently.item(0).getTextContent());

				// For buy price
				NodeList buy_price = element.getElementsByTagName("Buy_Price");
				if (buy_price.getLength() > 0) {
					request.setAttribute("buy_price", buy_price.item(0).getTextContent());
					// Store the information into http session
					session.setAttribute("buy_price", buy_price.item(0).getTextContent());
				}

				// For first bid
				NodeList first_bid = element.getElementsByTagName("First_Bid");
				request.setAttribute("first_bid", first_bid.item(0).getTextContent());
				
				// For number of bids
				NodeList number_of_bids = element.getElementsByTagName("Number_of_Bids");
				request.setAttribute("number_of_bids", number_of_bids.item(0).getTextContent());
				
				// For location
				NodeList locationList = element.getChildNodes();
				//Element location;
				for (int j = 0; j < locationList.getLength(); j++) {
					if (locationList.item(j).getNodeName() == "Location") {
						Element location = (Element) locationList.item(j);

						String attrLatitude = location.getAttribute("Latitude");
						String attrLongitude = location.getAttribute("Longitude");
						request.setAttribute("itemLocation", location.getTextContent());
						if (attrLatitude != null && !attrLatitude.isEmpty()) {
							request.setAttribute("latitude", attrLatitude);
							request.setAttribute("longitude", attrLongitude);
						}
					}
				}

				// For Country
				NodeList countryList = element.getChildNodes();
				for (int j = 0; j < countryList.getLength(); j++) {
					if (countryList.item(j).getNodeName() == "Country") {
						Element country = (Element) countryList.item(j);
						request.setAttribute("country", country.getTextContent());
					}
				}
				
				// For start time 
				NodeList started = element.getElementsByTagName("Started");
				request.setAttribute("started", started.item(0).getTextContent());

				// For end time
				NodeList ends = element.getElementsByTagName("Ends");
				request.setAttribute("ends", ends.item(0).getTextContent());
	
				// For seller information		
				NodeList seller = element.getElementsByTagName("Seller");
				Element attr = (Element) seller.item(0);
				request.setAttribute("sellerId", attr.getAttributeNode("UserID").getValue());
				request.setAttribute("sellerRating", attr.getAttributeNode("Rating").getValue());

				// For discription
				NodeList description = element.getElementsByTagName("Description");
				request.setAttribute("description", description.item(0).getTextContent());

				// Bidding history
				NodeList bids = element.getElementsByTagName("Bids");
				NodeList bid = ((Element) bids.item(0)).getElementsByTagName("Bid");

				// Convert a nodelist to an array
				int length = bid.getLength();
				Node[] array = new Node[length];
				for (int n = 0; n < length; n++)
					array[n] = bid.item(n);

				Arrays.sort(array, new MyComparator());
				BidderNode[] bidderNode = new BidderNode[array.length];

				for (int j = 0; j < array.length; j++) {
					// Bidder id and rating 
					NodeList bidder = ((Element) array[j]).getElementsByTagName("Bidder");
					String rating = ((Element) bidder.item(0)).getAttribute("Rating");
					String id = ((Element) bidder.item(0)).getAttribute("UserID");
					System.out.println(Integer.toString(j) + ":" + id);
					request.setAttribute("bidderId", id);
					request.setAttribute("bidderRating", rating);
					// Bidder location and country
					String bidderLocation = ((Element) bidder.item(0)).getElementsByTagName("Location").item(0).getTextContent();
					String bidderCountry = ((Element) bidder.item(0)).getElementsByTagName("Country").item(0).getTextContent();
					request.setAttribute("bidderLocation", bidderLocation);
					request.setAttribute("bidderCountry", bidderCountry);
					// Bid time and amount
					String time = ((Element) array[j]).getElementsByTagName("Time").item(0).getTextContent();
					String amount = ((Element) array[j]).getElementsByTagName("Amount").item(0).getTextContent();
					request.setAttribute("time", time);
					request.setAttribute("amount", amount);
					bidderNode[j] = new BidderNode(id, rating, bidderLocation, bidderCountry, time, amount);
				}
				request.setAttribute("bidderNode", bidderNode);
		}
		} catch (Exception e) {
			System.err.println("Caught Exception: " + e.getMessage());
		}
		request.getRequestDispatcher("/getItem.jsp").forward(request, response);
		} else {
			// 1) create a java calendar instance
			Calendar calendar = Calendar.getInstance();
			 
			// 2) get a java.util.Date from the calendar instance.
			//    this date will represent the current instant, or "now".
			java.util.Date now = calendar.getTime();
			  
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = outputFormat.format(now);
			session.setAttribute("creditCard", creditCard);
			session.setAttribute("time", currentTime);
			request.getRequestDispatcher("/confirmation.jsp").forward(request, response);
		}
    }

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		doGet(request, response);
	}
}
