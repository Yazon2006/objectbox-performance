package io.objectbox.performanceapp;

import android.content.Context;
import android.database.Cursor;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.performanceapp.greendao.DaoMaster;
import io.objectbox.performanceapp.greendao.DaoMaster.DevOpenHelper;
import io.objectbox.performanceapp.greendao.DaoSession;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNull;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNullDao;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNullIndexed;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNullIndexedDao;

/**
 * Created by Markus on 01.10.2016.
 */

public class GreendaoPerfTest extends PerfTest {
    private DaoSession daoSession;
    private SimpleEntityNotNullDao dao;
    private boolean versionLoggedOnce;
    private SimpleEntityNotNullIndexedDao daoIndexed;

    @Override
    public String name() {
        return "greenDAO";
    }

    public void setUp(Context context, PerfTestRunner testRunner) {
        super.setUp(context, testRunner);
        Database db = new DevOpenHelper(context, "sqlite-greendao").getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        dao = daoSession.getSimpleEntityNotNullDao();
        daoIndexed = daoSession.getSimpleEntityNotNullIndexedDao();

        if (!versionLoggedOnce) {
            Cursor cursor = db.rawQuery("select sqlite_version() AS sqlite_version", null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        log("SQLite version " + cursor.getString(0));
                    }
                } finally {
                    cursor.close();
                }
            }
            versionLoggedOnce = true;
        }
    }

    @Override
    public void run(TestType type) {
        switch (type.name) {
            case TestType.BULK_OPERATIONS:
                runBatchPerfTest();
                break;
            case TestType.BULK_OPERATIONS_INDEXED:
                runBatchPerfTestIndexed();
                break;
        }
    }

    public void runBatchPerfTest() {
        List<SimpleEntityNotNull> list = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            list.add(createEntity((long) i));
        }
        benchmark.start("insert");
        dao.insertInTx(list);
        log(benchmark.stop());

        for (SimpleEntityNotNull entity : list) {
            changeForUpdate(entity);
        }
        benchmark.start("update");
        dao.updateInTx(list);
        log(benchmark.stop());

        benchmark.start("load");
        List<SimpleEntityNotNull> reloaded = dao.loadAll();
        log(benchmark.stop());

        benchmark.start("access");
        accessAll(reloaded);
        log(benchmark.stop());

        benchmark.start("delete");
        dao.deleteAll();
        log(benchmark.stop());
    }

    protected void changeForUpdate(SimpleEntityNotNull entity) {
        entity.setSimpleInt(random.nextInt());
        entity.setSimpleLong(random.nextLong());
        entity.setSimpleBoolean(random.nextBoolean());
        entity.setSimpleDouble(random.nextDouble());
        entity.setSimpleFloat(random.nextFloat());
        entity.setSimpleString("Another " + entity.getSimpleString());
    }

    public static SimpleEntityNotNull createEntity(Long key) {
        SimpleEntityNotNull entity = new SimpleEntityNotNull();
        if (key != null) {
            entity.setId(key);
        }
        entity.setSimpleBoolean(true);
        entity.setSimpleByte(Byte.MAX_VALUE);
        entity.setSimpleShort(Short.MAX_VALUE);
        entity.setSimpleInt(Integer.MAX_VALUE);
        entity.setSimpleLong(Long.MAX_VALUE);
        entity.setSimpleFloat(Float.MAX_VALUE);
        entity.setSimpleDouble(Double.MAX_VALUE);
        entity.setSimpleString("greenrobot greenDAO");
        byte[] bytes = {42, -17, 23, 0, 127, -128};
        entity.setSimpleByteArray(bytes);
        return entity;
    }

    protected void accessAll(List<SimpleEntityNotNull> list) {
        for (SimpleEntityNotNull entity : list) {
            entity.getId();
            entity.getSimpleBoolean();
            entity.getSimpleByte();
            entity.getSimpleShort();
            entity.getSimpleInt();
            entity.getSimpleLong();
            entity.getSimpleFloat();
            entity.getSimpleDouble();
            entity.getSimpleString();
            entity.getSimpleByteArray();
        }
    }


    public void runBatchPerfTestIndexed() {
        List<SimpleEntityNotNullIndexed> list = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            list.add(createEntityIndexed((long) i));
        }
        benchmark.start("insert");
        daoIndexed.insertInTx(list);
        log(benchmark.stop());

        for (SimpleEntityNotNullIndexed entity : list) {
            changeForUpdateIndexed(entity);
        }
        benchmark.start("update");
        daoIndexed.updateInTx(list);
        log(benchmark.stop());

        benchmark.start("load");
        List<SimpleEntityNotNullIndexed> reloaded = daoIndexed.loadAll();
        log(benchmark.stop());

        benchmark.start("access");
        accessAllIndexed(reloaded);
        log(benchmark.stop());

        benchmark.start("delete");
        daoIndexed.deleteAll();
        log(benchmark.stop());
    }

    protected void changeForUpdateIndexed(SimpleEntityNotNullIndexed entity) {
        entity.setSimpleInt(random.nextInt());
        entity.setSimpleLong(random.nextLong());
        entity.setSimpleBoolean(random.nextBoolean());
        entity.setSimpleDouble(random.nextDouble());
        entity.setSimpleFloat(random.nextFloat());
        entity.setSimpleString("Another " + entity.getSimpleString());
    }

    public static SimpleEntityNotNullIndexed createEntityIndexed(Long key) {
        SimpleEntityNotNullIndexed entity = new SimpleEntityNotNullIndexed();
        if (key != null) {
            entity.setId(key);
        }
        entity.setSimpleBoolean(true);
        entity.setSimpleByte(Byte.MAX_VALUE);
        entity.setSimpleShort(Short.MAX_VALUE);
        entity.setSimpleInt(Integer.MAX_VALUE);
        entity.setSimpleLong(Long.MAX_VALUE);
        entity.setSimpleFloat(Float.MAX_VALUE);
        entity.setSimpleDouble(Double.MAX_VALUE);
        entity.setSimpleString("greenrobot greenDAO");
        byte[] bytes = {42, -17, 23, 0, 127, -128};
        entity.setSimpleByteArray(bytes);
        return entity;
    }

    protected void accessAllIndexed(List<SimpleEntityNotNullIndexed> list) {
        for (SimpleEntityNotNullIndexed entity : list) {
            entity.getId();
            entity.getSimpleBoolean();
            entity.getSimpleByte();
            entity.getSimpleShort();
            entity.getSimpleInt();
            entity.getSimpleLong();
            entity.getSimpleFloat();
            entity.getSimpleDouble();
            entity.getSimpleString();
            entity.getSimpleByteArray();
        }
    }

    @Override
    public void tearDown() {
        daoSession.getDatabase().close();
    }

}