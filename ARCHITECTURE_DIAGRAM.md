# 🏛️ Diagramme d'Architecture - RSLFrancoBot

## Vue d'ensemble des 3 Couches

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         EXTERNAL SYSTEMS                                 │
│                                                                          │
│  Discord API    ootrandomizer.com    Python Script    YAML/JSON Files   │
└─────────────────────────────────────────────────────────────────────────┘
                                    ▲
                                    │
┌───────────────────────────────────┼─────────────────────────────────────┐
│                            API LAYER                                     │
│                     (Infrastructure & Adapters)                          │
│                                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │
│  │ JDA Event    │  │ Mock         │  │ Mock RSL     │  │ Yaml       │ │
│  │ Listener     │  │ Randomizer   │  │ Script       │  │ Preset     │ │
│  │              │  │ Adapter      │  │ Adapter      │  │ Repository │ │
│  └──────────────┘  └──────────────┘  └──────────────┘  └────────────┘ │
│         │                  │                  │                 │       │
│         └──────────────────┴──────────────────┴─────────────────┘       │
│                                    │                                     │
└────────────────────────────────────┼─────────────────────────────────────┘
                                     │ implements ports
┌────────────────────────────────────┼─────────────────────────────────────┐
│                            BOT LAYER                                     │
│                      (Application & Handlers)                            │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                         HANDLERS                                  │  │
│  │  SeedCommand │ FrancoButton │ RSLButton │ PoTButton │ Validate   │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                    │                                     │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                      SEED SERVICE                                 │  │
│  │           (Coordinates Use Cases from Engine)                     │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                    │                                     │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                    SEED PRESENTER                                 │  │
│  │         (Formats domain objects → Discord messages)               │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                    │                                     │
└────────────────────────────────────┼─────────────────────────────────────┘
                                     │ uses
┌────────────────────────────────────┼─────────────────────────────────────┐
│                          ENGINE LAYER                                    │
│                    (Domain & Business Logic)                             │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                        USE CASES                                  │  │
│  │                                                                   │  │
│  │  ┌──────────────────────────┐  ┌──────────────────────────┐     │  │
│  │  │  Franco Use Cases        │  │  RSL Use Cases           │     │  │
│  │  │  • ValidateSettings      │  │  • GenerateRSLSeed       │     │  │
│  │  │  • BuildFinalSettings    │  │                          │     │  │
│  │  │  • GenerateFrancoSeed    │  │                          │     │  │
│  │  └──────────────────────────┘  └──────────────────────────┘     │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                    │                                     │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                     DOMAIN ENTITIES                               │  │
│  │  SeedRequest │ SettingsFile │ SeedResult │ Preset │ Validation   │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                    │                                     │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                    PORTS (Interfaces)                             │  │
│  │  IRandomizerApi │ IRSLScriptRunner │ IPresetRepository            │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                    │                                     │
└────────────────────────────────────┼─────────────────────────────────────┘
                                     ▲
                        Dependencies point upwards
                           (Dependency Inversion)
```

---

## Flux Détaillé : Génération Seed Franco

```
┌─────────────┐
│   Discord   │
│    User     │
└──────┬──────┘
       │ /seed
       ▼
┌──────────────────────────────────────────────────────────────┐
│                       API LAYER                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │ JDAEventListener.onSlashCommandInteraction()       │     │
│  │   │                                                 │     │
│  │   └──> JDASlashCommandAdapter.from(event)          │     │
│  └────────────────────────────────────────────────────┘     │
└───────────────────────────┬──────────────────────────────────┘
                            │ DiscordInteraction
                            ▼
┌──────────────────────────────────────────────────────────────┐
│                       BOT LAYER                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │ SeedCommandHandler.handle(interaction)             │     │
│  │   │                                                 │     │
│  │   └──> SeedPresenter.presentModeSelection()        │     │
│  │          │                                          │     │
│  │          └──> DiscordMessage (3 boutons)           │     │
│  └────────────────────────────────────────────────────┘     │
└───────────────────────────┬──────────────────────────────────┘
                            │
                            ▼
                    User clicks "Franco"
                            │
