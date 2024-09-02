package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Classe responsável por representar a lista de contatos de um contato específico
public class ContactCatalog implements Serializable{

	Contact owner;
	
	List<Contact> myContacts = new ArrayList<Contact>();

	public Contact getOwner() {
		return owner;
	}

	public void setOwner(Contact owner) {
		this.owner = owner;
	}

	public List<Contact> getMyContacts() {
		return myContacts;
	}

	public void addContact(Contact contact) {
		myContacts.add(contact);
	}
	
	public void removeContact(Contact contact) {
		for(int i = 0; i < myContacts.size(); i++) {
			if(myContacts.get(i).getName().equals(contact.getName())) {
				myContacts.remove(i);
				break;
			}
		}
	}
	
	public ContactCatalog(Contact owner) {
		this.owner = owner;
	}
	

}
