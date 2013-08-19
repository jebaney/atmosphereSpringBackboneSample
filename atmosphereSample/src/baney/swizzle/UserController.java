/**
 * 
 */
package baney.swizzle;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.jersey.api.core.InjectParam;

import baney.swizzle.dto.Command;
import baney.swizzle.dto.UserDTO;

import static org.atmosphere.cpr.HeaderConfig.X_ATMOSPHERE_TRACKING_ID;
import static org.atmosphere.cpr.HeaderConfig.X_CACHE_DATE;

/**
 * @author jonathan
 *
 */
@Path("/user/{id}")
@Produces({MediaType.APPLICATION_JSON})
public class UserController
{
	
	@InjectParam
	private FakeMessagePublisher publisher_;

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
	
	
	@POST
	@Path("modify")
	public String modifyUser(@Context AtmosphereResource resource)
	{
		System.out.println("got modify request");
		return "";
	}
	
	/**
	 * This will handle actual subscriptions to uuids of type UserDTO.
	 * The path will be <appRoot>/user/{id}
	 * 
	 * @param resource
	 * @param id
	 * @return
	 */
	@POST
	public String addSubscription(@Context AtmosphereResource resource,
																String message)
	{		
		System.out.println("message: " + message);
		Command command = createCommand(message);
		String id = command.getId();
		String action = command.getAction();
		
		if (action.equalsIgnoreCase("modify"))
		{
			System.out.println("modify command recieved " + id);
			try
			{
				resource.getResponse().sendRedirect(command.getUrl());
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			String suspendedId = (String)resource.getRequest().getAttribute(ApplicationConfig.SUSPENDED_ATMOSPHERE_RESOURCE_UUID);
			String websocketId = (String)resource.getRequest().getAttribute(ApplicationConfig.UNIQUE_UUID_WEBSOCKET);
			String headerId = resource.getResponse().getHeader(X_ATMOSPHERE_TRACKING_ID);
			
			
			System.out.println("starting subscribe to id " + id + " with uuid: " + resource.uuid() + " suspended: " + 
					suspendedId + " websocket: " + websocketId + " headerId: " + headerId);
					
			System.out.println("trying suspendedId " + suspendedId);
			AtmosphereResource originalResource = find(suspendedId);
			if (originalResource == null)
			{
				System.out.println("failed via suspeneded id (" + suspendedId + ")");
				System.out.println("tring headerId: " + headerId);
				originalResource = find(headerId);
				if (originalResource == null)
				{
					System.out.println("failed via header id (" + headerId + ")");
					System.out.println("trying websocketId " + websocketId);
					originalResource = find(websocketId);
					if (originalResource == null)
					{
						System.out.println("failed via websocket id (" + websocketId + ")");
						return "";
					}
				}
			}
			
			Broadcaster broadcaster = BroadcasterFactory.getDefault().lookup(id, true);
			broadcaster.addAtmosphereResource(originalResource);
			//broadcaster.addAtmosphereResource(resource);
			publisher_.addBroadcaster(id, broadcaster);
			
			// Note that the new subscription will come in on a new resource (connection)
			// but since we aren't explicitly suspending it, the "new" connection will
			// immediately die and all broadcasts to it will funnel through the 
			// original resource (connection) giving us multiplexing.
			
			System.out.println("subscription added uuid " + id);
		}
		
		return "";
	}
	
	
		
	private AtmosphereResource find(String id)
	{
		return AtmosphereResourceFactory.getDefault().find(id);
	}
	
	private Command createCommand(String jsonMessage)
	{
		Command command = null;
		try
		{
			command = FakeMessagePublisher.mapper_.readValue(jsonMessage, Command.class);
		} catch (JsonParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return command;
	}

}
