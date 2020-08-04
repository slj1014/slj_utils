package com.slj.util.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luffy on 15/12/21.
 * 该类用配置文件中读取参数,并初始化mongodb的连接
 */
public class MongoDB {
    private static MongoClient mongoClient;
    private final Logger logger = LoggerFactory.getLogger(MongoDB.class);

    private String userName="";
    private String authDb="";
    private String dbName="";
    private String password="";
    private String host="";


    public synchronized MongoClient getMongoClient() {
        if (mongoClient == null) {
            MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(userName, authDb, password.toCharArray());
            mongoClient = new MongoClient(new ServerAddress(host), mongoCredential,
                    new MongoClientOptions.Builder().cursorFinalizerEnabled(true).build());
        }
        return mongoClient;
    }

    public MongoDatabase getDefaultMongoDatabase() {
        return getMongoClient().getDatabase(dbName);
    }

    public MongoCollection<Document> getCollection(String col) {
        return getDefaultMongoDatabase().getCollection(col);
    }


    private Document replaceObjectId(Document doc) {
        if (doc.containsKey("_id")) {
            String id = doc.get("_id").toString();
            doc.remove("_id");
            doc.put("id", id);
        }
        return doc;
    }

    private Document getOne(FindIterable<Document> result) {
        if (result == null || result.first() == null) {
            return new Document();
        }
        return result.first();
    }

    private Document getOneWithoutObjectId(FindIterable<Document> result) {
        if (result == null || result.first() == null) {
            return new Document();
        }
        return replaceObjectId(result.first());
    }

    private List<Document> getMany(FindIterable<Document> result) {
        List<Document> list = new ArrayList<>();
        if (result == null) {
            return list;
        }

        MongoCursor<Document> iterator = result.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        iterator.close();
        return list;
    }

    private List<Document> getManyWithoutObjectId(FindIterable<Document> result) {
        List<Document> list = new ArrayList<>();
        if (result == null) {
            return list;
        }
        MongoCursor<Document> iterator = result.iterator();
        while (iterator.hasNext()) {
            Document doc = iterator.next();
            list.add(replaceObjectId(doc));
        }
        iterator.close();
        return list;
    }

    //查找一项
    public Document findOne(String table, Bson filter, Bson project) {
        Document doc = new Document();
        try {
            FindIterable<Document> result = getCollection(table).find(filter).projection(project);
            return getOne(result);
        } catch (Exception e) {
            logger.error("FindOne error-table:{},filter:{},filter:{},sort:{},exception:{}", table, filter, project, e.getMessage());
        }
        return doc;
    }

    //查找一项
    public Document findOneWithoutObjectId(String table, Bson filter, Bson project) {
        Document doc = new Document();
        try {
            FindIterable<Document> result = getCollection(table).find(filter).projection(project);
            return getOneWithoutObjectId(result);
        } catch (Exception e) {
            logger.error("FindOne error-table:{},filter:{},filter:{},sort:{},exception:{}", table, filter, project, e.getMessage());
        }
        return doc;
    }


    //查找一项
    public Document findOne(String table, Bson filter, Bson project, Bson sort) {
        Document doc = new Document();
        try {
            FindIterable<Document> result = getCollection(table).find(filter).projection(project).sort(sort);
            return getOne(result);
        } catch (Exception e) {
            logger.error("FindOne error-table:{},filter:{},filter:{},sort:{},exception:{}", table, filter, project, sort, e.getMessage());
        }
        return doc;
    }

    //查找一项
    public Document findOneWithoutObjectId(String table, Bson filter, Bson project, Bson sort) {
        Document doc = new Document();
        try {
            FindIterable<Document> result = getCollection(table).find(filter).projection(project).sort(sort);
            return getOneWithoutObjectId(result);
        } catch (Exception e) {
            logger.error("FindOne error-table:{},filter:{},filter:{},sort:{},exception:{}", table, filter, project, sort, e.getMessage());
        }
        return doc;
    }

    //查找多项
    public List<Document> findMany(String table, Bson filter, Bson project, Bson sort, int count) {
        List<Document> list = new ArrayList<>();
        try {
            FindIterable<Document> result = getCollection(table).find(filter).projection(project).sort(sort).limit(count);
            return getMany(result);
        } catch (Exception e) {
            logger.error("Find error-table:{},filter:{},project:{},sort:{},count:{},exception:{}", table, filter, project, sort, count, e.getMessage());
        }
        return list;
    }

