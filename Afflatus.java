package asciiextractor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.*;

public class Afflatus extends HttpServlet 
{
	int sno = 1 ;
	int a = 0;
	int count = 0;
	String authorEmail;
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException , ServletException
	{
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity sch = new Entity("Schedule",sno);
		
		Filter f = new FilterPredicate("sno",FilterOperator.GREATER_THAN_OR_EQUAL,a);
		Query q = new Query("Schedule").setFilter(f);
		PreparedQuery pq = datastore.prepare(q);
		for(Entity s : pq.asIterable())
		{
			if(s.getProperty("status").equals("no"))
			{
				Properties pro = new Properties();
		  		Session session = Session.getDefaultInstance(pro,null);
		  		String msgBody = "Dear "+(String)s.getProperty("authorName")+", \nYour"
		  				+ " turn is up next in 2 days to write the article on the discussed category."
		  						+ " \n\nHope you've discussed the ideas with concerned category lead."
		  						+ "\n \nSincerely,"
		  						+ "\n Afflatus Scheduler";
		  		try{
		  			
		  			Message msg = new MimeMessage(session);
		  			msg.setFrom(new InternetAddress("shivankawasthi.cse.msit@gmail.com","Afflatus Scheduler"));
		  			String address = (String)s.getProperty("authorEmail");
		  			String sender_name = (String)s.getProperty("authorName");
		  			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(address,sender_name));
		  			msg.addRecipient(Message.RecipientType.BCC, new InternetAddress("shivankawasthi.cse.msit@gmail.com","shivank"));
		  			msg.setSubject("Afflatus article notification!");
		  			msg.setText(msgBody);
		  			Transport.send(msg);
		  			s.setProperty("status", "yes");
		  			datastore.put(s);
		  		}catch(MessagingException e)
		  		{
		  			e.printStackTrace();
		  		}
				finally{
					
					break;
				}
			}
			else if(count==10)
			{
				Filter f1 = new FilterPredicate("sno",FilterOperator.GREATER_THAN_OR_EQUAL,a);
				Query q1 = new Query("Schedule").setFilter(f1);
				PreparedQuery pq1 = datastore.prepare(q1);
				for(Entity s1 : pq1.asIterable())
				{
					s1.setProperty("status", "no");
					datastore.put(s1);
				}
			}
			else{
				count = count+1;
			}
		}
		
	}
}
