package lexer;

public class InvalidCharacterException extends Exception{
		private static final long serialVersionUID = 1L;

		public InvalidCharacterException(String errorMessage) {
			super(errorMessage);
		}
	}