RemoteGui 1.0: a demo of remote Siena-driven GUI
by Ricky Chin
rkc10@columbia.edu

This file contains a publisher (GraphDemo) that publishes drawing instructions
and an application (RemoteGUI) that takes those notifications and draws a drawing.

Files submitted:
readme.txt 

wheat.txt
config.txt
RemoteGUI.java
SetupPanel.java
SubscribePanel.java
GraphDemo.java
demo.html


Installation:
Run the publisher with any browser by opening demo.html. (You must
have the JRE 1.2 or later installed)


To compile the source, you must have Siena installed in the same directory as the source and type "javac *.java"

This application was compiled with JDK1.3.1_01. It is packaged as an executable jar file.
Just type "java -jar Remote.jar" to run.


Know bugs:
For some reason, even if the Siena master specified was incorrect, the application still says that it was configured successfully 
and lets you publish and subscribe. You should know if you do not have the master configured corrected if you are not getting
notifications that you should be getting or if you know that the master you typed in does not exist.

RemoteGui is not multi-threading which may make it stall with trying to draw complex drawings.
A solution would be to minimize the notifications by having the drawing commands be bundled in
each notification in XML and let RemoteGUI read the notifications like a program. Another way is
to add multi-thread support to it. 

RemoteGui draws a graphic that flickers. You might want to customize the paint method when you
are multi-threading it.