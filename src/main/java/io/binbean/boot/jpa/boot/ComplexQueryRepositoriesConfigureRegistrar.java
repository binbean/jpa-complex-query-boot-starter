package io.binbean.boot.jpa.boot;

import io.binbean.boot.jpa.support.JpaComplexQueryRepository;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * jpa-repository配置类（设置repository实现基础类）
 * <p>参考{@link org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfigureRegistrar}</p>
 */
public class ComplexQueryRepositoriesConfigureRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJpaRepositories.class;
    }

    @Override
    protected Class<?> getConfiguration() {
        return ComplexQueryRepositoriesConfigureRegistrar.JpaComplexQueryRepositoriesConfiguration.class;
    }

    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new JpaRepositoryConfigExtension();
    }

    @EnableJpaRepositories(repositoryBaseClass = JpaComplexQueryRepository.class)
    private static class JpaComplexQueryRepositoriesConfiguration{

    }
}
