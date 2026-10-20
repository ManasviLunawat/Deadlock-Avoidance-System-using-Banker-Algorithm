# Deadlock Avoidance System Using Banker's Algorithm

## Overview

The **Deadlock Avoidance System Using Banker's Algorithm** is a Java-based desktop application. The application simulates deadlock avoidance by checking whether a system is in a safe state and evaluating resource requests before granting them. It uses a graphical user interface (GUI) built with Java Swing.

## Features

- **System Setup:** Enter the number of processes and resource types.
- **Resource Allocation:** Input the Allocation matrix, Maximum matrix, and Available resource vector.
- **Need Matrix Calculation:** Calculate the remaining resource requirements using the Maximum and Allocation matrices.
- **Safety Check:** Determine whether the system is in a safe state.
- **Safe Sequence:** Display a possible safe sequence when one exists.
- **Resource Request Handling:** Check whether a resource request can be granted without making the system unsafe.
- **Interactive GUI:** Perform operations through a Java Swing interface.
- **Reset and Exit:** Reset the system for a new configuration or close the application.

## Technologies Used
- Java
- Java Swing
- Banker's Algorithm
- Visual Studio Code

## Project Structure
Deadlock-Avoidance-System/
├── Main.java
├── MainFrame.java
├── SystemSetupPanel.java
├── AvoidancePanel.java
├── BankerAlgorithm.java

## Requirements
- Java Development Kit (JDK) installed
- A Java-compatible IDE or terminal

## How to Run

**1. Clone the repository**
git clone https://github.com/YOUR-USERNAME/Deadlock-Avoidance-System.git

**2. Navigate to the project folder**
cd Deadlock-Avoidance-System

**3. Compile the Java files**
javac *.java

**4. Run the application**
java Main

## Working Principle
The application uses Banker's Algorithm to evaluate system safety.

1. The user enters the system's resource information.
2. The application calculates the Need matrix. **Need = Maximum − Allocation**
3. The safety algorithm checks whether processes can complete with the available resources.
4. If all processes can finish in some order, the system is considered safe and a safe sequence is displayed.
5. When a resource request is submitted, the application checks the request against the process's remaining Need and the Available resources.
6. The request is granted only if the resulting system state remains safe; otherwise, the request is denied or the tentative allocation is rolled back.

## Learning Outcomes
- Understanding deadlocks and deadlock avoidance.
- Implementing Banker's Algorithm in Java.
- Working with matrices and resource allocation.
- Developing a graphical interface using Java Swing.
- Understanding safe states, unsafe states, and safe sequences.
