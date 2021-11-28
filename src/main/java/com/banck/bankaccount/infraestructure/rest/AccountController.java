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
import com.banck.bankaccount.utils.SunatUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
        reqAccount.setStatus(true);
        return Mono.just(reqAccount).flatMap(account -> {
            String msgTipoCuenta
                    = "Cuenta Ahorro = { \"accountType\": \"CA\" }\n"
                    + "Cuenta Corriente = { \"accountType\": \"CC\" }\n"
                    + "Cuenta Plazo Fijo = { \"accountType\": \"CPF\" }";
            String msgTipoCliente
                    = "Cliente Personal = { \"customerType\": \"CP\" }\n"
                    + "Cliente Personal VIP = { \"customerType\": \"CPV\" }\n"
                    + "Cliente Empresarial = { \"customerType\": \"CE\" }\n"
                    + "Cliente Empresarial PYME = { \"customerType\": \"CEP\" }";

            if (Optional.ofNullable(account.getAccountType()).isEmpty()) {
                return Mono.just(ResponseEntity.ok(""
                        + "Debe ingresar Tipo Cuenta, Ejemplos: \n"
                        + msgTipoCuenta));
            }

            if (Optional.ofNullable(account.getCustomer()).isEmpty()) {
                return Mono.just(ResponseEntity.ok("Debe ingresar el cliente, Ejemplo { \"customer\": \"78345212\" }"));
            }

            if (Optional.ofNullable(account.getCustomerType()).isEmpty()) {
                return Mono.just(ResponseEntity.ok(""
                        + "Debe ingresar Tipo Cliente, Ejemplos: \n"
                        + msgTipoCliente));
            }


            /*
                Se realiza validacion basica de datos para poder registrar
                una cuenta bancaria
             */
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
            if (!isAccountType || Optional.ofNullable(account.getAccountType()).isEmpty()) {
                return Mono.just(ResponseEntity.ok(""
                        + "Solo existen estos Codigos de Cuentas: \n"
                        + msgTipoCuenta));
            }
            if (!isCustomerType || Optional.ofNullable(account.getCustomerType()).isEmpty()) {
                return Mono.just(ResponseEntity.ok(""
                        + "Solo existen estos Codigos de Tipos de Clientes: \n"
                        + msgTipoCliente));
            }

            if (CustomerType.PERSONAL.equals(account.getCustomerType())
                    || CustomerType.PERSONAL_VIP.equals(account.getCustomerType())) {
                if (SunatUtils.isRUCValid(account.getCustomer())) {
                    return Mono.just(ResponseEntity.ok("El RUC " + account.getCustomer() + " es solo para empresas, debe registrarse con DNI."));
                }
            } else {
                if (!SunatUtils.isRUCValid(account.getCustomer())) {
                    return Mono.just(ResponseEntity.ok("El RUC " + account.getCustomer() + " de la Empresa No es Válido!!"));
                }
            }

            /*
                Se define la logica de las cuentas al registra una cuenta segun
                el tipo de la cuenta que se esta creando
             */
            if (AccountType.SAVINGS_ACCOUNT.equals(account.getAccountType())) {
                account.setComMaint(false);
                account.setDayMovem(0);
                if (CustomerType.PERSONAL.equals(account.getCustomerType())) {
                    account.setMmpdm(0);
                    // El registrador debe ingresar el Numero maximo de movimientos mensuales
                    if (Optional.ofNullable(account.getTopMMovem()).isEmpty() || account.getTopMMovem() <= 0) {
                        return Mono.just(ResponseEntity.ok("Debe ingresar el Numero maximo de movimientos mensuales Ejemplo: { \"topMMovem\": 2}"));
                    }
                } else if (CustomerType.PERSONAL_VIP.equals(account.getCustomerType())) {
                    account.setTopMMovem(0);
                    // El registrador debe ingresar el monto minimo de promedio diario mensual
                    if (Optional.ofNullable(account.getMmpdm()).isEmpty() || account.getMmpdm() <= 0) {
                        return Mono.just(ResponseEntity.ok("Debe ingresar el Monto minimo de promedio diario mensual Ejemplo: { \"mmpdm\": 100}"));
                    }
                } else {
                    account.setMmpdm(0);
                    account.setTopMMovem(0);
                }

            }
            if (AccountType.FIXED_TERM_ACCOUNT.equals(account.getAccountType())) {
                account.setComMaint(false);
                account.setTopMMovem(1);
                account.setMmpdm(0);
                if (CustomerType.PERSONAL.equals(account.getCustomerType())) {
                    //El registrador debe ingresar el dia del mes en que el usuario puede realizar algun movimiento bancario
                    if (Optional.ofNullable(account.getDayMovem()).isEmpty() || account.getDayMovem() <= 0 || account.getDayMovem() > 31) {
                        return Mono.just(ResponseEntity.ok("Debe ingresar el Dia del mes en que el usuario puede realizar algun movimiento Ejemplo:\n"
                                + "{ \"dayMovem\": 21} \n"
                                + "** Los valores son los dias del mes y deben ser entre 1 y 31"));
                    }
                } else {
                    account.setDayMovem(0);
                }
            }
            if (AccountType.CURRENT_ACCOUNT.equals(account.getAccountType())) {
                account.setComMaint(true);
                account.setTopMMovem(0);
                account.setDayMovem(0);
                account.setMmpdm(0);
            }

            /*
                Se realiza la logica de cuentas con relacion al tipo de
                cliente ya sea pesonal como empresarial
             */
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
