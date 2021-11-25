package com.banck.bankaccount.infraestructure.rest;

import com.banck.bankaccount.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.banck.bankaccount.aplication.AccountOperations;
import com.banck.bankaccount.aplication.impl.AccountOperationsImpl;
import com.banck.bankaccount.utils.AccountType;
import com.banck.bankaccount.utils.CustomerType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author jonavcar
 */
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    Logger logger = LoggerFactory.getLogger(AccountOperationsImpl.class);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("America/Bogota"));
    private final AccountOperations operations;

    @GetMapping
    public Flux<Account> listAll() {
        return operations.list();
    }

    @GetMapping("/{account}")
    public Mono<Account> get(@PathVariable("account") String account) {
        return operations.get(account);
    }

    @GetMapping("/{customer}/list")
    public Flux<Account> listAccountByCustomer(@PathVariable("customer") String customer) {
        return operations.listAccountByCustomer(customer);
    }

    @PostMapping
    public Mono<ResponseEntity> create(@RequestBody Account c) {
        c.setAccount(c.getCustomer() + "-" + getRandomNumberString());
        c.setDateCreated(dateTime.format(formatter));
        return Mono.just(c).flatMap(m -> {
            boolean isAccountType = false;
            for (AccountType tc : AccountType.values()) {
                if (c.getAccountType().equals(tc.value)) {
                    isAccountType = true;
                }
            }

            boolean isCustomerType = false;
            for (CustomerType tc : CustomerType.values()) {
                if (c.getCustomerType().equals(tc.value)) {
                    isCustomerType = true;
                }
            }
            if (!isAccountType) {
                return Mono.just(ResponseEntity.ok("El codigo de Tipo Cuenta (" + c.getAccountType() + "), no existe!"));
            }
            if (!isCustomerType) {
                return Mono.just(ResponseEntity.ok("El codigo de Tipo Cliente (" + c.getCustomerType() + "), no existe!"));
            }

            if (CustomerType.NATURAL_PERSON.equals(m.getCustomerType())) {

                return operations.listAccountByCustomer(m.getCustomer()).filter(p -> p.getAccountType().equals(m.getAccountType())).count().flatMap(fm -> {
                    if (fm.intValue() == 0) {
                        return operations.create(c).flatMap(rp -> {
                            return Mono.just(ResponseEntity.ok(rp));
                        });
                    } else {
                        return Mono.just(ResponseEntity.ok("El cliente ya tiene este tipo de cuenta."));
                    }
                });
            } else {
                if (AccountType.CURRENT_ACCOUNT.equals(m.getAccountType())) {
                    return operations.create(c).flatMap(rp -> {
                        return Mono.just(ResponseEntity.ok(rp));
                    });
                } else {
                    return Mono.just(ResponseEntity.ok("La empresa solo puede tener cuentas corrientes!!"));
                }
            }
        });
    }

    @PutMapping("/{account}")
    public Mono<Account> update(@PathVariable("account") String account, @RequestBody Account c) {
        return operations.update(account, c);
    }

    @DeleteMapping("/{account}")
    public void delete(@PathVariable("account") String account) {
        operations.delete(account);
    }

    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(9999);
        return String.format("%04d", number);
    }
}
