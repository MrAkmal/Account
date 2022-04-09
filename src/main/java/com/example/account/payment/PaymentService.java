package com.example.account.payment;


import com.example.account.transfer.TransferDto;
import com.example.account.user.User;
import com.example.account.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final UserRepository userRepository;

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public Payment getById(Long id) {
        Optional<Payment> optionalPayment = repository.findById(id);
        return optionalPayment.orElse(null);
    }


    public List<Payment> getAll() {
        return repository.findAll();
    }


    public Long create(PaymentDto dto) {

        Optional<User> optionalUser = userRepository.findById(dto.getUserId());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("USER NOT FOUND");
        }
        if (dto.getPrice() <= 0) {
            throw new RuntimeException("PAYMENT MUST BE GREATER THAN 0");
        }
        Payment payment = Payment.builder()
                .user(optionalUser.get())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .build();
        return repository.save(payment).getId();
    }


    public List<TotalDto> getTotal() {

        try {
            String total = repository.getTotal();
            return Objects.isNull(total) ? null : MAPPER.readValue(total, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<PaymentUserDto> getTotalPaymentsOfUser() {

        try {
            String payments = repository.findTotalPaymentsOfUser();
            return Objects.isNull(payments) ? null : MAPPER.readValue(payments, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<TransferDto> transferMe(Long userId) {

        List<TotalDto> total = getTotal();
        List<TransferDto> transfers = new ArrayList<>(Collections.emptyList());
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("USER NOT FOUND");
        }
        String usernameOfSupplier = optionalUser.get().getName();

        for (TotalDto dto : total) {
            double average = dto.getTotal() / dto.getNumber_of_users();

            List<PaymentUserDto> totalPaymentsOfUser = getTotalPaymentsOfUser();
            List<PaymentUserDto> recipients = getRecipients(average, totalPaymentsOfUser);
            Optional<PaymentUserDto> first = recipients.parallelStream()
                    .filter(recipient -> recipient.getUsername()
                            .equals(usernameOfSupplier)).findFirst();
            if (first.isEmpty()) {
                throw new RuntimeException("USER NOT FOUND");
            }

            PaymentUserDto getUser = first.get();
            List<PaymentUserDto> suppliers = getSuppliers(average, totalPaymentsOfUser);
            for (PaymentUserDto supplier : suppliers) {
                double getAmount = getUser.getTotal() - average;
                if (getAmount == 0) break;
                double giveAmount = average - supplier.getTotal();
                if (giveAmount >= getAmount) {
                    transfers.add(new TransferDto(supplier.getUsername(), usernameOfSupplier, getAmount));
                    getUser.setTotal(average);
                } else {
                    transfers.add(new TransferDto(supplier.getUsername(), usernameOfSupplier, giveAmount));
                    getUser.setTotal(getUser.getTotal() - giveAmount);
                }
                if (getUser.getTotal() == average) break;
            }
        }
        return transfers;
    }

    private List<PaymentUserDto> getSuppliers(double average, List<PaymentUserDto> totalPaymentsOfUser) {
        return totalPaymentsOfUser.stream()
                .filter(pay -> pay.getTotal() < average)
                .collect(Collectors.toList());
    }

    private List<PaymentUserDto> getRecipients(double average, List<PaymentUserDto> totalPaymentsOfUser) {
        return totalPaymentsOfUser.stream()
                .filter(pay -> pay.getTotal() >= average)
                .collect(Collectors.toList());
    }


    public List<TransferDto> transferTo(Long userId) {

        List<TotalDto> total = getTotal();
        List<TransferDto> transfers = new ArrayList<>(Collections.emptyList());
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("USER NOT FOUND");
        }
        String usernameOfSupplier = optionalUser.get().getName();


        for (TotalDto dto : total) {
            double average = dto.getTotal() / dto.getNumber_of_users();

            List<PaymentUserDto> totalPaymentsOfUser = getTotalPaymentsOfUser();

            List<PaymentUserDto> suppliers = getSuppliers(average, totalPaymentsOfUser);

            Optional<PaymentUserDto> first = suppliers.parallelStream()
                    .filter(supplier -> supplier.getUsername()
                            .equals(usernameOfSupplier)).findFirst();
            if (first.isEmpty()) {
                throw new RuntimeException("USER NOT FOUND");
            }

            PaymentUserDto getUser = first.get();

            List<PaymentUserDto> recipients = getRecipients(average, totalPaymentsOfUser);

            for (PaymentUserDto recipient : recipients) {
                double giveAmount = average - getUser.getTotal();
                double getAmount = recipient.getTotal() - average;
                if (getAmount >= giveAmount) {
                    transfers.add(new TransferDto(usernameOfSupplier, recipient.getUsername(), giveAmount));
                    giveAmount = 0;
                } else {
                    transfers.add(new TransferDto(usernameOfSupplier, recipient.getUsername(), getAmount));
                    getUser.setTotal(getUser.getTotal() + getAmount);
                }
                if (giveAmount == 0) break;
            }
        }

        return transfers;
    }
}
