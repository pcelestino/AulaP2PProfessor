package br.edu.ffb.pedro.aulap2pprofessor;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import br.edu.ffb.pedro.aulap2pprofessor.model.DaoMaster;
import br.edu.ffb.pedro.aulap2pprofessor.model.DaoSession;

public class AulaP2PProfessorApp extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "professor.db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}