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
package dev.neongenesis.tests.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import dev.neongenesis.annotation.Action;
import dev.neongenesis.annotation.CallWhen;
import dev.neongenesis.annotation.DataProvider;
import dev.neongenesis.annotation.Form;
import dev.neongenesis.tests.mockobjects.MockBean;
import dev.neongenesis.tests.TestCase;
import dev.neongenesis.ui.ActionInvoker;
import dev.neongenesis.ui.controller.DefaultFormController;
import dev.neongenesis.ui.controller.DefaultFormControllerFactory;
import dev.neongenesis.ui.controller.FormController;
import dev.neongenesis.ui.controller.FormControllerFactory;
import dev.neongenesis.ui.controller.FormControllerListener;
import dev.neongenesis.ui.controller.FormState;
import dev.neongenesis.ui.controller.FormStateImpl;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.MethodMetadata;

import dev.neongenesis.ui.swing.SwingBinder;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

public class FormControllerTest extends TestCase {

   private FormController getController(Object form) throws Exception {
      final FormController formController = new DefaultFormController();
      formController.setFormMetadata(getFormMetadata(form));
      formController.setForm(form);
      formController.setup();
      return formController;
   }

   private Map getSomeValues() throws Exception {
      final Map newValues = BeanUtils.describe(new FooForm());

      final boolean fieldBoolean = true;
      final char fieldChar = 'Z';
      final byte fieldByte = (byte) 0xFE;
      final short fieldShort = 125;
      final int fieldInt = 3452342;
      final long fieldLong = 123721281238L;
      final double fieldDouble = 23423.23429;
      final float fieldFloat = 234234234234.43535F;
      final Boolean fieldBooleanWrapper = Boolean.TRUE;
      final char fieldCharacterWrapper = 'w';
      final byte fieldByteWrapper = (byte) 0xCA;
      final short fieldShortWrapper = (short) 115;
      final int fieldIntegerWrapper = 23423423;
      final long fieldLongWrapper = 34223409813244L;
      final double fieldDoubleWrapper = 234234423.000123;
      final float fieldFloatWrapper =
            445095043543435421321333.434344324233F;
      final String fieldString = "aisfBCASADr";

      newValues.put("fieldBoolean", Boolean.toString(fieldBoolean));
      newValues.put("fieldChar", Character.toString(fieldChar));
      newValues.put("fieldByte", Byte.toString(fieldByte));
      newValues.put("fieldShort", Short.toString(fieldShort));
      newValues.put("fieldInt", Integer.toString(fieldInt));
      newValues.put("fieldLong", Long.toString(fieldLong));
      newValues.put("fieldDouble", Double.toString(fieldDouble));
      newValues.put("fieldFloat", Float.toString(fieldFloat));
      newValues.put("fieldBooleanWrapper", fieldBooleanWrapper.toString());
      newValues.put("fieldCharacterWrapper", Character.toString(fieldCharacterWrapper));
      newValues.put("fieldByteWrapper", Byte.toString(fieldByteWrapper));
      newValues.put("fieldShortWrapper", Short.toString(fieldShortWrapper));
      newValues.put("fieldIntegerWrapper", Integer.toString(fieldIntegerWrapper));
      newValues.put("fieldLongWrapper", Long.toString(fieldLongWrapper));
      newValues.put("fieldDoubleWrapper", Double.toString(fieldDoubleWrapper));
      newValues.put("fieldFloatWrapper", Float.toString(fieldFloatWrapper));
      newValues.put("fieldString", fieldString);
      newValues.put("fieldBigDecimal", "234423,423234");

      return newValues;
   }

   public void testNoopPopulate() throws Exception {
      final FooForm someForm = new FooForm();
      final FormController controller = getController(someForm);
      final Map describedMap = BeanUtils.describe(someForm);
      controller.populate(describedMap, null);
      final Map afterPopulateMap = BeanUtils.describe(someForm);
      assertDescribedMapEquals(describedMap, afterPopulateMap);
   }

   public void testSimplePopulate() throws Exception {
      final FooForm someForm = new FooForm();
      final FormController controller = getController(someForm);

      final Map someValues = getSomeValues();
      controller.populate(someValues, null);
      final Map newValuesAfterPopulate = BeanUtils.describe(someForm);
      newValuesAfterPopulate.put("fieldBigDecimal", newValuesAfterPopulate.get(
            "fieldBigDecimal").toString().replaceAll("[.]", ","));

      assertDescribedMapEquals(someValues, newValuesAfterPopulate);
   }

