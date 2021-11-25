package com.banck.bankaccount.aplication;

import com.banck.bankaccount.domain.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author jonavcar
 */
public interface AccountOperations {

    public Flux<Account> list();
    
    public Flux<Account> listAccountByCustomer(String customer);

    public Mono<Account> get(String credito);
    
    public Mono<Account> create(Account c);

    public Mono<Account> update(String credito, Account c);

    public void delete(String credito);

}
