package application;

import java.util.Enumeration;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

// Classe responsável por recuperar as mensagens na fila do respectivo contato
public class OfflineMessageReceiver {

	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	public Server server;
	
	public OfflineMessageReceiver(Server server) {
		this.server = server;
	}

	// Recuperar as mensagens na fila do respectivo contato
	public void receiveOfflineMessages(Contact contact) throws JMSException {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);
        
        Destination destination = session.createQueue(contact.getName());
        		
        int queueSize = getQueueSize(session, (Queue) destination);
        MessageConsumer consumer = session.createConsumer(destination);

        
        // Criar consumidores para receber todas as mensagens presentes na fila
        for(int i = 0; i < queueSize; i++) {
        	
        	Message message = consumer.receive();
        	         
            // Recebe a mensagem e a adiciona no histórico do servidor
    		if (message instanceof ObjectMessage) {
    			Object object;
    			try {
    				object = ((ObjectMessage) message).getObject();
    				String[] request = (String[]) object;
    				Boolean hasContactInList = false;
    				
    				for(int j = 0; j < this.server.allContactsLists.size(); j++) {
    					
    					if(this.server.allContactsLists.get(j).getOwner().getName().equals(contact.getName())) {
    						
    						for(int k = 0; k < this.server.allContactsLists.get(j).getMyContacts().size(); k++) {
    							// Se o contato for conhecido na lista analisada
    							if(this.server.allContactsLists.get(j).getMyContacts().get(k).getName().equals(request[0])) {
    								this.server.allContactsLists.get(j).getMyContacts().get(k).addMessage(request[2]);
    								hasContactInList = true;
    					 
    							}
    						}
    					
    						// Se o contato não for conhecido na lista analisada
    						if(hasContactInList == false) {   						
    							Contact newContact = new Contact(request[0], "-");   						
    							newContact.setUrl(request[1]);   							
    							newContact.addMessage(request[2]);   						
    							this.server.allContactsLists.get(j).addContact(newContact);   						
    						}
    						
    					}
    				}

    			} catch (JMSException e) {
    				e.printStackTrace();
    			}
    		}	
        }

        // Liberar trava para continuar a execução do programa
        this.server.latch.countDown();
        consumer.close();

    }
	
	// Função que retorna quantas mensagens ainda estão pendentes numa determinada fila
	private int getQueueSize(Session session, Queue queue) {
	    int count = 0;
	    try {
	        QueueBrowser browser = session.createBrowser(queue);
	        Enumeration elems = browser.getEnumeration();
	        while (elems.hasMoreElements()) {
	            elems.nextElement();
	            count++;
	        }
	    } catch (JMSException ex) {
	        ex.printStackTrace();
	    }
	    return count;
	}
}
