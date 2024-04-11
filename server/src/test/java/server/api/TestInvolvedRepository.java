package server.api;


import commons.Involved;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import server.database.InvolvedRepository;

import java.util.*;
import java.util.function.Function;

/**
 * Involved repository implementation for testing purposes
 */
public class TestInvolvedRepository implements InvolvedRepository {
    public final List<Involved> involveds = new ArrayList<>();

    public final List<String> calledMethods = new ArrayList<>();

    private static void setId(Involved toSet, UUID newID) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newID, true);
    }

    private void call(String name) {
        calledMethods.add(name);
    }

    private Optional<Involved> find(UUID id) {
        return involveds.stream().filter(involved -> Objects.equals(involved.getId(), id)).findFirst();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Involved> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Involved> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Involved> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> ids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Involved getOne(UUID aUUID) {
        return null;
    }

    @Override
    public Involved getById(UUID aUUID) {
        return null;
    }

    @Override
    public Involved getReferenceById(UUID aUUID) {
        for (var involved : involveds) {
            if (Objects.equals(involved.getId(), aUUID)) {
                return involved;
            }
        }
        return null;
    }

    @Override
    public <S extends Involved> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Involved> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Involved> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Involved> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Involved> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Involved> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Involved, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Involved> S save(S entity) {
        calledMethods.add("save");
        for (var involved : involveds) {
            if (Objects.equals(entity.getId(), involved.getId())) {
                return entity;
            }
        }
        try {
            setId(entity, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        involveds.add(entity);
        return entity;
    }

    @Override
    public <S extends Involved> List<S> saveAll(Iterable<S> entities) {
        List<S> list = new ArrayList<>();
        for(S entity : entities)
        {
            if(involveds.add(entity))
                list.add(entity);
        }
        return list;
    }

    @Override
    public Optional<Involved> findById(UUID aUUID) {
        for (var involved : involveds) {
            if (involved.getId().equals(aUUID)) {
                return Optional.of(involved);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID aUUID) {
        call("existsById");
        return find(aUUID).isPresent();
    }

    @Override
    public List<Involved> findAll() {
        return involveds;
    }

    @Override
    public List<Involved> findAllById(Iterable<UUID> uuids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(UUID aUUID) {
        delete(find(aUUID).get());
    }

    @Override
    public void delete(Involved entity) {
        involveds.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {
        for(UUID uuid : uuids)
            deleteById(uuid);
    }

    @Override
    public void deleteAll(Iterable<? extends Involved> entities) {
        for(Involved involved : entities)
            delete(involved);
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Involved> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Involved> findAll(Pageable pageable) {
        return null;
    }
}
