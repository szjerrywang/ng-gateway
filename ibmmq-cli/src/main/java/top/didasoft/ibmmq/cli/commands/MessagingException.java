package top.didasoft.ibmmq.cli.commands;

public class MessagingException extends NestedRuntimeException {


	public MessagingException(String description) {
		super(description);

	}

	public MessagingException(String description, Throwable cause) {
		super(description, cause);

	}





	@Override
	public String toString() {
		return super.toString();
	}

}
