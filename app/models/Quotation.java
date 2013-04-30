package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.joda.money.Money;

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
		if (o != null && o.quotationDate != null){
			return o.quotationDate.compareTo(quotationDate);
		}else {
			return 0;
		}
	}
}
