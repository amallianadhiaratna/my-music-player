# My Music Player

A simple music player applications. It has the music library and music players.
The intention of this applications is to learn a variety communication between classes and to implement few layouts on android.

## Table of Contents
- [My Music Player](#my-music-player)
    - [Table of Contents](#table-of-contents)
    - [Technologies](#technologies)
    - [Project Structures](#project-structures)
    
## Technologies

This project is mainly created with:

- Java version: 8
- Retrofit version: 2.9.0
- EventBus
- Activity & lifecycle
- Fragment & lifecycle
- BroadcastReceiver
- Notification
- Service (Foreground / Background)

## Project Structures

my-music-player
├─ Dockerfile
├─ README.md
├─ app           
│  ├─ src
│  │  ├─ androidTest
│  │  ├─ test
│  │  ├─ main
│  │  │  ├─ java
│  │  │  │  └─ com.learn.mymusic
│  │  │  │  │  ├─ Activity
│  │  │  │  │  ├─ Model
│  │  │  │  │  ├─ Receiver
│  │  │  │  │  ├─ Retrofit
│  │  │  │  │  └─  Service
│  │  │  ├─ res
│  │  │  └─ AndroidManifest.xml
│  │  └─ build.gradle
├─ gradle
├─ build.gradle
├─ gradle.properties
├─ gradlew
├─ gradlew.bat
└─ settings.gradle
