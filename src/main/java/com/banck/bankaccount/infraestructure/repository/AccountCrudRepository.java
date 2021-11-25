package com.banck.bankaccount.infraestructure.repository;

import com.banck.bankaccount.domain.Account;
import com.banck.bankaccount.infraestructure.model.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.banck.bankaccount.aplication.model.AccountRepository;

/**
 *
 * @author jonavcar
 */
@Component
public class AccountCrudRepository implements AccountRepository {

    @Autowired
    IAccountCrudRepository accountRepository;

    @Override
    public Mono<Account> get(String account) {
        return accountRepository.findById(account).map(this::AccountDaoToAccount);
    }

    public Account AccountDaoToAccount(AccountDao ad) {
        Account account = new Account();
        account.setAccount(ad.getAccount());
        account.setCustomer(ad.getCustomer());
        account.setCustomerType(ad.getCustomerType());
        account.setAccountType(ad.getAccountType());
        account.setDateCreated(ad.getDateCreated());
        return account;
    }

    @Override
    public Flux<Account> listAll() {
        return accountRepository.findAll().map(this::AccountDaoToAccount);
    }

    @Override
    public Mono<Account> create(Account c) {
        Flux<AccountDao> fa = accountRepository.findAllByCustomer(c.getCustomer());
        return accountRepository.save(AccountToAccountDao(c)).map(this::AccountDaoToAccount);
    }

    @Override
    public Mono<Account> update(String account, Account c) {
        c.setAccount(account);
        return accountRepository.save(AccountToAccountDao(c)).map(this::AccountDaoToAccount);
    }

    @Override
    public void delete(String account) {
        accountRepository.deleteById(account).subscribe();
    }

    public AccountDao AccountToAccountDao(Account c) {
        AccountDao creditDao = new AccountDao();
        creditDao.setAccount(c.getAccount());
        creditDao.setCustomer(c.getCustomer());
        creditDao.setCustomerType(c.getCustomerType());
        creditDao.setAccountType(c.getAccountType());
        creditDao.setDateCreated(c.getDateCreated());
        return creditDao;
    }

    @Override
    public Flux<Account> listAccountByCustomer(String customer) {
        return accountRepository.findAllByCustomer(customer).map(this::AccountDaoToAccount);
    }
    

}
