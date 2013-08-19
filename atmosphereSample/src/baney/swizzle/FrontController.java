/**
 * 
 */
package baney.swizzle;

import static org.atmosphere.cpr.HeaderConfig.X_ATMOSPHERE_TRACKING_ID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereResource;

/**
 * @author jonathan
 *
 */
@Path("/subscribe")
public class FrontController
{

	@GET
	@Suspend
	public String suspend(@Context AtmosphereResource resource)
	{
		String suspendedId = (String)resource.getRequest().getAttribute(ApplicationConfig.SUSPENDED_ATMOSPHERE_RESOURCE_UUID);
		String websocketId = (String)resource.getRequest().getAttribute(ApplicationConfig.UNIQUE_UUID_WEBSOCKET);
		String headerId = resource.getResponse().getHeader(X_ATMOSPHERE_TRACKING_ID);
		
		System.out.println("initial connection...uuid: " + resource.uuid() + 
				" suspendedId: " + suspendedId + " websocketId: " + websocketId + 
				" headerId: " + headerId);
		return "";
	}

}
