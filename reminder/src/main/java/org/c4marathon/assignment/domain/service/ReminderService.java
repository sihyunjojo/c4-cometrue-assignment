package org.c4marathon.assignment.domain.service;

import java.util.List;
import java.util.Map;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.model.PendingTransfer;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ReminderService {

	// public void remindTransactions(PendingTransfer transferTransaction) {
	// 	log.info("[Remind] Transaction ID: {}, Amount: {}, ExpiredAt: {}, ToMember: {}",
	// 		transferTransaction.getId(),
	// 		transferTransaction.getAmount(),
	// 		transferTransaction.getExpiredAt(),
	// 		transferTransaction.getToMainAccount().getMember().getName()
	// 	);
	// }

	public void remindTransactions(Map<Member, List<PendingTransfer>> grouped) {
		grouped.forEach((member, transactions) -> {
			log.info("[RemindGroup] Member: {} ({}건)", member.getName(), transactions.size());

			transactions.forEach(tx ->
				log.info(" - Tx ID: {}, Amount: {}, ExpiredAt: {}",
					tx.getId(), tx.getAmount(), tx.getExpiredAt())
			);
		});
	}
}
