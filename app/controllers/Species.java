package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Fish;
import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;

public class Species extends Controller {
	public static void list() {
		List<Fish> species = Fish.findAll();
		ValuePaginator paginator = new ValuePaginator(species);
		paginator.setPageSize(15);
		render(paginator);
	}
	public static void listJson() {
		List<Fish> species = Fish.findAll();
		List<Fish> fishes = new ArrayList();
		for (Fish fish : species){
			fishes.add(fish);
		}
		renderJSON(fishes);
	}
}
