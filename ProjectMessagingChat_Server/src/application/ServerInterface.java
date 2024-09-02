package application;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface do servidor
public interface ServerInterface extends Remote{
	
	public ContactCatalog registerContactOnServer(Contact contact) throws RemoteException;
	
	public ContactCatalog notifyOnlineStatus(Contact contact) throws RemoteException;
	
	public void registerNewContact(Contact sender, Contact newContact) throws RemoteException;
	
	public void deleteContact(Contact sender, Contact deletedContact) throws RemoteException;
	
	public void writeOfflineMessage(Contact sender, Contact receiver, String message) throws RemoteException;

}

