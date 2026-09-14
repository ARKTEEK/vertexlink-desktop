# VertexLink-Desktop
> **Warning**
> 
> This project is currently a **Work in Progress (WIP)**. Features, protocols, and APIs are subject to change.

VertexLink-Desktop is a Java-based desktop application designed to work with [VertexLink-Android](https://github.com/arkteek/vertexlink-android). It enables users to remotely control their desktop environment's mouse, keyboard, clipboard and audio directly from an Android device with little to no delay.

## Features

* **Mouse Control**: Real-time cursor movement, left/right clicks, and scrolling.
* **Keyboard Input**: Send keystrokes and text directly to the host machine.
* **Clipboard Synchronization**: Share and sync clipboard text between mobile and desktop devices.
* **Volume Management**: Adjust host device system volume remotely.
* **Secure Transport**:
  * **TLS**: Secures TCP connections for control signals and reliable data transfer.
  * **dTLS**: Secures UDP connections for low-latency stream data.
* **ACK Pairing**: Strict authentication model ensuring only explicitly paired devices can establish TCP, UDP, or other network connections.

## Prerequisites

* **Java Runtime Environment (JRE)**: Java 17 or higher recommended.
* **VertexLink-Android**: The mobile application required to control this host. Download the latest APK release from [github.com/arkteek/vertexlink-android](https://github.com/arkteek/vertexlink-android).

## Getting Started

### Installation

1. Download the latest release from the [Releases](https://github.com/arkteek/vertexlink-desktop/releases) section.
2. Ensure Java is installed and added to your system PATH:
   ```bash
   java -version
   ```

## Device Pairing & Security

1. Launch **VertexLink-Desktop** on your PC.
2. Open **VertexLink-Android** on your mobile device.
3. Initiate the ACK pairing process from the mobile app to negotiate keys and register the device.
4. Once paired, the desktop host accepts secure TLS (TCP) and dTLS (UDP) sessions from the verified client. Unpaired devices are rejected by default.
