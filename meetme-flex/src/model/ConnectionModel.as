package model
{
	import message.ConnectionMessage;
	
	import org.igniterealtime.xiff.conference.Room;
	import org.igniterealtime.xiff.core.EscapedJID;
	import org.igniterealtime.xiff.core.UnescapedJID;
	import org.igniterealtime.xiff.core.XMPPConnection;
	import org.igniterealtime.xiff.data.Message;
	import org.igniterealtime.xiff.data.Presence;
	import org.igniterealtime.xiff.events.LoginEvent;
	import org.igniterealtime.xiff.events.RoomEvent;
	import org.igniterealtime.xiff.events.XIFFErrorEvent;

	public class ConnectionModel
	{
		[MessageDispatcher]
		public var messageDispatcher:Function;
		
		public var connection:XMPPConnection;
		
		[MessageHandler]
		public function connectMessageHandler(msg:ConnectionMessage):void
		{
			connection = new XMPPConnection();
			connection.username = msg.userName;
			connection.password = msg.password;
			connection.server = "192.168.0.2";
			connection.port = 5222;
			connection.useAnonymousLogin = false;
			
			connection.addEventListener(LoginEvent.LOGIN, onLogin);
			connection.addEventListener(XIFFErrorEvent.XIFF_ERROR, onError);	
			
			var connected:Boolean = connection.connect(XMPPConnection.STREAM_TYPE_STANDARD);
		}
		
		private function onLogin(e:LoginEvent):void
		{
			var presence:Presence = new Presence(null, connection.jid.escaped,Presence.SHOW_CHAT, null, null, 1);
			connection.send(presence);
			connection.sendKeepAlive();
			
			/*var room:Room = new Room(connection);
			room.roomJID = new UnescapedJID("sala1@conference.meetme");
			room.addEventListener(RoomEvent.ROOM_JOIN, onRoomJoin);
			room.join();*/
			
			var jid:EscapedJID  = new EscapedJID("israelcamara|gmail.com@meetme");
			
			var msg:Message = new Message(jid, null, "Hello, world.", "foo", Message.TYPE_CHAT);
			connection.send(msg);
		}
		
		private function onRoomJoin(e:RoomEvent):void
		{
			Room(e.target).sendMessage("im here");
		}
		
		private function onError(e:XIFFErrorEvent):void
		{
			trace(e.errorCode);
		}
		
	}
}