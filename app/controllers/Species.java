package controllers;

import java.util.Date;
import java.util.List;

import models.Fish;
import models.Quotation;

import org.joda.time.DateTime;

import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
import utils.DateUtil;

public class Species extends Controller {
	public static void list() {

		List<Fish> species = Fish.findAll();
		ValuePaginator paginator = new ValuePaginator(species);
		paginator.setPageSize(15);

		
		render(paginator);

	}
}
