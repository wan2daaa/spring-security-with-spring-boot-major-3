package wane.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import wane.study.entity.User;
import wane.study.service.PrincipalService;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorizeController.class)
@AutoConfigureMockMvc
class AuthorizeControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	PrincipalService service;
	private final String API = "/api";

	private Cookie cookie;

	@BeforeEach
	public void before() {

		cookie = new Cookie("accessToken", "accessToken");
		cookie.setPath("/");
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(10000);

	}

	@Test
	@WithMockUser(roles = "USER")
	@DisplayName("사용자 인증 withMockUser")
	void authorizeWithMockUser() throws Exception {
		Mockito.when(service.changeName(Mockito.any())).thenReturn(User.createUser("loginId", "password"));

		mockMvc.perform(get(API + "/authorize").cookie(cookie))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("인증 되지 않은 사용자는 not authoriezed 리턴")
	void authorizeWithNotAuthoriezed() throws Exception {
		ResultActions resultActions = mockMvc.perform(get(API + "/authorize"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isUnauthorized());
	}

}