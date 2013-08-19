/**
 * 
 */
package baney.swizzle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.atmosphere.cpr.Broadcaster;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import baney.swizzle.dto.Command;
import baney.swizzle.dto.UserDTO;

/**
 * @author jonathan
 *
 */
@Service
public class FakeMessagePublisher
{
	
	public FakeMessagePublisher()
	{
		System.out.println("\n\nCreating FakeMessagePublisher\n\n");
	}

	/**
	 * Execute every 20 seconds
	 */
	@Scheduled(fixedRate=20000)
	public void generateMessages()
	{		
		synchronized (map_)
		{
			if (!map_.isEmpty())
			{
				int index = generateIndex(map_.size());
				int messageIdx = generateIndex(1000);
				String topic = topics_.get(index);
				Broadcaster b = map_.get(topic);
				UserDTO dto = dtoMap_.get(topic);
				dto.setAvailability("" + messageIdx);
				String stringified;
				try
				{
					stringified = mapper_.writeValueAsString(dto);
					System.out.println("broadcasting to topic " + topic + " message: " + stringified);
					b.broadcast(stringified);
				} catch (JsonGenerationException e)
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
			}
			else
			{
				System.out.println("publisher map was empty, skipping this time");
			}
		}
	}
	
	
	public void modify(final Map<String, String[]> requestParameters)
	{
				
	}
	
	
	public void addBroadcaster(final String topic, final Broadcaster broadcaster)
	{		
		synchronized (map_)
		{
			if (map_.containsKey(topic))
			{
				System.out.println(topic + " topic already registered");
			}
			else
			{
				System.out.println(topic + " added to publisher");
				UserDTO dto = new UserDTO(topic);
				dto.setFirstName("fakeFirst");
				dto.setLastName("fakeLast");
				map_.put(topic, broadcaster);
				dtoMap_.put(topic, dto);
				topics_.add(topic);
			}
		}
	}
	
	
	private int generateIndex(int max)
	{
		int nextIndex = random.nextInt(max);
		return nextIndex;
	}
	
	private Random random = new Random();
	
	
	private static final Map<String, Broadcaster> map_ = new HashMap<>();
	private static final List<String> topics_ = new ArrayList<>();
	private static final Map<String, UserDTO> dtoMap_ = new HashMap<>();
	public static final ObjectMapper mapper_ = new ObjectMapper();

}
