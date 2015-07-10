package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		// User input query
		String q = request.getParameter("q");
		// Google suggest service
		String urlEncode = java.net.URLEncoder.encode(q, "UTF-8");
		String url = "http://google.com/complete/search?output=toolbar&q=" + urlEncode;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		String responseMsg = con.getResponseMessage();
		System.out.println("\nSending 'GET' request to URL: " + url);
		System.out.println("Rsponse Code: " + responseCode + "  " + responseMsg);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer xmlResponse = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
			xmlResponse.append(inputLine);
		}
		in.close();

		// Set the response content type
		response.setContentType("text/xml");
		// Write to httpservlet response
		PrintWriter out = response.getWriter();
		out.write(xmlResponse.toString());
    }
}
