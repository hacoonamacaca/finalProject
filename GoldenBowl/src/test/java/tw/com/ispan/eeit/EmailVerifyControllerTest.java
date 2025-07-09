package tw.com.ispan.eeit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import tw.com.ispan.eeit.controller.emailVerify.EmailVerifyController;
import tw.com.ispan.eeit.jwt.JsonWebTokenInterceptor;
import tw.com.ispan.eeit.jwt.JsonWebTokenUtility;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.emailVerify.UserTokenBean;
import tw.com.ispan.eeit.repository.UserRepository;
import tw.com.ispan.eeit.service.emailVerify.UserTokenService;

@WebMvcTest(EmailVerifyController.class)
public class EmailVerifyControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserTokenService userTokenService;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private JsonWebTokenUtility jsonWebTokenUtility;
	@MockBean
	private JsonWebTokenInterceptor jsonWebToeknInterceptor;

	void verifyEmail_success() throws Exception {
		// 準備資料
		UserTokenBean userToken = new UserTokenBean();
		userToken.setUserId(1);
		userToken.setVerifyCode("test-token");

		UserBean user = new UserBean();
		user.setId(1);
		user.setIsVerify(false);

		// Mock 行為
		when(userTokenService.findValidToken("test-token")).thenReturn(Optional.of(userToken));
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		// 注意這裡 save 你可以不用特別 mock，Spring 會自動略過

		mockMvc.perform(MockMvcRequestBuilders.get("/api/verify-email")
				.param("token", "test-token"))
				.andExpect(status().isOk())
				.andExpect(content().string("Email 驗證成功，請返回登入頁！"));
	}

	@Test
	void verifyEmail_fail_tokenNotFound() throws Exception {
		when(userTokenService.findValidToken("invalid-token")).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/verify-email")
				.param("token", "invalid-token"))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string("驗證失敗或連結已失效，請重新註冊或聯絡客服。"));
	}

	void verifyEmail_fail_userNotFound() throws Exception {
		UserTokenBean userToken = new UserTokenBean();
		userToken.setUserId(99);

		when(userTokenService.findValidToken("token2")).thenReturn(Optional.of(userToken));
		when(userRepository.findById(99)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/verify-email")
				.param("token", "token2"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("驗證失敗"));
	}
}
