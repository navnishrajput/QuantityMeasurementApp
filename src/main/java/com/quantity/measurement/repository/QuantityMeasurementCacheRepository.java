package com.quantity.measurement.repository;

import com.quantity.measurement.entity.QuantityMeasurementEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// UC15: Singleton in-memory repository - implements cache pattern
public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

    private static QuantityMeasurementCacheRepository instance;
    private final List<QuantityMeasurementEntity> cache;

    // UC15: Private constructor for Singleton pattern
    private QuantityMeasurementCacheRepository() {
        this.cache = new ArrayList<>();
    }

    // UC15: Thread-safe Singleton accessor
    public static synchronized QuantityMeasurementCacheRepository getInstance() {
        if (instance == null) {
            instance = new QuantityMeasurementCacheRepository();
        }
        return instance;
    }

    @Override
    public void save(QuantityMeasurementEntity entity) {
        cache.add(entity);
    }

    @Override
    public List<QuantityMeasurementEntity> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(cache));
    }

    @Override
    public List<QuantityMeasurementEntity> findByOperation(String operation) {
        return cache.stream()
                .filter(e -> e.getOperation().equalsIgnoreCase(operation))
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        cache.clear();
    }

    // UC15: Disk serialization - reserved for future persistence implementation
    // private static final String STORAGE_FILE = "quantity_measurements.dat";
    //
    // public void saveToDisk() {
    //     try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_FILE))) {
    //         oos.writeObject(cache);
    //     } catch (IOException e) {
    //         throw new QuantityMeasurementException("Failed to save to disk", e);
    //     }
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void loadFromDisk() {
    //     try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORAGE_FILE))) {
    //         List<QuantityMeasurementEntity> loaded = (List<QuantityMeasurementEntity>) ois.readObject();
    //         cache.clear();
    //         cache.addAll(loaded);
    //     } catch (IOException | ClassNotFoundException e) {
    //         throw new QuantityMeasurementException("Failed to load from disk", e);
    //     }
    // }
}