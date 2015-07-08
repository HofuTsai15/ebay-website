#!/bin/bash

# Run the drop.sql batch file to drop existing tables
# Inside the drop.sql, you sould check whether the table exists. Drop them ONLY if they exists.
mysql CS144 < drop.sql

# Run the create.sql batch file to create the database and tables
mysql CS144 < create.sql

# Compile and run the parser to generate the appropriate load files
ant
ant run-all

# If the Java code does not handle duplicate removal, do this now
sort -u --output=bidInformation.txt bidInformation.txt
sort -u --output=bidderLocation.txt bidderLocation.txt
sort -u --output=bidderRating.txt bidderRating.txt
sort -u --output=itemBidAmount.txt itemBidAmount.txt
sort -u --output=itemBuyPrice.txt itemBuyPrice.txt
sort -u --output=itemCategory.txt itemCategory.txt
sort -u --output=itemInformation.txt itemInformation.txt
sort -u --output=itemLatitude.txt itemLatitude.txt
sort -u --output=itemName.txt itemName.txt
sort -u --output=itemTime.txt itemTime.txt
sort -u --output=sellerRating.txt sellerRating.txt

# Run the load.sql batch file to load the data
mysql CS144 < load.sql

# Remove all temporary files
rm bidInformation.txt
rm bidderLocation.txt
rm bidderRating.txt
rm itemBidAmount.txt
rm itemBuyPrice.txt
rm itemCategory.txt
rm itemInformation.txt
rm itemLatitude.txt
rm itemName.txt
rm itemTime.txt
rm sellerRating.txt

