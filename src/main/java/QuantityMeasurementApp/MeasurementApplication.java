package QuantityMeasurementApp;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.serviceImpl.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.config.ApplicationConfig;
import com.app.quantitymeasurement.database.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeasurementApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementApplication.class);
    private static QuantityMeasurementController controller;
    private static ConnectionPool connectionPool;

    public static void main(String[] args) {
        LOGGER.info("Starting Quantity Measurement Application - UC16");

        IQuantityMeasurementRepository repository = createRepository();
        IQuantityMeasurementService service = new QuantityMeasurementServiceImpl(repository);
        controller = new QuantityMeasurementController(service);

        LOGGER.info("Repository type: {}", repository.getClass().getSimpleName());

        demonstrateLengthEquality();
        demonstrateLengthConversion();
        demonstrateLengthAddition();
        demonstrateWeightEquality();
        demonstrateWeightConversion();
        demonstrateVolumeEquality();
        demonstrateVolumeConversion();
        demonstrateTemperatureEquality();
        demonstrateTemperatureConversion();
        demonstrateTemperatureUnsupportedOperation();

        if (repository instanceof QuantityMeasurementDatabaseRepository) {
            LOGGER.info("Total measurements stored: {}", repository.getTotalCount());
            LOGGER.info("Connection pool stats: {}", connectionPool.getStatistics());
        }

        shutdownGracefully(repository);
    }

    private static IQuantityMeasurementRepository createRepository() {
        ApplicationConfig config = ApplicationConfig.getInstance();
        String repositoryType = config.getRepositoryType();

        if ("database".equalsIgnoreCase(repositoryType)) {
            connectionPool = new ConnectionPool(
                    config.getDatabaseUrl(),
                    config.getDatabaseUsername(),
                    config.getDatabasePassword(),
                    config.getPoolMaxSize(),
                    config.getPoolTimeoutMs()
            );
            return new QuantityMeasurementDatabaseRepository(connectionPool);
        } else {
            LOGGER.info("Using cache repository");
            return QuantityMeasurementCacheRepository.getInstance();
        }
    }

    private static void shutdownGracefully(IQuantityMeasurementRepository repository) {
        if (connectionPool != null) {
            LOGGER.info("Shutting down connection pool...");
            connectionPool.closeAllConnections();
        }
        LOGGER.info("Application shutdown complete");
    }

    private static void demonstrateLengthEquality() {
        LOGGER.info("=== Length Equality Demo ===");
        QuantityDTO dto1 = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO dto2 = new QuantityDTO(12.0, "INCH", "LENGTH");
        controller.performCompare(dto1, dto2);
    }

    private static void demonstrateLengthConversion() {
        LOGGER.info("=== Length Conversion Demo ===");
        QuantityDTO dto = new QuantityDTO(1.0, "FEET", "LENGTH");
        controller.performConvert(dto, "INCH");
    }

    private static void demonstrateLengthAddition() {
        LOGGER.info("=== Length Addition Demo ===");
        QuantityDTO dto1 = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO dto2 = new QuantityDTO(12.0, "INCH", "LENGTH");
        controller.performAdd(dto1, dto2);
    }

    private static void demonstrateWeightEquality() {
        LOGGER.info("=== Weight Equality Demo ===");
        QuantityDTO dto1 = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        QuantityDTO dto2 = new QuantityDTO(1000.0, "GRAM", "WEIGHT");
        controller.performCompare(dto1, dto2);
    }

    private static void demonstrateWeightConversion() {
        LOGGER.info("=== Weight Conversion Demo ===");
        QuantityDTO dto = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        controller.performConvert(dto, "GRAM");
    }

    private static void demonstrateVolumeEquality() {
        LOGGER.info("=== Volume Equality Demo ===");
        QuantityDTO dto1 = new QuantityDTO(1.0, "LITRE", "VOLUME");
        QuantityDTO dto2 = new QuantityDTO(1000.0, "MILLILITRE", "VOLUME");
        controller.performCompare(dto1, dto2);
    }

    private static void demonstrateVolumeConversion() {
        LOGGER.info("=== Volume Conversion Demo ===");
        QuantityDTO dto = new QuantityDTO(1.0, "LITRE", "VOLUME");
        controller.performConvert(dto, "MILLILITRE");
    }

    private static void demonstrateTemperatureEquality() {
        LOGGER.info("=== Temperature Equality Demo ===");
        QuantityDTO dto1 = new QuantityDTO(0.0, "CELSIUS", "TEMPERATURE");
        QuantityDTO dto2 = new QuantityDTO(32.0, "FAHRENHEIT", "TEMPERATURE");
        controller.performCompare(dto1, dto2);
    }

    private static void demonstrateTemperatureConversion() {
        LOGGER.info("=== Temperature Conversion Demo ===");
        QuantityDTO dto = new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE");
        controller.performConvert(dto, "FAHRENHEIT");
    }

    private static void demonstrateTemperatureUnsupportedOperation() {
        LOGGER.info("=== Temperature Unsupported Operation Demo ===");
        QuantityDTO dto1 = new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE");
        QuantityDTO dto2 = new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE");
        controller.performAdd(dto1, dto2);
    }
}