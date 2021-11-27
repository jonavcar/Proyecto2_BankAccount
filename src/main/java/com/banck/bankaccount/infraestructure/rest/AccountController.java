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
import com.banck.bankaccount.aplication.CreditOperations;
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
    private final CreditOperations creditOperations;

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
    public Mono<ResponseEntity> create(@RequestBody Account reqAccount) {
        reqAccount.setAccount(reqAccount.getCustomer() + "-" + getRandomNumberString());
        reqAccount.setDateCreated(dateTime.format(formatter));
        return Mono.just(reqAccount).flatMap(account -> {
            boolean isAccountType = false;
            for (AccountType tc : AccountType.values()) {
                if (account.getAccountType().equals(tc.value)) {
                    isAccountType = true;
                }
            }

            boolean isCustomerType = false;
            for (CustomerType tc : CustomerType.values()) {
                if (account.getCustomerType().equals(tc.value)) {
                    isCustomerType = true;
                }
            }
            if (!isAccountType) {
                return Mono.just(ResponseEntity.ok("El codigo de Tipo Cuenta (" + account.getAccountType() + "), no existe!"));
            }
            if (!isCustomerType) {
                return Mono.just(ResponseEntity.ok("El codigo de Tipo Cliente (" + account.getCustomerType() + "), no existe!"));
            }

            if (CustomerType.PERSONAL.equals(account.getCustomerType())) {
                return operations.listAccountByCustomer(account.getCustomer()).filter(p -> p.getAccountType().equals(account.getAccountType())).count().flatMap(fm -> {
                    if (fm.intValue() == 0) {
                        return operations.create(account).flatMap(rp -> {
                            return Mono.just(ResponseEntity.ok(rp));
                        });
                    } else {
                        return Mono.just(ResponseEntity.ok("El Cliente Personal ya tiene este tipo de cuenta."));
                    }
                });
            } else if (CustomerType.PERSONAL_VIP.equals(account.getCustomerType())) {
                if (AccountType.SAVINGS_ACCOUNT.equals(account.getAccountType())) {
                    return creditOperations.creditCardsByCustomer(account.getCustomer()).flatMap(num -> {
                        if (num == 0) {
                            return Mono.just(ResponseEntity.ok("El Cliente Personal VIP debe tener previamente una Targeta de Credito."));
                        } else {
                            return operations.create(account).flatMap(rp -> {
                                return Mono.just(ResponseEntity.ok(rp));
                            });
                        }

                    }).onErrorReturn(ResponseEntity.ok("¡¡Ocurrio un Error en El Servicio de Credito (Consulta targetas), Reintente Mas Tarde!!"));
                } else {
                    return Mono.just(ResponseEntity.ok("El Cliente Personal VIP, solo puede tener cuentas de ahorro."));
                }
            } else if (CustomerType.BUSINESS.equals(account.getCustomerType())) {
                if (AccountType.CURRENT_ACCOUNT.equals(account.getAccountType())) {
                    return operations.create(account).flatMap(rp -> {
                        return Mono.just(ResponseEntity.ok(rp));
                    });
                } else {
                    return Mono.just(ResponseEntity.ok("El Cliente Empresarial, solo puede tener cuentas corrientes!!"));
                }
            } else if (CustomerType.BUSINESS_PYME.equals(account.getCustomerType())) {
                if (AccountType.CURRENT_ACCOUNT.equals(account.getAccountType())) {
                    return creditOperations.creditCardsByCustomer(account.getCustomer()).flatMap(num -> {
                        if (num == 0) {
                            return Mono.just(ResponseEntity.ok("El Cliente Empresarial PYME debe tener previamente una Targeta de Credito."));
                        } else {
                            return operations.create(account).flatMap(rp -> {
                                return Mono.just(ResponseEntity.ok(rp));
                            });
                        }

                    }).onErrorReturn(ResponseEntity.ok("¡¡Ocurrio un Error en El Servicio de Credito (Consulta targetas), Reintente Mas Tarde!!"));
                } else {
                    return Mono.just(ResponseEntity.ok("El Cliente Empresarial PYME, solo puede tener cuentas corrientes."));
                }
            } else {
                return Mono.just(ResponseEntity.ok("No se ha realizado nada!!"));
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
