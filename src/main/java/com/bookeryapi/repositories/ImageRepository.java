package com.bookeryapi.repositories;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.bookery.default_.public_.tables.Images;
import org.jooq.impl.DSL;
import org.jooq.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.postgresql.PGConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Component
@RequiredArgsConstructor
public class ImageRepository {
    private final DSLContext dslContext;

    public int saveImage(
            String name,
            InputStream imageStream)
            throws SQLException {

        final int[] insertedImageId = new int[1];
        dslContext.transaction((Configuration c) -> {
            DSL.using(c).connection(connection -> {
                LargeObjectManager manager = connection
                        .unwrap(PGConnection.class)
                        .getLargeObjectAPI();

                long oid = manager.createLO(LargeObjectManager.WRITE);
                LargeObject largeObject = manager.open(oid, LargeObjectManager.WRITE);

                largeObject.write(imageStream.readAllBytes());

                largeObject.close();

                insertedImageId[0] = dslContext
                        .insertInto(Images.IMAGES)
                        .set(Images.IMAGES.NAME, name)
                        .set(Images.IMAGES.LO_ID, oid)
                        .set(Images.IMAGES.TYPE, 1)
                        .returning(Images.IMAGES.ID)
                        .fetchOne(Images.IMAGES.ID);
            });
        });

        return insertedImageId[0];
    }

    public boolean isImageExistedById(int id) {
        Long oid = dslContext
                .select(Images.IMAGES.LO_ID)
                .from(Images.IMAGES)
                .where(Images.IMAGES.ID.eq(id))
                .fetchOne(Images.IMAGES.LO_ID);

        if (oid == null) {
            return false;
        }

        return true;
    }

    public byte[] getImageById(int id)
            throws SQLException {
        Long oid = dslContext
                .select(Images.IMAGES.LO_ID)
                .from(Images.IMAGES)
                .where(Images.IMAGES.ID.eq(id))
                .fetchOne(Images.IMAGES.LO_ID);

        if (oid == null) {
            throw new SQLException("Image not found for ID: " + id);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        dslContext.transaction((Configuration c) -> {
            DSL.using(c).connection(connection -> {
                LargeObjectManager manager = connection
                        .unwrap(PGConnection.class)
                        .getLargeObjectAPI();

                LargeObject largeObject = manager.open(oid, LargeObjectManager.READ);

                // Buffer size of 8 KB
                byte[] buffer = new byte[8192];

                int bytesRead;
                while ((bytesRead = largeObject.read(buffer, 0, buffer.length)) != 0) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                largeObject.close();
            });
        });

        return byteArrayOutputStream.toByteArray();
    }

    public void deleteImageById(int id) throws SQLException {
        Long oid = dslContext
                .select(Images.IMAGES.LO_ID)
                .from(Images.IMAGES)
                .where(Images.IMAGES.ID.eq(id))
                .fetchOne(Images.IMAGES.LO_ID);

        dslContext.transaction((Configuration c) -> {
            DSL.using(c).connection(connection -> {
                if (oid != null) {
                    LargeObjectManager manager = connection
                            .unwrap(PGConnection.class)
                            .getLargeObjectAPI();

                    manager.delete(oid);
                }
            });
        });

        dslContext.deleteFrom(Images.IMAGES)
                .where(Images.IMAGES.ID.eq(id))
                .execute();
    }

    public void updateImage(
            int id,
            InputStream imageData)
            throws SQLException {
        Long oldObjectId = dslContext
                .select(Images.IMAGES.LO_ID)
                .from(Images.IMAGES)
                .where(Images.IMAGES.ID.eq(id))
                .fetchOne(Images.IMAGES.LO_ID);

        dslContext.transaction((Configuration c) -> {
            DSL.using(c).connection(connection -> {
                if (oldObjectId != null) {
                    LargeObjectManager manager = connection
                            .unwrap(PGConnection.class)
                            .getLargeObjectAPI();

                    manager.delete(oldObjectId);

                    long newObjectId = manager.createLO(LargeObjectManager.WRITE);
                    LargeObject largeObject = manager.open(newObjectId, LargeObjectManager.WRITE);
                    
                    largeObject.write(imageData.readAllBytes());

                    largeObject.close();

                    dslContext.update(Images.IMAGES)
                            .set(Images.IMAGES.LO_ID, newObjectId)
                            .execute();
                }

            });
        });
    }
}
