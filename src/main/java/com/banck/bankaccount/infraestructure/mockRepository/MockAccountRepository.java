package com.banck.bankaccount.infraestructure.mockRepository;

import com.banck.bankaccount.domain.Account;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.banck.bankaccount.aplication.model.AccountRepository;

/**
 *
 * @author jonavcar
 */
@Component
public class MockAccountRepository implements AccountRepository {

    @Override
    public Mono<Account> get(String credito) {
        Account c = new Account();
        c.setAccount("34984545");
        c.setCustomer("CTP");
        return Mono.just(c);
    }

    @Override
    public Flux<Account> listAll() {
        List<Account> lc = new ArrayList<>();
        Account c = new Account();
        c.setAccount("34984545");
        c.setCustomer("CTP");
        lc.add(c);
        return Flux.fromIterable(lc);
    }

    @Override
    public Mono<Account> create(Account c) {
        return Mono.just(c);
    }

    @Override
    public Mono<Account> update(String credito, Account c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(String dniRuc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Flux<Account> listAccountByCustomer(String customer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
