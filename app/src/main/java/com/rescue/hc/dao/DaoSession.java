package com.rescue.hc.dao;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

//    private final DaoConfig taskMessageInfoDaoConfig;
//    private final DaoConfig taskScoreInfoDaoConfig;
//    private final DaoConfig trainItemsInfoDaoConfig;

//    private final TaskMessageInfoDao taskMessageInfoDao;
//    private final TaskScoreInfoDao taskScoreInfoDao;
//    private final TrainItemsInfoDao trainItemsInfoDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

//        taskMessageInfoDaoConfig = daoConfigMap.get(TaskMessageInfoDao.class).clone();
//        taskMessageInfoDaoConfig.initIdentityScope(type);
//
//        taskScoreInfoDaoConfig = daoConfigMap.get(TaskScoreInfoDao.class).clone();
//        taskScoreInfoDaoConfig.initIdentityScope(type);
//
//        trainItemsInfoDaoConfig = daoConfigMap.get(TrainItemsInfoDao.class).clone();
//        trainItemsInfoDaoConfig.initIdentityScope(type);
//
//        taskMessageInfoDao = new TaskMessageInfoDao(taskMessageInfoDaoConfig, this);
//        taskScoreInfoDao = new TaskScoreInfoDao(taskScoreInfoDaoConfig, this);
//        trainItemsInfoDao = new TrainItemsInfoDao(trainItemsInfoDaoConfig, this);
//
//        registerDao(TaskMessageInfo.class, taskMessageInfoDao);
//        registerDao(TaskScoreInfo.class, taskScoreInfoDao);
//        registerDao(TrainItemsInfo.class, trainItemsInfoDao);
    }
    
    public void clear() {
//        taskMessageInfoDaoConfig.clearIdentityScope();
//        taskScoreInfoDaoConfig.clearIdentityScope();
//        trainItemsInfoDaoConfig.clearIdentityScope();
    }

//    public TaskMessageInfoDao getTaskMessageInfoDao() {
//        return taskMessageInfoDao;
//    }
//
//    public TaskScoreInfoDao getTaskScoreInfoDao() {
//        return taskScoreInfoDao;
//    }
//
//    public TrainItemsInfoDao getTrainItemsInfoDao() {
//        return trainItemsInfoDao;
//    }

}
