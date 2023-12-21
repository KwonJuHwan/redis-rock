package com.ticketing.ticket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketLockFacade {

    private final TicketService ticketService;

    private final RedissonClient redissonClient;

    public void purchase(TicketRequest ticketRequest){
        RLock lock = redissonClient.getLock(String.format("purchase:ticket:%d", ticketRequest.id()));
        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!available){
                log.info("lock 획득 실패");
            }
            ticketService.purchase(ticketRequest);
        } catch (Exception e) {
            throw new RuntimeException("lock fail");
        } finally {
            lock.unlock();
        }
    }

}
