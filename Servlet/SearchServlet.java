package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		AuctionSearchClient client = new AuctionSearchClient();
		
		// Extracting parameter from the query passed in 
		String query = request.getParameter("q");
		String numSkip = request.getParameter("numResultsToSkip");
		String numReturn = request.getParameter("numResultsToReturn");
		// Set them to request attribute
		request.setAttribute("query", query);
		request.setAttribute("numSkip", numSkip);
		request.setAttribute("numReturn", numReturn);
		// Calling basic search service
		SearchResult[] result = client.basicSearch(query, Integer.parseInt(numSkip), Integer.parseInt(numReturn));
		request.setAttribute("numResult", result.length);
		request.setAttribute("searchResult", result);
		request.getRequestDispatcher("/keywordSearch.jsp").forward(request, response);
    }
}
