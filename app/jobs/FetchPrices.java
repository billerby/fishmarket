package jobs;

import java.io.IOException;

import models.Fish;
import models.Quotation;

import org.apache.commons.lang.StringUtils;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import utils.DateUtil;

@Every("5mn")
public class FetchPrices extends Job {
	public void doJob() {
		Logger.info("Fetching prices from the fish market ...");
		Document doc;
		DateTime auctionDate;

		try {
			doc = Jsoup.connect("http://www.gfa.se/prisnotering.htm").get();

			Element dateTable = doc.select("table").get(1);
			String dateString = dateTable.select("tr > td").get(3).text();
			auctionDate = DateUtil.createDateFromString(dateString, false);

			// Do we already have prices for this date?
			Quotation quotation = Quotation
					.find("byQuotationDate", auctionDate.toDate()).first();

			if (quotation == null) {
				Logger.info("We do not have quotations for "+DateUtil.getDateAsString(auctionDate, false)+", lets fetch those...");
				Elements tables = doc.getElementsByTag("table");
				FetchPrices.extractPriceData(tables, true, auctionDate);

				System.out.println("Calling next page:");
				// Fetch the following two pages (never exceeds three pages)
				for (int pageNo = 2; pageNo < 4; pageNo++) {
					String pageUrl = "http://www.gfa.se/prisnoteringSida"+pageNo+".htm";
					Logger.info("Fetching prices from "+pageUrl);
					doc = Jsoup.connect(pageUrl)
							.get();
					Element dateTable2 = doc.select("table").get(0);
					String dateString2 = dateTable2.select("tr > td").get(2).text();
					DateTime auctionDate2 = DateUtil.createDateFromString(
							dateString2, false);
					Logger.debug("Next page date: " + dateString2);
					// Check that this page is not holding prices from an earlier date
					if (auctionDate.isEqual(auctionDate2)) {
						FetchPrices.extractPriceData(doc.getElementsByTag("table"),
								false, auctionDate);
					}else{
						Logger.info(pageUrl+ " holds quotations for another date, and seems to just linger around, ignore it...");
					}
				}
			}else {
				// existing
				Logger.info("Quotations for "+DateUtil.getDateAsString(auctionDate, false)+" already fetched.");
			}

		} catch (IOException e) {
			Logger.error("An error occurred when trying to fetch prices from fish market", e);
		}

	}

	public static void extractPriceData(Elements tables, boolean pageOne,
			DateTime auctionDate) {
		int i = 0;

		int tableBeginIndex = 14;
		if (!pageOne) {
			tableBeginIndex = 4;
		}
		for (Element table : tables) {
			// Find the starting table for prices
			try {
				String headerCell = table.select("tr > td").get(1).text();
				if (StringUtils.contains(headerCell, "FISKNAMN")){
					if (pageOne){ // page one has an extra row in the top of the table for some reason (average for whole day???)
						tableBeginIndex = i + 4;
					}else {
						tableBeginIndex = i + 3;
					}
				}
			}catch (IndexOutOfBoundsException e){
				// Since the html is not looking the same all time we need to catch this exception.
				Logger.info("ugly code", e);
			}
			
			outerloop:
				
			
			// tableBeginIndex tells which table holds first quotation
			if (i >= tableBeginIndex) {
				Elements tds = table.getElementsByTag("td");
				int x = 0;
				Quotation quotation = new Quotation();
				quotation.quotationDate = auctionDate.toDate();
				for (Element td : tds) {

					if (x == 1) {
						// Fish fish = new Fish();
						// fish.name = td.text();
						// quotation.species = fish;

						Logger.debug("Fisksort: " + td.text());
						// Lookup the fish
						Fish fish = Fish.find("byName", td.text()).first();
						if (fish == null) {
							// The last row on the last page is not a fish...:)
							if (!td.text().startsWith("TILL PRISERNA")) {
								Logger.info(td.text()
										+ " is a new fish not present i our db, lets add it.");
								fish = new Fish(td.text());
								fish.save();
							}else{
								// A bit hackish (like all this stuff...)
								break outerloop;
							}
						}

						quotation.species = fish;

					} else if (x == 2) {

						// Wash the amount
						quotation.avgPrice = washAmount(td.text());
						// Average

						Logger.debug("Medelpris: " + quotation.avgPrice);
					} else if (x == 3) {
						// max
						quotation.maxPrice = washAmount(td.text());
						Logger.debug("Maxpris: " + quotation.maxPrice);

					} else if (x == 4) {
						// kilo
						quotation.kilos = Integer.parseInt(td.text());
						Logger.debug("Kilo: " + quotation.kilos);
					} else if (x == 5) {
						// boxes
						quotation.boxes = Integer.parseInt(td.text());
						Logger.debug("Boxes: " + quotation.boxes);
					}
					
					x++;
				}
				Logger.info("Saving quotation");
				quotation.save();
			}
			

			i++;
		}
	}

	private static Money washAmount(String htmlPrice) {
		System.out.println(htmlPrice);
		if (htmlPrice != null) {
			int krPosition = htmlPrice.indexOf('k') - 1;
			String tmpAmount = htmlPrice.substring(0, krPosition);
			// replace comma with dot
			tmpAmount = tmpAmount.replace(',', '.');
			return Money.parse("SEK " + tmpAmount);

		}

		return null;
	}
}
