package com.banck.bankaccount.infraestructure.repository;

import com.banck.bankaccount.infraestructure.model.dao.AccountDao;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 *
 * @author jonavcar
 */
public interface IAccountCrudRepository extends ReactiveCrudRepository<AccountDao, String> {

    Flux<AccountDao> findAllByCustomer(String cliente);
}
