public class NotFoundException extends Exception {
	NotFoundException(String s) {
		super(s);
	}
}

@SuppressWarnings("serial")
class HeaderNotFound extends Exception {
	HeaderNotFound(String s) {
		super(s);
	}
}

class MoreThanOneFound extends Exception {
	MoreThanOneFound(String s) {
		super(s);
	}
}