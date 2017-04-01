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
		
		public static String CMD_SEND_FILE = "send_file";
		public static String CMD_SEND_IMAGE = "send_image";
		public static String CMD_SEND_ERROR = "send_error";
		public static String CMD_REQUEST_SEND_FILE = "request_send_file";
		public static String CMD_ACCEPT_RECEIVE_FILE = "accept_receive_file";
		public static String CMD_DENY_RECEIVE_FILE = "deny_receive_file";
		
		public static String CMD_REQUEST_AUDIO_CALL = "request_audio_call";
		public static String CMD_ACCEPT_AUDIO_CALL = "accept_audio_call";
		public static String CMD_END_AUDIO_CALL = "end_audio_call";
		public static String CMD_SEND_AUDIO_CALL = "send_audio_call";
		public static String CMD_DENY_AUDIO_CALL = "deny_audio_call";
	}
	
	public interface Response {
		public static String SUCCESS = "success";
		public static String FAIL = "fail";
	}

}
