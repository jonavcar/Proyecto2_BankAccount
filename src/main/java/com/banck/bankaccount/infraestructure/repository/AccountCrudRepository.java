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

        account.setComMaint(ad.isComMaint());
        account.setDayMovem(ad.getDayMovem());
        account.setMmpdm(ad.getMmpdm());
        account.setTopMMovem(ad.getTopMMovem());
        account.setStatus(ad.isStatus());

        return account;
    }

    @Override
    public Flux<Account> listAll() {
        return accountRepository.findAll().map(this::AccountDaoToAccount);
    }

    @Override
    public Mono<Account> create(Account c) {
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

    public AccountDao AccountToAccountDao(Account account) {
        AccountDao creditDao = new AccountDao();
        creditDao.setAccount(account.getAccount());
        creditDao.setCustomer(account.getCustomer());
        creditDao.setCustomerType(account.getCustomerType());
        creditDao.setAccountType(account.getAccountType());
        creditDao.setDateCreated(account.getDateCreated());

        creditDao.setComMaint(account.isComMaint());
        creditDao.setDayMovem(account.getDayMovem());
        creditDao.setMmpdm(account.getMmpdm());
        creditDao.setTopMMovem(account.getTopMMovem());
        creditDao.setStatus(account.isStatus());

        return creditDao;
    }

    @Override
    public Flux<Account> listAccountByCustomer(String customer) {
        return accountRepository.findAllByCustomer(customer).map(this::AccountDaoToAccount);
    }

}
