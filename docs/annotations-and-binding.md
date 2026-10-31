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

### Swing (`SwingBinder`)
`SwingBinder` binds a Swing `Component` / `Container` hierarchy to a `@Form` and `@ViewHandler`:
- Matches components by `Component#getName()`.
- Supports `JTextComponent`, `JLabel`, `AbstractButton` (`JCheckBox`, `JRadioButton`, `JToggleButton`, `JButton`), `JComboBox`, `JList`, `JTable`, `JSlider`, `JSpinner`, and `JProgressBar`.

```java
SwingBinder binder = new SwingBinder(panel, userForm, viewHandler);
binder.bind();
```
