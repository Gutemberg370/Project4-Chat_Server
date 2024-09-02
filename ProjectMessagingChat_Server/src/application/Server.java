package application;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

// Implementação da interface servidor
public class Server extends UnicastRemoteObject implements ServerInterface {
	
	List<ContactCatalog> allContactsLists = new ArrayList<ContactCatalog>();
	OfflineMessageSender offlineMessageSender = new OfflineMessageSender();
	OfflineMessageReceiver offlineMessageReceiver = new OfflineMessageReceiver(this);
	CountDownLatch latch;
	
	// Serviço responsável por executar uma thread mais de uma vez
	final ExecutorService service = Executors.newCachedThreadPool();


	protected Server() throws RemoteException {
		super();
	}
	
	// Thread responsável por criar os consumidores necessários para o usuário receber suas mensagens offlines
	final class OfflineMessagesConsumer implements Runnable {
		
		Contact contact;
		
		OfflineMessagesConsumer(Contact contact){
			this.contact = contact;
		}
		
	    @Override
	    public void run() {
        	try {
        		offlineMessageReceiver.receiveOfflineMessages(this.contact);
			} catch (JMSException e) {
				e.printStackTrace();
			}	

	    }
	}; 
	
	public void callOfflineMessagesConsumer(Contact contact) {
		service.submit(new OfflineMessagesConsumer(contact));
	}
	
	// Função responsável por adicionar o novo contato logado no servidor e retornar as mensagens
	// offlines recebidas
	public ContactCatalog registerContactOnServer(Contact contact) {
		
		for(int i = 0; i < allContactsLists.size(); i++) {
			// O contato existe na lista do servidor
			if(allContactsLists.get(i).getOwner().getName().equals(contact.getName())) {
				
				allContactsLists.get(i).myContacts.forEach(cont ->{
					cont.setMessages(new ArrayList<String>());
				});
				
				this.latch = new CountDownLatch(1);
				callOfflineMessagesConsumer(contact);
				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
								
				return allContactsLists.get(i);
			}
		}
		
		// O contato não existe na lista do servidor
		ContactCatalog newCatalog = new ContactCatalog(contact);
		allContactsLists.add(newCatalog);
		
		this.latch = new CountDownLatch(1);
		callOfflineMessagesConsumer(contact);
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return newCatalog;
	}
	
	// Notificar ao servidor a mudança de status do contato e retornar as mensagens
	// offlines caso necessário
	public ContactCatalog notifyOnlineStatus(Contact contact) {
		for(int i = 0; i < allContactsLists.size(); i++) {
			if(allContactsLists.get(i).getOwner().getName().equals(contact.getName())) {
				allContactsLists.get(i).getOwner().setStatus(contact.getStatus());
				if(allContactsLists.get(i).getOwner().getStatus().equals("online")) {
					
					allContactsLists.get(i).myContacts.forEach(cont ->{
						cont.setMessages(new ArrayList<String>());
					});
					
					this.latch = new CountDownLatch(1);
					callOfflineMessagesConsumer(contact);
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
									
					return allContactsLists.get(i);
				}
				return null;
			}
		}
		return null;
	}
	
	// Adicionar um novo contato
	public void registerNewContact(Contact sender, Contact newContact) {
		for(int i = 0; i < allContactsLists.size(); i++) {
			if(allContactsLists.get(i).getOwner().getName().equals(sender.getName())) {
				allContactsLists.get(i).addContact(newContact);
				break;
			}
		}
	}
	
	// Deletar um contato da lista
	public void deleteContact(Contact sender, Contact deletedContact) {
		for(int i = 0; i < allContactsLists.size(); i++) {
			if(allContactsLists.get(i).getOwner().getName().equals(sender.getName())) {
				allContactsLists.get(i).removeContact(deletedContact);
				break;
			}
		}
	}
	
	// Publicar uma mensagem offline
	public void writeOfflineMessage(Contact sender, Contact receiver, String message) throws RemoteException{
		
		try {
			this.offlineMessageSender.sendOfflineMessage(sender, receiver, message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
