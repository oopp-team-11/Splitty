package server.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import commons.Participant;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import server.database.ParticipantRepository;

/**
 * Participant repository implementation for testing purposes
 */
public class TestParticipantRepository implements ParticipantRepository {

    private static void setId(Participant toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    public final List<Participant> participants = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<Participant> findAll() {
        calledMethods.add("findAll");
        return participants;
    }

    @Override
    public List<Participant> findAll(Sort sort) {
        return null;
    }

    @Override
    public List<Participant> findAllById(Iterable<UUID> ids) {
        return null;
    }

    @Override
    public <S extends Participant> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Participant> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Participant> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Participant> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> ids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Participant getOne(UUID id) {
        return null;
    }

    @Override
    public Participant getById(UUID id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public Participant getReferenceById(UUID id) {
        call("getReferenceById");
        return find(id).get();
    }

    private Optional<Participant> find(UUID id) {
        return participants.stream().filter(participant -> participant.getId().equals(id)).findFirst();
    }

    @Override
    public <S extends Participant> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Participant> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public Page<Participant> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Participant> S save(S entity) {
        call("save");
        for (Participant participant : participants) {
            if (participant.getId().equals(entity.getId()))
                return entity;
        }
        try {
            setId(entity, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        participants.add(entity);
        return entity;
    }

    @Override
    public Optional<Participant> findById(UUID id) {
        return null;
    }

    @Override
    public boolean existsById(UUID id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        return participants.size();
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void delete(Participant entity) {
        participants.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {

    }

    @Override
    public void deleteAll(Iterable<? extends Participant> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Participant> Optional<S> findOne(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Participant> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Participant> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Participant> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Participant, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}