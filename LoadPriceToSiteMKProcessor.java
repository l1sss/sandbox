package com.tander.camel.processors;

import com.google.common.collect.Lists;
import com.tander.camel.shopMk.LoadPricesMode;
import com.tander.camel.shopMk.Price;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoadPriceToSiteMKProcessor implements Processor {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private int fetchSize = 10000; // Размер батча при чтении цен из БДСМ
    private int batchSize = 10000; // Размер батча при записи цен в БД Сайта МК

    private DataSource bdsmDatasource;
    private DataSource siteMkDatasource;
    private int numOfThreads;

    private final String BDSM_GET_PRICES_PRC = "select * from TABLE(site_mk.rest_pricesale_pkg.get_rest_pricesale_for_mk_fnc(?))";
    private final String SITE_MK_PREPARE_STOCK_STAGE_TBL_PRC = "{call PrepareStockStgTable(?)}";
    private final String SITE_MK_COMMIT_STOCK_STAGE_TBL_PRC = "{call CommitStockTable(?,?)}";
    private final String SITE_MK_INSERT_INTO_STAGE_TBL =
            "INSERT INTO mk_products_stock_stg (PRODUCT_CODE,SHOP_CODE,QUANTITY,PRICE_REGULAR,PRICE_PROMO,INSERT_DATE,PRICE_PROMO_DATE,PRICE_REGULAR_DATE,QUANTITY_DATE,PARTITION_ID) VALUES(?,?,?,?,?,?,?,?,?,?)";

    //INSERT INTO
    //mk_products_stock_stg PARTITION (?- здесь указываем имя партици которое вернулось из PrepareStockStgTable) (PRODUCT_CODE,SHOP_CODE,QUANTITY,PRICE_REGULAR,PRICE_PROMO,INSERT_DATE,PRICE_PROMO_DATE,PRICE_REGULAR_DATE,QUANTITY_DATE,PARTITION_ID)
    //VALUES(?,?,?,?,?,?,?,?,?,?)

    private static final List<Integer> ORA_RECOVERABLE_ERROR_CODE = Arrays.asList(4060, 4061, 4062, 4063, 4064, 4065,
            4066, 4067, 4068, 4069, 6508, 1012, 1033, 1034, 1041, 1089, 1090, 1114, 12154, 12203, 12224, 12500, 12505,
            12519, 12520, 12535, 12541, 12545, 12571, 1688, 17002, 17008, 17410, 17447, 23001, 27091, 27092, 27101,
            3113, 3114, 12514, 12521, 12527, 12537, 12540, 12509, 12510, 12511, 12526, 12528, 12545, 29);

    @Override
    public void process(Exchange exchange) throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        List<? extends Serializable> migratedPricesCounter;
        long startTime = System.nanoTime();
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
            migratedPricesCounter = startLoadPrices(LoadPricesMode.FULL);
        } else {
            migratedPricesCounter = startLoadPrices(LoadPricesMode.DIFFERENCE);
        }
        long duration = (System.nanoTime()-startTime)/1_000_000;
        exchange.getIn().setHeader("partitionId",migratedPricesCounter.get(0));
        exchange.getIn().setHeader("migratedPricesCounter",migratedPricesCounter.get(1));
        exchange.getIn().setHeader("doneCommit",migratedPricesCounter.get(2));
        exchange.getIn().setHeader("time",String.format("%02d:%02d:%02d.%03d", (duration / 1000) / 3600, ((duration / 1000) % 3600) / 60, ((duration / 1000) % 60), (duration % 1000)));
    }

    private List<? extends Serializable> startLoadPrices(LoadPricesMode mode) throws SQLException {
        LOG.info("Start migrate prices to site MK, mode={}", mode);
        try(Connection bdsmConnection = bdsmDatasource.getConnection()){

            //отключаем автокоммит, он будет в самом конце
            bdsmConnection.setAutoCommit(false);
            //1. Вызываем PrepareStockStgTable(), получая partition_id для INSERT
            LOG.debug("Call PrepareStockStgTable()...");
            String partitionId = prepareStageTbl();
            LOG.debug("Create new partition with id = {}", partitionId);
            //2. Переливаем подготовленные цены
            LOG.debug("Migrate prices...");
            long migratedPricesCounter = migratePricesToSiteMk(bdsmConnection, partitionId, mode.getModeValue());
            LOG.debug("Success INSERT prices[{}] into MK_PRODUCTS_STOCK_STG", migratedPricesCounter);
            //3. Вызываем CommitStockTable(), подтверждаем окончание загрузки
            LOG.debug("Call CommitStockTable(partitionId, force)...");
            boolean doneCommit = commitUpload(partitionId, mode.getForceCommit());
            LOG.debug("Success commit partition with id = {}", partitionId);

            return Arrays.asList(partitionId, migratedPricesCounter, doneCommit);
        } catch (SQLException e) {
            LOG.error("Exception occurs migrate prices to site MK",e);
            if (ORA_RECOVERABLE_ERROR_CODE.contains(e.getErrorCode())) {
                throw new SQLRecoverableException(e);
            } else{
                throw e;
            }
        }
    }

    private String prepareStageTbl() throws SQLException {
        try(Connection connection = siteMkDatasource.getConnection()){
            try(CallableStatement cs = connection.prepareCall(SITE_MK_PREPARE_STOCK_STAGE_TBL_PRC)){
                cs.registerOutParameter(1,Types.VARCHAR);
                cs.execute();
                connection.commit();
                return cs.getString(1);
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Couldn't call PrepareStockStgTable()", e);
            }
        }
    }

    private boolean commitUpload(String partitionId, int forceCommit) throws SQLException {
        try(Connection connection = siteMkDatasource.getConnection()){
            try(CallableStatement cs = connection.prepareCall(SITE_MK_COMMIT_STOCK_STAGE_TBL_PRC)){
                cs.setString(1, partitionId);
                cs.setInt(2, forceCommit);
                cs.execute();
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Couldn't call CommitStockTable(partitionId, force)", e);
            }
        }
    }

    private long migratePricesToSiteMk(Connection bdsmConnection, String partitionId, int mode) throws SQLException {
        ResultSet rs = null;
        long count = 0;
        try(PreparedStatement psBdsm = bdsmConnection.prepareStatement(BDSM_GET_PRICES_PRC)){
            psBdsm.setFetchSize(fetchSize);
            psBdsm.setInt(1,mode);
            rs = psBdsm.executeQuery();
            int countBatchSize = 0;
            List<Price> prices = new ArrayList<>(fetchSize);
            LOG.debug("Start INSERT prices to DB MK");
            while (rs.next()){
                count++;
                Price price = new Price();
                price.setArtCode(rs.getString("code_art"));
                price.setWhsCode(rs.getString("code_ws"));
                price.setRestQuantity(rs.getInt("rest_quantity"));
                price.setRegularPrice(rs.getDouble("regular_price"));
                price.setActionPrice(rs.getDouble("action_price"));
                price.setActionPriceLastdate(rs.getDate("action_price_lastdate"));
                price.setRegularPriceLastdate(rs.getDate("regular_price_lastdate"));
                price.setRestLastdate(rs.getDate("rest_lastdate"));
                prices.add(price);

                if(++countBatchSize == fetchSize) {
                    savePricesToSiteMK(prices,partitionId);
                    LOG.debug("Execute INSERT batch into MK_PRODUCTS_STOCK_STG. Count = {}", count);
                    prices.clear();
                    countBatchSize = 0;
                }
            }
            if(countBatchSize % fetchSize != 0) {
                savePricesToSiteMK(prices,partitionId);
                LOG.debug("Execute INSERT batch into MK_PRODUCTS_STOCK_STG. Count = {}", count);
                prices.clear();
            }
            bdsmConnection.commit();
            return count;
        } catch (SQLException e) {
            bdsmConnection.rollback();
            throw new SQLException("Couldn't INSERT prices to MK_PRODUCTS_STOCK_STG", e);
        } finally {
            if (rs != null){
                rs.close();
            }
        }
    }

    private void savePricesToSiteMK(final List<Price> prices, final String partitionId){
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
        List<List<Price>> pricesSublists = Lists.partition(prices, batchSize);
        for (final List<Price> pricesSublist : pricesSublists) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try (Connection connection = siteMkDatasource.getConnection();
                         PreparedStatement psSitemk = connection.prepareStatement(SITE_MK_INSERT_INTO_STAGE_TBL)){
                            for (final Price price : pricesSublist) {
                                psSitemk.setString(1,price.getArtCode());//PRODUCT_CODE
                                psSitemk.setString(2,price.getWhsCode());//SHOP_CODE
                                psSitemk.setInt(3,price.getRestQuantity());//QUANTITY
                                psSitemk.setDouble(4,price.getRegularPrice());//PRICE_REGULAR
                                psSitemk.setDouble(5,price.getActionPrice());//PRICE_PROMO
                                psSitemk.setDate(6, new Date(new java.util.Date().getTime()));//INSERT_DATE
                                if(price.getActionPriceLastdate() != null){
                                    psSitemk.setDate(7, new Date(price.getActionPriceLastdate().getTime()));//PRICE_PROMO_DATE
                                } else {
                                    psSitemk.setNull(7, Types.TIMESTAMP);
                                }
                                if(price.getRegularPriceLastdate() != null){
                                    psSitemk.setDate(8, new Date(price.getRegularPriceLastdate().getTime()));//PRICE_REGULAR_DATE
                                } else {
                                    psSitemk.setNull(8, Types.TIMESTAMP);
                                }
                                if(price.getRestLastdate() != null){
                                    psSitemk.setDate(9, new Date(price.getRestLastdate().getTime()));//QUANTITY_DATE
                                } else {
                                    psSitemk.setNull(9, Types.TIMESTAMP);
                                }
                                psSitemk.setString(10, partitionId);//PATITION_ID
                                psSitemk.addBatch();
                            }
                            psSitemk.executeBatch();
                            connection.commit();
                    } catch (SQLException e) {
                        LOG.error("Couldn't prepare Prices to insert", pricesSublist.toString(), e);
                    }
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            LOG.error("LoaderVetExchange thread has been interrupted.");
        }
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public DataSource getBdsmDatasource() {
        return bdsmDatasource;
    }

    public void setBdsmDatasource(DataSource bdsmDatasource) {
        this.bdsmDatasource = bdsmDatasource;
    }

    public DataSource getSiteMkDatasource() {
        return siteMkDatasource;
    }

    public void setSiteMkDatasource(DataSource siteMkDatasource) {
        this.siteMkDatasource = siteMkDatasource;
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public void setNumOfThreads(int numOfThreads) {
        this.numOfThreads = numOfThreads;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }
}
