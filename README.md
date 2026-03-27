# Autonomous Driving Agent (TORCS)

## Overview
A Java-based autonomous driving agent developed for **The Open Racing Car Simulator (TORCS)**. This project utilizes a **K-Nearest Neighbors (KNN)** machine learning model to dynamically control steering, acceleration and braking in real-time. 

Operating on a client-server architecture, the system ingests telemetry data via UDP streams, parses the environment state, normalizes features,and executes predicted driving maneuvers at high frequencies.

---

## Core Features

### Real-Time Telemetry Processing
- Connects to the TORCS server via a custom UDP `SocketHandler` (`Client.java`).
- Parses complex string-based sensor messages into structured state representations (`MessageParser.java`).
- Extracts real-time physics data including speed, RPM, lateral velocity, track edge distances, and angle to track axis.

### K-Nearest Neighbors (KNN) Autonomous Control
- **Feature Extraction:** Constructs a state vector (Sample) using:
  - `angleToTrackAxis`
  - `speed`
  - `lateralSpeed`
  - `trackEdgeSensors` (range-finder arrays)
  - `trackPosition`
- **Feature Scaling:** Applies Min-Max normalization based on training dataset extremes to ensure balanced distance calculations in the multi-dimensional feature space.
- **Classification:** Maps the normalized state vector to one of 9 discrete driving behaviors (combinations of steering inputs, full/zero acceleration and braking).

### Heuristic Sub-Systems
- **Automated Gearbox:** Implements a dynamic RPM-based gear-shifting algorithm (`getGear`) optimized for distinct upshift/downshift power bands.
- **Data Collection UI:** Includes a Java Swing interface (`ContinuousCharReaderUI`) to manually drive the vehicle, allowing for the generation of custom `driving_data.csv` training sets.

---

## Machine Learning Architecture

The agent approximates continuous control by classifying the current state into one of 9 distinct action categories.

**Action Mapping (`predictedClass`):**
* `0`: Accelerate Straight
* `1`: Accelerate + Steer Right
* `2`: Accelerate + Steer Left
* `3`: Coast (No input) Straight
* `4`: Coast + Steer Right
* `5`: Brake Straight
* `6`: Brake + Steer Right
* `7`: Brake + Steer Left
* `8`: Coast + Steer Left

---

## Tech Stack & Engineering Practices

- **Language:** Java
- **Machine Learning:** Custom KNN implementation (`knn.NearestNeighbor`)
- **Networking:** UDP Socket Programming (`java.net`)
- **Simulation Environment:** TORCS (The Open Racing Car Simulator)
- **Architecture:** Client-Server, Event-Driven Processing Loop

---

## Code Structure Highlights

- **`Client.java`** The main entry point. Handles the UDP initialization string, manages the episode/step lifecycle and maintains the primary control loop.
- **`KnnDriver.java`** The core decision engine. Extends the abstract `Controller` class. Ingests the `SensorModel`, normalizes the features, queries the KNN model and outputs an `Action`.
- **`MessageBasedSensorModel.java`** Acts as an adapter/wrapper around the raw parsed UDP strings, exposing clean getter methods (e.g., `getSpeed()`, `getTrackEdgeSensors()`) for the controller logic.
- **`Action.java`** A data class representing the actuator outputs sent back to the TORCS server, complete with hard limits to prevent illegal out-of-bounds commands.

---

##  How to Run

### Prerequisites
1. Install [TORCS](https://sourceforge.net/projects/torcs/) and the SCR (Simulated Car Racing) Server patch.
2. Ensure the training dataset (`driving_data.csv`) is placed in the project root.

### Compiling the Client

1. Access the src folder through terminal.
2. Type the following command to compile the Java files:
	`javac -d ../classes scr/*.java`


### Launching the Client

1. Access the classes folder through terminal.
2. Type the following command to start the client:
	`java scr.Client scr.KnnDriver host:localhost port:3001 verbose:on`


## Instructions for the collection of data from manual driving in Torcs

These instructions will guide you through the collection of data from manual driving, which is necessary to execute autonomous driving on Torcs.

### Steps for data collection

1. Access the classes folder through terminal.
2. Having the terminal open, type the following command to start the client and initialize data collection:

   `java scr.Client scr.SimpleDriver host:localhost port:3001 verbose:on`


