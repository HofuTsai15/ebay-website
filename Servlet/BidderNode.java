package edu.ucla.cs.cs144;

public class BidderNode {
	private String bidderId;
	private String bidderRating;
	private String bidderLocation;
	private String bidderCountry;
	private String time;
	private String amount;

	public BidderNode() {}

	public BidderNode(String bidderId, String bidderRating, String bidderLocation, String bidderCountry, 
		String time, String amount) {
		this.bidderId = bidderId;
		this.bidderRating = bidderRating;
		this.bidderLocation = bidderLocation;
		this.bidderCountry = bidderCountry;
		this.time = time;
		this.amount = amount;
	}
	
	public String getBidderId() {
		return bidderId;
	}

	public void setBidderId(String bidderId) {
		this.bidderId = bidderId;
	}

	public String getBidderRating() {
		return bidderRating;
	}

	public void setBidderRating(String bidderRating) {
		this.bidderRating = bidderRating;
	}

	public String getBidderLocation() {
		return bidderLocation;
	}

	public void setBidderLocation(String bidderLocation) {
		this.bidderLocation = bidderLocation;
	}

	public String getBidderCountry() {
		return bidderCountry;
	}

	public void setBidderCountry(String bidderCountry) {
		this.bidderCountry = bidderCountry;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
}
