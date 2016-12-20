package se.plushogskolan.restcaseservice.exception;

public class UnathorizedException extends RuntimeException {
	
	private static final long serialVersionUID = 3445658517116753172L;
	
	public UnathorizedException(String message){
		super(message);
	}
	
	public UnathorizedException(String message, Exception e){
		super(message, e);
	}

}
