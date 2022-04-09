package com.example.account.payment;


import com.example.account.transfer.TransferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService service;

    //payment for something
    @PostMapping(value = "/pay_for")
    public ResponseEntity<Long> create(@RequestBody PaymentDto dto) {
        Long paymentId = service.create(dto);
        return ResponseEntity.ok(paymentId);
    }


    //total payment price
    @GetMapping(value = "/total")
    public ResponseEntity<List<TotalDto>> total() {
        List<TotalDto> total = service.getTotal();
        return ResponseEntity.ok(total);
    }


    //get count and total price of payments of user
    @GetMapping(value = "/total-payments-of-user")
    public ResponseEntity<List<PaymentUserDto>> totalPaymentsOfUser() {
        List<PaymentUserDto> total = service.getTotalPaymentsOfUser();
        return ResponseEntity.ok(total);
    }



    @GetMapping(value = "/transfer-me/{id}")
    public ResponseEntity<List<TransferDto>> transferMe(@PathVariable Long id) {
        List<TransferDto> total = service.transferMe(id);
        return ResponseEntity.ok(total);
    }


    @GetMapping(value = "/transfer-to/{id}")
    public ResponseEntity<List<TransferDto>> transferTo(@PathVariable Long id) {
        List<TransferDto> total = service.transferTo(id);
        return ResponseEntity.ok(total);
    }


    //get one payment with paymentId
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<Payment> get(@PathVariable Long id) {
        Payment payment = service.getById(id);
        return ResponseEntity.ok(payment);
    }



    //all payments
    @GetMapping(value = "/list")
    public ResponseEntity<List<Payment>> getAll() {
        List<Payment> payments = service.getAll();
        return ResponseEntity.ok(payments);
    }


}
