package tw.com.ispan.eeit.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:emailToken.properties")
public class UserMailConfig {

	@Value("${spring.mail.host:smtp.gmail.com}")
	private String host;

	@Value("${spring.mail.port:587}")
	private int port;

	@Value("${spring.mail.username:eattiy1986@gmail.com}")
	private String username;

	@Value("${MAIL_PASSWORD:}")
	private String password;

	@Value("${spring.mail.properties.mail.smtp.auth:true}")
	private boolean auth;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
	private boolean starttlsEnable;

	@Value("${spring.mail.default-encoding:UTF-8}")
	private String encoding;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.smtp.auth", String.valueOf(auth));
		props.put("mail.smtp.starttls.enable", String.valueOf(starttlsEnable));
		mailSender.setDefaultEncoding(encoding);

		return mailSender;
	}
}
