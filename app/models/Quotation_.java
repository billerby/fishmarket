package models;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;

import org.joda.money.Money;

@javax.persistence.metamodel.StaticMetamodel(models.Quotation.class)
public class Quotation_ {
	public static volatile SingularAttribute<Quotation, Date> quotationDate;
	public static volatile SingularAttribute<Quotation, Fish> species;
	public static volatile SingularAttribute<Quotation, Money> avgPrice;
	public static volatile SingularAttribute<Quotation, Money> maxPrice;
	public static volatile SingularAttribute<Quotation, Integer> boxes;
	public static volatile SingularAttribute<Quotation, Integer> kilos;
}
