package com.checkout.ledger.web;

import com.checkout.ledger.application.usecase.getjournalentries.GetJournalEntriesHandler;
import com.checkout.ledger.application.usecase.gettrialbalance.GetTrialBalanceHandler;
import com.checkout.ledger.infrastructure.projection.AccountJpaRepository;
import com.checkout.ledger.web.dto.AccountResponse;
import com.checkout.ledger.web.dto.JournalEntryResponse;
import com.checkout.ledger.web.dto.TrialBalanceResponse;
import com.checkout.ledger.web.mapper.LedgerWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
@Tag(name = "Ledger", description = "Double-entry bookkeeping - accounts, journal entries, and trial balance")
public class LedgerController {

    private final AccountJpaRepository accountJpaRepository;
    private final GetTrialBalanceHandler trialBalanceHandler;
    private final GetJournalEntriesHandler journalEntriesHandler;
    private final LedgerWebMapper ledgerWebMapper;

    @GetMapping("/accounts")
    @Operation(summary = "Get chart of accounts", description = "Returns all ledger accounts and their current balances.")
    @ApiResponse(responseCode = "200", description = "Account list returned")
    public ResponseEntity<List<AccountResponse>> getAccounts() {
        return ResponseEntity.ok(
                accountJpaRepository.findAll().stream().map(ledgerWebMapper::toResponse).toList());
    }

    @GetMapping("/journal-entries")
    @Operation(
            summary = "Get journal entries",
            description = "Returns all posted journal entries. Each entry is balanced - debits and credits always equal within a single entry."
    )
    @ApiResponse(responseCode = "200", description = "Journal entry list returned")
    public ResponseEntity<List<JournalEntryResponse>> getJournalEntries() {
        return ResponseEntity.ok(
                journalEntriesHandler.handle().stream().map(ledgerWebMapper::toResponse).toList());
    }

    @GetMapping("/trial-balance")
    @Operation(
            summary = "Get trial balance",
            description = "Returns aggregated balances for all accounts and a boolean confirming whether the books are in balance. After any confirmed payment, balanced must be true."
    )
    @ApiResponse(responseCode = "200", description = "Trial balance returned")
    public ResponseEntity<TrialBalanceResponse> getTrialBalance() {
        return ResponseEntity.ok(ledgerWebMapper.toResponse(trialBalanceHandler.handle()));
    }
}
