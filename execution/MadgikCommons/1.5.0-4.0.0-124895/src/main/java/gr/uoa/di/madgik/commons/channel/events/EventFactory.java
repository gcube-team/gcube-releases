package gr.uoa.di.madgik.commons.channel.events;

/**
 * A factory for creating Event objects.
 */
public class EventFactory
{
	
	/**
	 * Retrieves an instance of the event based on the provided name as provided by the {@link ChannelState.EventName} 
	 * and the serialization of the event as this was produced by {@link ChannelStateEvent#Encode()}
	 * 
	 * @param Name the name
	 * @param serialization the serialization
	 * @return the instantiated channel state event
	 * @throws Exception The event could not be instantiated
	 */
	public static ChannelStateEvent GetEvent(ChannelState.EventName Name, byte[] serialization) throws Exception
	{
		ChannelStateEvent ev=null;
		switch(Name)
		{
			case BytePayload:
			{
				ev=new BytePayloadChannelEvent();
				ev.Decode(serialization);
				break;
			}
			case ObjectPayload:
			{
				ev=new ObjectPayloadChannelEvent();
				ev.Decode(serialization);
				break;
			}
			case StringPayload:
			{
				ev=new StringPayloadChannelEvent();
				ev.Decode(serialization);
				break;
			}
			case DisposeChannel:
			{
				ev=new DisposeChannelEvent();
				break;
			}
			default:
			{
				ev=null;
			}
		}
		return ev;
	}
}
