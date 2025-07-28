package com.invoice.approval.service;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(List<String> toEmails, String subject, String bodyTemplate) {
        for (String email : toEmails) {
            try {
                String name = extractNameFromEmail(email);
                String personalizedBody = bodyTemplate.replace("{name}", name);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom("gjayabalan08@gmail.com");
                helper.setTo(email);
                helper.setSubject(subject);
                helper.setText(personalizedBody, true);

                mailSender.send(message);
                System.out.println("✅ HTML Mail sent to: " + email);
            } catch (MessagingException e) {
                System.err.println("❌ Failed to send mail to: " + email);
                e.printStackTrace();
            }
        }
    }
    
    private String extractNameFromEmail(String email) {
        String namePart = email.split("@")[0]; // "john.doe"
        namePart = namePart.replaceAll("[^a-zA-Z0-9]", " "); // Replace dots/underscores with space
        namePart = Arrays.stream(namePart.split(" "))
                         .map(word -> word.isEmpty() ? "" : Character.toUpperCase(word.charAt(0)) + word.substring(1))
                         .reduce((a, b) -> a + " " + b).orElse("User");
        return namePart;
    }

    // You may keep this in a scheduled service instead.
//    public String getStyledBody() {
//        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; background-color:#f4f6f8; border:1px solid #ddd; border-radius:8px;\">" +
//               "<h2 style=\"color: #2e6da4;\">📢 Hello, {name}</h2>" +  // dynamic name here
//               "<p style=\"font-size: 14px; color: #333;\">" +
//               "This is a reminder to <strong style='color: #007bff;'>set your performance goals</strong> for the upcoming month." +
//               "</p>" +
//               "<ul style=\"font-size: 14px; color: #333;\">" +
//               "<li>💼 Define clear and measurable goals</li>" +
//               "<li>📅 Submit before <strong>month-end</strong></li>" +
//               "<li>📝 Ensure alignment with your team lead</li>" +
//               "</ul>" +
//               "<hr style=\"border: none; border-top: 1px solid #ccc; margin: 20px 0;\" />" +
//               "<p style=\"font-size: 12px; color: #999;\">" +
//               "This is an automated reminder email. Please do not reply." +
//               "</p>" +
//               "</div>";
//    }

    public String getStyledBody() {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
               "</head>" +
               "<body style=\"margin:0; padding:0; font-family: Arial, sans-serif;\">" +
               
               // Main container table (Outlook needs tables)
               "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" bgcolor=\"#f5f7fa\">" +
               "<tr>" +
               "<td align=\"center\" style=\"padding:20px 0;\">" +
               
               // Email container (600px width)
               "<table width=\"600\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" bgcolor=\"#ffffff\" style=\"border:1px solid #e1e5eb; border-radius:8px; overflow:hidden;\">" +
               
               // Header with solid color (Outlook doesn't support background gradients well)
               "<tr>" +
               "<td bgcolor=\"#667eea\" style=\"padding:30px; text-align:center;\">" +
               "<h1 style=\"color:white; margin:0; font-size:28px; font-weight:bold;\">🌟 Performance Goals Reminder</h1>" +
               "<p style=\"color:white; margin:10px 0 0; font-size:16px;\">Time to set your objectives for success!</p>" +
               "</td>" +
               "</tr>" +
               
               // Main content
               "<tr>" +
               "<td style=\"padding:30px;\">" +
               "<h2 style=\"color:#2d3748; margin-top:0; font-size:22px;\">Hello, {name}!</h2>" +
               
               // Content box
               "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"20\" border=\"0\" bgcolor=\"#ffffff\" style=\"border:1px solid #e1e5eb; border-radius:8px; margin-bottom:20px;\">" +
               "<tr>" +
               "<td>" +
               "<p style=\"font-size:15px; color:#4a5568; line-height:1.6; margin:0;\">" +
               "This is a friendly reminder to <strong style='color:#667eea;'>set your performance goals</strong> for the upcoming month to stay on track with your professional development." +
               "</p>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               
               // Feature boxes (using nested tables for Outlook)
               "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">" +
               "<tr>" +
               "<td width=\"33%\" valign=\"top\" style=\"padding-right:10px;\">" +
               "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"15\" border=\"0\" bgcolor=\"#ffffff\" style=\"border:1px solid #e1e5eb; border-radius:8px; text-align:center;\">" +
               "<tr>" +
               "<td>" +
               "<div style=\"background:#e6f7ff; width:40px; height:40px; border-radius:50%; margin:0 auto 10px; text-align:center; font-size:20px;\">" +
               "📝" +
               "</div>" +
               "<p style=\"font-size:14px; color:#4a5568; margin:0;\">Define SMART goals</p>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</td>" +
               
               "<td width=\"33%\" valign=\"top\" style=\"padding:0 5px;\">" +
               "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"15\" border=\"0\" bgcolor=\"#ffffff\" style=\"border:1px solid #e1e5eb; border-radius:8px; text-align:center;\">" +
               "<tr>" +
               "<td>" +
               "<div style=\"background:#f0fff4; width:40px; height:40px; border-radius:50%; margin:0 auto 10px; text-align:center; font-size:20px;\">" +
               "⏰" +
               "</div>" +
               "<p style=\"font-size:14px; color:#4a5568; margin:0;\">Submit by month-end</p>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</td>" +
               
               "<td width=\"33%\" valign=\"top\" style=\"padding-left:10px;\">" +
               "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"15\" border=\"0\" bgcolor=\"#ffffff\" style=\"border:1px solid #e1e5eb; border-radius:8px; text-align:center;\">" +
               "<tr>" +
               "<td>" +
               "<div style=\"background:#fff8e1; width:40px; height:40px; border-radius:50%; margin:0 auto 10px; text-align:center; font-size:20px;\">" +
               "🤝" +
               "</div>" +
               "<p style=\"font-size:14px; color:#4a5568; margin:0;\">Align with your team</p>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               
               // Spacer
               "<div style=\"height:20px;\"></div>" +
               
               // CTA Button (Outlook needs VML for button styling)
               "<!--[if mso]>" +
               "<v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"{action_url}\" style=\"height:42px;v-text-anchor:middle;width:200px;\" arcsize=\"10%\" strokecolor=\"#667eea\" fillcolor=\"#667eea\">" +
               "<w:anchorlock/>" +
               "<center style=\"color:#ffffff;font-family:Arial,sans-serif;font-size:16px;font-weight:bold;\">Set Your Goals Now</center>" +
               "</v:roundrect>" +
               "<![endif]-->" +
               "<![if !mso]>" +
               "<a href=\"{action_url}\" style=\"display:inline-block; background:#667eea; color:white; text-decoration:none; padding:12px 25px; border-radius:6px; font-weight:bold; text-align:center; margin:15px 0;\">" +
               "Set Your Goals Now" +
               "</a>" +
               "<![endif]>" +
               
               // Footer
               "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">" +
               "<tr>" +
               "<td style=\"border-top:1px solid #e2e8f0; padding:25px 0 0;\">" +
               "<p style=\"font-size:12px; color:#718096; text-align:center; margin-bottom:0;\">" +
               "This is an automated reminder. Need help? <a href=\"mailto:support@company.com\" style=\"color:#667eea; text-decoration:none;\">Contact support</a>" +
               "</p>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</td>" +
               "</tr>" +
               
               // Bottom accent
               "<tr>" +
               "<td height=\"4\" bgcolor=\"#667eea\"></td>" +
               "</tr>" +
               "</table>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</body>" +
               "</html>";
    }    
}
