create table itemName (
	ItemID varchar(20) not null, 
	Name varchar(100), 
	primary key (ItemID) );

create table itemCategory (
	ItemID varchar(20) not null, 
	Category varchar(50), 
	primary key (ItemID, Category) );

create table bidInformation (
	ItemID varchar(20) not null, 
	Currently decimal(8,2), 
	First_Bid decimal(8,2), 
	Number_of_Bids integer, 
	primary key (ItemID) );

create table sellerRating (
	UserID varchar(50) not null, 
	SellerRating integer, 
	primary key (UserID) );

create table bidderRating (
	UserID varchar(50) not null, 
	BidderRating integer, 
	primary key (UserID) );

create table itemInformation (
	ItemID varchar(50) not null, 
	Location varchar(50), 
	Country varchar(20), 
	Description varchar(4000), 
	primary key (ItemID) );

create table itemTime (
	ItemID varchar(50) not null, 
	Started timestamp, 
	Ends timestamp, 
	primary key (ItemID) );

create table itemLatitude (
	ItemID varchar(50) not null, 
	Latitude decimal(9,6), 
	Longitude decimal(9,6), 
	primary key (ItemID) );

create table itemBidAmount (
	ItemID varchar(50) not null, 
	UserID varchar(50), 
	Time timestamp, 
	Amount decimal(8,2), 
	primary key (ItemID, UserID, Time) );

create table itemBuyPrice (
	ItemID varchar(50) not null, 
	Buy_Price decimal(8,2), 
	primary key (ItemID) );

create table bidderLocation (
	UserID varchar(50) not null, 
	Location varchar(50),
	Country varchar(20), 
	primary key (UserID) );