┌───────────────────────────┴──────────────────────────────────┐
│                       API LAYER                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │ JDAEventListener.onButtonInteraction()             │     │
│  │   │                                                 │     │
│  │   └──> JDAInteractionAdapter.fromButtonEvent()     │     │
│  └────────────────────────────────────────────────────┘     │
└───────────────────────────┬──────────────────────────────────┘
                            │ DiscordInteraction
                            ▼
┌──────────────────────────────────────────────────────────────┐
│                       BOT LAYER                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │ FrancoButtonHandler.handle(interaction)            │     │
│  │   │                                                 │     │
│  │   └──> SeedService.getAvailableOptions("franco")   │     │
│  └────────────────────────┬───────────────────────────┘     │
└────────────────────────────┼──────────────────────────────────┘
                             │
┌────────────────────────────┼──────────────────────────────────┐
│                       ENGINE LAYER                           │
│  ┌─────────────────────────▼──────────────────────────┐     │
│  │                                                     │     │
│  └─────────────────────────┬──────────────────────────┘     │
└────────────────────────────┼──────────────────────────────────┘
                             │ uses port
┌────────────────────────────┼──────────────────────────────────┐
│                       API LAYER                              │
│  ┌─────────────────────────▼──────────────────────────┐     │
│  │ YamlPresetRepository.getPreset("franco")           │     │
│  │   │                                                 │     │
│  │   ├──> Load franco.yaml (options)                  │     │
│  │   └──> Load franco.json (base settings)            │     │
│  │          │                                          │     │
│  │          └──> Return Preset object                 │     │
│  └─────────────────────────┬──────────────────────────┘     │
└────────────────────────────┼──────────────────────────────────┘
                             │ List<PresetOption>
                             ▼
┌──────────────────────────────────────────────────────────────┐
│                       BOT LAYER                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │ SeedPresenter.presentFrancoOptions(options)        │     │
│  │   │                                                 │     │
│  │   └──> DiscordMessage (SelectMenus + Buttons)      │     │
│  └────────────────────────────────────────────────────┘     │
└───────────────────────────┬──────────────────────────────────┘
                            │
                            ▼
                 User selects options + clicks "Validate"
                            │
┌───────────────────────────┴──────────────────────────────────┐
│                       BOT LAYER                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │ FrancoValidateHandler.handle(interaction)          │     │
│  │   │                                                 │     │
│  │   └──> SeedService.generateSeed(                   │     │
│  │           mode=FRANCO,                              │     │
│  │           userId,                                   │     │
│  │           userSettings                              │     │
│  │        )                                            │     │
│  └────────────────────────┬───────────────────────────┘     │
└────────────────────────────┼──────────────────────────────────┘
                             │
┌────────────────────────────┼──────────────────────────────────┐
│                       ENGINE LAYER                           │
│  ┌─────────────────────────▼──────────────────────────┐     │
│  │ GenerateFrancoSeedUseCase.execute(request)         │     │
│  │   │                                                 │     │
│  │   ├──> 1. Get Preset from repository               │     │
│  │   │                                                 │     │
│  │   ├──> 2. ValidateSettingsUseCase                  │     │
│  │   │       └──> Check incompatibilities              │     │
│  │   │                                                 │     │
│  │   ├──> 3. BuildFinalSettingsUseCase                │     │
│  │   │       └──> Merge base + user options           │     │
│  │   │                                                 │     │
│  │   └──> 4. IRandomizerApi.generateSeed()            │     │
│  └─────────────────────────┬──────────────────────────┘     │
└────────────────────────────┼──────────────────────────────────┘
                             │ via port
