package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) throws IOException,ParseException {
		// Instantiate the search engine
		SearchEngine se = new SearchEngine();
		// A list for appending search result
		List<SearchResult> list = new ArrayList<SearchResult>();

		int totalNum = numResultsToSkip + numResultsToReturn;
		TopDocs topDocs = se.performSearch("name:" + query + " OR category:" + query + " OR description:" + query, totalNum);

		// Obtain the ScoreDocs (=documentID, relevanceScore) array from topDocs
		ScoreDoc[] hits = topDocs.scoreDocs;
		
		// Show the total matching tuples
		System.out.println("The number of matching tuples: " + topDocs.totalHits);

		// Retrieve each matching document from the ScoreDoc array
		for (int i = numResultsToSkip; i < hits.length && i < topDocs.totalHits; i++) {
			Document doc = se.getDocument(hits[i].doc);	
			list.add(new SearchResult(doc.get("itemID"), doc.get("name")));
		}

		SearchResult[] result = new SearchResult[list.size()];
		result = list.toArray(result);
		return result;
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) throws IOException,ParseException {
		
		// The connection for querying the database
		Connection conn = null;
		// The array for storing the final result
		SearchResult[] result = null;
		// The list for temporarily storing the spatial search result
		List<SearchResult> list = new ArrayList<SearchResult>();
		// The result of keyword search from lucene indexing
		SearchResult[] luceneResult = basicSearch(query, 0, 20000);
		// Create a connection to the database
		try {
			conn = DbManager.getConnection(true);
			// Create a statement to query the database
			for (SearchResult r : luceneResult) {
				String itemId = r.getItemId();
				Statement stmt = conn.createStatement();
				stmt.executeQuery("set @region = GeomFromText('Polygon((" + 
					region.getLx() + " " + region.getLy() + "," +
					region.getLx() + " " + region.getRy() + "," +
					region.getRx() + " " + region.getRy() + "," +
					region.getRx() + " " + region.getLy() + "," + 
					region.getLx() + " " + region.getLy() +
					"))')"
				);
				stmt.executeQuery("set @point = GeomFromText((select aswkt(pt) from geom where ItemID = " + itemId + "))");
				ResultSet rs = stmt.executeQuery("select MBRWithin(@point, @region) as answer");
				while (rs.next()) {
					if (rs.getInt("answer") == 1) { 
						list.add(new SearchResult(r.getItemId(), r.getName()));
					}
				}
			}
			result = new SearchResult[list.size()];
			result = list.toArray(result);
		} catch (SQLException ex) {
			System.out.println(ex);
		}

		// Close the database connection
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		return result;
	}

	public String getXMLDataForItemId(String itemId) throws ParseException {
		String xmlString = "";
		// The connection for querying the database
		Connection conn = null;
		try {
			conn = DbManager.getConnection(true);
			Statement stmt = conn.createStatement();

			// ItemID and Name 
			ResultSet rs = stmt.executeQuery("select Name from itemName where ItemID = " + itemId);
			if (!rs.next()) return "";
			else {
				xmlString = xmlString + "<Item ItemID=\"" + itemId + "\">\n";
				String parsed = parseString(rs.getString("Name"));
				xmlString = xmlString + "\t<Name>" + parsed + "</Name>\n";
			}

			// Category
			rs = stmt.executeQuery("select Category from itemCategory where ItemID =" + itemId);
			while (rs.next()) {
				String parsed = parseString(rs.getString("Category"));
				xmlString = xmlString + "\t<Category>" + parsed + "</Category>\n";
			}

			// Currently
			rs = stmt.executeQuery("select Currently from bidInformation where ItemID =" + itemId);
			if (rs.next()) {
				xmlString = xmlString + "\t<Currently>$" + rs.getString("Currently") + "</Currently>\n";
			}

			// Buy_Price
			rs = stmt.executeQuery("select Buy_Price from itemBuyPrice where ItemID =" + itemId);
			if (rs.next()) {
				xmlString = xmlString + "\t<Buy_Price>$" + rs.getString("Buy_Price") + "</Buy_Price>\n";
			}

			// First_Bid and Number_of_Bids
			rs = stmt.executeQuery("select First_Bid,Number_of_Bids from bidInformation where ItemID =" + itemId);
			if (rs.next()) {
				xmlString = xmlString + "\t<First_Bid>$" + rs.getString("First_Bid") + "</First_Bid>\n";
				xmlString = xmlString + "\t<Number_of_Bids>" + rs.getString("Number_of_Bids") + "</Number_of_Bids>\n";
			}

			// Bids
			rs = stmt.executeQuery("select UserID,Time,Amount from itemBidAmount where ItemID =" + itemId);
			if (!rs.next())
				xmlString = xmlString + "\t<Bids />\n";
			else {
				xmlString = xmlString + "\t<Bids>\n";
			   	do {
				String amount = rs.getString("Amount");
				String time = rs.getString("Time");
				String id = rs.getString("UserID");
				xmlString = xmlString + "\t\t<Bid>\n";
				String parsed = parseString(rs.getString("UserID"));
				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery("select BidderRating from bidderRating where UserID =\'" + rs.getString("UserID") + "\' and ItemID =" + itemId);
				if (rs2.next())
					xmlString = xmlString + "\t\t\t<Bidder Rating=\"" + rs2.getString("BidderRating") + "\" UserID=\"" + parsed + "\">\n";
				Statement stmt3 = conn.createStatement();
				ResultSet rs3 = stmt3.executeQuery("select Location,Country from bidderLocation where UserID =\'" + id + "\'");
				if (rs3.next()) {
						xmlString = xmlString + "\t\t\t\t<Location>" + rs3.getString("Location") + "</Location>\n";
						xmlString = xmlString + "\t\t\t\t<Country>" + rs3.getString("Country") + "</Country>\n";
				}
				xmlString = xmlString + "\t\t\t</Bidder>\n";
				try {
					SimpleDateFormat outputFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
               	 	SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date timestamp = inputFormat.parse(time);
					String date = outputFormat.format(timestamp);
					xmlString = xmlString + "\t\t\t<Time>" + date + "</Time>\n";
					xmlString = xmlString + "\t\t\t<Amount>$" + amount + "</Amount>\n";
				} catch (Exception err) {
					System.out.println(err.getMessage());
				}
				xmlString = xmlString + "\t\t</Bid>\n";
				} while (rs.next());
				xmlString = xmlString + "\t</Bids>\n";
			}

			// Location and Country
			rs = stmt.executeQuery("select Location,Country from itemInformation where ItemID =" + itemId);
			Statement stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery("select Latitude,Longitude from itemLatitude where ItemID =\'" + itemId + "\'");
			if (rs.next()) {
				if (rs2.next()) {
					String parsed = parseString(rs.getString("Location"));
					xmlString = xmlString + "\t<Location Latitude=\"" + rs2.getString("Latitude") + "\" Longitude=\"" + rs2.getString("Longitude") + "\">" + parsed + "</Location>\n";
					parsed = parseString(rs.getString("Country"));
					xmlString = xmlString + "\t<Country>" + parsed + "</Country>\n";
				}
				else {
					String parsed = parseString(rs.getString("Location"));
					xmlString = xmlString + "\t<Location>" + parsed + "</Location>\n";
					parsed = parseString(rs.getString("Country"));
					xmlString = xmlString + "\t<Country>" + parsed + "</Country>\n";
				}
			}

			// Started and Ends
			rs = stmt.executeQuery("select Started,Ends from itemTime where ItemID =" + itemId);
			if (rs.next()) {
				try {
					SimpleDateFormat outputFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
                	SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date timestamp = inputFormat.parse(rs.getString("Started"));
					String date = outputFormat.format(timestamp);
					xmlString = xmlString + "\t<Started>" + date + "</Started>\n";
					timestamp = inputFormat.parse(rs.getString("Ends"));
                	date = outputFormat.format(timestamp);
					xmlString = xmlString + "\t<Ends>" + date + "</Ends>\n";
				} catch (Exception err) {
					System.out.println(err.getMessage());
				}
			}
			
			// seller 
			rs = stmt.executeQuery("select UserID,SellerRating from sellerRating where ItemID =" + itemId);
			if (rs.next()) {
				String parsed = parseString(rs.getString("UserID"));
				xmlString = xmlString + "\t<Seller Rating=\"" + rs.getString("SellerRating") + "\" UserID=\"" + parsed + "\" />\n";
			}
			// description
			rs = stmt.executeQuery("select Description from itemInformation where ItemID =" + itemId);
			if (rs.next()) {
				String parsed = parseString(rs.getString("Description"));
				xmlString = xmlString + "\t<Description>" + parsed + "</Description>\n";
			}
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		xmlString = xmlString + "</Item>\n";
		return xmlString;
	}
	
	public String parseString(String str) {
		str = str.replace("&", "&amp;");
		str = str.replace("\"", "\\\"");
		str = str.replace("'", "&apos");
		str = str.replace("<", "&lt");
		str = str.replace(">", "&gt");
		str = str.replace("\\", "\\\\"); 
		return str;
	}
	public String echo(String message) {
		return message;
	}
}
