package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Classe que representa um contato da aplicação
public class Contact implements Serializable{
	
	private String name;
	private String status;
	private String url;
	private List<String> messages = new ArrayList<String>();
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public List<String> getMessages() {
		return messages;
	}
	
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	public void addMessage(String message) {
		this.messages.add(message);
	}
	
	// Definir a url de conexão do contato com base em ip, porta e nome
	public void setUrl(String ip, String port, String name) {
		String url = String.format("rmi://%s:%s/%s", ip, port, name);
		this.url = url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
	
	public Contact(String name, String status) {
		this.name = name;
		this.status = status;
	}

}
