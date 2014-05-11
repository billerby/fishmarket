package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name= "fmuser")
public class User extends Model {

	@Required
	@Email
	public String email;
	@Required
	@Password
    public String password;
	public String firstname;
    public String lastname;
    public String cellno;
    public boolean admin;
    public String toString() {
        return email;
    }
    
    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }

    

    
}
