package com.slj.util.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class MongoDBUtil {

   /*   MongoClient的实例代表数据库连接池，是线程安全的，可以被多线程共享，客户端在多线程条件下仅维持一个实例即可
      Mongo是非线程安全的，目前mongodb API中已经建议用MongoClient替代Mongo*/

    private MongoClient mongoClient = null;


    public static MongoDBUtil getMongoDBUtilInstance() {
        return mongoDBUtil;
    }

    private static final MongoDBUtil mongoDBUtil = new MongoDBUtil();

    @SuppressWarnings("static-access")
    private MongoDBUtil() {

        if (mongoClient == null) {
            String[] mongoHosts=System.getProperty ("http://mongo.host").split (",");
            List<ServerAddress> addrs = new ArrayList<ServerAddress>();
            for(String host:mongoHosts) {
                if (host!=null&&!host.trim().isEmpty()) {
                    ServerAddress serverAddress = new ServerAddress(host);
                    addrs.add(serverAddress);
                }
            }
            MongoCredential credential = MongoCredential.createCredential("userName",
                     "auth_db","password".toCharArray());

            MongoClientOptions.Builder build = new MongoClientOptions.Builder();
            build.connectionsPerHost(50); // 与目标数据库能够建立的最大connection数量为50
            build.threadsAllowedToBlockForConnectionMultiplier(50); // 如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
            build.maxWaitTime(50);
            build.connectTimeout(6000); // 与数据库建立连接的timeout设置为1分钟
            MongoClientOptions options = build.build();

            try {
                // 数据库连接实例
                mongoClient = new MongoClient(addrs, credential, options);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public MongoCollection<Document> getMongoCollection(String dbName, String collectionName) {
        return mongoClient.getDatabase(dbName).getCollection(collectionName);
    }

    public MongoDatabase getMongoDatabase(String dbName) {
        return mongoClient.getDatabase(dbName);
    }

    public String save(String dbName, String collectionName,Document document){
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        try {
            mongoDB = mongoClient.getDatabase (dbName); // 获取数据库实例
            MongoCol = mongoDB.getCollection (collectionName); // 获取数据库中指定的collection集合
            MongoCol.insertOne (document);
            return document.get ("_id").toString ( );
        }catch (Exception e){
            e.printStackTrace ();
        }
        return null;
    }

    public boolean inSert(String dbName, String collectionName, List<Document> documents) {
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;

        if (documents == null || documents.size() == 0) {
            return false;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合

                MongoCol.insertMany(documents);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

    }

    public boolean deleteOne(String dbName, String collectionName, Document doc) {
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (doc == null) {
            return false;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取指定的数据库
                MongoCol = mongoDB.getCollection(collectionName); // 获取指定的collectionName集合

                // MongoCol.deleteOne(Filters.eq("age",21));
                return MongoCol.deleteOne(doc).getDeletedCount() >= 0 ? true : false; // 根据删除执行结果进行判断后返回结果
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
    }

    /**
     *
     * @param dbName
     * @param collectionName
     * @param doc
     * @return
     */
    public boolean deleteOneById(String dbName, String collectionName, Bson doc) {
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (doc == null) {
            return false;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取指定的数据库
                MongoCol = mongoDB.getCollection(collectionName); // 获取指定的collectionName集合

                // MongoCol.deleteOne(Filters.eq("age",21));
                return MongoCol.deleteOne(doc).getDeletedCount() >= 0 ? true : false; // 根据删除执行结果进行判断后返回结果
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
    }

    public boolean deleteMany(String dbName, String collectionName, Document doc) {
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (doc == null) {
            return false;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取指定的数据库
                MongoCol = mongoDB.getCollection(collectionName); // 获取指定的collectionName集合

                return MongoCol.deleteMany(doc).getDeletedCount() >= 0 ? true : false; // 根据删除执行结果进行判断后返回结果
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
    }

    public boolean updateOne(String dbName, String collectionName, Document oldDoc, Document newDoc) {
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;

        if (oldDoc == null || newDoc == null) {
            return false;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合

                return MongoCol.updateOne(oldDoc, newDoc).getModifiedCount() >= 0 ? true : false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
    }

    public boolean updateMany(String dbName, String collectionName, Document oldDoc, Document newDoc) {
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;

        if (oldDoc == null || newDoc == null) {
            return false;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合

                return MongoCol.updateMany(oldDoc, newDoc).getModifiedCount() >= 0 ? true : false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
    }

    public ArrayList<Document> find(String dbName, String collectionName, Document doc, int limit) {
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (doc == null) {
            return resultList;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合

                FindIterable<Document> iterable = MongoCol.find(doc).limit(limit); // 查询获取数据
                for (Document document : iterable) {
                    if(document.containsKey ("_id")){
                        document.append ("_id",document.get ("_id").toString ());
                    }
                    resultList.add(document);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public ArrayList<Document> findOwnSort(String dbName, String collectionName, Document doc, String sortColumn,
                                           boolean isAsc, int limit) {
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (doc == null) {
            return resultList;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合

                FindIterable<Document> iterable = MongoCol.find(doc).sort(new Document(sortColumn, isAsc ? 1 : -1))
                        .limit(limit); // 查询获取数据
                for (Document document : iterable) {
                    if(document.containsKey ("_id")){
                        document.append ("_id",document.get ("_id").toString ());
                    }
                    resultList.add(document);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public ArrayList<Document> findByPage(String dbName,String collectionName,Document doc,String sortColumn,boolean isAsc,int startNum,int pageSize){
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (doc == null) {
            return resultList;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合

                FindIterable<Document> iterable = MongoCol.find(doc).sort(new Document(sortColumn, isAsc ? 1 : -1))
                        .skip (startNum).limit (pageSize);
                for (Document document : iterable) {
                    if(document.containsKey ("_id")){
                        document.append ("_id",document.get ("_id").toString ());
                    }
                    resultList.add(document);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }


    public long count(String dbName,String collectionName,Document doc){
        MongoDatabase mongoDB  = mongoClient.getDatabase(dbName); // 获取数据库实例
        MongoCollection<Document> MongoCol  = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合
        if (doc == null) {
            return  MongoCol.count ();
        }else{
            return MongoCol.count (doc);
        }
    }

    public long count(String dbName,String collectionName,Bson doc){
        MongoDatabase mongoDB  = mongoClient.getDatabase(dbName); // 获取数据库实例
        MongoCollection<Document> MongoCol  = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合
        if (doc == null) {
            return  MongoCol.count ();
        }else{
            return MongoCol.count (doc);
        }
    }

    public ArrayList<Document> findOwnSort(String dbName, String collectionName, Document doc, String sortColumn,
                                           boolean isAsc) {
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (doc == null) {
            return resultList;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合
                FindIterable<Document> iterable = MongoCol.find(doc).sort(new Document(sortColumn, isAsc ? 1 : -1)); // 查询获取数据
                for (Document document : iterable) {
                    if(document.containsKey ("_id")){
                        document.append ("_id",document.get ("_id").toString ());
                    }
                    resultList.add(document);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public ArrayList<Document> findOwnSort(String dbName, String collectionName, Document doc, Bson projection,String sortColumn,
                                           boolean isAsc) {
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (doc == null) {
            return resultList;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合
                FindIterable<Document> iterable = MongoCol.find(doc).projection(projection).sort(new Document(sortColumn, isAsc ? 1 : -1)); // 查询获取数据
                for (Document document : iterable) {
                    if(document.containsKey ("_id")){
                        document.append ("_id",document.get ("_id").toString ());
                    }
                    resultList.add(document);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public ArrayList<Document> findAll(String dbName, String collectionName, String sortColumn,boolean isAsc){
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        try {
            mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
            MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合
            FindIterable<Document> iterable = MongoCol.find().sort(new Document(sortColumn, isAsc ? 1 : -1)); // 查询获取数据
            for (Document document : iterable) {
                if(document.containsKey ("_id")){
                    document.append ("_id",document.get ("_id").toString ());
                }
                resultList.add(document);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public Document findByObjectId(String dbName, String collectionName,String objectId){
        if(StringUtils.isBlank (objectId)){
            return null;
        }
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        try {
            mongoDB = mongoClient.getDatabase (dbName); // 获取数据库实例
            MongoCol = mongoDB.getCollection (collectionName); // 获取数据库中指定的collection集合
            Document document = new Document ( );
            document.append ("_id", new ObjectId(objectId));
            FindIterable<Document> iterable = MongoCol.find (document);
            return iterable.first ( );
        }catch (Exception e){
            e.printStackTrace ();
        }
        return null;
    }

    public Document findOne(String dbName, String collectionName, Document doc, String sortColumn, boolean isAsc) {
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (doc == null || sortColumn == null || sortColumn.isEmpty()) {
            return null;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合
                FindIterable<Document> iterable = MongoCol.find(doc).sort(new Document(sortColumn, isAsc ? 1 : -1))
                        .limit(1); // 查询获取数据
                for (Document document : iterable) {
                    resultList.add(document);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList.size() == 0 ? null : resultList.get(0);
    }

    public boolean isExit(String dbName, String collectionName, Document doc) {
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;

        if (doc == null)
            return false;
        try {
            mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
            MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合

            return MongoCol.count(doc) > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Document> findAll(String dbName, String collectionName){
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        try {
            mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
            MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合
            FindIterable<Document> iterable = MongoCol.find(); // 查询获取数据
            for (Document document : iterable) {
                if(document.containsKey ("_id")){
                    document.append ("_id",document.get ("_id").toString ());
                }
                resultList.add(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public ArrayList<Document> find(String dbName, String collectionName, Document doc) {
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (doc == null) {
            return resultList;
        } else {
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合

                FindIterable<Document> iterable = MongoCol.find(doc); // 查询获取数据
                for (Document document : iterable) {
                    if(document.containsKey ("_id")){
                        document.append ("_id",document.get ("_id").toString ());
                    }
                    resultList.add(document);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public ArrayList<Document> findDocumentList(String dbName, String collectionName, Bson filter,Bson sorts){
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (filter == null) {
            return resultList;
        } else {
            try{
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合
                FindIterable<Document> iterable = MongoCol.find(filter).sort(sorts); // 查询获取数据
                for (Document document : iterable) {
                    if(document.containsKey ("_id")){
                        document.append ("_id",document.get ("_id").toString ());
                    }
                    resultList.add(document);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return resultList;
    }

    /**
     * MongoDB聚合方法
     * 可用于处理数据(诸如统计平均值,求和等)，并返回计算后的数据结果
     * @param dbName
     * @param collectionName
     * @param operation
     * @return
     */
    public ArrayList<Document> aggregate(String dbName,String collectionName,List operation){
        ArrayList<Document> resultList = new ArrayList<Document>(); // 创建返回的结果集
        MongoDatabase mongoDB = null;
        MongoCollection<Document> MongoCol = null;
        if (false) {
            return resultList;
        }else{
            try {
                mongoDB = mongoClient.getDatabase(dbName); // 获取数据库实例
                MongoCol = mongoDB.getCollection(collectionName); // 获取数据库中指定的collection集合
                AggregateIterable<Document> iterable = MongoCol.aggregate(operation);

                for (Document document : iterable) {
                    resultList.add(document);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }
}
