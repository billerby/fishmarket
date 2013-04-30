package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import play.data.validation.Required;
import play.db.jpa.Model;
@Entity
public class Fish extends Model {
	@Required
	public String name;
	
	
	public Fish(String name){
		this.name = name;
		
	}
}
