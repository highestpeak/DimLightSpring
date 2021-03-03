package com.highestpeak.dimlight.model.entity.support;

import com.highestpeak.dimlight.utils.ReflectionUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

/**
 * @author highestpeak
 */
public class CustomIdGenerator extends IdentityGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        Object id = ReflectionUtils.getFieldValue("id", object);
        if (id != null) {
            return (Serializable) id;
        }
        return super.generate(session, object);
    }
}
