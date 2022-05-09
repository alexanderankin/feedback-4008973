package springapp;

import org.springframework.data.repository.CrudRepository;

import java.util.Collections;
import java.util.Optional;

public class MockRepository<T> implements CrudRepository<T, Long> {
    @Override
    public <S extends T> S save(S entity) {
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return Collections.emptyList();
    }

    @Override
    public Optional<T> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<T> findAll() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<T> findAllById(Iterable<Long> longs) {
        return Collections.emptyList();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
    }

    @Override
    public void delete(T entity) {
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
    }

    @Override
    public void deleteAll() {
    }
}