    //查找多项
    public List<Document> findManyWithoutObjectId(String table, Bson filter, Bson project, Bson sort, int count) {
        List<Document> list = new ArrayList<>();
        try {
            FindIterable<Document> result = getCollection(table).find(filter).projection(project).sort(sort).limit(count);
            return getManyWithoutObjectId(result);
        } catch (Exception e) {
            logger.error("Find error-table:{},filter:{},project:{},sort:{},count:{},exception:{}", table, filter, project, sort, count, e.getMessage());
        }
        return list;
    }


    //查找多项(不限数量)
    public List<Document> findMany(String table, Bson filter, Bson project, Bson sort) {
        List<Document> list = new ArrayList<>();
        try {
            FindIterable<Document> result = getCollection(table).find(filter).projection(project).sort(sort);
            return getMany(result);
        } catch (Exception e) {
            logger.error("Find error-table:{},filter:{},project:{},sort:{},exception:{}", table, filter, project, sort, e.getMessage());
        }
        return list;
    }

    //查找多项(不限数量)
    public List<Document> findManyWithoutObjectId(String table, Bson filter, Bson project, Bson sort) {
        List<Document> list = new ArrayList<>();
        try {
            FindIterable<Document> result = getCollection(table).find(filter).projection(project).sort(sort);
            return getManyWithoutObjectId(result);
        } catch (Exception e) {
            logger.error("Find error-table:{},filter:{},project:{},sort:{},exception:{}", table, filter, project, sort, e.getMessage());
        }
        return list;
    }

    //查找多项(指定数量)
    public List<Document> findMany(String table, Bson filter, Bson project, Bson sort,int count,int skip) {
        List<Document> list = new ArrayList<>();
        try {
            FindIterable<Document> result = getCollection(table).find(filter).projection(project).sort(sort).limit(count).skip(skip);
            return getMany(result);
        } catch (Exception e) {
            logger.error("Find error-table:{},filter:{},project:{},sort:{},exception:{}", table, filter, project, sort, e.getMessage());
        }
        return list;
    }

    //查找多项(指定数量)
    public List<Document> findManyWithoutObjectId(String table, Bson filter, Bson project, Bson sort,int count,int skip) {
        List<Document> list = new ArrayList<>();
        try {
            FindIterable<Document> result = getCollection(table).find(filter).projection(project).sort(sort).limit(count).skip(skip);
            return getManyWithoutObjectId(result);
        } catch (Exception e) {
            logger.error("Find error-table:{},filter:{},project:{},sort:{},exception:{}", table, filter, project, sort, e.getMessage());
        }
        return list;
    }

    //计算条数
    public long count(String table, Bson filter) {
        long count = 0;
        try {
            count = getCollection(table).count(filter);
        } catch (Exception e) {
            logger.error("count error-table:{},filter:{},exception:{}", table, filter, e.getMessage());
        }
        return count;
    }

    //插入文档
    public boolean insertOne(String table, Document doc) {
        boolean result = false;
        try {
            getCollection(table).insertOne(doc);
            result = true;
        } catch (Exception e) {
            logger.error("insert error-table:{},doc:{},exception:{}", table, doc, e.getMessage());
        }
        return result;
    }

    //插入文档
    public boolean insertMany(String table, List<Document> list) {
        boolean result = false;
        try {
            getCollection(table).insertMany(list);
            result = true;
        } catch (Exception e) {
            logger.error("insert error-table:{},doc:{},exception:{}", table, list.size(), e.getMessage());
        }
        return result;
    }

    //删除文档
    public boolean deleteOne(String table, Bson filter) {
        boolean result = false;
        try {
            getCollection(table).deleteOne(filter);
            result = true;
        } catch (Exception e) {
            logger.error("delete error-table:{},filter:{},exception:{}", table, filter, e.getMessage());
        }
        return result;
    }

    //删除多个文档
    public boolean deleteMany(String table, Bson filter) {
        boolean result = false;
        try {
            DeleteResult rs = getCollection(table).deleteMany(filter);
            result = rs.wasAcknowledged();
        } catch (Exception e) {
            logger.error("deleteMany error-table:{},filter:{},exception:{}", table, filter, e.getMessage());
        }
        return result;
    }

