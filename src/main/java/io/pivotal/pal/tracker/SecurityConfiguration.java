package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${SECURITY_FORCE_HTTPS}")
    private boolean secure;

//    @Autowired
//    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        if (secure) {
            http.requiresChannel().anyRequest().requiresSecure();
        }

        http.authorizeRequests().antMatchers("/**").hasRole("USER")
                    .and()
                .httpBasic()
                    .and()
                .csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }

//    public void configureGlobal() throws Exception {
//        authenticationManagerBuilder
//                .inMemoryAuthentication()
//                .withUser("user").password("password").roles("USER");
//    }


}
