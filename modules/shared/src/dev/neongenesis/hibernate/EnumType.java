/*
 * Neon Genesis
 * Copyright (C) 2026 Leandro Cardoso de Andrade (@kyogoiano <leandro.ueg@gmail.com>)
 * Original Genesis Project Copyright (C) 2004-2009 Summa Technologies do Brasil Ltda.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.neongenesis.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import dev.neongenesis.helpers.EnumHelper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.apache.commons.beanutils.BeanUtils;

public abstract class EnumType implements UserType<Object> {
    private static final int[] TYPES = new int[] {Types.VARCHAR};

    public Object deepCopy(Object obj) throws HibernateException {
        return obj;
    }
    
    public boolean equals(Object obj, Object obj1) throws HibernateException {
       if (obj == null) {
           return obj1 == null;
       }

       return obj.equals(obj1);
    }

    public boolean isMutable() {
        return false;
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, int position,
                              SharedSessionContractImplementor session, Object obj)
                                     throws HibernateException, SQLException {
       String key = resultSet.getString(position);

       return toEnum(key);
    }

    public Object nullSafeGet(ResultSet resultSet, String[] str,
                              SharedSessionContractImplementor session, Object obj)
                                     throws HibernateException, SQLException {
       String key = resultSet.getString(str[0]);

       return toEnum(key);
    }

    protected Object toEnum(String key) {
       if (key == null || key.trim().length() == 0) {
          return null;
       }

       return EnumHelper.getInstance().valueOf(returnedClass(), key);
    }

    public void nullSafeSet(PreparedStatement preparedStatement, Object obj,
                            int param, SharedSessionContractImplementor session)
                            throws HibernateException, SQLException {
       obj = toDatabaseObject(obj);

       if (obj == null) {
           preparedStatement.setNull(param, TYPES[0]);
           return;
       }

       preparedStatement.setString(param, obj.toString());
    }

    protected Object toDatabaseObject(Object o) {
       return o;
    }

    public abstract Class returnedClass();

    @Override
    public int getSqlType() {
        return TYPES[0];
    }

    public int[] sqlTypes() {
        return TYPES;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        assert (x != null);
        return x.hashCode();
    }

    protected Object realDeepCopy(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }

        try {
            return BeanUtils.cloneBean(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        // also safe for mutable objects
        return realDeepCopy(cached);
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        // also safe for mutable objects
        Object deepCopy = realDeepCopy(value);
        assert (deepCopy instanceof Serializable);
        return (Serializable) deepCopy;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        // also safe for mutable objects
        return realDeepCopy(original);
    }

}