    //更新单个文档
    public boolean updateOne(String table, Bson filter, Bson update) {
        boolean result = false;
        try {
            UpdateResult rs = getCollection(table).updateOne(filter, update);
            result = rs.wasAcknowledged();
        } catch (Exception e) {
            logger.error("updateOne error-table:{},filter:{},update:{},exception:{}", table, filter, update, e.getMessage());
        }
        return result;
    }

    //更新单个文档
    public boolean updateOne(String table, Bson filter, Bson update, UpdateOptions updateOptions) {
        boolean result = false;
        try {
            UpdateResult rs = getCollection(table).updateOne(filter, update, updateOptions);
            result = rs.wasAcknowledged();
        } catch (Exception e) {
            logger.error("updateOne error-table:{},filter:{},update:{},updateOptions:{},exception:{}", table, filter, update, updateOptions, e.getMessage());
        }
        return result;
    }

    //更新多个文档
    public boolean updateMany(String table, Bson filter, Bson update) {
        boolean result = false;
        try {
            UpdateResult rs = getCollection(table).updateMany(filter, update);
            result = rs.wasAcknowledged();
        } catch (Exception e) {
            logger.error("updateMany error-table:{},filter:{},update:{},exception:{}", table, filter, update, e.getMessage());
        }
        return result;
    }

    //取出不同项目
    public List<String> distinct(String table, String fieldName, Bson filter) {
        List<String> list = new ArrayList<String>();
        try {
            DistinctIterable<String> rs = getCollection(table).distinct(fieldName, filter, String.class);
            MongoCursor<String> iterator = rs.iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
        } catch (Exception e) {
            logger.error("distinct error-table:{},FieldName:{},filter:{},exception:{}", table, fieldName, filter, e.getMessage());
        }
        return list;
    }

    //删除整个表
    public boolean Drop(String table) {
        boolean result = false;
        try {
            getCollection(table).drop();
            result = true;
        } catch (Exception e) {
            logger.error("Drop table error-table:{},exception:{}", table, e.getMessage());
        }
        return result;
    }

    public Document group(String table, Document key, Document condition, Document initial, String reduce, String finalize) {
        Document doc = new Document();
        Document group = new Document();
        if (condition == null) {
            condition = new Document();
        }
        if (initial == null) {
            initial = new Document();
        }
        try {
            group.append("ns", table)
                    .append("key", key)
                    .append("cond", condition)
                    .append("initial", initial)
                /*.append("$reduce", reduce)*/
                /*.append("finalize", finalize)*/;
            if (StringUtils.isNotEmpty(reduce)) {
                group.append("$reduce", reduce);
            }
            if (StringUtils.isNotEmpty(finalize)) {
                group.append("finalize", finalize);
            }
            doc = getDefaultMongoDatabase().runCommand(new Document("group", group));
        } catch (Exception e) {
            logger.error("Group error-table:{},key:{},condition:{},reduce:{},initial:{},finalize:{},exception:{}", table, key, condition, reduce, initial, finalize, e.getMessage());
        }
        return doc;
    }


   /* public DBInitTaskEntity getDBInitTaskEntityByTaskIdOrResId(String taskId, String resourceId,String creatorId) {
        if (StringUtils.isEmpty(taskId) && StringUtils.isEmpty(resourceId))
            throw new ServiceException("at least one of taskId or resourceId is required");
        if(StringUtils.isEmpty(taskId) && !StringUtils.isEmpty(resourceId) && StringUtils.isEmpty(creatorId))
            throw new ServiceException("creatorId is required when resourceId is not empty");
        try {
            Bson filters = null;
            if (!StringUtils.isEmpty(taskId))
                filters = eq("taskId", taskId);
            else
                filters = and(eq("resourceId", resourceId),eq("creatorId", creatorId));
            MongoCollection<DBInitTaskEntity> collection = exDB.getCollection(ExchangeConstants.TABLE_DB_INIT_TASK
                    , DBInitTaskEntity.class);
            return collection.find(filters).first();
        } catch (Exception e) {
            logger.error(ExceptionHandleUtil.getExceptionInfo(e));
            throw new ServiceException("failed to do mongodb operation");
        }
    }*/
}
