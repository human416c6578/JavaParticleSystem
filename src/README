# Particle System in Java using LWJGL & OpenGL

This project implements an **interactive particle system** written in **Java** using **LWJGL** and **OpenGL**. It was developed as part of a homework assignment focused on **object-oriented programming** and **multithreaded task execution**.

## Description

- Spawns particles interactively by clicking inside the OpenGL window.
- Particles simulate **realistic movement** and **collision detection** with each other.
- Scales particle size and color dynamically based on remaining life.
- Implements a multithreaded task system to update particle states efficiently.

## Technologies Used

- Java
- LWJGL (Lightweight Java Game Library)
- OpenGL (GLSL for vertex & fragment shaders)
- Java `Phaser` and thread pool for parallel task execution

## Features

- **Object-Oriented Design** with separate classes:
  - `Shader`: Manages GLSL shader programs
  - `Particle`: Represents an individual particle
  - `ParticleSpawner`: Handles particle generation on mouse input
  - `ParticleSystem`: Manages particle lifecycle and logic
  - `ParticleRenderer`: Handles rendering of particles
- **Parallelized Updates**: Uses a task system to distribute work across threads:
  ```java
  waitForPhase(taskCount, chunkSize, i -> {
      if (!particleEmpty[i]) {
          particles[i].detectCollisions(i, particles, particleCount);
      }
  });

  waitForPhase(taskCount, chunkSize, i -> {
      if (!particleEmpty[i]) {
          particles[i].updatePosition(deltaTime);
      }
  });
  ```
- **Visual Effects**:
  - Life-based scaling and color interpolation in shaders
  - Shader inputs include color, lifetime, and start lifetime per particle
  - Real-time updates and rendering through OpenGL

## How It Works

1. Clicking in the window spawns new particles at the cursor position.
2. Particles are updated in parallel using chunked task execution and a `Phaser` to sync threads.
3. Collisions between particles are calculated before positions are updated.
4. The vertex and fragment shaders use per-particle attributes to compute size and color.
