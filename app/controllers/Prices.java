package controllers;

import java.util.Date;
import java.util.List;

import models.Fish;
import models.Quotation;

import org.joda.time.DateTime;

import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
import utils.DateUtil;

public class Prices extends Controller {
	
	public static void list(String simpleDate) {

		Date dateToFetch = null;
		if (simpleDate == null){
			dateToFetch = Quotation.find("select max(quotationDate) from Quotation").first();
		}else {
			dateToFetch = DateUtil.createDateFromString(simpleDate, false).toDate();
		}
		List<Quotation> lastQuotations = Quotation.find("byQuotationDate", dateToFetch).fetch();
		ValuePaginator paginator = new ValuePaginator(lastQuotations);
		paginator.setPageSize(15);
		
		String maxDateString = DateUtil.getDateAsString(new DateTime(dateToFetch), false);
		render(paginator, maxDateString);

	}
	
	public static void search(){
		render();
	}

}
