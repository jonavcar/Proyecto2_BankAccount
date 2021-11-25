package com.banck.bankaccount.aplication.impl;

import com.banck.bankaccount.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.banck.bankaccount.aplication.AccountOperations;
import com.banck.bankaccount.aplication.model.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jonavcar
 */
@Service
@RequiredArgsConstructor
public class AccountOperationsImpl implements AccountOperations {

    Logger logger = LoggerFactory.getLogger(AccountOperationsImpl.class);
    private final AccountRepository accountRepository;

    @Override
    public Flux<Account> list() {
        return accountRepository.listAll();
    }

    @Override
    public Mono<Account> get(String credito) {
        return accountRepository.get(credito);
    }

    @Override
    public Mono<Account> create(Account account) {
        return accountRepository.create(account);
    }

    @Override
    public Mono<Account> update(String credito, Account c) {
        return accountRepository.update(credito, c);
    }

    @Override
    public void delete(String credito) {
        accountRepository.delete(credito);
    }

    @Override
    public Flux<Account> listAccountByCustomer(String customer) {
        return accountRepository.listAccountByCustomer(customer);
    }

}
