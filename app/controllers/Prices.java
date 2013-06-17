package controllers;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import models.Fish;
import models.Quotation;

import org.joda.time.DateTime;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
import utils.DateTypeAdapter;
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
		
		String dateToFetchString = DateUtil.getDateAsString(new DateTime(dateToFetch), false);
		render(paginator, dateToFetchString);

	}
	
	public static void search(){
		render();
	}
	
	
	public static void getDataForSpecies(String fishId){
		Fish fish = Fish.findById(new Long(fishId));
		
		List<Quotation> quotations = Quotation.getQuotationsForLastMonth(fish);
		
		Collections.sort(quotations);
		DateTypeAdapter adapters = new DateTypeAdapter();
		renderJSON(quotations, adapters);
		
	}
	
	
}
