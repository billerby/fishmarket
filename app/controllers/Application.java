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

		render();
	}


}