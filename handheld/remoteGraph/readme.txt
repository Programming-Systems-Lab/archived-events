RemoteGraph 1.0: a demo of remote Siena-driven GUI
by Ricky Chin
rkc10@columbia.edu

This file contains a Java application that contains a three node graph. You can modify
the graph and observer the modification of others with this application. In other words,
it publishes and receives notifications about the graph.

Files submitted:
readme.txt 

wheat.txt

RemoteGraph.java
SetupPanel.java
GraphGUI.java
GraphPanel.java
Node.java

RemoteGraph.jar

Under siena folder: 
Files extracted from the Siena jar downloaded from the Siena site.

Installation:


To compile the source, install Siena and type "javac *.java"

To run just type "java RemoteGraph"

This application was compiled with JDK1.3.0_02. It is packaged as an executable jar file.
Just type "java -jar RemoteGraph.jar" to run. 


Know bugs:
For some reason, even if the Siena master specified was incorrect, the application still says that it was configured successfully 
and lets you publish and subscribe. You should know if you do not have the master configured corrected if you are not getting
notifications that you should be getting or if you know that the master you typed in does not exist.

I have not taken into account any conflicts that might arise if two users modify the graph at the same time.
