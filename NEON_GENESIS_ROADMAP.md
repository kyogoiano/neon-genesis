# Neon Genesis Roadmap

This roadmap sets the direction for the next modernization cycle. It prioritizes
concurrency, JDK compatibility, and modernization of the framework's public
surface before Jakarta EE 12. Jakarta EE 12 is intentionally the final
compatibility migration. This is a sequence of goals, not a release calendar;
versions and implementation choices should be confirmed against released
specifications and compatible runtimes before adoption.

## Guiding principles

- Preserve the existing command, binding, and deployment behavior unless a
  migration explicitly changes it.
- Keep compatibility boundaries clear across supported JDKs, Jakarta runtimes,
  and UI toolkits.
- Prefer standard Jakarta APIs over project-specific replacements where they
  meet the use case.
- Make asynchronous behavior explicit; do not silently change synchronous APIs
  or block UI and request threads.
- Keep source and binary compatibility in mind when replacing deprecated APIs.

## Priorities

### 0. Make virtual threads the default for internal execution

Use virtual threads for framework-managed blocking work, including command
dispatch and relevant server-side execution paths. Keep executor ownership and
shutdown behavior explicit, and avoid changing application-provided executors.
Virtual threads must not be used to bypass JavaFX or Swing thread-confinement
rules.

**Initial implementation:** Framework-owned bootstrap work in `StartupHelper`
now uses named virtual threads for the no-op command warm-up and validator-rule
loading. The timeout test harness also uses virtual threads for its worker and
disposer, and asserts that timeout-wrapped work runs on a virtual thread. CDI
command calls remain synchronous, and EJB execution remains container-managed;
changing those paths requires separate transaction and context-propagation
validation. No direct platform-thread creation sites remain in framework code;
threads owned by CDI/EJB containers remain under container control.
CDI and EJB dispatch reuse the shared command-execution guard instead of
maintaining duplicate thread-local flags. Criteria and local-EJB reentrancy
markers use non-inheritable, typed thread-local state, and the local execution
test aspect uses typed thread-local state instead of thread-keyed weak maps.

**Acceptance criteria**

- Framework-owned blocking tasks use virtual-thread-backed execution by
  default on supported JDKs.
- Execution mode and lifecycle are consistent across local, CDI, EJB, and
  remote command paths where the runtime permits framework control.
- Tests cover concurrent command execution, error propagation, cancellation,
  and orderly shutdown.
- Any path that cannot use virtual threads documents why and retains a safe
  fallback.

### 1. Certify support for JDK 25

Establish JDK 25 as a tested runtime and build target while preserving the
existing minimum supported JDK. Validate the build toolchain, AspectJ weaving,
JavaFX platform dependencies, Jakarta integrations, and test suites; update
documentation only after the supported matrix is verified.

**Status:** Verified on JDK 21 and JDK 25. CI now runs the full build, including
Core and Web Profile integration tests, on both versions.

**Acceptance criteria**

- [x] CI builds and runs the full suite on JDK 25 and the existing minimum
  supported JDK.
- [x] Dependency and plugin compatibility issues are resolved for the verified
  JDK range.
- [x] The README states the verified JDK support range.

### 2. Replace deprecated APIs with modern alternatives

Inventory deprecated public APIs and their usages, identify supported
replacements, then migrate internal callers before considering removal. Current
examples to assess include the legacy command/query/transaction abstractions,
deprecated Hibernate command types, older validation and UI utilities, and
deprecated binder APIs.

This modernization accepts the public API compatibility break: document
replacements, migrate internal callers, and remove obsolete declarations once
their replacement behavior is covered. Do not remove an API solely because it
is annotated `@Deprecated`; first confirm its replacement covers the same
behavior.

**Status:** All deprecated public API declarations have been removed, and
internal test usage now exercises the annotation-based APIs. Deprecated
reflection, Hibernate query creation, BeanUtils array conversion, and Swing
selection APIs have also been replaced in internal callers. This is a
source/binary compatibility break; migration guidance is in
`docs/annotations-and-binding.md`.

| Deprecated API | Modern direction | Status |
| --- | --- | --- |
| `Command`, `Query`, `Transaction`, `NoopQuery` | Plain serializable command objects with method-level `@Remotable` / `@Transactional` | Removed; internal test usages migrated |
| `AbstractHibernateQuery`, `AbstractHibernateTransaction`, and marker interfaces | `AbstractHibernateCommand` plus method-level annotations | Removed; internal test usages migrated |
| `ValidationUtils.isValid(Form)` and `ui.Form` | `ValidationUtils.isValid(Object)` and regular POJOs with `@Form` | Removed |
| `BasicValidator.validateRequiredIf` | `BasicValidator.validateScript` | Removed; validator examples use script-based validation |
| `BasicValidator.validateJXPath` | `BasicValidator.validateScript` | Removed; validator rules use script-based validation |
| `SwingBinder.registerComponentBinder` | `SwingBinder.registerWidgetBinder` | Removed |
| `UIUtils.asIdentifier` / `asClassName` | Toolkit-native component IDs/names | Removed; no internal callers |

