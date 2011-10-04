package views.login
{
	import message.ConnectionMessage;

	public class LoginPM
	{
		[MessageDispatcher]
		public var messageDispatcher:Function;
		
		public function login(userName:String, password:String):void
		{
			var connectionMessage:ConnectionMessage = new ConnectionMessage(userName, password);
			messageDispatcher(connectionMessage);
		}
	}
}