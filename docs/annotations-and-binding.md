# Declarative Annotations, Scripting & UI Binding

## 1. Form & View Annotations (`dev.neongenesis.annotation`)

| Annotation | Target | Description |
| :--- | :--- | :--- |
| `@Form` | Type | Marks a JavaBean as a bindable form controller model. |
| `@ViewHandler` | Type | Marks a UI container / controller as a view handler. |
| `@Action` | Method | Exposes a method as a UI action bound to a button with the matching name/id. |
| `@DataProvider` | Method | Populates a list, combo box, or table (`objectField`, `indexField`, `callOnInit`). |
| `@Condition` | Field | Declares reusable named script expressions (`{"isAdmin=form.role == 'ADMIN'"}`). |
| `@EnabledWhen` | Field / Method | Enables the bound widget when the script expression evaluates to `true`. |
| `@VisibleWhen` | Field / Method | Shows the bound widget when the script expression evaluates to `true`. |
| `@ClearOn` | Field / Method | Clears a field or resets a data provider when the expression changes/evaluates to `true`. |
| `@CallWhen` | Method | Automatically invokes an `@Action` or `@DataProvider` when the expression evaluates to `true`. |
| `@ValidateBefore` | Method | Runs Commons Validator rules on the form before executing an `@Action`. |
| `@NotBound` | Field | Excludes a property from automatic widget binding. |
| `@EmptyResolver` | Field | Customizes how empty values are detected for a field. |
| `@EqualityComparator` | Field | Customizes equality comparison when detecting property changes. |
| `@Cloner` | Field | Customizes deep/shallow cloning of form property values. |

---

## 2. Scripting Engines

Expressions inside `@Condition`, `@EnabledWhen`, `@VisibleWhen`, `@ClearOn`, and `@CallWhen` are evaluated through the pluggable `Script` SPI:

- **Jakarta Expression Language (`el`)**: Uses Jakarta EL (`jakarta.el.ExpressionFactory`) with built-in `g:` functions (`g:isEmpty`, `g:hasChanged`, `g:isEqual`, `g:isNotEqual`).
- **JSR-223 `javax.script` (`javasript` / `beanshell`)**: Uses standard `javax.script.ScriptEngineManager` with Mozilla Rhino (`rhino-engine`) or BeanShell (`bsh`).
- **Apache Commons JXPath (`jxpath`)**: Evaluates XPath expressions against the form object graph.

---

## 3. UI Binding: OpenJFX & Swing

### OpenJFX (`JavaFXBinder`)
`JavaFXBinder` binds a JavaFX `Node` / `Parent` (such as a root pane loaded from FXML or constructed programmatically) to a `@Form` and `@ViewHandler`:
- Matches controls by `Node#getId()` (`fx:id` in FXML).
- Supports `TextInputControl` (`TextField`, `PasswordField`, `TextArea`), `Labeled` (`Label`), `CheckBox`, `ToggleButton`, `RadioButton` (`ToggleGroup`), `ComboBox`, `ChoiceBox`, `ListView`, `TableView`, `Slider`, `Spinner`, `ProgressBar`, `DatePicker`, and `ButtonBase` (`Button`, `Hyperlink`).

```java
JavaFXBinder binder = new JavaFXBinder(rootPane, userForm, viewController);
binder.bind();
```

For FXML-backed views, `FXMLView` loads the document, gets its controller, binds
the view, and unbinds it when closed. Load and close views on the JavaFX
Application Thread:

```java
FXMLView<MyViewController> view = FXMLView.load(
      getClass().getResource("my-view.fxml"), userForm);
Parent root = (Parent) view.getRoot();
// Add root to the scene.
view.close(); // Before disposing the view.
```

To supply controllers from a dependency-injection container, pass an
`FXMLLoader` controller factory:

```java
FXMLView<MyViewController> view = FXMLView.load(
      getClass().getResource("my-view.fxml"),
      userForm,
      controllerType -> container.select(controllerType).get());
```

### Swing (`SwingBinder`)
`SwingBinder` binds a Swing `Component` / `Container` hierarchy to a `@Form` and `@ViewHandler`:
- Matches components by `Component#getName()`.
- Supports `JTextComponent`, `JLabel`, `AbstractButton` (`JCheckBox`, `JRadioButton`, `JToggleButton`, `JButton`), `JComboBox`, `JList`, `JTable`, `JSlider`, `JSpinner`, and `JProgressBar`.

```java
SwingBinder binder = new SwingBinder(panel, userForm, viewHandler);
binder.bind();
```

## 4. Jakarta JSON APIs

The server module exposes the Jakarta JSON Binding 3.0 and JSON Processing 2.1
APIs as compile-time-only APIs. Integration tests exercise JSON-B serialization
and deserialization plus JSON-P object/stream processing and malformed-input
handling.

The APIs do not change the existing command transport or add a production
runtime JSON provider. A Jakarta EE runtime supplies the specification
implementations; standalone deployments must select and add compatible JSON-B
and JSON-P providers explicitly. This keeps JSON support optional and avoids
changing the existing serialized command wire format.

## 5. Removed deprecated APIs

The deprecated public APIs have been removed. Migrate callers to these
replacements:

| Removed API | Replacement |
| --- | --- |
| `dev.neongenesis.ui.Form` and `ValidationUtils.isValid(Form)` | Use a regular POJO, annotate it with `@Form`, and call `ValidationUtils.isValid(Object)`. |
| `BasicValidator.validateRequiredIf` | Use the `script` validator with a JavaScript expression that describes when the field is valid. For example, `form.getDependency() == null || form.getDependency() == '' || (form.getValue() != null && form.getValue() != '')`. |
| `BasicValidator.validateJXPath` and the `jxpath` validator rule | Use `BasicValidator.validateScript` and a `script` rule variable with the configured script language. |
| `UIUtils.asIdentifier` / `asClassName` | Use toolkit-native IDs (`Node#getId()` in JavaFX or `Component#getName()` in Swing). |
| `SwingBinder.registerComponentBinder` | Use `registerWidgetBinder`. |
| `Command`, `Query`, `Transaction`, `NoopQuery` | Use serializable command objects with method-level `@Remotable` or `@Transactional` annotations. |
| `HibernateQuery`, `HibernateTransaction`, `AbstractHibernateQuery`, `AbstractHibernateTransaction` | Extend `AbstractHibernateCommand`; annotate each remotely executed method with `@Remotable` or `@Transactional`. |
