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
import play.jobs.Job;
import play.jobs.On;
import utils.DateUtil;

/** Fire at 12pm (noon) every day **/
@On("0 0 10 * * ?")
public class FetchPrices extends Job {
	public void doJob() {
		Logger.info("Fetching prices from the fish market ...");
		Document doc;

		try {
			doc = Jsoup.connect("http://www.gfa.se/prisnotering.htm").get();

			Element dateTable = doc.select("table").get(1);
			String dateString = dateTable.select("tr > td").get(3).text();
			DateTime auctionDate = DateUtil.createDateFromString(dateString,
					false);

			Elements tables = doc.getElementsByTag("table");

			for (Element table : tables) {

				Elements tds = table.getElementsByTag("td");
				for (Element td : tds) {
					Logger.debug(td.text());

				}
			}
		} catch (IOException e) {
			Logger.error(e, "Failed scraping prices from the fish market");
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
