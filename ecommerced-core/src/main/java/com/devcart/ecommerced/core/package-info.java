/**
 * Ecommerce Core Module - Hexagonal Architecture
 * <p>
 * This module contains the core domain logic and application services for the ecommerce platform.
 * It follows the hexagonal architecture pattern (also known as Ports and Adapters).
 * <p>
 * Package Structure:
 * <p>
 * com.devcart.ecommerced.core
 * ├── domain
 * │   ├── common          - Base classes for entities, value objects, and domain events
 * │   ├── shared          - Shared value objects used across bounded contexts
 * │   └── [bounded-context] - Domain entities and services for specific bounded contexts
 * │
 * ├── application
 * │   ├── common          - Common application layer utilities (Result, etc.)
 * │   ├── port
 * │   │   ├── in          - Input ports (Use cases, Commands, Queries)
 * │   │   └── out         - Output ports (Repositories, External services)
 * │   └── service         - Application services implementing use cases
 * │
 * └── infrastructure      - (Will be in separate modules)
 * ├── persistence     - Database adapters
 * ├── messaging       - Event publishing adapters
 * └── web             - REST API adapters
 * <p>
 * Key Principles:
 * <p>
 * 1. Domain Independence: The domain layer has no dependencies on external concerns
 * 2. Ports and Adapters: Clear separation between core logic and external adapters
 * 3. Dependency Inversion: Core depends on abstractions, not concretions
 * 4. CQRS: Command Query Responsibility Segregation for better separation of concerns
 * 5. Domain Events: Communicate changes within and between bounded contexts
 * <p>
 * Dependencies:
 * - Only depends on Lombok for boilerplate reduction
 * - No logging, web, or persistence dependencies in this module
 * - External dependencies are represented as interfaces (ports)
 */
package com.devcart.ecommerced.core; 