┌────────────────────────────┼──────────────────────────────────┐
│                       API LAYER                              │
│  ┌─────────────────────────▼──────────────────────────┐     │
│  │ MockRandomizerApiAdapter.generateSeed(settings)    │     │
│  │   │                                                 │     │
│  │   ├──> Simulate HTTP call (1s delay)               │     │
│  │   └──> Return mock SeedResult                      │     │
│  └─────────────────────────┬──────────────────────────┘     │
└────────────────────────────┼──────────────────────────────────┘
                             │ SeedResult
                             ▼
┌──────────────────────────────────────────────────────────────┐
│                       BOT LAYER                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │ SeedPresenter.presentSeedResult(result)            │     │
│  │   │                                                 │     │
│  │   └──> DiscordMessage ("✅ Seed generated! 🔗...")│     │
│  └────────────────────────┬───────────────────────────┘     │
└────────────────────────────┼──────────────────────────────────┘
                             │ DiscordMessage
                             ▼
┌──────────────────────────────────────────────────────────────┐
│                       API LAYER                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │ JDAInteractionAdapter.deferAndReply(message)       │     │
│  │   │                                                 │     │
│  │   └──> Convert to JDA MessageEditAction            │     │
│  └────────────────────────┬───────────────────────────┘     │
└────────────────────────────┼──────────────────────────────────┘
                             │
                             ▼
                      ┌─────────────┐
                      │   Discord   │
                      │   Message   │
                      └─────────────┘
```

---

## Dépendances entre Couches

```
┌──────────────────────────────────────────────────────────────┐
│                    DEPENDENCY FLOW                           │
│                                                              │
│   API Layer ────────────> Engine Layer (implements ports)   │
│       │                          ▲                           │
│       │                          │                           │
│       ▼                          │                           │
│   Bot Layer ─────────────────────┘ (uses use cases)         │
│                                                              │
│  ❌ Engine Layer NEVER depends on Bot or API                │
│  ❌ Bot Layer NEVER depends on API (only abstractions)      │
│  ✅ All dependencies point INWARD (to the domain)           │
└──────────────────────────────────────────────────────────────┘
```

---

## Clean Architecture Cercles Concentriques

```
                    ┌─────────────────────┐
                    │   External Systems  │
                    │  Discord, HTTP, FS  │
                    └──────────┬──────────┘
                               │
                ┌──────────────▼──────────────┐
                │      API LAYER (Blue)       │
                │   Frameworks & Drivers      │
                │  JDA, Jackson, SnakeYAML    │
                └──────────────┬──────────────┘
                               │
                    ┌──────────▼──────────┐
                    │   BOT LAYER (Green) │
                    │  Interface Adapters │
                    │ Handlers, Presenters│
                    └──────────┬──────────┘
                               │
                        ┌──────▼──────┐
                        │ ENGINE LAYER│
                        │   (Gold)    │
                        │  Use Cases  │
                        └──────┬──────┘
                               │
                          ┌────▼────┐
                          │ ENTITIES│
                          │ (Core)  │
                          └─────────┘

        ← Dependencies flow INWARD (Dependency Inversion)
```

---

## Packages Java et leurs Responsabilités

| Package | Responsabilité | Dépend de |
|---------|---------------|-----------|
| `engine.domain.entities` | Objets métier purs | Rien |
| `engine.domain.ports` | Contrats (interfaces) | `entities` |
| `engine.usecases` | Logique métier | `entities`, `ports` |
| `bot.models` | DTOs Discord abstraits | Rien |
| `bot.handlers` | Gestion interactions | `bot.models`, `bot.services`, `bot.presenters` |
| `bot.services` | Orchestration | `engine.usecases` |
| `bot.presenters` | Formatage réponses | `bot.models`, `engine.domain.entities` |
| `api.discord` | Adaptateurs JDA | `bot.models`, `bot.handlers`, JDA |
| `api.randomizer` | HTTP Mock | `engine.domain.ports`, `engine.domain.entities` |
| `api.python` | Python Mock | `engine.domain.ports`, `engine.domain.entities` |
| `api.repositories` | Chargement presets | `engine.domain.ports`, `engine.domain.entities` |

---

**Architecture respectant les principes SOLID et Clean Architecture**