   public void testReset() throws Exception {
      final FooForm someForm = new FooForm();
      final FormController controller = getController(someForm);
      final Map describedMap = PropertyUtils.describe(someForm);
      final Map someValues = getSomeValues();
      final FormState state = new FormStateImpl(controller.getFormState());
      controller.populate(someValues, null);
      controller.reset(state);
      assertDescribedMapEquals(describedMap, PropertyUtils.describe(someForm));

      final JTextField foo1 = new JTextField();
      final JTextField foo2 = new JTextField();
      final JButton search = new JButton();
      final JTable foos = new JTable();
      final JPanel panel = new JPanel();

      foo1.setName("foo1");
      foo2.setName("foo2");
      search.setName("search");
      foos.setName("foos");

      panel.add(foo1);
      panel.add(foo2);
      panel.add(search);
      panel.add(foos);

      final ResetTestForm resetForm = new ResetTestForm();
      SwingBinder binder = new SwingBinder(panel, resetForm);
      binder.bind();

      FormController resetController = ((FormControllerFactory)resetForm).
            getFormController(resetForm);
      final FormState initialState = new FormStateImpl(resetController.
            getFormState());

      resetForm.setFoo1("1");
      resetForm.setFoo2("2");
      resetController.update();
      resetController.invokeAction("search", null);
      foos.getSelectionModel().setSelectionInterval(0, 0);

      resetController.reset(initialState);

      final FormState newState = new FormStateImpl(
            resetController.getFormState());
      assertDescribedMapEquals(initialState.getValuesMap(), newState.
            getValuesMap());
   }
   
   public void testNonAnnotatedFormClass() throws Exception {
      final NonAnnotatedFormClass form = new NonAnnotatedFormClass();
      final FormController controller = new DefaultFormControllerFactory()
            .getFormController(form);
      final List reference = new ArrayList();

      controller.addFormControllerListener(new FormControllerListener() {
         public void enabledConditionsChanged(Map updatedEnabledConditions) {
         }

         public void visibleConditionsChanged(Map updatedVisibleConditions) {
         }

         public boolean beforeInvokingMethod(MethodMetadata methodMetadata) throws Exception {
            return true;
         }

         public void afterInvokingMethod(MethodMetadata methodMetadata) throws Exception {
         }

         public void dataProvidedListChanged(DataProviderMetadata metadata, List items) throws Exception {
            reference.clear();
            reference.addAll(items);
         }

         public void dataProvidedIndexesChanged(DataProviderMetadata metadata, int[] selectedIndexes) {
         }

         public void valuesChanged(Map updatedValues) throws Exception {
         }
      });

      controller.setup();
      assertTrue(reference.isEmpty());
      
      controller.invokeAction("add", null);
      assertEquals(1, reference.size());
      assertEquals(form.getProvidedList(), reference);

      controller.invokeAction("add", null);
      assertEquals(2, reference.size());
      assertEquals(form.getProvidedList(), reference);
   }

   public void testMultipleEvaluationsForm() throws Exception {
      MultipleEvaluationsForm form = new MultipleEvaluationsForm();
      final FormController controller = new DefaultFormControllerFactory()
            .getFormController(form);
      controller.setMaximumEvaluationTimes(2);
      controller.setup();
      
      assertEquals(1, form.getCallOnInitCount());
      assertEquals(1, form.getConditionalCount());
   }

   @Form
   public static class FooForm {

      private boolean fieldBoolean;
      private char fieldChar;
      private byte fieldByte;
      private short fieldShort;
      private int fieldInt;
      private long fieldLong;
      private double fieldDouble;
      private float fieldFloat;
      private Boolean fieldBooleanWrapper;
      private Character fieldCharacterWrapper;
      private Byte fieldByteWrapper;
      private Short fieldShortWrapper;
      private Integer fieldIntegerWrapper;
      private Long fieldLongWrapper;
      private Double fieldDoubleWrapper;
      private Float fieldFloatWrapper;
      private String fieldString;
      private BigDecimal fieldBigDecimal;

      public BigDecimal getFieldBigDecimal() {
         return fieldBigDecimal;
      }

      public void setFieldBigDecimal(BigDecimal fieldBigDecimal) {
         this.fieldBigDecimal = fieldBigDecimal;
      }

      public boolean isFieldBoolean() {
         return fieldBoolean;
      }

      public void setFieldBoolean(boolean fieldBoolean) {
         this.fieldBoolean = fieldBoolean;
      }

      public Boolean getFieldBooleanWrapper() {
         return fieldBooleanWrapper;
      }

      public void setFieldBooleanWrapper(Boolean fieldBooleanWrapper) {
         this.fieldBooleanWrapper = fieldBooleanWrapper;
      }

      public byte getFieldByte() {
         return fieldByte;
      }

      public void setFieldByte(byte fieldByte) {
         this.fieldByte = fieldByte;
      }

