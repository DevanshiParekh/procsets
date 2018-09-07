package com.semaifour.facesix.domain;

import java.text.MessageFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Message {
	
	public enum Type {BLACK, YELLOW, BROWN, RED, GREEN};
	
	private String message;
	private Type type = Type.BLACK;
	
	public Message(String message) {
		this.setMessage(message);
	}
	
	public Message(String message, Type type) {
		this.setMessage(message);
		this.setType(type);
	}
	
	public static Message newInfo(String message) {
		return new Message(message, Type.BLACK);
	}

	public static Message newInfo(String pattern, Object... params) {
		return new Message(MessageFormat.format(pattern, params), Type.BLACK);
	}

	public static Message newWarning(String message) {
		return new Message(message, Type.YELLOW);
	}

	public static Message newWarning(String pattern, Object... params) {
		return new Message(MessageFormat.format(pattern, params), Type.YELLOW);
	}

	public static Message newError(String message) {
		return new Message(message, Type.RED);
	}

	public static Message newError(String pattern, Object... params) {
		return new Message(MessageFormat.format(pattern, params), Type.RED);
	}

	public static Message newFailure(String message) {
		return new Message(message, Type.BROWN);
	}

	public static Message newFailure(String pattern, Object... params) {
		return new Message(MessageFormat.format(pattern, params), Type.BROWN);
	}

	public static Message newSuccess(String message) {
		return new Message(message, Type.GREEN);
	}

	public static Message newSuccess(String pattern, Object... params) {
		return new Message(MessageFormat.format(pattern, params), Type.GREEN);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Message [message=" + message + ", type=" + type + "]";
	}
	
}
