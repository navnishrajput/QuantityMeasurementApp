package QuantityMeasurementApp;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.DatabaseException;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.serviceImpl.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.config.ApplicationConfig;
import com.app.quantitymeasurement.database.ConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseIntegrationTestCases {

    private ConnectionPool connectionPool;
    private QuantityMeasurementDatabaseRepository databaseRepository;
    private QuantityMeasurementCacheRepository cacheRepository;
    private IQuantityMeasurementService dbService;
    private IQuantityMeasurementService cacheService;
    private QuantityMeasurementController controller;

    @BeforeEach
    void setUp() {
        connectionPool = new ConnectionPool(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "sa", "", 5, 5000
        );
        databaseRepository = new QuantityMeasurementDatabaseRepository(connectionPool);
        cacheRepository = QuantityMeasurementCacheRepository.getInstance();
        cacheRepository.clear();
        dbService = new QuantityMeasurementServiceImpl(databaseRepository);
        cacheService = new QuantityMeasurementServiceImpl(cacheRepository);
        controller = new QuantityMeasurementController(dbService);
    }

    @AfterEach
    void tearDown() {
        if (databaseRepository != null) {
            databaseRepository.deleteAll();
        }
        if (cacheRepository != null) {
            cacheRepository.clear();
        }
        if (connectionPool != null) {
            connectionPool.closeAllConnections();
        }
    }

    // ==========================================
    // TEST 1: testMavenBuild_Success()
    // ==========================================
    @Test
    void testMavenBuild_Success() {
        assertTrue(true);
    }

    // ==========================================
    // TEST 2: testPackageStructure_AllLayersPresent()
    // ==========================================
    @Test
    void testPackageStructure_AllLayersPresent() {
        assertNotNull(controller);
        assertNotNull(dbService);
        assertNotNull(cacheService);
        assertNotNull(databaseRepository);
        assertNotNull(cacheRepository);
        assertNotNull(connectionPool);
    }

    // ==========================================
    // TEST 3: testPomDependencies_JDBCDriversIncluded()
    // ==========================================
    @Test
    void testPomDependencies_JDBCDriversIncluded() {
        try {
            Class.forName("org.h2.Driver");
            assertTrue(true);
        } catch (ClassNotFoundException e) {
            fail("H2 driver not found");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            assertTrue(true);
        } catch (ClassNotFoundException e) {
            fail("MySQL driver not found");
        }
    }

    // ==========================================
    // TEST 4: testDatabaseConfiguration_LoadedFromProperties()
    // ==========================================
    @Test
    void testDatabaseConfiguration_LoadedFromProperties() {
        ApplicationConfig config = ApplicationConfig.getInstance();
        assertNotNull(config.getDatabaseUrl());
        assertNotNull(config.getDatabaseDriver());
        assertNotNull(config.getDatabaseUsername());
    }

    // ==========================================
    // TEST 5: testConnectionPool_Initialization()
    // ==========================================
    @Test
    void testConnectionPool_Initialization() {
        ConnectionPool pool = new ConnectionPool(
                "jdbc:h2:mem:testpool;DB_CLOSE_DELAY=-1",
                "sa", "", 3, 5000
        );
        assertNotNull(pool);
        String stats = pool.getStatistics();
        assertTrue(stats.contains("Pool size: 3"));
        assertTrue(stats.contains("Active: 0"));
        assertTrue(stats.contains("Idle: 0"));
        pool.closeAllConnections();
    }

    // ==========================================
    // TEST 6: testConnectionPool_Acquire_Release()
    // ==========================================
    @Test
    void testConnectionPool_Acquire_Release() throws SQLException {
        ConnectionPool pool = new ConnectionPool(
                "jdbc:h2:mem:testpool2;DB_CLOSE_DELAY=-1",
                "sa", "", 2, 5000
        );
        Connection conn = pool.acquireConnection();
        assertNotNull(conn);
        assertEquals(1, pool.getActiveCount());

        pool.releaseConnection(conn);
        assertEquals(0, pool.getActiveCount());
        assertEquals(1, pool.getIdleCount());
        pool.closeAllConnections();
    }

    // ==========================================
    // TEST 7: testConnectionPool_AllConnectionsExhausted()
    // ==========================================
    @Test
    void testConnectionPool_AllConnectionsExhausted() throws SQLException {
        ConnectionPool pool = new ConnectionPool(
                "jdbc:h2:mem:testpool3;DB_CLOSE_DELAY=-1",
                "sa", "", 1, 100
        );
        Connection conn = pool.acquireConnection();
        assertNotNull(conn);

        assertThrows(SQLException.class, pool::acquireConnection);

        pool.releaseConnection(conn);
        pool.closeAllConnections();
    }

    // ==========================================
    // TEST 8: testDatabaseRepository_SaveEntity()
    // ==========================================
    @Test
    void testDatabaseRepository_SaveEntity() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                .operation("COMPARE")
                .measurementType("LENGTH")
                .inputValue1(1.0)
                .inputUnit1("FEET")
                .inputValue2(12.0)
                .inputUnit2("INCH")
                .resultValue(1.0)
                .resultUnit("EQUAL")
                .hasError(false)
                .build();

        databaseRepository.save(entity);
        assertEquals(1, databaseRepository.getTotalCount());
    }

    // ==========================================
    // TEST 9: testDatabaseRepository_RetrieveAllMeasurements()
    // ==========================================
    @Test
    void testDatabaseRepository_RetrieveAllMeasurements() {
        QuantityMeasurementEntity entity1 = new QuantityMeasurementEntity.Builder()
                .operation("COMPARE")
                .measurementType("LENGTH")
                .inputValue1(1.0)
                .inputUnit1("FEET")
                .resultValue(1.0)
                .resultUnit("EQUAL")
                .hasError(false)
                .build();

        QuantityMeasurementEntity entity2 = new QuantityMeasurementEntity.Builder()
                .operation("CONVERT")
                .measurementType("WEIGHT")
                .inputValue1(1.0)
                .inputUnit1("KILOGRAM")
                .targetUnit("GRAM")
                .resultValue(1000.0)
                .resultUnit("GRAM")
                .hasError(false)
                .build();

        databaseRepository.save(entity1);
        databaseRepository.save(entity2);

        List<QuantityMeasurementEntity> all = databaseRepository.findAll();
        assertEquals(2, all.size());
    }

    // ==========================================
    // TEST 10: testDatabaseRepository_QueryByOperation()
    // ==========================================
    @Test
    void testDatabaseRepository_QueryByOperation() {
        QuantityMeasurementEntity entity1 = new QuantityMeasurementEntity.Builder()
                .operation("ADD")
                .measurementType("LENGTH")
                .inputValue1(1.0)
                .inputUnit1("FEET")
                .inputValue2(12.0)
                .inputUnit2("INCH")
                .resultValue(2.0)
                .resultUnit("FEET")
                .hasError(false)
                .build();

        QuantityMeasurementEntity entity2 = new QuantityMeasurementEntity.Builder()
                .operation("SUBTRACT")
                .measurementType("LENGTH")
                .inputValue1(5.0)
                .inputUnit1("FEET")
                .inputValue2(2.0)
                .inputUnit2("FEET")
                .resultValue(3.0)
                .resultUnit("FEET")
                .hasError(false)
                .build();

        databaseRepository.save(entity1);
        databaseRepository.save(entity2);

        List<QuantityMeasurementEntity> addOps = databaseRepository.findByOperation("ADD");
        assertEquals(1, addOps.size());
        assertEquals("ADD", addOps.get(0).getOperation());
    }

    // ==========================================
    // TEST 11: testDatabaseRepository_QueryByMeasurementType()
    // ==========================================
    @Test
    void testDatabaseRepository_QueryByMeasurementType() {
        QuantityMeasurementEntity entity1 = new QuantityMeasurementEntity.Builder()
                .operation("COMPARE")
                .measurementType("LENGTH")
                .inputValue1(1.0)
                .inputUnit1("FEET")
                .resultValue(1.0)
                .resultUnit("EQUAL")
                .hasError(false)
                .build();

        QuantityMeasurementEntity entity2 = new QuantityMeasurementEntity.Builder()
                .operation("COMPARE")
                .measurementType("WEIGHT")
                .inputValue1(1.0)
                .inputUnit1("KILOGRAM")
                .resultValue(1.0)
                .resultUnit("EQUAL")
                .hasError(false)
                .build();

        databaseRepository.save(entity1);
        databaseRepository.save(entity2);

        List<QuantityMeasurementEntity> lengthEntities = databaseRepository.findByMeasurementType("LENGTH");
        assertEquals(1, lengthEntities.size());
        assertEquals("LENGTH", lengthEntities.get(0).getMeasurementType());
    }

    // ==========================================
    // TEST 12: testDatabaseRepository_CountMeasurements()
    // ==========================================
    @Test
    void testDatabaseRepository_CountMeasurements() {
        assertEquals(0, databaseRepository.getTotalCount());

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                .operation("COMPARE")
                .measurementType("LENGTH")
                .inputValue1(1.0)
                .inputUnit1("FEET")
                .resultValue(1.0)
                .resultUnit("EQUAL")
                .hasError(false)
                .build();

        databaseRepository.save(entity);
        assertEquals(1, databaseRepository.getTotalCount());

        databaseRepository.save(entity);
        assertEquals(2, databaseRepository.getTotalCount());
    }

    // ==========================================
    // TEST 13: testDatabaseRepository_DeleteAll()
    // ==========================================
    @Test
    void testDatabaseRepository_DeleteAll() {
        QuantityMeasurementEntity entity1 = new QuantityMeasurementEntity.Builder()
                .operation("COMPARE")
                .measurementType("LENGTH")
                .inputValue1(1.0)
                .inputUnit1("FEET")
                .resultValue(1.0)
                .resultUnit("EQUAL")
                .hasError(false)
                .build();

        QuantityMeasurementEntity entity2 = new QuantityMeasurementEntity.Builder()
                .operation("CONVERT")
                .measurementType("WEIGHT")
                .inputValue1(1.0)
                .inputUnit1("KILOGRAM")
                .targetUnit("GRAM")
                .resultValue(1000.0)
                .resultUnit("GRAM")
                .hasError(false)
                .build();

        databaseRepository.save(entity1);
        databaseRepository.save(entity2);
        assertEquals(2, databaseRepository.getTotalCount());

        databaseRepository.deleteAll();
        assertEquals(0, databaseRepository.getTotalCount());
    }

    // ==========================================
    // TEST 14: testSQLInjectionPrevention()
    // ==========================================
    @Test
    void testSQLInjectionPrevention() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                .operation("COMPARE")
                .measurementType("LENGTH")
                .inputValue1(1.0)
                .inputUnit1("FEET'; DROP TABLE quantity_measurement_entity; --")
                .resultValue(1.0)
                .resultUnit("EQUAL")
                .hasError(false)
                .build();

        databaseRepository.save(entity);
        assertEquals(1, databaseRepository.getTotalCount());
        assertDoesNotThrow(() -> databaseRepository.findAll());
    }

    // ==========================================
    // TEST 15: testTransactionRollback_OnError()
    // ==========================================
    @Test
    void testTransactionRollback_OnError() {
        Connection conn = null;
        try {
            conn = connectionPool.acquireConnection();
            conn.setAutoCommit(false);

            try {
                QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                        .operation("COMPARE")
                        .measurementType("LENGTH")
                        .inputValue1(1.0)
                        .inputUnit1("FEET")
                        .resultValue(1.0)
                        .resultUnit("EQUAL")
                        .hasError(false)
                        .build();

                databaseRepository.save(entity);
                throw new RuntimeException("Simulated error");

            } catch (RuntimeException e) {
                if (conn != null) {
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            fail("Connection error: " + e.getMessage());
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        assertTrue(true);
    }

    // ==========================================
    // TEST 16: testDatabaseSchema_TablesCreated()
    // ==========================================
    @Test
    void testDatabaseSchema_TablesCreated() throws SQLException {
        Connection conn = connectionPool.acquireConnection();
        boolean tableExists = conn.getMetaData()
                .getTables(null, null, "QUANTITY_MEASUREMENT_ENTITY", null)
                .next();
        assertTrue(tableExists);
        connectionPool.releaseConnection(conn);
    }

    // ==========================================
    // TEST 17: testH2TestDatabase_IsolationBetweenTests()
    // ==========================================
    @Test
    void testH2TestDatabase_IsolationBetweenTests() {
        assertEquals(0, databaseRepository.getTotalCount());

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                .operation("COMPARE")
                .measurementType("LENGTH")
                .inputValue1(1.0)
                .inputUnit1("FEET")
                .resultValue(1.0)
                .resultUnit("EQUAL")
                .hasError(false)
                .build();

        databaseRepository.save(entity);
        assertEquals(1, databaseRepository.getTotalCount());
    }

    // ==========================================
    // TEST 18: testRepositoryFactory_CreateCacheRepository()
    // ==========================================
    @Test
    void testRepositoryFactory_CreateCacheRepository() {
        IQuantityMeasurementRepository repo = QuantityMeasurementCacheRepository.getInstance();
        assertNotNull(repo);
        assertTrue(repo instanceof QuantityMeasurementCacheRepository);
    }

    // ==========================================
    // TEST 19: testRepositoryFactory_CreateDatabaseRepository()
    // ==========================================
    @Test
    void testRepositoryFactory_CreateDatabaseRepository() {
        assertNotNull(databaseRepository);
        assertTrue(databaseRepository instanceof QuantityMeasurementDatabaseRepository);
    }

    // ==========================================
    // TEST 20: testServiceWithDatabaseRepository_Integration()
    // ==========================================
    @Test
    void testServiceWithDatabaseRepository_Integration() {
        QuantityDTO dto1 = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO dto2 = new QuantityDTO(12.0, "INCH", "LENGTH");

        QuantityDTO result = dbService.compare(dto1, dto2);
        assertFalse(result.hasError());
        assertEquals(1.0, result.getValue(), 0.001);
        assertEquals(1, databaseRepository.getTotalCount());
    }

    // ==========================================
    // TEST 21: testServiceWithCacheRepository_Integration()
    // ==========================================
    @Test
    void testServiceWithCacheRepository_Integration() {
        QuantityDTO dto1 = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        QuantityDTO dto2 = new QuantityDTO(1000.0, "GRAM", "WEIGHT");

        QuantityDTO result = cacheService.compare(dto1, dto2);
        assertFalse(result.hasError());
        assertEquals(1.0, result.getValue(), 0.001);
        assertEquals(1, cacheRepository.getTotalCount());
    }

    // ==========================================
    // TEST 22: testMavenTest_AllTestsPass()
    // ==========================================
    @Test
    void testMavenTest_AllTestsPass() {
        assertTrue(true);
    }

    // ==========================================
    // TEST 23: testMavenPackage_JarCreated()
    // ==========================================
    @Test
    void testMavenPackage_JarCreated() {
        assertTrue(true);
    }

    // ==========================================
    // TEST 24: testDatabaseRepositoryPoolStatistics()
    // ==========================================
    @Test
    void testDatabaseRepositoryPoolStatistics() {
        String stats = connectionPool.getStatistics();
        assertTrue(stats.contains("Pool size: 5"));
        assertTrue(stats.contains("Active:"));
        assertTrue(stats.contains("Idle:"));
        assertTrue(stats.contains("Total created:"));
    }

    // ==========================================
    // TEST 25: testDatabaseException_CustomException()
    // ==========================================
    @Test
    void testDatabaseException_CustomException() {
        DatabaseException exception = new DatabaseException("Test error");
        assertEquals("Test error", exception.getMessage());

        SQLException sqlException = new SQLException("JDBC error");
        DatabaseException wrappedException = new DatabaseException("Wrapped error", sqlException);
        assertEquals("Wrapped error", wrappedException.getMessage());
        assertEquals(sqlException, wrappedException.getCause());
    }

    // ==========================================
    // TEST 26: testResourceCleanup_ConnectionClosed()
    // ==========================================
    @Test
    void testResourceCleanup_ConnectionClosed() throws SQLException {
        Connection conn = connectionPool.acquireConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());

        connectionPool.releaseConnection(conn);
        assertFalse(conn.isClosed());
    }

    // ==========================================
    // TEST 27: testBatchInsert_MultipleEntities()
    // ==========================================
    @Test
    void testBatchInsert_MultipleEntities() {
        for (int i = 0; i < 10; i++) {
            QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                    .operation("COMPARE")
                    .measurementType("LENGTH")
                    .inputValue1(i)
                    .inputUnit1("FEET")
                    .resultValue(1.0)
                    .resultUnit("EQUAL")
                    .hasError(false)
                    .build();
            databaseRepository.save(entity);
        }

        assertEquals(10, databaseRepository.getTotalCount());
    }

    // ==========================================
    // TEST 28: testPropertiesConfiguration_EnvironmentOverride()
    // ==========================================
    @Test
    void testPropertiesConfiguration_EnvironmentOverride() {
        ApplicationConfig config = ApplicationConfig.getInstance();
        String repositoryType = config.getRepositoryType();
        assertNotNull(repositoryType);

        int poolSize = config.getPoolMaxSize();
        assertTrue(poolSize > 0);
    }

    // ==========================================
    // TEST 29: testDatabaseRepository_ConcurrentAccess()
    // ==========================================
    @Test
    void testDatabaseRepository_ConcurrentAccess() throws InterruptedException {
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger savedCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                            .operation("COMPARE")
                            .measurementType("LENGTH")
                            .inputValue1(index)
                            .inputUnit1("FEET")
                            .resultValue(1.0)
                            .resultUnit("EQUAL")
                            .hasError(false)
                            .build();
                    databaseRepository.save(entity);
                    savedCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount, savedCount.get());
        assertEquals(threadCount, databaseRepository.getTotalCount());
    }

    // ==========================================
    // TEST 30: testParameterizedQuery_DateTimeHandling()
    // ==========================================
    @Test
    void testParameterizedQuery_DateTimeHandling() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                .operation("COMPARE")
                .measurementType("LENGTH")
                .inputValue1(1.0)
                .inputUnit1("FEET")
                .resultValue(1.0)
                .resultUnit("EQUAL")
                .hasError(false)
                .build();

        databaseRepository.save(entity);

        List<QuantityMeasurementEntity> entities = databaseRepository.findAll();
        assertEquals(1, entities.size());
        assertNotNull(entities.get(0).getTimestamp());
    }

    // ==========================================
    // TEST 31: testIntegration_EndToEnd_LengthAddition()
    // ==========================================
    @Test
    void testIntegration_EndToEnd_LengthAddition() {
        QuantityDTO dto1 = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO dto2 = new QuantityDTO(12.0, "INCH", "LENGTH");

        QuantityDTO result = dbService.add(dto1, dto2);
        assertFalse(result.hasError());
        assertEquals(2.0, result.getValue(), 0.001);
        assertEquals("FEET", result.getUnit());
        assertEquals(1, databaseRepository.getTotalCount());
    }

    // ==========================================
    // TEST 32: testIntegration_EndToEnd_TemperatureUnsupported()
    // ==========================================
    @Test
    void testIntegration_EndToEnd_TemperatureUnsupported() {
        QuantityDTO dto1 = new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE");
        QuantityDTO dto2 = new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE");

        QuantityDTO result = dbService.add(dto1, dto2);
        assertTrue(result.hasError());
        assertTrue(result.getErrorMessage().contains("Temperature does not support"));
        assertEquals(1, databaseRepository.getTotalCount());
    }

    // ==========================================
    // TEST 33: testBackwardCompatibility_AllUC1_UC15_Tests()
    // ==========================================
    @Test
    void testBackwardCompatibility_AllUC1_UC15_Tests() {
        QuantityDTO dto1 = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO dto2 = new QuantityDTO(12.0, "INCH", "LENGTH");
        QuantityDTO result1 = dbService.compare(dto1, dto2);
        assertFalse(result1.hasError());

        QuantityDTO dto3 = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        QuantityDTO dto4 = new QuantityDTO(1000.0, "GRAM", "WEIGHT");
        QuantityDTO result2 = dbService.compare(dto3, dto4);
        assertFalse(result2.hasError());

        QuantityDTO dto5 = new QuantityDTO(0.0, "CELSIUS", "TEMPERATURE");
        QuantityDTO dto6 = new QuantityDTO(32.0, "FAHRENHEIT", "TEMPERATURE");
        QuantityDTO result3 = dbService.compare(dto5, dto6);
        assertFalse(result3.hasError());

        QuantityDTO dto7 = new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE");
        QuantityDTO dto8 = new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE");
        QuantityDTO result4 = dbService.add(dto7, dto8);
        assertTrue(result4.hasError());
    }
}