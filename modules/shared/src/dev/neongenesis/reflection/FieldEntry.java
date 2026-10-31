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
package dev.neongenesis.reflection;

import java.io.Serializable;
import java.util.Collection;

public class FieldEntry implements Serializable {
   private final String fieldName;
   private final String fieldTypeName;
   private final boolean isPrimitive;
   private final boolean isArray;
   private final boolean isPrimitiveArray;
   private final boolean isCollection;

   public FieldEntry(final String fieldName, final Class fieldType) {
      this.fieldName = fieldName;
      this.fieldTypeName = fieldType.getName();
      this.isPrimitive = fieldType.isPrimitive();
      this.isArray = fieldType.isArray();
      this.isPrimitiveArray = isArray && fieldType.getComponentType().isPrimitive();
      this.isCollection = Collection.class.isAssignableFrom(fieldType);
   }

   public String getFieldTypeName() {
      return fieldTypeName;
   }

   public String getFieldName() {
      return fieldName;
   }

   public boolean isPrimitive() {
      return isPrimitive;
   }

   public boolean isArray() {
      return isArray;
   }

   public boolean isPrimitiveArray() {
      return isPrimitiveArray;
   }

   public boolean isCollection() {
      return isCollection;
   }

   public boolean isMultiple(){
      return isArray() || isCollection();
   }

   public boolean equals(Object o) {
      final FieldEntry that = (FieldEntry) o;
      return that.fieldName.equals(this.fieldName)
            && that.fieldTypeName.equals(this.fieldTypeName)
            && that.isArray == this.isArray
            && that.isCollection == this.isCollection
            && that.isPrimitive == this.isPrimitive
            && that.isPrimitiveArray == this.isPrimitiveArray;
   }

   public int hashCode() {
      return fieldName.hashCode();
   }

   public String toString() {
      final StringBuffer buffer = new StringBuffer();
      buffer.append(fieldName);
      buffer.append("["); // NOI18N
      buffer.append(fieldTypeName);
      buffer.append("]"); // NOI18N
      return buffer.toString();
   }
}
