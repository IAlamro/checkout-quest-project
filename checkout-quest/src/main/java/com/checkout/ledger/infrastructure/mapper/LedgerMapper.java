package com.checkout.ledger.infrastructure.mapper;

import com.checkout.ledger.domain.EntryLine;
import com.checkout.ledger.infrastructure.projection.EntryLineReadModelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LedgerMapper {

    @Mapping(target = "id", expression = "java(com.checkout.shared.util.UuidV7.generateAsString())")
    @Mapping(target = "entryId", source = "entryId")
    @Mapping(target = "accountCode", source = "line.accountCode.value")
    @Mapping(target = "amount", source = "line.amount.amount")
    @Mapping(target = "currency", expression = "java(line.amount().currency().getCurrencyCode())")
    @Mapping(target = "side", expression = "java(line.side().name())")
    @Mapping(target = "description", source = "line.description")
    EntryLineReadModelEntity toReadModel(String entryId, EntryLine line);
}
