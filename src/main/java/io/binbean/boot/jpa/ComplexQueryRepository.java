package io.binbean.boot.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * 查询repository
 * （所有方法的searchParams参数的格式参照{@link SpecificationUtils#parse(Map)}）
 * 参考自：Johnnyzhoutq/aicloud-boot-starter
 */
@NoRepositoryBean //表示我们自定义的这个接口不让spring 帮我们自动创建代理类
public interface ComplexQueryRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 分页查询
     *
     * @param searchParams 条件参数
     * @param pageable     分页信息
     * @return Page
     */
    Page<T> query(Map<String, Object> searchParams, Pageable pageable);

    /**
     * 查询
     *
     * @param searchParams 条件参数
     * @return List
     */
    List<T> query(Map<String, Object> searchParams);

    /**
     * 排序查询
     *
     * @param searchParams 条件参数
     * @param sort         排序
     * @return List
     */
    List<T> query(Map<String, Object> searchParams, Sort sort);

    /**
     * 统计
     *
     * @param searchParams 条件参数
     * @return long
     */
    long count(Map<String, Object> searchParams);
}
