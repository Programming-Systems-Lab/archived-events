//README.txt
// Ian Zilla – iaz6@columbia.edu

How to run (on Windows):
1. compile source code (javac *.java)
2. java SEventDriver (output is written to C:\psl\events\smartevents\ as a text file)
note:  basic info needed to create the events is hard-coded into the driver.

Major issues:
1. The overall SmartEvent schema is not complete.  A SOAP-based schema should be made to 
hold a header block of XML and one or more body blocks of XML.  Once that is complete, the 
file which has output written to it from the toolkit can be changed from .txt to .xml.  
2. In the toolkit, ArchMutations is stubbed out, so that needs to be implemented.
3. The toolkit currently just builds the Context, MethodCall, and ComponentStatus Events from a 
SmartEvent structure.  It should also be able to parse SmartEvent XML to create the structure.
