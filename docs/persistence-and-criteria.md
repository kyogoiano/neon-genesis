# Persistence, Transactions, Dynamic `@Criteria` & Paging

The persistence baseline is Hibernate ORM 7.4.14.Final with Jakarta Persistence
3.2. The ORM version is independent of the CDI API version; CDI 5.0 Lite remains
part of the planned final Jakarta EE 12 migration.

## 1. Hibernate Commands (`AbstractHibernateCommand`)

Server commands that interact with Hibernate extend `AbstractHibernateCommand` and annotate methods with `@Remotable` (read-only query) or `@Transactional` (read-write transaction with automatic flush, commit, and rollback on exception):

```java
public class UserCommands extends AbstractHibernateCommand {

   @Remotable
   public List<User> findAll() {
      return getSession().createQuery("from User u order by u.name", User.class)
            .getResultList();
   }

   @Transactional
   public void saveUser(User user) {
      getSession().persist(user);
   }
}
```

When a `@Transactional` method throws an exception, `CDIHibernateTransactionalInjector` (Core Profile) or `EJBHibernateTransactionalInjector` (Web Profile) automatically rolls back the transaction and closes the Hibernate `Session`.

---

## 2. Dynamic Search with `@Criteria`

Classes extending `AbstractHibernateCriteria` can declare `@Criteria` methods whose query filters are dynamically populated from a `@Form` instance using `CriteriaPropertyHelper.fillCriteria(criteriaCommand, form)`:

```java
public class UserSearchCriteria extends AbstractHibernateCriteria {

   public void setName(String name) {
      getCriteria().add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
   }

   @Criteria(value = User.class, orderby = {"name asc"})
   public List<User> search() {
      return getCriteria().list();
   }
}
```

### Invoking from a Form
```java
UserSearchCriteria searcher = new UserSearchCriteria();
CriteriaPropertyHelper.fillCriteria(searcher, userSearchForm);
List<User> results = searcher.search();
```

Only non-empty properties on `userSearchForm` that have matching setter methods on `UserSearchCriteria` are applied to the query.

---

## 3. Paging (`QueryPager` & `CriteriaPager`)

`AbstractHibernateCommand` provides built-in `getPage(...)` helper methods returning a `Page` slice (`pageNumber`, `resultsPerPage`, `results`, `isFirst()`, `isLast()`):

```java
@Remotable
public Page findUsersPage(int pageNumber, int resultsPerPage) throws Exception {
   return getPage(getSession().createQuery("from User", User.class),
         pageNumber, resultsPerPage);
}
```
