package edu.ensf480.airline.model;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Properties;


public class SendEmail {
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";
    private static final String PROTOCOL = "TLSv1.2";


    private static final String EMAIL_FROM = "trustedflight@gmail.com";
    private static final String EMAIL_PASSWORD = "ncfldegtkvtteizj";

    /**
     * sendEmail interacts with the airline email to send a message.
     * @param messageText String - The text of the message. Can include embedded HTML
     * @param subject String - The subject line
     * @param emailto String array - All emails to send the message to
     */
    public static void sendEmail(String messageText, String subject, String[] emailto) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.auth", "true"); //enable authentication
        properties.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.ssl.protocols", PROTOCOL);

        Authenticator authentication = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        };

        Session session = Session.getInstance(properties, authentication);

        try {
            MimeMessage message = new MimeMessage(session);
            InternetAddress[] recipients = new InternetAddress[emailto.length];
            for (int i = 0; i < emailto.length; i++) {
                recipients[i] = new InternetAddress(emailto[i]);
            }
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.addRecipients(Message.RecipientType.BCC, recipients);
            message.setSubject(subject);
            message.setContent(messageText, "text/html");

            Transport.send(message);
            System.out.println("Email(s) have been sent");
        } catch (MessagingException e) {
            System.out.println("ERROR: Message to " + Arrays.toString(emailto) + " with subject" + subject + " failed to send!");
            //e.printStackTrace();
        }
    }

        private String bookingTemplate =
                    "<p>Hello, %s %s!<br>" +
                    "Thank you for your booking with Trusted Flights!<br><br>" +
                    "Please keep this booking confirmation for your reference<hr>" +
                    "Booking number: %s<br>"+
                    "Date: %s<br>" +
                    "Departure: %s from %s<br>"+
                    "Arrival: %s from %s<br>"+
                    "Seat: %s in section %s<br>"+
                    "Amount Paid: $%s<br>"+
                    "<hr>Please reach out with any questions!<br>Sincerely,<br>Trusted Flights</p>";    public void SendBookingEmail(Booking b){
        String[] emails = new String[1];
        emails[0] = b.getUser().getEmail();
        String subject = "Trusted Flights Booking Confirmation";
        String message = String.format(bookingTemplate,
                b.getUser().getFname(),
                b.getUser().getLname(),
                b.getBookingNumber(),
                b.getFlight().getFlightDate(),
                b.getFlight().getDepartureTime(),
                b.getFlight().getDepartureAirport(),
                b.getFlight().getArrivalTime(),
                b.getFlight().getArrivalAirport(),
                b.getSeat().getSeatNumber(),
                b.getSeat().getSeatClass(),
                b.getTotalCost());
        SendEmail.sendEmail(message,subject,emails);
    }
    private String cancellationTemplate =
            "<p>Hello, %s %s!<br>" +
                    "Sorry to hear that you cancelled with Trusted Flights!<br><br>" +
                    "Please keep this cancellation for your reference<hr>" +
                    "Cancelled Booking number: %s<br></p>";
    public void SendCancellationEmail(Booking b,boolean insurance){
        String[] emails = new String[1];
        emails[0] = b.getUser().getEmail();
        String subject = "Trusted Flights Cancellation Confirmation";
        String message = String.format(cancellationTemplate,
                b.getUser().getFname(),
                b.getUser().getLname(),
                b.getBookingNumber());
        if (insurance){
            message += "<p>Based on you having cancellation insurance, the full balance of $" + b.getTotalCost() + " will be refunded to your card automatically</p>";
        }
        else {
            message += "<p>You are not eligible for a refund due no cancellation insurance. We encourage considering it for next time.</p>";
        }
        message += "<hr>Please reach out with any questions. We hope you will book with us again soon!<br>Sincerely,<br>Trusted Flights</p>";
        SendEmail.sendEmail(message,subject,emails);
    }
}
