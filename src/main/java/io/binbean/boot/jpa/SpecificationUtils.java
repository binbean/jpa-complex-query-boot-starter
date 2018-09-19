package io.binbean.boot.jpa;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import javax.servlet.ServletRequest;
import java.util.*;

/**
 * 构建jpa动态查询条件工具类
 * 参考自：Johnnyzhoutq/aicloud-boot-starter
 */
public class SpecificationUtils {

    /**
     * 操作符与属性名之间的分隔符
     */
    public static final char OPERATOR_ATTRNAME_SEPARATOR = '_';
    /**
     * 嵌套的属性名分隔符
     */
    public static final char NESTED_ATTRNAME_SEPARATOR = '.';

    /**
     * 嵌套的属性名分隔符
     */
    public static final String SEARCH_FILTER_PREFIX = "search";

    /**
     * 根据Map解析
     *
     * @param searchParams 查询参数。
     *                     Map的key格式：操作符_属性名，比如：GT_age表示age大于指定值。如果嵌套的属性，则以“.”隔开，比如：GT_user.age表示以属性user的属性age大于指定值
     *                     Map的value格式：非IN操作符，就是指定的value；IN操作符，value必须得是数组或Collection类型
     * @return Specification
     */
    public static <T> Specification<T> parse(Map<String, Object> searchParams) {
        return parse(SearchFilter.parse(searchParams));
    }

    /**
     * 根据查询条件解析
     *
     * @param filters 查询条件
     * @return Specification
     */
    public static <T> Specification<T> parse(final List<SearchFilter> filters) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                for (SearchFilter filter : filters) {
                    Path<String> path = getPath(root, filter.attrName);
                    Predicate predicate = buildPredicate(cb, path, filter);
                    predicates.add(predicate);
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }


    /**
     * 构建Predicate
     * @param cb CriteriaBuilder
     * @param path Path
     * @param filter  SearchFilter
     * @return Predicate
     */
    private static Predicate buildPredicate(CriteriaBuilder cb, Path path, SearchFilter filter) {
        Predicate predicate;
        switch (filter.operator) {
            case EQ:
                predicate = cb.equal(path, filter.value);
                break;
            case NOTEQ:
                predicate = cb.notEqual(path, filter.value);
                break;
            case GT:
                predicate = cb.greaterThan(path, (Comparable) filter.value);
                break;
            case GTE:
                predicate = cb.greaterThanOrEqualTo(path, (Comparable) filter.value);
                break;
            case LT:
                predicate = cb.lessThan(path, (Comparable) filter.value);
                break;
            case LTE:
                predicate = cb.lessThanOrEqualTo(path, (Comparable) filter.value);
                break;
            case LIKE:
                //predicate = cb.like(path, "%" + filter.value.toString() + "%");
                predicate = cb.like(path, "%" +filter.value.toString().trim()
                        .replaceAll("/","//")
                        .replaceAll("_","/_")
                        .replaceAll("%","/%")+"%",'/');
                break;
            case NULL:
                predicate = cb.isNull(path);
                break;
            case NOTNULL:
                predicate = cb.isNotNull(path);
                break;
            case IN:
                if (filter.value instanceof Object[]) {
                    predicate = path.in((Object[]) filter.value);
                } else if (filter.value instanceof Collection) {
                    predicate = path.in((Collection) filter.value);
                } else {
                    throw new IllegalArgumentException("非法的IN操作");
                }
                break;
            default:
                throw new IllegalArgumentException("非法的操作符");
        }

        return predicate;
    }

    /**
     * 根据属性名获取路径
     * @param root Root
     * @param attrName 属性名称
     * @return Path
     */
    private static Path getPath(Root root, String attrName) {
        Path path = root;
        String[] attrs = StringUtils.split(attrName, String.valueOf(NESTED_ATTRNAME_SEPARATOR));
        attrs = (null == attrs) ? new String[]{attrName} : attrs;
        if (null != attrs) {
            for (String name : attrs) {
                path = path.get(name);
            }
        }

        return path;
    }

    /**
     *  取得带相同前缀的Request Parameters.返回的结果的Parameter名已去除前缀.
     * @param request ServletRequest
     * @param prefix 前缀
     * @return Map
     */
    public static Map<String, Object> getParametersWithPrefix(ServletRequest request, String prefix) {
        Assert.notNull(request, "Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<>();
        if (prefix == null) {
            prefix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String stripPrefix = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(stripPrefix, values);
                } else {
                    params.put(stripPrefix, values[0]);
                }
            }
        }
        return params;
    }

    /**
     *
     * @param request ServletRequest
     * @return Map
     */
    public static Map<String, Object> getParametersWithPrefix(ServletRequest request) {
        return getParametersWithPrefix(request, SEARCH_FILTER_PREFIX + "_");
    }


    /**
     * 查询条件
     */
    public static class SearchFilter {
        // 属性名
        private String attrName;
        // 操作符
        private Operator operator;
        // 值
        private Object value;

        public SearchFilter(String attrName, Operator operator, Object value) {
            Assert.hasText(attrName, "无效的属性名：" + attrName);
            Assert.notNull(operator, "操作符不能为null");
            this.attrName = attrName;
            this.operator = operator;
            this.value = value;
        }

        public String getAttrName() {
            return attrName;
        }

        public Operator getOperator() {
            return operator;
        }

        public Object getValue() {
            return value;
        }

        /**
         * 解析
         *
         * @param searchParams 需被解析的查询参数
         * @return  List
         */
        public static List<SearchFilter> parse(Map<String, Object> searchParams) {
            List<SearchFilter> filters = new ArrayList<>();
            for (String key : searchParams.keySet()) {
                String[] names = StringUtils.split(key, String.valueOf(OPERATOR_ATTRNAME_SEPARATOR));
                if (names.length != 2) {
                    throw new IllegalArgumentException("非法查询参数：" + key);
                }
                filters.add(new SearchFilter(names[1], Operator.valueOf(names[0]), searchParams.get(key)));
            }

            return filters;
        }
    }

    /**
     * 操作符
     */
    public enum Operator {
        // 等于
        EQ,
        // 不等于
        NOTEQ,
        // 大于
        GT,
        // 大于等于
        GTE,
        // 小于
        LT,
        // 小于等于
        LTE,
        // like操作
        LIKE,
        // 等于null
        NULL,
        // 不等于null
        NOTNULL,
        // in操作
        IN
    }
}
