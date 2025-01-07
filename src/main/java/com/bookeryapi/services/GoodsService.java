package com.bookeryapi.services;

import java.util.UUID;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import org.jooq.bookery.default_.public_.tables.Goods;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsService {

    private final DSLContext dslContext;

    public com.bookeryapi.dto.Goods create(com.bookeryapi.dto.Goods goods) {
        var id = UUID.randomUUID();
        var result = dslContext.insertInto(Goods.GOODS)
                .values(id, goods.name(), goods.price(), goods.totalCount(), goods.soldCount(), goods.deleted())
                .execute();
        log.info("Inserted with result: {}", result);
        return getById(id);
    }

    public com.bookeryapi.dto.Goods update(com.bookeryapi.dto.Goods goods) {
        var updated = dslContext.update(Goods.GOODS)
                .set(Goods.GOODS.ID, goods.id())
                .set(Goods.GOODS.NAME, goods.name())
                .set(Goods.GOODS.PRICE, goods.price())
                .set(Goods.GOODS.SOLD_COUNT, goods.soldCount())
                .set(Goods.GOODS.TOTAL_COUNT, goods.totalCount())
                .set(Goods.GOODS.DELETED, goods.deleted())
                .execute();
        log.info("Successfully updated {} rows", updated);
        return this.getById(goods.id());
    }

    public com.bookeryapi.dto.Goods getById(UUID id) {
        final var fetchedRecord = dslContext.select(
                        Goods.GOODS.ID, Goods.GOODS.NAME, Goods.GOODS.PRICE,
                        Goods.GOODS.SOLD_COUNT, Goods.GOODS.TOTAL_COUNT, Goods.GOODS.DELETED
                )
                .from(Goods.GOODS)
                .where(Goods.GOODS.ID.eq(id))
                .fetchOne();
        Assert.notNull(fetchedRecord, "Record with id = " + id + " is not exists");
        return new com.bookeryapi.dto.Goods(
                fetchedRecord.get(Goods.GOODS.ID),
                fetchedRecord.get(Goods.GOODS.NAME),
                fetchedRecord.get(Goods.GOODS.PRICE),
                fetchedRecord.get(Goods.GOODS.SOLD_COUNT),
                fetchedRecord.get(Goods.GOODS.TOTAL_COUNT),
                fetchedRecord.get(Goods.GOODS.DELETED)
        );
    }

    public void delete(UUID id) {
        dslContext.update(Goods.GOODS).
                set(Goods.GOODS.DELETED, true)
                .where(Goods.GOODS.ID.eq(id))
                .execute();

        log.info("Successfully deleted the good with id = [" + id + "]");
    }
}