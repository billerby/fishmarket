package models;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.joda.money.Money;
import org.joda.time.DateTime;

import play.db.jpa.JPA;
import play.db.jpa.Model;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "quotationDate",
		"species" }) })
public class Quotation extends Model implements Comparable<Quotation>{
	public Date quotationDate;
	public Fish species;
	public Money avgPrice;
	public Money maxPrice;
	public int boxes;
	public int kilos;
	@Override
	public int compareTo(Quotation o) {
		if (o != null && o.species != null){
			return o.species.name.compareTo(this.species.name);
		}else {
			return 0;
		}
	}

	public static List<Quotation> getQuotationsForLastMonth(Fish species){
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		Date aMonthAgo = new DateTime().minusMonths(1).toDate();
		
		CriteriaQuery<Quotation> query = builder.createQuery(Quotation.class);
		Root<Quotation> e = query.from(Quotation.class);
		Path<Date> quotationDate = e.get(Quotation_.quotationDate);
		Path<Fish> s = e.get(Quotation_.species);
		query.where(builder.and(
				builder.greaterThanOrEqualTo(quotationDate, aMonthAgo),
				builder.equal(s, species)));
		return JPA.em().createQuery(query).getResultList();
	}
	
	public static List<Quotation> getQuotations(Fish species){
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		//Date aMonthAgo = new DateTime().minusMonths(1).toDate();
		
		CriteriaQuery<Quotation> query = builder.createQuery(Quotation.class);
		Root<Quotation> e = query.from(Quotation.class);
		//Path<Date> quotationDate = e.get(Quotation_.quotationDate);
		Path<Fish> s = e.get(Quotation_.species);
		query.where(builder.and(
				//builder.greaterThanOrEqualTo(quotationDate, aMonthAgo),
				builder.equal(s, species)));
		List<Quotation> quotations = JPA.em().createQuery(query).getResultList();
		Collections.sort(quotations,dateComparator);
		return quotations;
	}
	
    public static final Comparator<Quotation> dateComparator = new Comparator<Quotation>(){

        @Override
        public int compare(Quotation o1, Quotation o2) {
        	// descending
            return o2.quotationDate.compareTo(o1.quotationDate);
        }
      
    };

}
