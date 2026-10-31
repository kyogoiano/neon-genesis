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
package dev.neongenesis.cdi.jndi;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.inject.spi.CDI;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import dev.neongenesis.cdi.CDITransactionContext;
import dev.neongenesis.cdi.CommandExecutorCDI;
import dev.neongenesis.ejb.CommandExecutor;

public class CDIInitialContextFactory implements InitialContextFactory {
   private static final Map<String, Object> BINDINGS = new ConcurrentHashMap<>();
   private static final Context CONTEXT = new CDIContext();

   static {
      BINDINGS.put("java:comp/UserTransaction", CDITransactionContext.getInstance());
   }

   public static void bindStatic(String name, Object value) {
      BINDINGS.put(name, value);
   }

   @Override
   public Context getInitialContext(Hashtable<?, ?> environment)
         throws NamingException {
      return CONTEXT;
   }

   private static final class CDIContext implements Context {
      @Override
      public Object lookup(String name) throws NamingException {
         if (BINDINGS.containsKey(name)) {
            return BINDINGS.get(name);
         }
         if ("java:comp/UserTransaction".equals(name)) {
            return CDITransactionContext.getInstance();
         }
         if (name != null && name.contains("CommandExecutor")) {
            try {
               return CDI.current().select(CommandExecutorCDI.class).get();
            } catch (IllegalStateException e) {
               throw new NamingException("CDI container is not active for lookup: " + name);
            }
         }
         if ("java:comp/env/TransactionalInjector".equals(name)) {
            String injector = System.getProperty("genesis.cdi.transactionalInjector");
            if (injector != null) {
               return injector;
            }
         }
         throw new NamingException("Name not found in CDIInitialContext: " + name);
      }

      @Override
      public Object lookup(Name name) throws NamingException {
         return lookup(name.toString());
      }

      @Override
      public void bind(String name, Object obj) throws NamingException {
         BINDINGS.put(name, obj);
      }

      @Override
      public void bind(Name name, Object obj) throws NamingException {
         bind(name.toString(), obj);
      }

      @Override
      public void rebind(String name, Object obj) throws NamingException {
         BINDINGS.put(name, obj);
      }

      @Override
      public void rebind(Name name, Object obj) throws NamingException {
         rebind(name.toString(), obj);
      }

      @Override
      public void unbind(String name) throws NamingException {
         BINDINGS.remove(name);
      }

      @Override
      public void unbind(Name name) throws NamingException {
         unbind(name.toString());
      }

      @Override
      public void rename(String oldName, String newName) throws NamingException {
         Object val = BINDINGS.remove(oldName);
         if (val != null) {
            BINDINGS.put(newName, val);
         }
      }

      @Override
      public void rename(Name oldName, Name newName) throws NamingException {
         rename(oldName.toString(), newName.toString());
      }

      @Override
      public NamingEnumeration<NameClassPair> list(String name)
            throws NamingException {
         return null;
      }

      @Override
      public NamingEnumeration<NameClassPair> list(Name name)
            throws NamingException {
         return null;
      }

      @Override
      public NamingEnumeration<Binding> listBindings(String name)
            throws NamingException {
         return null;
      }

      @Override
      public NamingEnumeration<Binding> listBindings(Name name)
            throws NamingException {
         return null;
      }

      @Override
      public void destroySubcontext(String name) throws NamingException {
      }

      @Override
      public void destroySubcontext(Name name) throws NamingException {
      }

      @Override
      public Context createSubcontext(String name) throws NamingException {
         return this;
      }

      @Override
      public Context createSubcontext(Name name) throws NamingException {
         return this;
      }

      @Override
      public Object lookupLink(String name) throws NamingException {
         return lookup(name);
      }

      @Override
      public Object lookupLink(Name name) throws NamingException {
         return lookup(name.toString());
      }

      @Override
      public NameParser getNameParser(String name) throws NamingException {
         return null;
      }

      @Override
      public NameParser getNameParser(Name name) throws NamingException {
         return null;
      }

      @Override
      public String composeName(String name, String prefix)
            throws NamingException {
         return prefix + "/" + name;
      }

      @Override
      public Name composeName(Name name, Name prefix) throws NamingException {
         return name;
      }

      @Override
      public Object addToEnvironment(String propName, Object propVal)
            throws NamingException {
         return null;
      }

      @Override
      public Object removeFromEnvironment(String propName)
            throws NamingException {
         return null;
      }

      @Override
      public Hashtable<?, ?> getEnvironment() throws NamingException {
         return new Hashtable<>();
      }

      @Override
      public void close() throws NamingException {
      }

      @Override
      public String getNameInNamespace() throws NamingException {
         return "";
      }
   }
}
