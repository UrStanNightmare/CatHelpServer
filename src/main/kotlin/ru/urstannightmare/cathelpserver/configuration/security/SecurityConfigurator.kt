package ru.urstannightmare.cathelpserver.configuration.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.urstannightmare.cathelpserver.user.UserService

@Configuration
@EnableWebSecurity
class SecurityConfigurator(private val userService: UserService, private val tokenFilter: TokenFilter) {

//    @Value("\${server.origins}")
//    val origins: List<String> = listOf()

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    @Primary
    fun configureAuthenticationManagerBuilder(authenticationManagerBuilder: AuthenticationManagerBuilder): AuthenticationManagerBuilder {
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder())
        return authenticationManagerBuilder
    }

    @Bean
    fun corsConfigurationSource(@Autowired originsProperty: OriginsProperty): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = originsProperty.origins
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { httpBasic -> httpBasic.disable() }
            .csrf { csrf -> csrf.disable() }
            .cors {}
            .exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .sessionManagement { customizer ->
                customizer
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/api/**").fullyAuthenticated()
            }
            .addFilterAfter(tokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}