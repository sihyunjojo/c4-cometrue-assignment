package org.c4marathon.assignment.domain.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.AccountNumberGenerator;
import org.c4marathon.assignment.AccountNumberRetryExecutor;
import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.domain.policy.SavingAccountPolicy;
import org.c4marathon.assignment.domain.repository.MainAccountRepository;
import org.c4marathon.assignment.domain.repository.SavingAccountRepository;
import org.c4marathon.assignment.exception.RetryableException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavingAccountService {

	private final SavingAccountRepository savingAccountRepository;
	private final MainAccountRepository mainAccountRepository;
	private final AccountNumberGenerator accountNumberGenerator;
	private final AccountNumberRetryExecutor accountNumberRetryExecutor;
	private final SavingAccountPolicy savingAccountPolicy;

	public SavingAccount createFixedSavingAccount(Long memberId, Long subscribedDepositAmount) {
		MainAccount mainAccount = mainAccountRepository.findByMemberId(memberId)
			.orElseThrow(() -> new IllegalArgumentException("메인 계좌를 찾을 수 없습니다."));

		String accountNumber = generateUniqueAccountNumber();

		SavingAccount savingAccount = SavingAccount.createFixed(
			accountNumber,
			memberId,
			mainAccount.getId(),
			subscribedDepositAmount
		);

		savingAccountRepository.save(savingAccount);
		return savingAccount;
	}

	public List<SavingAccount> findAll(){
		return savingAccountRepository.findAll();
	}

	public SavingAccount createFlexibleSavingAccount(Long memberId) {
		MainAccount mainAccount = mainAccountRepository.findByMemberId(memberId)
			.orElseThrow(() -> new IllegalArgumentException("메인 계좌를 찾을 수 없습니다."));

		String accountNumber = generateUniqueAccountNumber();

		SavingAccount savingAccount = SavingAccount.createFlexible(
			accountNumber,
			memberId,
			mainAccount.getId()
		);

		savingAccountRepository.save(savingAccount);
		return savingAccount;
	}

	private String generateUniqueAccountNumber() {
		return accountNumberRetryExecutor.executeWithRetry(() -> {
			String candidate = accountNumberGenerator.generate(savingAccountPolicy.getAccountPrefix());

			if (savingAccountRepository.existsByAccountNumber(candidate)) {
				throw new RetryableException("중복된 적금 계좌번호 발생. 재시도합니다.");
			}
			return candidate;
		});
	}

	public SavingAccount findByAccountNumberOrThrow(String accountNumber) {
		return savingAccountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new IllegalStateException(String.format("계좌번호 %s인 적금 계좌가 존재하지 않습니다.", accountNumber)));
	}

	public SavingAccount findById(Long accountId) {
		return savingAccountRepository.findById(accountId)
			.orElseThrow(() -> new IllegalStateException(String.format("ID가 %s인 적금 계좌가 존재하지 않습니다.", accountId)));
	}

	public SavingAccount findByIdWithoutSecondCache(Long accountId) {
		return savingAccountRepository.findByIdWithoutSecondCache(accountId)
			.orElseThrow(() -> new IllegalStateException(String.format("ID가 %s인 적금 계좌가 존재하지 않습니다.", accountId)));
	}

	public Long getMainAccountId(Long savingAccountId) {
		SavingAccount savingAccount = savingAccountRepository.findById(savingAccountId)
			.orElseThrow(() -> new IllegalArgumentException("적금 계좌를 찾을 수 없음"));
		return savingAccount.getMainAccountId();
	}

	public Long applyInterest(SavingAccount account) {
		double rate = savingAccountPolicy.getInterestRate(account.getSavingType());
		Long interest = account.calculateInterest(rate);
		account.deposit(interest);
		return interest;
	}

	public Map<Long, List<SavingAccount>> getSubscribedDepositAmount() {
		List<SavingAccount> accounts = savingAccountRepository.findAllFixedSavingAccountWithMainAccount();

		return accounts.stream()
			.filter(this::isLinkedToMainAccount)
			.collect(Collectors.groupingBy(
				SavingAccount::getMainAccountId,
				Collectors.toList()
			));
	}

	private boolean isLinkedToMainAccount(SavingAccount account) {
		return account.getMemberId() != null && account.getMainAccountId() != null;
	}
}

