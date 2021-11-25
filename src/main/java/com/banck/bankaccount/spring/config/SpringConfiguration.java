package com.banck.bankaccount.spring.config;

import com.banck.bankaccount.infraestructure.repository.AccountCrudRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.banck.bankaccount.aplication.model.AccountRepository;

/**
 *
 * @author jonavcar
 */
@Configuration
public class SpringConfiguration {

    @Bean
    public AccountRepository accountRepository() {
        return new AccountCrudRepository();
    }
}
