# core

**core** is a Bukkit/Spigot plugin providing the foundational runtime infrastructure for a multi-server Minecraft network. It implements shared services that every game server on the network depends on: player profile management, a permission system, cross-server messaging, punishment enforcement, two-factor authentication, Discord account linking, a scoreboard abstraction, and a version-spanning NMS abstraction layer. It is designed as a modular monolith — a single deployable artefact composed of loosely coupled, independently addressable modules.

---

## Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                         core plugin                          │
│                                                              │
│   ┌──────────────┐   ┌──────────────┐   ┌────────────────┐  │
│   │ ModuleManager│   │ CommandCenter│   │   NMS Layer    │  │
│   │  (Locator    │   │  (command    │   │  (version-     │  │
│   │   / service  │   │   registry)  │   │   abstracted   │  │
│   │   locator)   │   └──────────────┘   │   Bukkit ops)  │  │
│   └──────┬───────┘                      └────────────────┘  │
│          │  resolves                                         │
│   ┌──────▼───────────────────────────────────────────────┐  │
│   │                   Domain Modules                      │  │
│   │                                                       │  │
│   │  ProfileManager   PunishmentManager   TwoFactor       │  │
│   │  MessageManager   PermissionManager   LinkManager     │  │
│   │  ServerDataManager   CurrencyModule   ChatLogModule   │  │
│   └──────────────────────────────┬────────────────────────┘  │
│                                  │                           │
│   ┌──────────────┐   ┌───────────▼──────────────────────┐   │
│   │  ThreadPool  │   │      DatabaseMessageManager      │   │
│   │ (ASYNC_POOL) │   │  (Redis pub/sub message bus)     │   │
│   └──────────────┘   └──────────────────────────────────┘   │
│                                  │                           │
└──────────────────────────────────┼───────────────────────────┘
                         ┌─────────▼──────────┐
                         │   Redis / MySQL    │
                         └────────────────────┘
