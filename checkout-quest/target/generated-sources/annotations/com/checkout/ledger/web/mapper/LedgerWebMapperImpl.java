package com.checkout.ledger.web.mapper;

import com.checkout.ledger.application.usecase.gettrialbalance.TrialBalanceResult;
import com.checkout.ledger.infrastructure.projection.AccountReadModelEntity;
import com.checkout.ledger.infrastructure.projection.EntryLineReadModelEntity;
import com.checkout.ledger.infrastructure.projection.JournalEntryReadModelEntity;
import com.checkout.ledger.web.dto.AccountResponse;
import com.checkout.ledger.web.dto.EntryLineResponse;
import com.checkout.ledger.web.dto.JournalEntryResponse;
import com.checkout.ledger.web.dto.TrialBalanceResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-10T21:45:10+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class LedgerWebMapperImpl implements LedgerWebMapper {

    @Override
    public AccountResponse toResponse(AccountReadModelEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String accountId = null;
        String code = null;
        String name = null;
        String type = null;
        String normalBalance = null;
        BigDecimal balanceAmount = null;
        String currency = null;

        accountId = entity.getAccountId();
        code = entity.getCode();
        name = entity.getName();
        type = entity.getType();
        normalBalance = entity.getNormalBalance();
        balanceAmount = entity.getBalanceAmount();
        currency = entity.getCurrency();

        AccountResponse accountResponse = new AccountResponse( accountId, code, name, type, normalBalance, balanceAmount, currency );

        return accountResponse;
    }

    @Override
    public EntryLineResponse toResponse(EntryLineReadModelEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String accountCode = null;
        BigDecimal amount = null;
        String currency = null;
        String side = null;
        String description = null;

        accountCode = entity.getAccountCode();
        amount = entity.getAmount();
        currency = entity.getCurrency();
        side = entity.getSide();
        description = entity.getDescription();

        EntryLineResponse entryLineResponse = new EntryLineResponse( accountCode, amount, currency, side, description );

        return entryLineResponse;
    }

    @Override
    public JournalEntryResponse toResponse(JournalEntryReadModelEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String entryId = null;
        String referenceId = null;
        String referenceType = null;
        Instant postedAt = null;
        List<EntryLineResponse> lines = null;

        entryId = entity.getEntryId();
        referenceId = entity.getReferenceId();
        referenceType = entity.getReferenceType();
        postedAt = entity.getPostedAt();
        lines = entryLineReadModelEntityListToEntryLineResponseList( entity.getLines() );

        JournalEntryResponse journalEntryResponse = new JournalEntryResponse( entryId, referenceId, referenceType, postedAt, lines );

        return journalEntryResponse;
    }

    @Override
    public TrialBalanceResponse toResponse(TrialBalanceResult result) {
        if ( result == null ) {
            return null;
        }

        List<AccountResponse> accounts = null;
        BigDecimal totalDebitBalances = null;
        BigDecimal totalCreditBalances = null;
        boolean balanced = false;

        accounts = accountReadModelEntityListToAccountResponseList( result.accounts() );
        totalDebitBalances = result.totalDebitBalances();
        totalCreditBalances = result.totalCreditBalances();
        balanced = result.balanced();

        TrialBalanceResponse trialBalanceResponse = new TrialBalanceResponse( accounts, totalDebitBalances, totalCreditBalances, balanced );

        return trialBalanceResponse;
    }

    protected List<EntryLineResponse> entryLineReadModelEntityListToEntryLineResponseList(List<EntryLineReadModelEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<EntryLineResponse> list1 = new ArrayList<EntryLineResponse>( list.size() );
        for ( EntryLineReadModelEntity entryLineReadModelEntity : list ) {
            list1.add( toResponse( entryLineReadModelEntity ) );
        }

        return list1;
    }

    protected List<AccountResponse> accountReadModelEntityListToAccountResponseList(List<AccountReadModelEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<AccountResponse> list1 = new ArrayList<AccountResponse>( list.size() );
        for ( AccountReadModelEntity accountReadModelEntity : list ) {
            list1.add( toResponse( accountReadModelEntity ) );
        }

        return list1;
    }
}
