package server.api;



import java.util.*;
import java.util.function.Function;

import commons.Event;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

//import commons.Event;
import server.database.EventRepository;

/**
 * Event repository implementation for testing purposes
 */
public class TestEventRepository implements EventRepository {
    public final List<Event> events = new ArrayList<>();

    public final List<String> calledMethods = new ArrayList<>();


    private void call(String name) {
        calledMethods.add(name);
    }

    private Optional<Event> find(UUID id) {
        return events.stream().filter(event -> Objects.equals(event.getId(), id)).findFirst();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Event> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Event> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Event> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> ids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Event getOne(UUID aUUID) {
        return null;
    }

    @Override
    public Event getById(UUID aUUID) {
        return null;
    }

    @Override
    public Event getReferenceById(UUID aUUID) {
        for (var event : events) {
            if (Objects.equals(event.getId(), aUUID)) {
                return event;
            }
        }
        return null;
    }

    @Override
    public <S extends Event> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Event> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Event> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Event> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Event> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Event> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Event, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Event> S save(S entity) {
        calledMethods.add("save");
        for (var event : events) {
            if (Objects.equals(entity.getId(), event.getId())) {
                return entity;
            }
        }
        events.add(entity);
        return entity;
    }

    @Override
    public <S extends Event> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Event> findById(UUID aUUID) {
        for (var event : events) {
            if (event.getId().equals(aUUID)) {
                return Optional.of(event);
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
    public List<Event> findAll() {
        return events;
    }

    @Override
    public List<Event> findAllById(Iterable<UUID> uuids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(UUID aUUID) {

    }

    @Override
    public void delete(Event entity) {
        events.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends Event> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Event> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        return null;
    }
}
