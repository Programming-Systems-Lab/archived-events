WHEATIES 1.0: a simple Siena client
by Ricky Chin
rkc10@columbia.edu

This Java application lets the user configure a Siena service and publish notifications or subscribe according
a series of filters.

Files submitted:
readme.txt 

Under src folder: (the source code)
wheat.txt
config.txt
Wheaties.java
SetupPanel.java
PublishPanel.java
SubscribePanel.java
WheatiesApplet.java
WheatiesApplet.html
Wheaties.jar

Under siena folder: 
Files extracted from the Siena jar downloaded from the Siena site.

Installation:
You must install Siena under the Wheaties directory.
To compile the source type "javac *.java"
To package as a jar file, compile and type "jar cvmf wheat.txt Wheaties.jar config.txt *.class siena

This application was compiled with JDK1.3.1_01. It is packaged as an executable jar file.
Just type "java -jar Wheaties.jar" to run.

Wheaties is also available as an applet. Since siena uses classes in Java 2 and the Wheaties
GUI uses Swing, you must have a Java 2 plugin to run the applet.
After the classes are compiled, open WheatiesApplet.html or use "appletviewer WheatiesApplet.html" 
to run.

Know bugs:
For some reason, even if the Siena master specified was incorrect, the application still says that it was configured successfully 
and lets you publish and subscribe. You should know if you do not have the master configured corrected if you are not getting
notifications that you should be getting or if you know that the master you typed in does not exist.
