package br.com.alura.AluraFake.util;

public class ErrorItem extends Throwable {

	private final String field;
	private final String message;

	public ErrorItem(String field, String message) {
		this.field = field;
		this.message = message;
	}

	public String getField() {
		return field;
	}

	public String getMessage() {
		return message;
	}
}
