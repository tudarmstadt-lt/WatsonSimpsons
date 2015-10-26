# Simpsons Quiz

## About

The Simpsons Quiz was developed as part of the students lab [Question Answering Technologies Behind IBM Watson](https://www.lt.informatik.tu-darmstadt.de/de/teaching/lectures-and-classes/summer-term-2015/question-answering-technologies-behind-ibm-watson/)
offered by the Language Technology Group at Technische Universität Darmstadt and uses IBM Watson technology.

It was created in 2015 by Stefan Bauregger, Timo Gerecht, Daniel Theiß and Ute Winchenbach.

## Components

### AnswerProcessing
Containing the Answer Processing Pipeline, which further processes IBM Watson answers to obtain shorter answers. It communicates with the StanfordCoreNLPServer for text annotations.

### JWatson
Library for communication with IBM Watson instance (sending questions and receiving answers).

### QuizBackend
The QuizBackend Java implementation acts as the client component that communicates with the Web-Backend over the
REST-API.

### SimpsonsQuiz
SimpsonsQuiz Android App

### SimpsonsQuiz-Web
SimpsonsQuiz Web Application, using [Play Framework](https://www.playframework.com/)

### StanfordCoreNLPServer
Server component for Natural Language Processing, using [Stanford CoreNLP Toolkit](http://nlp.stanford.edu/software/corenlp.shtml)

### ServerBackend
For managing SimpsonsQuiz related data like users, questions and reviews a RESTful Web-Backend was implemented.
The Web-Backend mainly acts as intermediary between the database and the SimpsonsQuiz applications. It was programmed in
PHP and uses [Slim](http://www.slimframework.com/), a micro framework for PHP.

## Prerequisites
* [Oracle JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or [OpenJDK](http://openjdk.java.net/install/) version 7 or later
* [Android SDK](https://developer.android.com/sdk/index.html) including Android Studio as IDE for App Development
* [Gradle](http://gradle.org/) for building libaries and Android App (included in Android Studio)
* [Scala SBT](http://www.scala-sbt.org/) for building the Web Application
* [Apache Ant](http://ant.apache.org) for building StanfordCoreNLPServer

## Installation / Preparations

Before deploying and using the Applications, the ServerBackend and the StanfordCoreNLPServer needs to be installed.
Installation instructions for these can be found in the README files in the corresponding directories.
After installation, you should have all the needed credentials and URLs together.

## Deployment

### Android Application

1. Rename 'java/SimpsonsQuiz/app/src/main/assets/simpsonsquiz_sample.properties' to 'simpsonsquiz.properties'
2. Open the file and enter the credentials and URLs where asked for.
3. Run
   ```gradlew assembleDebug```
   in 'java/SimpsonsQuiz'
4. The builded .apk file can be found in 'java/SimpsonsQuiz/app/build/outputs/apk/app-debug.apk'

### Web Application

1. Rename 'java/SimpsonsQuiz-Web/conf/credentials_sample.conf' to 'credentials.conf'
2. Open the file and enter the credentials and URLs where asked for.
3. Run
   ```gradle copyJars```
   in 'java/SimpsonsQuiz' to copy compiled jars into web project
4. Run
   ```sbt dist```
   in 'java/SimpsonsQuiz-Web' for packaging
5. The packaged zip can be found in 'java/SimpsonsQuiz-Web/target/universal/'
6. The executable can be found in the bin-folder of the extracted zip
#### for Linux only:
7. Make 'simpsonsquiz-web-1.0/bin/simpsonsquiz-web' executable with
   ```chmod +x simpsonsquiz-web```
8. Run the executable with
   ```./simpsonsquiz-web -DapplyEvolutions.default=true```
   Optionally you can specify the port with ```-Dhttp.port=8080``` as parameter

(More infos on Play Framework Deployment: https://www.playframework.com/documentation/latest/Production)

	
