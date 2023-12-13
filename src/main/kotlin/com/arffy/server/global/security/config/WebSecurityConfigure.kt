package com.arffy.server.global.security.config

import com.arffy.server.domian.user.reposiroty.RefreshTokenRepository
import com.arffy.server.global.security.filter.JwtAuthenticationFilter
import com.arffy.server.global.security.filter.JwtExceptionFilter
import com.arffy.server.global.security.handler.OAuth2AuthenticationFailureHandler
import com.arffy.server.global.security.handler.OAuth2AuthenticationSuccessHandler
import com.arffy.server.global.security.lib.JwtTokenProvider
import com.arffy.server.global.security.repository.CookieAuthorizationRequestRepository
import com.arffy.server.global.security.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@EnableWebSecurity
@Configuration
class WebSecurityConfigure(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val cookieAuthorizationRequestRepository: CookieAuthorizationRequestRepository,
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .cors()
            .and()
            .httpBasic().disable()
            .csrf().disable()
            .formLogin().disable()
            .rememberMe().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.authorizeRequests()
            .antMatchers("/oauth2/**").permitAll()
            .antMatchers("/api/v1/qna/product/**").permitAll()
            .anyRequest().authenticated()
//            .anyRequest().permitAll()
        http.oauth2Login()
            .authorizationEndpoint()
            .authorizationRequestRepository(cookieAuthorizationRequestRepository)
            .and()
            .redirectionEndpoint()
            .and()
            .userInfoEndpoint().userService(customOAuth2UserService)
            .and()
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(oAuth2AuthenticationFailureHandler)
        http.logout()
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID")

        http.addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter::class.java
        )
        http.addFilterBefore(
            jwtExceptionFilter,
            jwtAuthenticationFilter::class.java
        )
        return http.build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer {
            it.ignoring()
                .antMatchers("/")
                .antMatchers(
                    "/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "swagger-resources/**",
                    "/v3/api-docs/**"
                )
                .antMatchers("/api/v1/auth/**")
                .antMatchers(HttpMethod.GET, "/api/v1/product/**")
                .antMatchers(HttpMethod.GET, "/api/v1/notice/**")
                .antMatchers(HttpMethod.POST, "/api/v1/payment/confirm")
                .antMatchers(HttpMethod.POST, "/api/v1/payment/verify/webhook")
        }
    }

    val jwtAuthenticationFilter: JwtAuthenticationFilter
        get() = JwtAuthenticationFilter(jwtTokenProvider, refreshTokenRepository)

    val jwtExceptionFilter: JwtExceptionFilter
        get() = JwtExceptionFilter()
}