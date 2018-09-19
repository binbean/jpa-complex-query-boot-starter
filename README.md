# jpa-complex-query-boot-starter
Jpa complex query boot starter(Jpa 复杂查询)

## 操作类型
```
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
```
## 查询写法
search_EQ_name=test

search_LIKE_name=test

## 使用
1 继承ComplexQueryRepository接口
```
public interface DemoRepository extends ComplexQueryRepository<Entity, Long>
```


2 在controller方法里自动从request请求中获取查询条件
```
Map<String, Object> filters = SpecificationUtils.getParametersWithPrefix(request);
Page<Entity> entities = demoService.query(filters, pageable);
```