      public Byte getFieldByteWrapper() {
         return fieldByteWrapper;
      }

      public void setFieldByteWrapper(Byte fieldByteWrapper) {
         this.fieldByteWrapper = fieldByteWrapper;
      }

      public char getFieldChar() {
         return fieldChar;
      }

      public void setFieldChar(char fieldChar) {
         this.fieldChar = fieldChar;
      }

      public Character getFieldCharacterWrapper() {
         return fieldCharacterWrapper;
      }

      public void setFieldCharacterWrapper(Character fieldCharacterWrapper) {
         this.fieldCharacterWrapper = fieldCharacterWrapper;
      }

      public double getFieldDouble() {
         return fieldDouble;
      }

      public void setFieldDouble(double fieldDouble) {
         this.fieldDouble = fieldDouble;
      }

      public Double getFieldDoubleWrapper() {
         return fieldDoubleWrapper;
      }

      public void setFieldDoubleWrapper(Double fieldDoubleWrapper) {
         this.fieldDoubleWrapper = fieldDoubleWrapper;
      }

      public float getFieldFloat() {
         return fieldFloat;
      }

      public void setFieldFloat(float fieldFloat) {
         this.fieldFloat = fieldFloat;
      }

      public Float getFieldFloatWrapper() {
         return fieldFloatWrapper;
      }

      public void setFieldFloatWrapper(Float fieldFloatWrapper) {
         this.fieldFloatWrapper = fieldFloatWrapper;
      }

      public int getFieldInt() {
         return fieldInt;
      }

      public void setFieldInt(int fieldInt) {
         this.fieldInt = fieldInt;
      }

      public Integer getFieldIntegerWrapper() {
         return fieldIntegerWrapper;
      }

      public void setFieldIntegerWrapper(Integer fieldIntegerWrapper) {
         this.fieldIntegerWrapper = fieldIntegerWrapper;
      }

      public long getFieldLong() {
         return fieldLong;
      }

      public void setFieldLong(long fieldLong) {
         this.fieldLong = fieldLong;
      }

      public Long getFieldLongWrapper() {
         return fieldLongWrapper;
      }

      public void setFieldLongWrapper(Long fieldLongWrapper) {
         this.fieldLongWrapper = fieldLongWrapper;
      }

      public short getFieldShort() {
         return fieldShort;
      }

      public void setFieldShort(short fieldShort) {
         this.fieldShort = fieldShort;
      }

      public Short getFieldShortWrapper() {
         return fieldShortWrapper;
      }

      public void setFieldShortWrapper(Short fieldShortWrapper) {
         this.fieldShortWrapper = fieldShortWrapper;
      }

      public String getFieldString() {
         return fieldString;
      }

      public void setFieldString(String fieldString) {
         this.fieldString = fieldString;
      }
   }

   public static class NonAnnotatedFormClass {
      private List providedList = new ArrayList();

      @DataProvider(widgetName="someWidget")
      public List populateWidget() {
         return providedList;
      }

      @Action
      public void add() throws Exception {
         providedList.add(providedList);
         ActionInvoker.invoke(this, "populateWidget");
      }

      public List getProvidedList() {
         return providedList;
      }
   }
   
   public static class MultipleEvaluationsForm {
       private int callOnInitCount;
       private int conditionalCount;

       // Makes form unstable and requires multiple evaluations
       public double getRandom() {
           return Math.random();
       }

       @DataProvider(widgetName="someWidget")
       public List callOnInit() {
           callOnInitCount++;
           return Collections.EMPTY_LIST;
       }
       
       public int getCallOnInitCount() {
          return callOnInitCount;
       }

       @CallWhen("true")
       @DataProvider(widgetName="anotherWidget",callOnInit=false)
       public List conditional() {
           conditionalCount++;
           return Collections.EMPTY_LIST;
       }
       
       public int getConditionalCount() {
          return conditionalCount;
       }
   }

   @Form
   public static class ResetTestForm {
      private String foo1;
      private String foo2;
      private List foos = Collections.EMPTY_LIST;

      public String getFoo1() {
         return foo1;
      }

      public void setFoo1(String foo1) {
         this.foo1 = foo1;
      }

      public String getFoo2() {
         return foo2;
      }

      public void setFoo2(String foo2) {
         this.foo2 = foo2;
      }

      @Action
      public void search() throws Exception {
         foos = Arrays.asList(new Object[] {new MockBean("1", "Foo 1"),
                  new MockBean("2", "Foo 2"), new MockBean("3", "Foo 3")});
         ActionInvoker.invoke(this, "foos");
      }

      @DataProvider(widgetName="foos")
      public List foos() {
         return foos;
      }
   }
}