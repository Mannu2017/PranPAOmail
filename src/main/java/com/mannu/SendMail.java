package com.mannu;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimerTask;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail extends TimerTask {
	private Connection con;
	String strLine = null;
	
	StringBuffer mailStirng = new StringBuffer("");
	@Override
	public void run() {
		this.con=DbCon.getConnection();
		StringBuilder body = null; 
		try {
			if(con.isClosed()) 
			{
				con=DbCon.getConnection();
			}
			
			PreparedStatement ps=con.prepareStatement("select  distinct r.PaoRejNo,r.Office,r.DTOAddress,m.EMAILID from PranRejection r inner join prandotnet..PaoMaster m on r.PaoRejNo=m.PAONO where r.PaoPrint is not null and r.PaoMail is null and m.EMAILID!='' order by r.PaoRejNo");
			ResultSet rs=ps.executeQuery();
			while(rs.next()) 
			{
				System.out.println("POP: "+rs.getString(1));
				Properties prop=new Properties();
				prop.put("mail.smtp.port", "25");
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.starttls.enable", "true");
				prop.put("mail.smtp.ssl.trust", "srv-mail-ch5.karvy.com");
				Session session = Session.getInstance(prop,new javax.mail.Authenticator() {
					protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
						return new javax.mail.PasswordAuthentication("thelp.tin@karvy.com","Karvy@123");
					}
				});
				DateFormat df=new SimpleDateFormat("dd-MM-yyyy");
				String sysdate=df.format(new Date());
				
				body = new StringBuilder();
				body.append("<div>")
					.append("<div style=\"font-style: oblique; font-size: 15px;\">Ref	: KDMSL/CRA/PRAN/"+rs.getString(1)+"</div>")
					.append("<div style=\"font-style: oblique; font-size: 15px;\">Date	: "+sysdate+"</div>")
					.append("<br></br>")
					.append("<div style=\"font-style: oblique; font-size: 17px;\"><b>"+rs.getString(2)+"</b></div>");
					String[] resadd=rs.getString(3).split("\\^");
					String ad = "";
					for(int i=0; i<resadd.length; i++ ) {
						 if(resadd[i].length()>0) {
							 ad+="<div style=\"font-style: oblique; font-size: 17px;\">"+resadd[i]+"</div>";
						 }
					 }
					body.append(ad)
					.append("<br></br>")
					.append("<div style=\"font-style: oblique; font-size: 17px;\">Dear Sir/Madam,</div>")
					.append("<p></p>")
					.append("<div style=\"font-style: oblique; font-size: 17px;\">This is with reference to the request forwarded by you for  the allotment of PRAN. In this regard we wish to inform"
							+ " you that the following discrepancy/s have been observed while processing the below application/s."
							+ "</div>");
					if(rs.getString(1).startsWith("6")) 
					{
						body.append("<p></p>")
						.append("<table border=\"2\" style=\"border-collapse:collapse;text-align:center\">")
						.append("  <tr>")
					     .append("    <th style=\"padding:5px\">Sl No</th>")
					     .append("    <th style=\"padding:5px\">Inward</th>")
					     .append("    <th style=\"padding:5px\">Acknowledge No</th>")
					     .append("    <th style=\"padding:5px\">Name</th>")
					     .append("    <th style=\"padding:5px\">POPSP RECEIPT NO</th>")
					     .append("    <th style=\"padding:5px\">Rejection Reason</th>")
					     .append("  </tr>");
						
						try {
							if(con.isClosed()) 
							{
								con=DbCon.getConnection();
							}
							PreparedStatement ps1=con.prepareStatement("select distinct rj.InwardNo,rj.AckNo,rj.FullName,org.DDOTAN,org.PPANO,rj.popno,rj.RejDescription from PranRejection rj left join prandotnet..PranRegularOrg org on rj.AckNo=org.AcknowledgeNo where rj.PaoRejNo='"+rs.getString(1)+"' and len(rj.PaoRejNo)=7 and rj.PaoPrint is not null and PaoMail is null");
							ResultSet rs1=ps1.executeQuery();
							int sl=0;
							while (rs1.next()) {
								sl=1+sl;
								body.append("  <tr>")
							     .append("    <td>"+sl+"</td>")
							     .append("    <td>"+rs1.getString(1)+"</td>")
							     .append("    <td>"+rs1.getString(2)+"</td>")
							     .append("    <td>"+rs1.getString(3)+"</td>")
							     .append("    <td>"+rs1.getString(6)+"</td>")
							     .append("    <td>"+rs1.getString(7)+"</td>")
							     .append("  </tr>")
							     .append("");
								PreparedStatement update=con.prepareStatement("update PranRejection set PaoMail='Send' where InwardNo='"+rs1.getString(1)+"'");
								update.execute();
								update.close();
							}
							ps1.close();
							rs1.close();
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						body.append("<p></p>")
							.append("<table border=\"1\" style=\"border-collapse:collapse;text-align:center\">")
							.append("  <tr>")
						     .append("    <th style=\"padding:5px\">Sl No</th>")
						     .append("    <th style=\"padding:5px\">Inward</th>")
						     .append("    <th style=\"padding:5px\">Acknowledge No</th>")
						     .append("    <th style=\"padding:5px\">Name</th>")
						     .append("    <th style=\"padding:5px\">DDOTAN</th>")
						     .append("    <th style=\"padding:5px\">PPAN</th>")
						     .append("    <th style=\"padding:5px\">Rejection Reason</th>")
						     .append("  </tr>");
						try {
							if(con.isClosed()) 
							{
								con=DbCon.getConnection();
							}
							PreparedStatement ps1=con.prepareStatement("select distinct rj.InwardNo,rj.AckNo,rj.FullName,org.DDOTAN,org.PPANO,rj.popno,rj.RejDescription from PranRejection rj left join prandotnet..PranRegularOrg org on rj.AckNo=org.AcknowledgeNo where rj.PaoRejNo='"+rs.getString(1)+"' and len(rj.PaoRejNo)=7 and rj.PaoPrint is not null and PaoMail is null");
							ResultSet rs1=ps1.executeQuery();
							int sl=0;
							while (rs1.next()) {
								sl=1+sl;
								body.append(" <tr>")
							     .append("    <td>"+sl+"</td>")
							     .append("    <td>"+rs1.getString(1)+"</td>")
							     .append("    <td>"+rs1.getString(2)+"</td>")
							     .append("    <td>"+rs1.getString(3)+"</td>")
							     .append("    <td>"+rs1.getString(4)+"</td>")
							     .append("    <td>"+rs1.getString(5)+"</td>")
							     .append("    <td >"+rs1.getString(7)+"</td>")
							     .append("  </tr>")
							     .append("");
								PreparedStatement update=con.prepareStatement("update PranRejection set PaoMail='Send' where InwardNo='"+rs1.getString(1)+"'");
								update.execute();
								update.close();
							}
							ps1.close();
							rs1.close();
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					body.append("</table>")
						.append("<p></p>")
						.append("<div style=\"font-style: oblique; font-size: 17px;\">You are requested to submit fresh application form/s along with the requisite documents/information at our branch to enable us to process the same.</div>")
						.append("<p></p>")
						.append("<div style=\"font-style: oblique; font-size: 17px;\">Thanking you,</div>")
						.append("<p></p>")
						.append("<div style=\"font-style: oblique; font-size: 17px;\">Karvy Data Management Services Ltd - CRA FC</div>")
						.append("<div style=\"font-style: oblique; font-size: 17px;\">Plot No: 25,26,27 Near Image Hospital,</div>")
						.append("<div style=\"font-style: oblique; font-size: 17px;\">Vittal Rao Nagar, Madhapur,Hyderabad,</div>")
						.append("<div style=\"font-style: oblique; font-size: 17px;\">Telangana  500081, India.</div>")
						.append("<div style=\"font-style: oblique; font-size: 17px;\">Ph no. 040-66282809/040-66282810</div>")
						.append("<br></br>")
						.append("<div>*** This is an automatically generated email, please do not reply ***</div>")
				     	.append("</div>")
				     	.append("<br></br>");
					
					MimeMessage msg = new MimeMessage(session);
					try {
						msg.setFrom(new InternetAddress("help.tin@karvy.com"));
						msg.setSubject("Rejection Memo - Additional Documents / Information Required");
						
						msg.addRecipient(Message.RecipientType.TO, new InternetAddress(rs.getString(4)));
						msg.addRecipient(Message.RecipientType.CC, new InternetAddress("crarejections@karvy.com"));
						 Multipart multipart = new MimeMultipart();
					        BodyPart htmlBodyPart = new MimeBodyPart();
					        htmlBodyPart.setContent(body.toString() , "text/html"); 
					        multipart.addBodyPart(htmlBodyPart);
					        msg.setContent(multipart);
						Transport transport = session.getTransport("smtp");
				        transport.connect("srv-mail-ch5.karvy.com", 25,"help.tin@karvy.com","Karvy@123");
				        transport.sendMessage(msg, msg.getAllRecipients());
				        transport.close();
						 
					} catch (AddressException e) {
						e.printStackTrace();
					} catch (MessagingException e) {
						e.printStackTrace();
					}		
			}
			ps.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
