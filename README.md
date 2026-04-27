# Simple NTRIP client

### About
A simple tool to quickly test the status of an NTRIP user account or a server. The tool has both a CLI and a GUI.
It also offers the possibility to output corrections to a serialport.

### Installation (using Maven)
`git clone https://github.com/rDjupedal/NtripClient`  
`cd NtripClient`  
`mvn clean package`  

CLI version  
`java -cp target/NtripClient-1.0-SNAPSHOT.jar cli.Main`  

GUI version  
`java -cp target/NtripClient-1.0-SNAPSHOT.jar gui.Main_GUI`

### Instructions
Run one of the entrypoints (cli/Main or gui/Main_GUI) depending on which interface you want.