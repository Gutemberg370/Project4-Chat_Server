package application;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

// Classe responsável por enviar as mensagens offlines à fila
public class OfflineMessageSender {
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	private String[] messageToSend = new String[3];
	
	public OfflineMessageSender() {
	}

	// Enviar a mensagem offline à fila do respectivo contato
	public void sendOfflineMessage(Contact sender, Contact receiver, String message) throws JMSException {
              	
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);       

        Destination destination = session.createQueue(receiver.getName());
        
        this.messageToSend[0] = sender.getName();
        this.messageToSend[1] = sender.getUrl();
        this.messageToSend[2] = message;
       
        MessageProducer producer = session.createProducer(destination);
          
        ObjectMessage objectMessage = session.createObjectMessage();
        objectMessage.setObject(this.messageToSend);
            
        producer.send(objectMessage);

        connection.close();
        
    }

}
