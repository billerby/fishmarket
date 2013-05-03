package controllers;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import models.Fish;
import models.Quotation;

import org.joda.time.DateTime;

import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
import utils.DateTypeAdapter;
import utils.DateUtil;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
		List<Quotation> quotations = Quotation.find("bySpecies", fish).fetch();
		Collections.sort(quotations);
		DateTypeAdapter adapters = new DateTypeAdapter();
		renderJSON(quotations, adapters);
		
	}
	
	
}
