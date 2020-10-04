import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class User {
    
    @JSONField(name = "AGE")
    private int age;
 
    @JSONField(name = "FULL NAME")
    private String fullName;
 
    @JSONField(name = "DATE OF BIRTH")
    private Date dateOfBirth;
 
    public User() {}
    public User(int age, String fullName, Date dateOfBirth) {
        super();
        this.age = age;
        this.fullName= fullName;
        this.dateOfBirth = dateOfBirth;
    }

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
 
    // standard getters & setters
    
}