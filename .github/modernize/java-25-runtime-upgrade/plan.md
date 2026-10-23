# Modernization Plan: Java 25 LTS Runtime Upgrade

**Project**: management

## Technical Framework

- **Language**: Java 21
- **Framework**: Spring Boot 4.1.0
- **Build Tool**: Maven

## Overview

This plan addresses only the assessment finding that the application is not
using the latest LTS Java version. The project currently targets Java 21 in
`pom.xml`; the goal is to move the runtime and build target to Java 25 LTS while
preserving existing application behavior. Spring Boot 4.1.0 supports Java 25
per the Java upgrade guideline, so no framework upgrade is included.

The scope is limited to the Java runtime/version upgrade and the build and
existing-test validation needed to verify it. Cloud readiness, credentials and
security, containerization, and database recommendations from the assessment
are explicitly out of scope.

## Migration Impact Summary

| Application | Current runtime | Target runtime | Scope |
|---|---|---|---|
| management | Java 21 | Java 25 LTS | Runtime/version only |

## Assessment Finding

- **Issue**: Java Version is not the latest LTS
- **Rule ID**: `azure-java-version-02000`
- **Evidence**: Assessment identifies Java 21; `pom.xml` sets
  `java.version` to `21`.
- **Selected outcome**: Upgrade the Java runtime/version to Java 25 LTS only.
