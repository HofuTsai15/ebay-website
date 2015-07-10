package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }

	private Connection conn = null;
	private IndexWriter indexWriter = null;

	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}
 
	public IndexWriter getIndexWriter(boolean create) throws IOException {

		if (indexWriter == null) {
			Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/index1"));
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
			indexWriter = new IndexWriter(indexDir, config);

			indexWriter.deleteAll();
			indexWriter.commit();
		}
		return indexWriter;
	}

	public void indexRows() throws SQLException,IOException {

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT N.ItemID,Name,Category,Description from itemName N,itemCategory C,itemInformation F where N.ItemID = C.ItemID AND C.ItemID = F.ItemID");
		int i = 1;
		String idStr = "";
		String nameStr = "";
		String categoryStr = "";
		String descriptionStr = "";
		while (rs.next()) {
			if (idStr.equals("")) {
				idStr = rs.getString("ItemID");
				nameStr = rs.getString("Name");
				categoryStr = rs.getString("Category");
				descriptionStr = rs.getString("Description");
			}
			else if (idStr.equals(rs.getString("ItemID"))) {
				categoryStr = categoryStr + " " + rs.getString("Category");
			}
			else if (!idStr.equals(rs.getString("ItemID"))) {
				System.out.println("Indexing the row: " + i);
				IndexWriter writer = getIndexWriter(false);
				Document doc = new Document();
				doc.add(new StringField("itemID", idStr, Field.Store.YES));
				doc.add(new TextField("name", nameStr, Field.Store.YES));
				doc.add(new TextField("category", categoryStr, Field.Store.YES));
				doc.add(new TextField("description", descriptionStr, Field.Store.NO));
				writer.addDocument(doc);
				i++;
				idStr = rs.getString("ItemID");
				nameStr = rs.getString("Name");
				categoryStr = rs.getString("Category");
				descriptionStr = rs.getString("Description");
			}
		}
		System.out.println("Indexing the row: " + i);
		IndexWriter writer = getIndexWriter(false);
		Document doc = new Document();
		doc.add(new StringField("itemID", idStr, Field.Store.YES));
		doc.add(new TextField("name", nameStr, Field.Store.YES));
		doc.add(new TextField("category", categoryStr, Field.Store.YES));
		doc.add(new TextField("description", descriptionStr, Field.Store.NO));
		writer.addDocument(doc);
	}

    public void rebuildIndexes() throws IOException, SQLException {
        // create a connection to the database to retrieve Items from MySQL
	try {
	    this.conn = DbManager.getConnection(true);
	} catch (SQLException ex) {
	    System.out.println(ex);
	}

	/*
	 * Add your code here to retrieve Items using the connection
	 * and add corresponding entries to your Lucene inverted indexes.
         *
         * You will have to use JDBC API to retrieve MySQL data from Java.
         * Read our tutorial on JDBC if you do not know how to use JDBC.
         *
         * You will also have to use Lucene IndexWriter and Document
         * classes to create an index and populate it with Items data.
         * Read our tutorial on Lucene as well if you don't know how.
         *
         * As part of this development, you may want to add 
         * new methods and create additional Java classes. 
         * If you create new classes, make sure that
         * the classes become part of "edu.ucla.cs.cs144" package
         * and place your class source files at src/edu/ucla/cs/cs144/.
	 * 
	 */

	// Erase existing index
	getIndexWriter(true);

	// Index all entries
	indexRows();

	// Close the index writer when done
	closeIndexWriter();

    // close the database connection
	try {
	    this.conn.close();
	} catch (SQLException ex) {
	    System.out.println(ex);
	}
    }    

    public static void main(String args[]) throws IOException, SQLException {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
