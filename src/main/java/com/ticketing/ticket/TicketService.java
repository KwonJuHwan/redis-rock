package com.ticketing.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public void purchase(TicketRequest ticketRequest) throws IllegalAccessException {
        Ticket ticket = ticketRepository.findById(ticketRequest.id()).orElseThrow(IllegalAccessException::new);
        ticket.purchase(ticketRequest.quantity());
    }

}
