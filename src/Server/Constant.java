package Server;

public class Constant {
	public interface Comand {
		public static String CMD_LOG_IN = "log_in";
		public static String CMD_LOG_OUT = "log_out";
		public static String CMD_CHAT_ALL = "chat_all";
		public static String CMD_CHAT_ONE = "chat_one";
		public static String CMD_MESSAGE_GROUP = "message_group";
		public static String CMD_MESSAGE_CHAT = "message_chat";
		public static String CMD_ONLINE = "online";
		public static String CMD_REQUEST_ONLINE = "request_online";
	}
	
	public interface Response {
		public static String SUCCESS = "success";
		public static String FAIL = "fail";
	}

}