**Acceptance criteria**

- [x] Every deprecated public API has a documented replacement.
- [x] Command integration tests use the modern command/annotation APIs.
- [x] The intentional source/binary compatibility break and migration guidance
  are documented.

### 3. Add Jakarta JSON Binding and JSON Processing

Add optional integrations for Jakarta JSON Binding (JSON-B) and Jakarta JSON
Processing (JSON-P). Use JSON-B for object binding and JSON-P for standard JSON
model/stream processing where those capabilities are needed, while keeping
transport and serialization choices replaceable.

Use JSON-B 3.0 and JSON-P 2.1, compatible with the current Jakarta EE 11
baseline. Reassess these APIs and providers as part of the final Jakarta EE 12
migration. Keep the APIs modular so projects that do not need JSON support do
not acquire unnecessary runtime dependencies.

**Initial implementation:** The server module now exposes JSON-B 3.0 and JSON-P
2.1 APIs as compile-time-only APIs. No provider is forced on applications, and
the current command transport remains unchanged. Core integration tests use
Yasson and Parsson to exercise serialization, deserialization, JSON processing,
and malformed input.

**Acceptance criteria**

- [x] JSON-B and JSON-P APIs are available through optional dependencies.
- [x] Tests cover serialization, deserialization, JSON processing, and
  malformed input behavior.
- Any use in remote command transport has documented compatibility and does
  not silently change the existing wire format.

### 4. Make FXML integration a first-class JavaFX workflow

The project already has JavaFX binding and an FXML dependency. Build on that
foundation with explicit FXML loading and controller lifecycle integration,
clear binding setup/teardown, and documented dependency-injection or controller
construction boundaries. Keep JavaFX UI updates on the JavaFX Application
Thread.

**Status:** `FXMLView` now loads an FXML document, resolves its controller,
binds it to the supplied form, and provides an `AutoCloseable` lifecycle for
unbinding. A controller factory supports application-managed controllers.

**Acceptance criteria**

- [x] An FXML-backed view can be loaded and bound with a documented, minimal setup.
- [x] Controller factory and binder lifecycle are tested.
- [x] Integration works with the existing `JavaFXBinder` without regressing
  programmatically constructed views.

### 5. Plan asynchronous processing for key paths

Identify where asynchronous APIs provide a concrete benefit before
implementation. Candidate areas include remote command calls, long-running
command execution, and UI-facing data-provider operations. Define cancellation,
timeouts, error propagation, ordering, and transaction boundaries for each
candidate.

Introduce async entry points alongside existing synchronous APIs where
practical. Do not move blocking work onto UI threads, and do not assume that
using virtual threads alone makes an API asynchronous.

**Initial design boundaries**

- Start with an asynchronous remote HTTP command client; keep CDI-local and
  EJB/container execution synchronous until their transaction and request
  context semantics can be preserved.
- Return `CompletionStage` from new async entry points, preserve existing
  synchronous methods, and propagate transport/command failures through
  exceptional completion.
- Define cancellation and timeout behavior at the HTTP request boundary;
  cancelling a client request does not imply that already-running server work
  was rolled back.
- Marshal JavaFX updates back to the JavaFX Application Thread. Do not imply
  CDI, transaction, or security context propagation across executor boundaries.

**Acceptance criteria**

- Each selected path has a written contract for completion, cancellation,
  timeout, errors, and execution context.
- Existing synchronous callers retain their behavior.
- Tests cover completion, failure, cancellation, and thread/context boundaries.

### 6. Adopt Jakarta EE 12 APIs, starting with CDI 5.0 Lite

As the final platform migration, move the current Jakarta EE 11 integrations
forward to Jakarta EE 12. Begin with CDI 5.0 Lite and update the Core and Web
profiles only when compatible implementations and runtimes are available.
Review related specifications and provider versions together rather than
upgrading individual artifacts without a tested compatibility set. Revalidate
the JSON APIs and providers from Priority 3 as part of this migration.

**Acceptance criteria**

- CDI 5.0 Lite integration works in the supported standalone and application
  server profiles.
- Jakarta EE API, implementation, and runtime versions are aligned and tested
  as a documented matrix.
- Existing CDI and EJB command execution and transactional behavior remains
  covered by integration tests.
- Migration notes describe runtime configuration and any incompatible changes.

## Delivery approach

1. Establish the virtual-thread default and JDK 25 compatibility baseline.
2. Migrate deprecated API callers and document compatibility boundaries.
3. Add optional Jakarta JSON APIs and first-class FXML loading/binding.
4. Deliver asynchronous entry points incrementally, based on their contracts.
5. Upgrade the Jakarta profiles as a tested compatibility set, last.

Each priority should land with focused unit and integration coverage, updated
user-facing documentation, and release notes for any behavior or compatibility
change.
