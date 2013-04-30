package controllers;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import jobs.FetchPrices;
import models.Fish;
import models.Quotation;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import play.Logger;
import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
import utils.DateUtil;

public class Application extends Controller {

	public static void index() {

		renderSpecies();
	}

	public static void fetchPrices() {
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
		index();
	}

	public static void renderSpecies() {

		Date maxDate = Quotation.find("select max(quotationDate) from Quotation").first();
		
		List<Quotation> lastQuotations = Quotation.find("byQuotationDate", maxDate).fetch();
		List<Fish> species = Fish.findAll();
		ValuePaginator paginator = new ValuePaginator(lastQuotations);
		paginator.setPageSize(15);
		
		String maxDateString = DateUtil.getDateAsString(new DateTime(maxDate), false);
		render(paginator, maxDateString);

	}

}