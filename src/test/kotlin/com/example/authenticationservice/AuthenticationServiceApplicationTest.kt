package com.example.authenticationservice

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import com.jayway.jsonpath.JsonPath
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.hamcrest.Matchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*


@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationServiceApplicationTest {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Test
    fun `Registering user with empty name should be unsuccessful`() {
        val json = "{\"name\":\"\",\"email\":\"nelson@mandela.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError);
    }

    @Test
    fun `Registering user with missing name should be unsuccessful`() {
        val json = "{\"email\":\"nelson@mandela.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError);
    }

    @Test
    fun `Registering user with empty email should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError);
    }

    @Test
    fun `Registering user with missing email should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError);
    }

    @Test
    fun `Registering user with wrong email should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelsonmandela.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError)
    }

    @Test
    fun `Registering user with empty password should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela.com\",\"password\":\"\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError);
    }

    @Test
    fun `Registering user with missing password should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela.com\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError);
    }

    @Test
    fun `Registering user with short password should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela.com\",\"password\":\"123\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError);
    }

    @Test
    fun `Registering user with long password should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela.com\",\"password\":\"12345678123456781234567812345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError);
    }

    @Test
    fun `Registering valid user should be successful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela1.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk);
    }

    @Test
    fun `Registering user with same email should be unsuccessful`() {
        val json = "{\"name\":\"Ricky\",\"email\":\"rick@martin.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andDo() {
                    this.mockMvc.perform(post("/api/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().is4xxClientError);
                };

    }


    @Test
    fun `Confirming user with valid token should be successful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela2.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andDo {
                    val response = this.mockMvc.perform(get("/admin/email_confirmation_token/nelson@mandela2.com")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk)
                            .andExpect(jsonPath("$.is_registration_confirmed", equalTo(false)))
                            .andReturn().response.contentAsString
                    val token = JsonPath.read<String>(response, "$.registration_token")
                    this.mockMvc.perform(get("/api/register/nelson@mandela2.com/$token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk)
                    this.mockMvc.perform(get("/admin/email_confirmation_token/nelson@mandela2.com")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk)
                            .andExpect(jsonPath("$.is_registration_confirmed", equalTo(true)))
                }
    }

    @Test
    fun `Confirming user with invalid token should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela3.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andDo {
                    this.mockMvc.perform(get("/admin/email_confirmation_token/nelson@mandela3.com")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk)
                            .andExpect(jsonPath("$.is_registration_confirmed", equalTo(false)))
                    this.mockMvc.perform(get("/api/register/nelson@mandela2.com/123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().is4xxClientError);
                }
    }

    @Test
    fun `Password reset request for an unregistered user should be unsuccessful`() {
        val json = "{\"email\":\"nelson@mandela4.com\"}"
        this.mockMvc!!.perform(post("/api/password_reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError);
    }

    @Test
    fun `Password reset request for an unconfirmed user should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela5.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andDo {
                    this.mockMvc.perform(post("/api/password_reset")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"nelson@mandela5.com\"}")
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().is4xxClientError);
                }

    }

    @Test
    fun `Password reset for a confirmed user with valid token should be successful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela6.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andDo {
                    val registrationResponse = this.mockMvc.perform(get("/admin/email_confirmation_token/nelson@mandela6.com")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk)
                            .andExpect(jsonPath("$.is_registration_confirmed", equalTo(false)))
                            .andReturn().response.contentAsString
                    val registrationToken = JsonPath.read<String>(registrationResponse, "$.registration_token")
                    this.mockMvc.perform(get("/api/register/nelson@mandela6.com/$registrationToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk).andDo {
                                this.mockMvc.perform(post("/api/password_reset")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"email\":\"nelson@mandela6.com\"}")
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk).andDo {
                                            val passwordResetResponse = this.mockMvc.perform(get("/admin/password_reset_token/nelson@mandela6.com")
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .accept(MediaType.APPLICATION_JSON))
                                                    .andExpect(status().isOk)
                                                    .andExpect(jsonPath("$.is_password_reset_requested", equalTo(true)))
                                                    .andReturn().response.contentAsString
                                            val passwordResetToken = JsonPath.read<String>(passwordResetResponse, "$.password_reset_token")
                                            this.mockMvc.perform(put("/api/password_reset")
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .content("{\"email\":\"nelson@mandela6.com\",\"password\":\"987654321\",\"token\":\"$passwordResetToken\"}")
                                                    .accept(MediaType.APPLICATION_JSON))
                                                    .andExpect(status().isOk)
                                        }
                            }


                }
    }

    @Test
    fun `Password reset for a confirmed user with invalid token should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela7.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andDo {
                    val registrationResponse = this.mockMvc.perform(get("/admin/email_confirmation_token/nelson@mandela7.com")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk)
                            .andExpect(jsonPath("$.is_registration_confirmed", equalTo(false)))
                            .andReturn().response.contentAsString
                    val registrationToken = JsonPath.read<String>(registrationResponse, "$.registration_token")
                    this.mockMvc.perform(get("/api/register/nelson@mandela7.com/$registrationToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk).andDo {
                                this.mockMvc.perform(post("/api/password_reset")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"email\":\"nelson@mandela7.com\"}")
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk).andDo {
                                            this.mockMvc.perform(put("/api/password_reset")
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .content("{\"email\":\"nelson@mandela7.com\",\"password\":\"987654321\",\"token\":\"123\"}")
                                                    .accept(MediaType.APPLICATION_JSON))
                                                    .andExpect(status().is4xxClientError);
                                        }
                            }


                }
    }

    @Test
    fun `Password reset for a confirmed user who hasn't requested change should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela8.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andDo {
                    val registrationResponse = this.mockMvc.perform(get("/admin/email_confirmation_token/nelson@mandela8.com")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk)
                            .andExpect(jsonPath("$.is_registration_confirmed", equalTo(false)))
                            .andReturn().response.contentAsString
                    val registrationToken = JsonPath.read<String>(registrationResponse, "$.registration_token")
                    this.mockMvc.perform(get("/api/register/nelson@mandela8.com/$registrationToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk).andDo {
                                this.mockMvc.perform(get("/admin/password_reset_token/nelson@mandela8.com")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk)
                                        .andExpect(jsonPath("$.is_password_reset_requested", equalTo(false)))
                                this.mockMvc.perform(put("/api/password_reset")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"email\":\"nelson@mandela8.com\",\"password\":\"987654321\",\"token\":\"123\"}")
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().is4xxClientError);
                            }


                }
    }

    @Test
    fun `Accessing the home page without logging in should be unsuccessful`() {
        this.mockMvc!!.perform(get("/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden)
    }

    @Test
    fun `Accessing the home page with wrong authorization should be unsuccessful`() {
        this.mockMvc!!.perform(get("/").header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhc2RAdGVzLmNvbWUiLCJpYXQiOjE1NzIwMzk5NzksImV4cCI6MTU3MjA0MzU3OX0.wTFlKBfEyw1jQMQ_dO0dgmYf7gVkYHZxc5hQRBXKUmU")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun `Accessing the home page after logging in should be successful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela9.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andDo {
                    val registrationResponse = this.mockMvc.perform(get("/admin/email_confirmation_token/nelson@mandela9.com")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk)
                            .andExpect(jsonPath("$.is_registration_confirmed", equalTo(false)))
                            .andReturn().response.contentAsString
                    val registrationToken = JsonPath.read<String>(registrationResponse, "$.registration_token")
                    this.mockMvc.perform(get("/api/register/nelson@mandela9.com/$registrationToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk).andDo {
                                val loginResponse = this.mockMvc.perform(post("/api/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"email\":\"nelson@mandela9.com\",\"password\":\"12345678\"}")
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk).andReturn().response.contentAsString
                                val loginToken = JsonPath.read<String>(loginResponse, "$.token")
                                this.mockMvc.perform(get("/").header("Authorization", "Bearer $loginToken")
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk)
                            }


                }
    }

    @Test
    fun `Logging in with unregistered user should be unsuccessful`() {
        this.mockMvc!!.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"nelson@mandela10.com\",\"password\":\"12345678\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError)
    }

    @Test
    fun `Logging in with unconfirmed user should be unsuccessful`() {
        val json = "{\"name\":\"Nelson Mandela\",\"email\":\"nelson@mandela11.com\",\"password\":\"12345678\"}"
        this.mockMvc!!.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andDo {
                    this.mockMvc.perform(post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"nelson@mandela11.com\",\"password\":\"12345678\"}")
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().is4xxClientError)
                }
    }

}
