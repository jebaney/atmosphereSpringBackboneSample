/**
 * 
 */
package baney.swizzle.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jonathan
 *
 */
@XmlRootElement
public class UserDTO
{
	
	public UserDTO(String id, String firstName, String lastName)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public UserDTO(String id)
	{
		this.id = id;
	}



	public String getFirstName()
	{
		return firstName;
	}
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	public String getLastName()
	{
		return lastName;
	}
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getAvailability()
	{
		return availability;
	}

	public void setAvailability(String availability)
	{
		this.availability = availability;
	}

	public String toString()
	{
		return "UserDTO: " + getId() + ", " + getFirstName() + ", " + getLastName();
	}
	
	
	private String firstName;
	private String lastName;
	private String id;
	private String availability;

}
