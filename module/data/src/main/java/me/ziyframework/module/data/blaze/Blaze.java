package me.ziyframework.module.data.blaze;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import me.ziyframework.boot.core.SpringHolder;

public class Blaze {

    private final CriteriaBuilderFactory cbf;

    private final EntityViewManager evm;

    private final EntityManager em;

    public Blaze(CriteriaBuilderFactory cbf, EntityViewManager evm, EntityManager em) {
        this.cbf = cbf;
        this.evm = evm;
        this.em = em;
    }

    /**
     * 创建CriteriaBuilder.
     * @param clazz 映射对象类型
     * @param alias 别名
     * @param <T> 映射对象类型
     * @return CriteriaBuilder
     */
    public <T> CriteriaBuilder<T> create(Class<T> clazz, String alias) {
        return cbf.create(em, clazz, alias);
    }

    /**
     * 创建CriteriaBuilder.
     * @param clazz 映射对象类型
     * @param <T> 映射对象类型
     * @return CriteriaBuilder
     */
    public <T> CriteriaBuilder<T> create(Class<T> clazz) {
        return cbf.create(em, clazz);
    }

    /**
     * 创建EntityView的CriteriaBuilder.<br/>
     * 用于查询返回EntityView的条件构建器.
     * @param entityClazz 实体类类型
     * @param viewClazz 视图类类型
     * @param <E> 实体类类型
     * @param <V> 视图类类型
     * @return CriteriaBuilder
     */
    public <E, V> CriteriaBuilder<V> create(Class<E> entityClazz, Class<V> viewClazz) {
        CriteriaBuilder<E> cb = cbf.create(em, entityClazz);
        EntityViewSetting<V, CriteriaBuilder<V>> setting = EntityViewSetting.create(viewClazz);
        return evm.applySetting(setting, cb);
    }

    /**
     * Blaze.
     */
    public static Blaze get() {
        return SpringHolder.getBean(Blaze.class);
    }
}
