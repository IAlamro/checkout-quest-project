package com.checkout.ledger.infrastructure.mapper;

import com.checkout.ledger.domain.AccountCode;
import com.checkout.ledger.domain.EntryLine;
import com.checkout.ledger.infrastructure.projection.EntryLineReadModelEntity;
import com.checkout.shared.domain.Money;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-10T21:45:10+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class LedgerMapperImpl implements LedgerMapper {

    @Override
    public EntryLineReadModelEntity toReadModel(String entryId, EntryLine line) {
        if ( entryId == null && line == null ) {
            return null;
        }

        String accountCode = null;
        BigDecimal amount = null;
        String description = null;
        if ( line != null ) {
            accountCode = lineAccountCodeValue( line );
            amount = lineAmountAmount( line );
            description = line.description();
        }
        String entryId1 = null;
        entryId1 = entryId;

        String id = com.checkout.shared.util.UuidV7.generateAsString();
        String currency = line.amount().currency().getCurrencyCode();
        String side = line.side().name();

        EntryLineReadModelEntity entryLineReadModelEntity = new EntryLineReadModelEntity( id, entryId1, accountCode, amount, currency, side, description );

        return entryLineReadModelEntity;
    }

    private String lineAccountCodeValue(EntryLine entryLine) {
        if ( entryLine == null ) {
            return null;
        }
        AccountCode accountCode = entryLine.accountCode();
        if ( accountCode == null ) {
            return null;
        }
        String value = accountCode.value();
        if ( value == null ) {
            return null;
        }
        return value;
    }

    private BigDecimal lineAmountAmount(EntryLine entryLine) {
        if ( entryLine == null ) {
            return null;
        }
        Money amount = entryLine.amount();
        if ( amount == null ) {
            return null;
        }
        BigDecimal amount1 = amount.amount();
        if ( amount1 == null ) {
            return null;
        }
        return amount1;
    }
}
