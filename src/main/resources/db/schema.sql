CREATE TABLE IF NOT EXISTS quantity_measurement_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation VARCHAR(50) NOT NULL,
    measurement_type VARCHAR(50) NOT NULL,
    input_value1 DOUBLE NOT NULL,
    input_unit1 VARCHAR(50) NOT NULL,
    input_value2 DOUBLE,
    input_unit2 VARCHAR(50),
    target_unit VARCHAR(50),
    result_value DOUBLE,
    result_unit VARCHAR(50),
    has_error BOOLEAN DEFAULT FALSE,
    error_message VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_operation ON quantity_measurement_entity(operation);
CREATE INDEX IF NOT EXISTS idx_measurement_type ON quantity_measurement_entity(measurement_type);
CREATE INDEX IF NOT EXISTS idx_created_at ON quantity_measurement_entity(created_at);