package message
{
	public class ConnectionMessage
	{
		public function ConnectionMessage(userName:String, password:String)
		{
			_userName = userName;
			_password = password;
		}
		
		private var _userName:String;
		
		private var _password:String;
		
		public function get password():String
		{
			return _password;
		}

		public function get userName():String
		{
			return _userName;
		}

	}
}