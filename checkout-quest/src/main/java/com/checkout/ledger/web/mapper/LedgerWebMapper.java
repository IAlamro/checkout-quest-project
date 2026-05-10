package com.checkout.ledger.web.mapper;

import com.checkout.ledger.application.usecase.gettrialbalance.TrialBalanceResult;
import com.checkout.ledger.infrastructure.projection.AccountReadModelEntity;
import com.checkout.ledger.infrastructure.projection.EntryLineReadModelEntity;
import com.checkout.ledger.infrastructure.projection.JournalEntryReadModelEntity;
import com.checkout.ledger.web.dto.AccountResponse;
import com.checkout.ledger.web.dto.EntryLineResponse;
import com.checkout.ledger.web.dto.JournalEntryResponse;
import com.checkout.ledger.web.dto.TrialBalanceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LedgerWebMapper {

    AccountResponse toResponse(AccountReadModelEntity entity);

    EntryLineResponse toResponse(EntryLineReadModelEntity entity);

    JournalEntryResponse toResponse(JournalEntryReadModelEntity entity);

    TrialBalanceResponse toResponse(TrialBalanceResult result);
}
