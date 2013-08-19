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
public class Command
{

	public Command()
	{
		
	}
	
	
	public Command(String action, String payload, String url, String id)
	{
		this.url = url;
		this.payload = payload;
		this.action = action;
		this.id = id;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	
	/**
	 * @return the payload
	 */
	public String getPayload()
	{
		return payload;
	}


	/**
	 * @param payload the payload to set
	 */
	public void setPayload(String payload)
	{
		this.payload = payload;
	}


	public String getId()
	{
		return id;
	}


	public void setId(String id)
	{
		this.id = id;
	}


	@Override
	public String toString()
	{
		return "[Command: url: " + url + ", command: " + action + ", payload: " + payload;
	}
	
	private String url;
	private String action;
	private String payload;
	private String id;
	/**
	 * @return the command
	 */
	public String getAction()
	{
		return action;
	}


	/**
	 * @param command the command to set
	 */
	public void setAction(String action)
	{
		this.action = action;
	}
}