```

The `Locator` service locator provides a lightweight alternative to a full DI container: modules register themselves on construction, and any module can resolve a dependency by class at runtime. `DatabaseMessageManager` underpins cross-server communication by maintaining a registry of named channels, each backed by a Redis `psubscribe` listener on the `maritime:*` namespace, running on a dedicated single-thread executor.

---

## Scientifically Relevant Features

**TOTP-based cryptographic authentication (2FA).** The `TwoFactor` module implements RFC 6238-compliant Time-based One-Time Password authentication via the `GoogleAuthenticator` library (`TwoFactorManager.verify()`). During setup, the server generates a TOTP secret using `googleAuthenticator.createCredentials()`, derives a QR-code URL from the secret and player identity via `SecretDataUtil.getUrl()`, renders this onto an in-game `MapView` using a custom `MapRenderer` (`QRMap`), and stores the secret persistently in the database via `TwoFactorDb`. On subsequent logins, the server determines whether re-authentication is needed by inspecting both the player's IP address (checking for a new source) and elapsed time since last login (24-hour window), implemented in `TwoFactorManager.needsToAuthorise()`.

TOTP is a directly relevant cryptographic primitive in regulatory and legal contexts. In pharmaceutical regulatory affairs and patent filings, digital authentication of timestamped records (e.g., invention disclosure submissions, audit trails in GxP-regulated systems) often relies on TOTP or equivalent OTP schemes. The correct, library-backed implementation here — rather than a hand-rolled scheme — demonstrates an understanding of where cryptographic primitives should and should not be implemented from scratch.

**Version-spanning API abstraction.** The `nms-abstraction` module defines two interfaces — `IMaterialMapper` and `INmsHandler` — that expose stable typed operations (title packets, action bars, tab header/footer, skull textures, scoreboard team colours, material mappings) independent of the underlying Bukkit/NMS version. Concrete implementations for 1.8 (`v1_8_R3`), 1.17 (`v1_17_R1`), and 1.18 (`v1_18_R2`) are compiled as separate subprojects and selected at runtime via a version-detection bridge. This is structurally equivalent to the adapter/versioning pattern used in cheminformatics toolkits: both RDKit and CDK expose stable high-level APIs (e.g., `Mol.GetAtomWithIdx()`) that abstract over internal representation changes across library versions. Maintaining source compatibility while supporting multiple target environments is a real challenge in both domains.

**Redis as a distributed in-memory data layer.** Redis is used throughout core for several distinct purposes: player profile caching (`client_cache:<uuid>` keys in `ProfileManager`), cross-server player location lookups, Discord-Minecraft account link verification codes (with TTL-based expiry via `j.expire(key, 10 * 60L)` in `LinkManager`), and group-to-server mapping publication. The TTL-based expiry of verification codes is a concrete example of using Redis's native key expiration as a security mechanism — the link code becomes invalid automatically after 10 minutes without requiring a cleanup job.

**Thread pool execution decoupled from the server tick.** `ThreadPool.ASYNC_POOL` (a cached thread pool) is used across profile loading, database writes, Redis operations, and 2FA authorisation logging. The pool also exposes `runOnNextServerTick()` as a utility for marshalling results back onto the main server thread — a necessary step in Bukkit because inventory and entity operations are not thread-safe. This two-phase pattern (async I/O, sync state update) mirrors the threading model used in event-loop-based data processing frameworks, and demonstrates a working understanding of when thread safety must be considered explicitly.

**Event-driven cross-server messaging.** `DatabaseMessageManager` abstracts Redis pub/sub into a typed message channel system. Modules register interest in named channels (e.g., `ProfileManager.RELOAD_PROFILE_CHANNEL`) with a callback that receives deserialized payloads. The `psubscribe` call runs on a dedicated `Executors.newSingleThreadExecutor()` thread, ensuring that message dispatch is independent of both the main server thread and the general async pool. This maps directly to event-driven data pipeline patterns where producers and consumers are decoupled by a message broker — a pattern used in production cheminformatics platforms (e.g., compound registration services that fire a notification when a new substance is canonicalised).

---

## Project-Specific Features

**Role-based 2FA policy.** 2FA enforcement is applied differentially by permission group: `HELPER` rank and above are required to set up 2FA (enforced at login via `forceSetup2fa()`); `BUILDER` and `MEDIA` ranks have the permission explicitly disabled (preventing unintended propagation through permission inheritance). Administrators hold a separate `RESET_2FA` permission. This granular permission model avoids blunt all-or-nothing enforcement.

**Inventory isolation during 2FA setup.** When 2FA setup begins, the player's inventory contents are saved (`setupInventory.put(...)`) and replaced with a blank inventory containing only a rendered QR-code map item in slot 4. On successful setup or on quit, the original inventory is restored. This UX-motivated design also has a security implication: it prevents the player from interacting with the game world while in a locked, unauthenticated state.

**Full lock enforcement.** When a player is authentication-locked, `TwoFactor` cancels movement beyond a 1-block radius (using coordinate comparisons rather than an imprecise delta on the `PlayerMoveEvent`), blocks all commands, cancels inventory interactions, drops, and pickups. The movement lock uses stored last-known position and teleports the player back on violation — effectively freezing them in place without using an imprecise or exploitable method.

**Punishment system with async CompletableFuture pattern.** `PunishmentManager` resolves punishment operations (ban, mute, kick, warn) through a `PunishClient.addPunishment()` call that returns a `CompletableFuture`, with actions taken in the `whenComplete` callback. This means punishment application is non-blocking: the database write completes asynchronously and the actual enforcement (kick, broadcast) only occurs on confirmed persistence.

**NMS multi-version Gradle build.** The `settings.gradle` declares `bukkit-1.8-impl`, `bukkit-1.17-impl`, and `bukkit-1.18-impl` as subprojects, each compiled against the NMS jar for that version. The top-level `build.gradle` aggregates these into a single shaded artefact. This is an unusual but effective build architecture that achieves compile-time correctness on version-specific APIs without requiring runtime reflection hacks.

**GitHub Actions cascade on release.** A workflow triggers `repository_dispatch` events to downstream plugin repositories (`hub`, `block-party`) when a core release is published, propagating a dependency bump automatically. This closes the loop on dependency management without manual intervention.

---

## Technology Stack

- **Language:** Java 11+
- **Platform:** Bukkit/Spigot (1.8 – 1.18)
- **Build:** Gradle (multi-module)
- **Databases:** MySQL (punishments, discord links), Redis (profiles, messaging, caches)
- **Cryptography:** TOTP via `GoogleAuthenticator` (RFC 6238), QR code generation
- **Concurrency:** `Executors.newCachedThreadPool()`, `Executors.newSingleThreadExecutor()`, `CompletableFuture`
- **Serialisation:** Gson
- **Utilities:** Lombok, BungeeCord chat API
- **CI:** GitHub Actions (cross-repository release dispatch)
