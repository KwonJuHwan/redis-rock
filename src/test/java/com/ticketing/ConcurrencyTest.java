package com.ticketing;

import com.ticketing.ticket.Stock;
import com.ticketing.ticket.Ticket;
import com.ticketing.ticket.TicketLockFacade;
import com.ticketing.ticket.TicketRepository;
import com.ticketing.ticket.TicketRequest;
import com.ticketing.ticket.TicketService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private TicketLockFacade ticketLockFacade;

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketService ticketService;


    @AfterEach
    void cleanUp(){
        ticketRepository.deleteAll();
    }

    @Test
    void 동시에_100명이_티켓을_구매한다() throws InterruptedException {
        Long id = ticketRepository.save(Ticket.builder().name("아이유 2024").price(100000)
            .stock(Stock.builder().quantity(100).build()).build()).getId();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        TicketRequest ticketRequest = new TicketRequest(id,1);
        for (int i =0; i<100; i++){
            executorService.submit(()->{
                try {
                    ticketLockFacade.purchase(ticketRequest);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        Assertions.assertThat(ticket.getStock().getQuantity()).isZero();
    }

}
