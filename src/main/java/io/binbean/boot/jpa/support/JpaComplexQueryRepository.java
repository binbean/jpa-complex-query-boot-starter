package io.binbean.boot.jpa.support;

import io.binbean.boot.jpa.ComplexQueryRepository;
import io.binbean.boot.jpa.SpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class JpaComplexQueryRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ComplexQueryRepository<T, ID> {

    public JpaComplexQueryRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public Page<T> query(Map<String, Object> searchParams, Pageable pageable) {
        return findAll(SpecificationUtils.parse(searchParams), pageable);
    }

    @Override
    public List<T> query(Map<String, Object> searchParams) {
        return findAll(SpecificationUtils.parse(searchParams));
    }

    @Override
    public List<T> query(Map<String, Object> searchParams, Sort sort) {
        return findAll(SpecificationUtils.parse(searchParams), sort);
    }

    @Override
    public long count(Map<String, Object> searchParams) {
        return count(SpecificationUtils.parse(searchParams));
    }

}
