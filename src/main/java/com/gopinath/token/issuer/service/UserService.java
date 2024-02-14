package com.gopinath.token.issuer.service;

import com.gopinath.token.issuer.model.RequestData;
import com.gopinath.token.issuer.model.User;
import com.gopinath.token.issuer.dao.UserDao;
import com.gopinath.token.issuer.model.UserOtp;
import com.gopinath.token.issuer.model.UserRolePermission;
import com.gopinath.token.issuer.responses.TokenResponse;
import com.sun.mail.smtp.SMTPTransport;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    UserDao userDao;

    @Autowired
    public UserService(UserDao userDao){
        this.userDao = userDao;
    }

    public User loadUserByUsername(String username) {

        logger.info("{}", username);
        List<User> allUsers = userDao.getUserByEmailAddress(username);
        logger.info("{}", allUsers);

        if(allUsers!=null && !allUsers.isEmpty()) {
            User us = allUsers.get(0);
            logger.info("user dob ...{}", us.getDateOfBirth());
            return us;
        }
        return null;

    }

    public void createOtp(RequestData requestData, int otpExpiryInMins, String otpKey) {
        String otp = RandomStringUtils.randomNumeric(4);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusMinutes(otpExpiryInMins);
        userDao.createOneTimePassword(requestData.getUsername(), otp, expiryDate, otpKey);

        String htmlMessage = "<div style='background:#f5f5f5;background-color:#f5f5f5;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding-bottom:0px;text-align:center;vertical-align:top;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:transparent;background-color:transparent;width:100%;'><tbody>	<tr><td>	<div style='Margin:0px auto;max-width:620px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:30px;padding-bottom:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>" +
                "<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'>  </table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table><table align='center' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;' '=''>	<tbody><tr style='width:{headerImageWidth}px;'>	<td align='center' style='font-size:0px;padding:0px;word-break:break-word;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse:collapse;border-spacing:0px;'>	<tbody><tr>	<td style='width:780px;padding:0pm 0px 0px 0px;padding-bottom:0px;'><img alt='Vend' height='auto' src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_header.png' style='border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;' width='{headerImageWidth}'> 	</td></tr>	</tbody></table>	</td></tr>	" +
                "</tbody></table><div class='main-content' style='background:#fff;background-color:#fff;Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#fff;background-color:#fff;width:100%'><tbody>	<tr><td colspan='3' style='height:30px'></td>	</tr>	<tr><td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;' class='mktoContainer' id='container'>	<table class='mktoModule' id='textSection' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable'><h1 style='text-align: center;'>Welcome to PayAccess! Let's get&nbsp;started.</h1><p style='padding-bottom: 20px;'>&nbsp;<br>" +
                "Your One-time Password is to be entered in the dialog box</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd971' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd971'><table width='100%' style='background-color: #e9f6e8;'>	<tbody><tr>	<td width='10%'>&nbsp;</td>	<td width='80%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Your PayAccess Login Details</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262; padding-bottom: 10px;'>" +
                "Enter the OTP: "+ otp +"</p><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse: separate; line-height: 100%;'>	<tbody><tr>	<td align='center' bgcolor='#41af4b' role='presentation' style='border: 2px solid transparent; border-radius: 0px; cursor: auto; padding: 14px 24px;' valign='middle'>" +
                "</td></tr>	</tbody></table><p>&nbsp;</p>	</td>	<td width='10%'>&nbsp;</td></tr>	</tbody></table><p>&nbsp;</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89'><table width='100%' style='background-color: #f8f8f5;'>	<tbody><tr>	<td width='15%'>&nbsp;</td>	<td width='70%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Set up PayAccess</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Get started with PayAccess by reading these four essential guides from our Help Centre and you'll be selling in no time!</p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_01.png' alt='V2439-Adoption-Onboarding-nurture-email-1_01.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Set up your outlets and registers</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Learn how to get PayAccess running on all of your registers and outlets.<br><a href='#'>Learn about registers</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_02.png' alt='V2439-Adoption-Onboarding-nurture-email-1_02.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Organise your sales taxes</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Add any sales or value-added taxes (VAT) that are for your location&nbsp;or&nbsp;products.<br><a href='#'>Learn about taxes</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_03.png' alt='V2439-Adoption-Onboarding-nurture-email-1_03.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Create different payment types</h3><p>Set up your payment terminal and registers so you can accept cash, cards and other&nbsp;payment&nbsp;types.<br><a href='#'>Learn about integrated payments</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_04.png' alt='V2439-Adoption-Onboarding-nurture-email-1_04.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Add a product</h3><p>Start adding your products, stock levels and descriptions to&nbsp;your&nbsp;catalog.<br><a href='#'>Learn how to add products</a></p><p>&nbsp;</p>	</td>	<td width='15%'>&nbsp;</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection2b16ab9a-73ae-43cf-8972-6db3159390c2' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable2b16ab9a-73ae-43cf-8972-6db3159390c2'><p>&nbsp;</p><h2>Your PayAccess to-do list</h2><p>Like to read ahead? Our <a href='#'>setup checklist</a> gives you a list of steps that you can check off at your own pace to get PayAccess set up.</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='signoffWithoutProfilePhoto15f0ef74-c2b9-420f-a62b-2e96f521ed08' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;padding-top:30px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;'><p>Here's to your retail success, <br><br> <strong>Peters</strong> <br>Director of Adoption <br> </p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr>	<tr><td colspan='3' style='height:50px'></td>	</tr></tbody>	</table></div><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:680px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding:5px;text-align:center;vertical-align:top;'>	<div style='background:#f5f5f5;background-color:#f5f5f5;Margin:0px auto;max-width:650px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:15px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='center' style='font-size:0px;padding:0px;word-break:break-word;'>	<div class='mktoSnippet' id='unsubscribeFooter'><div style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>	PayAccess HQ, 2-36 Obalende Street, Abuja, Nigeria 	<br> 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='#' target='_blank'>Unsubscribe</a> ∙ 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='https://email.vendhq.com/Nzc2LVFGTy0zMzQAAAF_hWiDyzkDXU0MPpA_mZQkOV6uelqxQNlKl80Dp7nbfZsoBZZomppxXFRKN_z6O69Y_RlWN_c=' target='_blank'>Privacy Policy</a></div>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table></div>";

        try {
            logger.info("=========================");
            Properties props = System.getProperties();
            props.put("mail.smtps.host", "smtp.mailgun.org");
            props.put("mail.smtps.auth", "true");

            Session session = Session.getInstance(props, null);
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("emailer@payaccess.com", "PayAccess"));

            InternetAddress[] addrs = InternetAddress.parse(requestData.getUsername(), false);
            msg.setRecipients(Message.RecipientType.TO, addrs);

            msg.setSubject("Welcome to PayAccess");
            msg.setContent(htmlMessage, "text/html; charset=utf-8");

            //msg.setText("Copy the url and paste in your browser to activate your account - http://137.184.47.182:8081/payaccess/api/v1/user/activate-account/"+user.getEmailAddress()+"/" + verificationLink +" - providing the OTP: " + otp);

            msg.setSentDate(new Date());

            SMTPTransport t =
                    (SMTPTransport) session.getTransport("smtps");
            t.connect("smtp.mailgun.org", "postmaster@mails.valuenaira.com", "k0l01qaz!QAZ");
            t.sendMessage(msg, msg.getAllRecipients());

            logger.info("Response: {}" , t.getLastServerResponse());

            t.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error("Error Sending Mail ...{}", e);
        }
    }


    public User getUserByEmailAddress(String emailAddress) {

        List<User> userList = userDao.getUserByEmailAddress(emailAddress);

        if(userList.isEmpty())
            return null;

        return userList.get(0);
    }


    public List<Long> getMerchantIdsByUsername(String username) {
        List<Long> merchantIdList = userDao.getMerchantIdsByUsername(username);

        if(merchantIdList.isEmpty())
            return null;

        return merchantIdList;
    }


    public List<UserRolePermission> getPermissionsByRole(String userRole, Integer pageNumber, Integer rowCount)
    {
        List<UserRolePermission> userRolePermissionList = userDao.getPermissionsByRole(userRole, pageNumber, rowCount);

        if(userRolePermissionList.isEmpty())
            return null;

        return userRolePermissionList;
    }
}
