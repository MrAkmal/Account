package com.example.account.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {


    @Query(value = "select cast( (select array_to_json(array_agg(\"table\")) from (\n" +
            "               select sum(price) as total, count(distinct user_id) as number_of_users from payment\n" +
            "           )\"table\") as text);", nativeQuery = true)
    String getTotal();


    @Query(value = "select cast((select array_to_json(array_agg(\"table\"))\n" +
            "             from (\n" +
            "                      select u.name as username,sum(p.price) as total\n" +
            "                      from payment p\n" +
            "                               inner join users u on u.id = p.user_id\n" +
            "                      group by username\n" +
            "                 ) \"table\") as text); ",nativeQuery = true)
    String findTotalPaymentsOfUser();



}
